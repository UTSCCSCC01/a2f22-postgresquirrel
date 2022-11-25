package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Request extends Endpoint {

    /**
     * POST /trip/request
     * 
     * @body uid, radius
     * @return 200, 400, 404, 500
     *         Returns a list of drivers within the specified radius
     *         using location microservice. List should be obtained
     *         from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("Reached /trip/request endpoint");
        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            if (body.has("uid") && body.has("radius")) {
                String[] drivers = this.dao.postTripRequest(body.getString("uid"), body.getInt("radius"));

                if (drivers.length < 1) {
                    this.sendStatus(r, 404);
                }

                JSONObject res = new JSONObject();
                res.put("data", drivers);
                res.put("status", "OK");

                System.out.println("posted request");
                this.sendResponse(r, res,200);

            } else {
                this.sendStatus(r, 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
