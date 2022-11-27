package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * 
     * @body driver, passenger, startTime
     * @return 200, 400
     *         Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        System.out.println("confirming");
        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            if (body.has("driver") && body.has("startTime") && body.has("passenger")) {
                String id = this.dao.postConfirmTrip(body.getString("driver"), body.getLong("startTime"),
                        body.getString("passenger"));
                if (id != "") {
                    System.out.println("confirmed");

                    JSONObject res = new JSONObject();
                    JSONObject obj = new JSONObject();
                    obj.put("_id", id);
                    res.put("data", obj);
                    this.sendResponse(r, res, 200);
                } else {
                    System.out.println("fail to confirm");
                    this.sendStatus(r, 400);
                }

            } else {
                this.sendStatus(r, 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
