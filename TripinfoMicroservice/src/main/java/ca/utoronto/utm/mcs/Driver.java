package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Driver extends Endpoint {

    /**
     * GET /trip/driver/:uid
     * 
     * @param uid
     * @return 200, 400, 404
     *         Get all trips driver with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        System.out.println("getting driver");
        try {
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            if (body.has("uid")) {

                JSONObject driver = this.dao.getTripDriver(body.getString("uid"));
                System.out.println("got driver");

                this.sendResponse(r, driver, 200);

            } else {
                this.sendStatus(r, 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
