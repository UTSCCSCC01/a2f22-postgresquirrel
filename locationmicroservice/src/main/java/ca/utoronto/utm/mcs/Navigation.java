package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("Reached /location/navigation handle");
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        String[] split = params[3].split("\\?passengerUid=");

        if (split.length != 2 || split[0].equals("")) {
            this.sendStatus(r, 400);
            return;
        }

        String driverUid = split[0];
        String passengerUid = split[1];

        try {
            Result startRes = this.dao.getUserLocationByUid(driverUid);
            Result destRes = this.dao.getUserLocationByUid(passengerUid);

            String start = null;
            String dest = null;
            if (startRes.hasNext() && destRes.hasNext()) {
                Record startRec = startRes.next();
                start = startRec.get("n.street").asString();
                Record destRec = destRes.next();
                dest = destRec.get("n.street").asString();
            } else {
                this.sendStatus(r, 404);
            }
            Result result = this.dao.getNavigation(start, dest);

            if (result.hasNext()) {
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();
                Record driver = result.next();
                data.put("total_time", driver.get("total_time").asInt());

                int length = driver.get("length").asInt();

                String streetsString = driver.get("nodeNames").asObject().toString();
                String timesString = driver.get("times").asObject().toString();
                String trafficBoolsString = driver.get("trafficBools").asObject().toString();

                String[] streetsList = streetsString.substring(1,streetsString.length()-1).split(", ");
                String[] timesList = timesString.substring(1,timesString.length()-3).split(".0, ");
                String[] trafficBoolsList = trafficBoolsString.substring(1,trafficBoolsString.length()-1).split(", ");

                int[] properTimesList = new int[length];
                boolean[] properTrafficBoolsList = new boolean[length];

                for (int i=0; i<length; i++) {
                    properTimesList[i] = Integer.parseInt(timesList[i]);
                    properTrafficBoolsList[i] = Boolean.parseBoolean(trafficBoolsList[i]);
                }

                int traveled = 0;
                JSONArray route = new JSONArray();
                for (int i=0; i<length; i++) {
                    JSONObject currStreet = new JSONObject();
                    currStreet.put("street", streetsList[i]);
                    currStreet.put("time", properTimesList[i]-traveled);
                    traveled = properTimesList[i];
                    currStreet.put("is_traffic", properTrafficBoolsList[i]);
                    route.put(currStreet);
                }
                data.put("route", route);

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
