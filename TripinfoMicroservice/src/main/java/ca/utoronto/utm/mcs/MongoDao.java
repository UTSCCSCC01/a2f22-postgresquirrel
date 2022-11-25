package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import io.github.cdimascio.dotenv.Dotenv;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

public class MongoDao {

	public MongoCollection<Document> collection;
	public MongoClient client;
	public MongoDatabase db;

	public MongoDao() {
		// TODO:
		// Connect to the mongodb database and create the database and collection.
		// Use Dotenv like in the DAOs of the other microservices.

		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		String url = "mongodb://root:123456@" + addr + ":27017";
		try {
			this.client = MongoClients.create(url);
			this.client.startSession();

			this.db = client.getDatabase("trips");
			System.out.println("Connected to the DB");
			// this.db.createCollection("trips");
			// System.out.println("created a new collection");
			collection = this.db.getCollection("trips");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// *** implement database operations here *** //
	public JSONObject getDriverTrips(String uid) throws JSONException {

		Bson filt = Filters.eq("driver", uid);
		MongoCursor<Document> cursor = collection.find(filt).iterator();

		ArrayList<Document> list = new ArrayList<Document>();
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		JSONObject obj = new JSONObject();
		obj.put("trips", list);

		return obj;
	}

	public boolean postConfirmTrip(String driver, String startTime, String passenger) {
		Document doc = new Document();

		boolean x = true;
		try {
			doc.put("driver", driver);
			doc.put("startTime", startTime);
			doc.put("passenger", passenger);
			collection.insertOne(doc);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("confirm trip error");
			x = false;
		}

		return x;

	}

	private HttpResponse<String> sendHttpRequest(URI uri, String method, JSONObject body)
			throws IOException, InterruptedException {
		HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.followRedirects(HttpClient.Redirect.NORMAL)
				.connectTimeout(Duration.ofSeconds(20))
				.build();

		// GET /location/nearbyDriver/:uid?radius=
		HttpRequest testRequest = HttpRequest.newBuilder(uri)
				.method(method, HttpRequest.BodyPublishers.ofString(body.toString())).build();

		return client.send(testRequest, HttpResponse.BodyHandlers.ofString());
	}
}
