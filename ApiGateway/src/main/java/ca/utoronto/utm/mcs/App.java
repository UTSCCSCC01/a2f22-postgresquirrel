package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App {
	static int PORT = 8000;

	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

		// TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.
		server.createContext("/location", new RequestRouter("http://locationmicroservice:8000"));
		server.createContext("/user", new RequestRouter("http://usermicroservice:8001"));
		server.createContext("/trip", new RequestRouter("http://tripinfomicroservice:8002"));

		server.start();
		System.out.printf("Server started on port %d...\n", PORT);
	}
}
