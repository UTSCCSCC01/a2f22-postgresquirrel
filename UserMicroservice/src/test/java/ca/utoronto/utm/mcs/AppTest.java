package ca.utoronto.utm.mcs;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Please write your tests in this class.
 */

public class AppTest {

    HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

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
    public void userRegisterPass() {
        JSONObject user1 = new JSONObject();
        try {
            user1.put("name", "user1");
            user1.put("email", "user1@example.com");
            user1.put("password", "password");

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/user/register"), "POST",
                    user1);
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void userRegisterFail() {
        JSONObject user2 = new JSONObject();
        try {
            user2.put("email", "user2@example.com");
            user2.put("password", "password");

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/user/register"), "POST",
                    user2);
            assertEquals(400, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void userLoginPass() {
        JSONObject user3 = new JSONObject();
        try {
            user3.put("name", "user3");
            user3.put("email", "user333@example.com");
            user3.put("password", "password");

            sendHttpRequest(new URI("http://localhost:8004/user/register"), "POST", user3);

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/user/login"), "POST", user3);
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void userLoginFail() {
        JSONObject user4 = new JSONObject();
        try {
            user4.put("password", "password");

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/user/login"), "POST", user4);
            assertEquals(400, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }
}
