package ca.utoronto.utm.mcs;

import org.neo4j.driver.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Neo4jDAO {

    private final Session session;
    private final Driver driver;
    private final String username = "neo4j";
    private final String password = "123456";

    public Neo4jDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        String uriDb = "bolt://" + addr + ":7687";

        this.driver = GraphDatabase.driver(uriDb, AuthTokens.basic(this.username, this.password));
        this.session = this.driver.session();
    }

    // *** implement database operations here *** //

    public Result addUser(String uid, boolean is_driver) {
        String query = "CREATE (n: user {uid: '%s', is_driver: %b, longitude: 0, latitude: 0, street: ''}) RETURN n";
        query = String.format(query, uid, is_driver);
        return this.session.run(query);
    }

    public Result deleteUser(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) DETACH DELETE n RETURN n";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    public Result getUserLocationByUid(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) RETURN n.longitude, n.latitude, n.street";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    public Result getUserByUid(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) RETURN n";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    public Result updateUserIsDriver(String uid, boolean isDriver) {
        String query = "MATCH (n:user {uid: '%s'}) SET n.is_driver = %b RETURN n";
        query = String.format(query, uid, isDriver);
        return this.session.run(query);
    }

    public Result updateUserLocation(String uid, double longitude, double latitude, String street) {
        String query = "MATCH(n: user {uid: '%s'}) SET n.longitude = %f, n.latitude = %f, n.street = \"%s\" RETURN n";
        query = String.format(query, uid, longitude, latitude, street);
        return this.session.run(query);
    }

    public Result getRoad(String roadName) {
        String query = "MATCH (n :road) where n.name='%s' RETURN n";
        query = String.format(query, roadName);
        return this.session.run(query);
    }

    public Result createRoad(String roadName, boolean has_traffic) {
        String query = "CREATE (n: road {name: '%s', has_traffic: %b}) RETURN n";
        query = String.format(query, roadName, has_traffic);
        return this.session.run(query);
    }

    public Result updateRoad(String roadName, boolean has_traffic) {
        String query = "MATCH (n:road {name: '%s'}) SET n.has_traffic = %b RETURN n";
        query = String.format(query, roadName, has_traffic);
        return this.session.run(query);
    }

    public Result createRoute(String roadname1, String roadname2, int travel_time, boolean has_traffic) {
        String query = "MATCH (r1:road {name: '%s'}), (r2:road {name: '%s'}) CREATE (r1) -[r:ROUTE_TO {travel_time: %d, has_traffic: %b}]->(r2) RETURN type(r)";
        query = String.format(query, roadname1, roadname2, travel_time, has_traffic);
        return this.session.run(query);
    }

    public Result deleteRoute(String roadname1, String roadname2) {
        String query = "MATCH (r1:road {name: '%s'})-[r:ROUTE_TO]->(r2:road {name: '%s'}) DELETE r RETURN COUNT(r) AS numDeletedRoutes";
        query = String.format(query, roadname1, roadname2);
        return this.session.run(query);
    }

    public Result getNearbyDrivers(String uid, int radius) {
        String query =
                "MATCH (u:user {uid: '%s'}) " +
                "MATCH (d:user {is_driver: true}) " +
                    "WHERE (abs(u.longitude - d.longitude) <= %d AND abs(u.latitude - d.latitude) <= %d) " +
                "RETURN d.uid, d.longitude, d.latitude, d.street";
        query = String.format(query, uid, radius, radius);
        System.out.println(query);
        return this.session.run(query);
    }

    public Result getNavigation(String start, String dest) {
        System.out.println("Reached getNavigation in DAO");
        this.session.run("CALL gds.graph.drop('myGraph', false)");
        System.out.println("Cleared myGraph");
        this.session.run("CALL gds.graph.project('myGraph', 'road', 'ROUTE_TO', {relationshipProperties: 'travel_time'})");
        System.out.println("Created myGraph");
        String query =
                "MATCH (start:road {name: '%s'}), (end:road {name: '%s'}) " +
                        "CALL gds.shortestPath.dijkstra.stream('myGraph', { " +
                            "sourceNode: start, " +
                            "targetNode: end, " +
                            "relationshipWeightProperty: 'travel_time' " +
                        "}) " +
                        "YIELD sourceNode, targetNode, totalCost, nodeIds, costs, path " +
                            "RETURN totalCost as total_time, " +
                            "size([nodeId IN nodeIds | gds.util.asNode(nodeId).name]) as length, " +
                            "[nodeId IN nodeIds | gds.util.asNode(nodeId).name] AS nodeNames, " +
                            "costs as times, " +
                            "[nodeId IN nodeIds | gds.util.asNode(nodeId).has_traffic] as trafficBools, " +
                            "nodes(path) as path";
        query = String.format(query, start, dest);
        System.out.println(query);
        return this.session.run(query);
    }
}