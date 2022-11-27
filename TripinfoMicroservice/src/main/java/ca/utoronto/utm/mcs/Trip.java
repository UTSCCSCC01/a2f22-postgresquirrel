package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * 
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     *         Adds extra information to the trip with the given id when the
     *         trip is done.
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("patching trip");
        try {
            String[] params = r.getRequestURI().toString().split("/");
            if (params.length != 3 || params[2].isEmpty()) {
                this.sendStatus(r, 400);
                return;
            }

            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

            if (body.has("distance") && body.has("endTime") && body.has("timeElapsed") && body.has("totalCost")) {

                // this.dao.patchTrip(body.getString("_id"));
                long p = this.dao.patchTrip(params[2], body.getInt("distance"),
                        body.getLong("endTime"), body.getLong("timeElapsed"), body.getDouble("totalCost"));
                if (p > 0) {
                    System.out.println("patched trip");
                    this.sendStatus(r, 200);
                } else if (p == 0) {
                    this.sendStatus(r, 404);
                } else if (p == -1) {
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
