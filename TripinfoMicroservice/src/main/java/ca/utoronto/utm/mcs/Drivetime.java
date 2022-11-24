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

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * 
     * @param _id
     * @return 200, 400, 404, 500
     *         Get time taken to get from driver to passenger on the trip with
     *         the given _id. Time should be obtained from navigation endpoint
     *         in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("getting drivetime");
        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            if (body.has("_id")) {

                this.dao.getTripDrivetime(body.getString("_id"));
                System.out.println("got drivetime");
                this.sendStatus(r, 200);

            } else {
                this.sendStatus(r, 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
