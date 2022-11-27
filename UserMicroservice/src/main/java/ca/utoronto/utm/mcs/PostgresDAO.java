package ca.utoronto.utm.mcs;

import java.sql.*;

import javax.naming.spi.DirStateFactory.Result;

import io.github.cdimascio.dotenv.Dotenv;

public class PostgresDAO {

    public Connection conn;
    public Statement st;

    public PostgresDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("POSTGRES_ADDR");
        String url = "jdbc:postgresql://" + addr + ":5432/root";
        try {
            Class.forName("org.postgresql.Driver");
            this.conn = DriverManager.getConnection(url, "root", "123456");
            this.st = this.conn.createStatement();
            System.out.println("Logged in postgresSQL");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // *** implement database operations here *** //
    public boolean checkUserExists(String email) throws SQLException {
        String checkUser = "SELECT * FROM users WHERE email = '%s'";
        checkUser = String.format(checkUser, email);
        ResultSet rs = this.st.executeQuery(checkUser);
        boolean row = rs.next();
        boolean empty = false;
        if (row) {
            String emailResult = rs.getString("email");
            System.out.println("Result login: " + emailResult + row);
            rs.close();
            empty = true;

        }
        return empty;

    }

    public int postLoginUser(String email, String password) throws SQLException {
        // must hash string here
        password = Utils.hashString(password);

        String query = "SELECT * FROM users WHERE email = '%1$s' AND \"password\" = '%2$s'";
        query = String.format(query, email, password);
        ResultSet q = this.st.executeQuery(query);
        boolean qExists = q.next();
        String qEmail = null;
        if (qExists) {
            qEmail = q.getString("email");
            System.out.println("Logging in email: " + qEmail);
        }

        if (qEmail != null) {
            return 1;
        } else {
            return 2;
        }
    }

    public int postRegisterUser(String name, String email, String password) throws SQLException {

        // must hash string
        password = Utils.hashString(password);

        String query = "INSERT INTO users (prefer_name, email, \"password\", rides) VALUES ('%1$s', '%2$s', '%3$s' , 0)";
        query = String.format(query, name, email, password);
        this.st.execute(query);
        return 1;

    }

    public ResultSet getUsersFromUid(int uid) throws SQLException {
        String query = "SELECT * FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public ResultSet getUserData(int uid) throws SQLException {
        String query = "SELECT prefer_name as name, email, rides, isdriver FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides,
            Boolean isDriver) throws SQLException {

        String query;
        if (email != null) {
            query = "UPDATE users SET email = '%s' WHERE uid = %d";
            query = String.format(query, email, uid);
            this.st.execute(query);
        }
        if (password != null) {
            query = "UPDATE users SET password = '%s' WHERE uid = %d";
            query = String.format(query, password, uid);
            this.st.execute(query);
        }
        if (prefer_name != null) {
            query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
            query = String.format(query, prefer_name, uid);
            this.st.execute(query);
        }
        if ((rides != null)) {
            query = "UPDATE users SET rides = %d WHERE uid = %d";
            query = String.format(query, rides, uid);
            this.st.execute(query);
        }
        if (isDriver != null) {
            query = "UPDATE users SET isdriver = %s WHERE uid = %d";
            query = String.format(query, isDriver.toString(), uid);
            this.st.execute(query);
        }
    }
}
