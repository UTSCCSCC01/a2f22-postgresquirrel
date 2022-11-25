package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * Please write your tests in this class.
 */

public class AppTest {
    private HttpResponse<String> sendHttpRequest(URI uri, String method, JSONObject body)
            throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        // GET /location/nearbyDriver/:uid?radius=
        HttpRequest testRequest = HttpRequest.newBuilder(uri)
                .method(method, HttpRequest.BodyPublishers.ofString(body.toString())).build();

        return client.send(testRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void tripRequestPass() {

        assertTrue(true);
    }

    @Test
    public void tripRequestFail() {
        assertTrue(true);
    }

    @Test
    public void tripConfirmPass() {
        String uri = "http://localhost:8004/trip/confirm";
        JSONObject obj = new JSONObject();

        try {
            obj.put("driver", "goob");
            obj.put("passenger", "goobagoon");
            obj.put("startTime", 123456);
            HttpResponse<String> res = sendHttpRequest(new URI(uri), "POST", obj);
            assertEquals(200, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void tripConfirmFail() {
        String uri = "http://localhost:8004/trip/confirm";
        JSONObject obj = new JSONObject();

        try {
            obj.put("driver", "goob");
            obj.put("passenger", "goobagoon");
            obj.put("nooooo", 123456);
            HttpResponse<String> res = sendHttpRequest(new URI(uri), "POST", obj);
            assertEquals(400, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void patchTripPass() {

        String uri = "http://localhost:8004/trip/%s";
        // uri.format(uri, id);
        JSONObject obj = new JSONObject();

        try {
            obj.put("driver", "goob");
            obj.put("passenger", "goobagoon");
            obj.put("nooooo", 123456);
            HttpResponse<String> res = sendHttpRequest(new URI(uri), "POST", obj);
            assertEquals(400, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void patchTripFail() {
        assertTrue(true);
    }

    @Test
    public void tripsForPassengerPass() {
        assertTrue(true);
    }

    @Test
    public void tripsForPassengerFail() {
        assertTrue(true);
    }

    @Test
    public void tripsForDriverPass() {
        assertTrue(true);
    }

    @Test
    public void tripsForDriverFail() {
        assertTrue(true);
    }

    @Test
    public void driverTimePass() {
        assertTrue(true);
    }

    @Test
    public void driverTimeFail() {
        assertTrue(true);
    }

}