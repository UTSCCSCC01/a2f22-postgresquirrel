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

import com.mongodb.util.JSON;
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
        JSONObject person1 = new JSONObject();
        JSONObject person2 = new JSONObject();
        JSONObject relocate1 = new JSONObject();
        JSONObject relocate2 = new JSONObject();
        JSONObject body = new JSONObject();
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

            body.put("uid", "Person1");
            body.put("radius", 2);

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/trip/request"), "POST",
                    body);
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void tripRequestFail() {
        JSONObject person3 = new JSONObject();
        JSONObject relocate3 = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            person3.put("uid", "Person1");
            person3.put("is_driver", false);

            sendHttpRequest(new URI("http://localhost:8004/location/user"), "PUT", person3);

            relocate3.put("longitude", 500.5);
            relocate3.put("latitude", 501.5);
            relocate3.put("street", "Street street");

            sendHttpRequest(new URI("http://localhost:8004/location/Person1"), "PATCH", relocate3);

            body.put("uid", "Person3");
            body.put("radius", 1);

            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/trip/request"), "POST",
                    body);
            assertEquals(404, response.statusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

        String uri = "http://localhost:8004/trip/";
        String uri2 = "http://localhost:8004/trip/confirm";
        // uri.format(uri, id);
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();

        try {
            obj1.put("driver", "goob");
            obj1.put("passenger", "goobagoon");
            obj1.put("startTime", 123456);
            HttpResponse<String> resCreate = sendHttpRequest(new URI(uri2), "POST", obj1);
            JSONObject idObj = new JSONObject(resCreate.body());
            String id = idObj.getJSONObject("data").getString("id");
            uri = uri + id;
            obj2.put("distance", 33);
            obj2.put("endTime", 323);
            obj2.put("timeElapsed", 123456);
            obj2.put("totalCost", 23.45);

            HttpResponse<String> res = sendHttpRequest(new URI(uri), "PATCH", obj2);
            assertEquals(200, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void patchTripFail() {
        String uri = "http://localhost:8004/trip/wrongId";
        // uri.format(uri, id);
        JSONObject obj = new JSONObject();

        try {

            obj.put("distance", 33);
            obj.put("endTime", 323);
            obj.put("timeElapsed", 123456);
            obj.put("totalCost", 23.45);

            HttpResponse<String> res = sendHttpRequest(new URI(uri), "PATCH", obj);
            assertEquals(404, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void tripsForPassengerPass() {
        String uri1 = "http://localhost:8004/trip/confirm";
        String uri2 = "http://localhost:8004/trip/passenger/%s";
        JSONObject obj = new JSONObject();

        try {
            obj.put("driver", "goob");
            obj.put("passenger", "goobagoon");
            obj.put("startTime", 123456);
            sendHttpRequest(new URI(uri1), "POST", obj);

            HttpResponse<String> res = sendHttpRequest(new URI(uri2), "GET", obj);
            assertEquals(200, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void tripsForPassengerFail() {
        String uri = "http://localhost:8004/trip/passenger/doesnotexists";
        JSONObject obj = new JSONObject();

        try {

            sendHttpRequest(new URI(uri), "GET", obj);

            HttpResponse<String> res = sendHttpRequest(new URI(uri), "GET", obj);
            assertEquals(404, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void tripsForDriverPass() {
        String uri1 = "http://localhost:8004/trip/confirm";
        String uri2 = "http://localhost:8004/trip/driver/goob";
        JSONObject obj = new JSONObject();

        try {
            obj.put("driver", "goob");
            obj.put("passenger", "goobagoon");
            obj.put("startTime", 123456);
            sendHttpRequest(new URI(uri1), "POST", obj);

            HttpResponse<String> res = sendHttpRequest(new URI(uri2), "GET", obj);
            assertEquals(200, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void tripsForDriverFail() {
        String uri1 = "http://localhost:8004/trip/confirm";
        String uri2 = "http://localhost:8004/trip/driver/";
        JSONObject obj = new JSONObject();

        try {
            obj.put("driver", "goob");
            obj.put("passenger", "goobagoon");
            obj.put("startTime", 123456);
            sendHttpRequest(new URI(uri1), "POST", obj);

            HttpResponse<String> res = sendHttpRequest(new URI(uri2), "GET", obj);
            assertEquals(400, res.statusCode());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void driverTimePass() {
        JSONObject person5 = new JSONObject();
        JSONObject person6 = new JSONObject();
        JSONObject relocate5 = new JSONObject();
        JSONObject relocate6 = new JSONObject();
        JSONObject road1 = new JSONObject();
        JSONObject road2 = new JSONObject();
        JSONObject connection1 = new JSONObject();
        JSONObject connection2 = new JSONObject();
        JSONObject body = new JSONObject();

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

            body.put("driver", "Person5");
            body.put("passenger", "Person6");
            body.put("startTime", 123456);

            HttpResponse<String> confirm_res = sendHttpRequest(new URI("http://localhost:8004/trip/confirm"), "POST",
                    body);

            JSONObject json = new JSONObject(confirm_res.body());
            JSONObject data = json.getJSONObject("data");
            String id = data.getString("id");
            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/trip/driverTime/" + id),
                    "GET", new JSONObject());
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void driverTimeFail() {
        try {
            HttpResponse<String> response = sendHttpRequest(new URI("http://localhost:8004/trip/driverTime/"), "GET",
                    new JSONObject());
            assertEquals(400, response.statusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}