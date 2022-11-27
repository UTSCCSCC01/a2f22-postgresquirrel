package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * 
     * @param uid
     * @return 200, 400, 404
     *         Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("getting passenger");
        try {
            String[] params = r.getRequestURI().toString().split("/");
            if (params.length != 4 || params[3].isEmpty()) {
                this.sendStatus(r, 400);
                return;
            }

            JSONObject passenger = this.dao.getPassengerTrips(params[3]);


            if (passenger.has("empty")) {
                System.out.println("Passenger does not exist");
                this.sendStatus(r, 404);
                return;
            } else {
                JSONObject res = new JSONObject();
                res.put("data", passenger);
                System.out.println("got passenger");
                this.sendResponse(r, res, 200);
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();

            this.sendStatus(r, 500);
        }
    }
}
