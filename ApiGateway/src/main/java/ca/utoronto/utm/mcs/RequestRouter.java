package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * the microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.OutputStream; // Also given to you to send back your response
import java.time.Duration;
import java.util.HashMap;

public class RequestRouter implements HttpHandler {

	/**
	 * You may add and/or initialize attributes here if you
	 * need.
	 */

	String route;

	public HashMap<Integer, String> errorMap;

	public RequestRouter(String route) {
		this.route = route;
		errorMap = new HashMap<>();
		errorMap.put(200, "OK");
		errorMap.put(400, "BAD REQUEST");
		errorMap.put(401, "UNAUTHORIZED");
		errorMap.put(404, "NOT FOUND");
		errorMap.put(405, "METHOD NOT ALLOWED");
		errorMap.put(409, "CONFLICT");
		errorMap.put(500, "INTERNAL SERVER ERROR");
	}

	@Override
	public void handle(HttpExchange r) throws IOException {
		// TODO
		System.out.println("Reached APIGateway handle");
		HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.followRedirects(HttpClient.Redirect.NORMAL)
				.connectTimeout(Duration.ofSeconds(20))
				.build();

		JSONObject body = null;
		try {
			body = new JSONObject(Utils.convert(r.getRequestBody()));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		System.out.println("Building request...");
		System.out.println(route + r.getRequestURI().toString());
		System.out.println(r.getRequestMethod());
		System.out.println(body.toString());

		HttpRequest request = null;
		try {
			request = HttpRequest.newBuilder(new URI(route + r.getRequestURI().toString()))
					.method(r.getRequestMethod(), HttpRequest.BodyPublishers.ofString(body.toString())).build();
		} catch (Exception e) {
			System.out.println("Build failed");
			try {
				this.sendStatus(r, 400);
			} catch (JSONException ex) {
				throw new RuntimeException(ex);
			}
		}
		System.out.println("Build success");

		HttpResponse<String> response;
		try {
			System.out.println("Sending request...");
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (InterruptedException e) {
			System.out.println("Interrupted exception");
			try {
				this.sendStatus(r, 500);
			} catch (JSONException ex) {
				throw new RuntimeException(ex);
			}
			return;
		}
		System.out.println("Request sent");

		System.out.println("Parsing response...");
		System.out.println("Status code: " + response.statusCode());
		System.out.println("Body: " + response.body());
		r.sendResponseHeaders(response.statusCode(), response.body().length());
		this.writeOutputStream(r, response.body());
		System.out.println("DONE");
	}

	public void writeOutputStream(HttpExchange r, String response) throws IOException {
		OutputStream os = r.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
		JSONObject res = new JSONObject();
		res.put("status", errorMap.get(statusCode));
		String response = res.toString();
		r.sendResponseHeaders(statusCode, response.length());
		this.writeOutputStream(r, response);
	}
}
