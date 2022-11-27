package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import java.sql.ResultSet;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * 
     * @body email, password
     * @return 200, 400, 401, 404, 500
     *         Login a user into the system if the given information matches the
     *         information of the user in the database.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        System.out.println("logging in");
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("email") && body.has("password")) {
            try {
                int status = this.dao.postLoginUser(body.getString("email"), body.getString("password"));
                if (!this.dao.checkUserExists(body.getString("email"))) {
                    System.out.println("User does not exist");
                    this.sendStatus(r, 404);
                    return;
                }
                if (status == 1) {
                    System.out.println("logged in");
                    this.sendStatus(r, 200);
                } else if (status == 2) {
                    System.out.println("Unauthorized");
                    this.sendStatus(r, 401);
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
