package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import java.sql.ResultSet;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * 
     * @body name, email, password
     * @return 200, 400, 500
     *         Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("registering");
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("name") && body.has("email") && body.has("password")) {
            try {

                if (this.dao.checkUserExists(body.getString("email"))) {
                    this.sendStatus(r, 400);
                    return;
                }
                int status = this.dao.postRegisterUser(body.getString("name"), body.getString("email"),
                        body.getString("password"));
                if (status == 1) {
                    System.out.println("registered");
                    this.sendStatus(r, 200);
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
            }
        } else {
            this.sendStatus(r, 400);
        }
    }

}
