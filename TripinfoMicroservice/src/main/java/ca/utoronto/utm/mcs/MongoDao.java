package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.Filters;
import io.github.cdimascio.dotenv.Dotenv;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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

	public int getTripDrivetime(String id) {
		Bson filt = Filters.eq("_id", new ObjectId(id));
		Document cursor = collection.find(filt).first();
		try {
			JSONObject json = new JSONObject(cursor.toJson());
			String driver_uid = json.getString("driver");
			String passenger_uid = json.getString("passenger");

			HttpResponse<String> res = sendHttpRequest(new URI("http://locationmicroservice:8000/location/navigation/"
					+ driver_uid + "?passengerUid=" + passenger_uid), "GET", new JSONObject());

			JSONObject json_res = new JSONObject(res.body());
			System.out.println(json_res.toString());

			JSONObject data = json_res.getJSONObject("data");
			System.out.println(data.toString());

			return data.getInt("total_time");
		} catch (Exception e) {
			return -1;
		}
	}

	public String[] postTripRequest(String uid, int radius) {
		JSONObject body = new JSONObject();
		ArrayList<String> list = new ArrayList<String>();
		try {
			body.put("uid", uid);
			body.put("radius", radius);
			System.out.println("Sending to microservice...");
			HttpResponse<String> res = sendHttpRequest(
					new URI("http://locationmicroservice:8000/location/nearbyDriver/" + uid + "?radius=" + radius),
					"GET", body);

			JSONObject json_res = new JSONObject(res.body());
			System.out.println(json_res.toString());

			JSONObject data = json_res.getJSONObject("data");
			System.out.println(data.toString());

			Iterator<String> keys = data.keys();

			while (keys.hasNext()) {
				String curr = keys.next();
				list.add(curr);
				System.out.println(curr);
			}
		} catch (Exception e) {
			String[] empty = {};
			return empty;
		}

		String[] string_list = new String[list.size()];
		int i = 0;
		for (String item : list) {
			string_list[i] = item;
			i++;
		}
		return string_list;
	}

	public JSONObject getDriverTrips(String uid) throws JSONException {
		System.out.println("getting driver trips");
		Bson filt = Filters.eq("driver", uid);
		MongoCursor<Document> cursor = collection.find(filt).iterator();

		ArrayList<Document> list = new ArrayList<Document>();
		JSONObject obj = new JSONObject();

		if (!cursor.hasNext()) {
			obj.put("empty", "");
			cursor.close();
			return obj;
		}
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		System.out.println(list);
		cursor.close();
		obj.put("trips", list);

		return obj;
	}

	public JSONObject getPassengerTrips(String uid) throws JSONException {

		System.out.println("getting passenger trips");
		Bson filt = Filters.eq("passenger", uid);
		MongoCursor<Document> cursor = collection.find(filt).iterator();

		ArrayList<Document> list = new ArrayList<Document>();
		JSONObject obj = new JSONObject();

		if (!cursor.hasNext()) {
			obj.put("empty", "");
			cursor.close();
			return obj;
		}
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		obj.put("trips", list);

		return obj;
	}

	public String postConfirmTrip(String driver, long startTime, String passenger) {
		Document doc = new Document();

		String x = "";
		try {
			ObjectId id = new ObjectId();
			doc.put("_id", id);
			doc.put("driver", driver);
			doc.put("startTime", startTime);
			doc.put("passenger", passenger);
			collection.insertOne(doc);
			x = id.toString();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("confirm trip error");
			x = "";
		}

		return x;

	}

	public long patchTrip(String id, int distance, long endTime, long timeElapsed, double totalCost) {
		System.out.println("updating");

		try {
			Bson filt = Filters.eq("_id", new ObjectId(id));

			Bson updates = Updates.combine(
					Updates.set("distance", distance),
					Updates.set("endTime", endTime),
					Updates.set("timeElapsed", timeElapsed),
					Updates.set("totalCost", totalCost));

			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult res = collection.updateOne(filt, updates, options);
			System.out.println("updated");
			return res.getModifiedCount();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" issue with object id");
			return -1;
		}

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
