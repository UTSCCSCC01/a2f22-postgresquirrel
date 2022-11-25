package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.bson.Document;

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
            String[] params = r.getRequestURI().toString().split("/");
            if (params.length != 4 || params[3].isEmpty()) {
                this.sendStatus(r, 400);
                return;
            }

            JSONObject driver = this.dao.getDriverTrips(params[3]);

            JSONObject res = new JSONObject();
            res.put("data", driver);
            System.out.println("got driver");

            this.sendResponse(r, res, 200);

        } catch (Exception e) {
            e.printStackTrace();

            this.sendStatus(r, 404);
        }
    }
}
