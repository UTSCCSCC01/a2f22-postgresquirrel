package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("Reached /location/nearbyDriver handle");
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        String[] split = params[3].split("\\?radius=");

        if (split.length != 2 || split[0].equals("")) {
            this.sendStatus(r, 400);
            return;
        }

        String uid = split[0];
        int radius;

        try {
            radius = Integer.parseInt((split[1]));
        } catch (Exception e) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            Result result = this.dao.getNearbyDrivers(uid, radius);

            if (result.hasNext()) {
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();

                while (result.hasNext()) {
                    Record driver = result.next();

                    JSONObject currDriver = new JSONObject();
                    String driverId = driver.get("d.uid").asString();
                    if (driverId.equals(uid)) continue;
                    Double longitude = driver.get("d.longitude").asDouble();
                    Double latitude = driver.get("d.latitude").asDouble();
                    String street;
                    if (driver.get("d.street") == null) {
                        street = "";
                    } else {
                        street = driver.get("d.street").asString();
                    }

                    currDriver.put("longitude", longitude);
                    currDriver.put("latitude", latitude);
                    currDriver.put("street", street);

                    data.put(driverId, currDriver);
                }

                res.put("data", data);
                res.put("status", "OK");
                this.sendResponse(r, res, 200);
            } else {
                this.sendStatus(r, 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
