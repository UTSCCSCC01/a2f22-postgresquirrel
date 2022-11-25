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
        System.out.println("Reached /trip/driverTime/ endpoint");
        try {
            String[] params = r.getRequestURI().toString().split("/");
            if (params.length != 4 || params[3].isEmpty()) {
                this.sendStatus(r, 400);
                return;
            }

            int time = this.dao.getTripDrivetime(params[3]);
            if (time == -1) {
                this.sendStatus(r, 404);
                return;
            }
            System.out.println("got drivetime");

            JSONObject res = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("arrival_time", time);
            res.put("data", data);
            res.put("status", "OK");
            this.sendResponse(r, res,200);

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
