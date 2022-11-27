package ca.utoronto.utm.mcs;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    private HttpResponse<String> sendHttpRequest(URI uri, String method, JSONObject body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        //GET /location/nearbyDriver/:uid?radius=
        HttpRequest testRequest = HttpRequest.newBuilder(uri)
                .method(method, HttpRequest.BodyPublishers.ofString(body.toString())).build();

        return client.send(testRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void getNearbyDriverPass() {
        JSONObject person1 = new JSONObject();
        JSONObject person2 = new JSONObject();
        JSONObject relocate1 = new JSONObject();
        JSONObject relocate2 = new JSONObject();
        try {
            person1.put("uid", "Person1");
            person1.put("is_driver", false);

            person2.put("uid", "Person2");
            person2.put("is_driver", true);

            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person1);
            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person2);

            relocate1.put("longitude", 0.5);
            relocate1.put("latitude", 1.5);
            relocate1.put("street", "Street street");

            relocate2.put("longitude", 1.5);
            relocate2.put("latitude", 2.5);
            relocate2.put("street", "Street street");

            sendHttpRequest(new URI("http://localhost:8004/location/Person1"), "PATCH", relocate1);
            sendHttpRequest(new URI("http://localhost:8004/location/Person2"), "PATCH", relocate2);

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/location/nearbyDriver/Person1?radius=5"), "GET", new JSONObject());
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getNearbyDriverFail() {
        JSONObject person3 = new JSONObject();
        JSONObject person4 = new JSONObject();
        JSONObject relocate3 = new JSONObject();
        JSONObject relocate4 = new JSONObject();
        try {
            person3.put("uid", "Person3");
            person3.put("is_driver", false);

            person4.put("uid", "Person4");
            person4.put("is_driver", true);

            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person3);
            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person4);

            relocate3.put("longitude", 200.5);
            relocate3.put("latitude", 201.5);
            relocate3.put("street", "Street street");

            relocate4.put("longitude", 200.5);
            relocate4.put("latitude", 204.5);
            relocate4.put("street", "Street street");

            sendHttpRequest(new URI("http://localhost:8004/location/Person3"), "PATCH", relocate3);
            sendHttpRequest(new URI("http://localhost:8004/location/Person4"), "PATCH", relocate4);

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/location/nearbyDriver/Person3?radius=1"), "GET", new JSONObject());
            assertEquals(404, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getNavigationPass() {
        JSONObject person5 = new JSONObject();
        JSONObject person6 = new JSONObject();
        JSONObject relocate5 = new JSONObject();
        JSONObject relocate6 = new JSONObject();
        JSONObject road1 = new JSONObject();
        JSONObject road2 = new JSONObject();
        JSONObject connection1 = new JSONObject();
        JSONObject connection2 = new JSONObject();
        try {
            person5.put("uid", "Person5");
            person5.put("is_driver", false);

            person6.put("uid", "Person6");
            person6.put("is_driver", true);

            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person5);
            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person6);

            relocate5.put("longitude", 0.5);
            relocate5.put("latitude", 1.5);
            relocate5.put("street", "Street street");

            relocate6.put("longitude", 1.5);
            relocate6.put("latitude", 2.5);
            relocate6.put("street", "Road road");

            sendHttpRequest(new URI("http://localhost:8004/location/Person5"), "PATCH", relocate5);
            sendHttpRequest(new URI("http://localhost:8004/location/Person6"), "PATCH", relocate6);

            road1.put("roadName", "Street street");
            road1.put("hasTraffic", false);

            road2.put("roadName", "Road road");
            road2.put("hasTraffic", false);

            sendHttpRequest(new URI("http://localhost:8004/location/road"), "PUT", road1);
            sendHttpRequest(new URI("http://localhost:8004/location/road"), "PUT", road2);

            connection1.put("roadName1", "Street street");
            connection1.put("roadName2", "Road road");
            connection1.put("hasTraffic", false);
            connection1.put("time", 2);

            connection2.put("roadName1", "Road road");
            connection2.put("roadName2", "Street street");
            connection2.put("hasTraffic", false);
            connection2.put("time", 2);

            sendHttpRequest(new URI("http://localhost:8004/location/hasRoute"), "POST", connection1);
            sendHttpRequest(new URI("http://localhost:8004/location/hasRoute"), "POST", connection2);

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/location/navigation/Person6?passengerUid=Person5"), "GET", new JSONObject());
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getNavigationFail() {
        JSONObject person7 = new JSONObject();
        JSONObject person8 = new JSONObject();
        JSONObject relocate7 = new JSONObject();
        JSONObject relocate8 = new JSONObject();
        JSONObject road3 = new JSONObject();
        JSONObject road4 = new JSONObject();
        JSONObject connection3 = new JSONObject();
        JSONObject connection4 = new JSONObject();
        try {
            person7.put("uid", "Person7");
            person7.put("is_driver", false);

            person8.put("uid", "Person8");
            person8.put("is_driver", true);

            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person7);
            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person8);

            relocate7.put("longitude", 0.5);
            relocate7.put("latitude", 1.5);
            relocate7.put("street", "Street street");

            relocate8.put("longitude", 1.5);
            relocate8.put("latitude", 2.5);
            relocate8.put("street", "Nowhere");

            sendHttpRequest(new URI("http://localhost:8004/location/Person7"), "PATCH", relocate7);
            sendHttpRequest(new URI("http://localhost:8004/location/Person8"), "PATCH", relocate8);

            road3.put("roadName", "Street street");
            road3.put("hasTraffic", false);

            road4.put("roadName", "Road road");
            road4.put("hasTraffic", false);

            sendHttpRequest(new URI("http://localhost:8004/location/road"), "PUT", road3);
            sendHttpRequest(new URI("http://localhost:8004/location/road"), "PUT", road4);

            connection3.put("roadName1", "Street street");
            connection3.put("roadName2", "Road road");
            connection3.put("hasTraffic", false);
            connection3.put("time", 2);

            connection4.put("roadName1", "Road road");
            connection4.put("roadName2", "Street street");
            connection4.put("hasTraffic", false);
            connection4.put("time", 2);

            sendHttpRequest(new URI("http://localhost:8004/location/hasRoute"), "POST", connection3);
            sendHttpRequest(new URI("http://localhost:8004/location/hasRoute"), "POST", connection4);

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/location/navigation/Person8?passengerUid=Person7"), "GET", new JSONObject());
            assertEquals(404, response.statusCode());
        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }
    }
}

