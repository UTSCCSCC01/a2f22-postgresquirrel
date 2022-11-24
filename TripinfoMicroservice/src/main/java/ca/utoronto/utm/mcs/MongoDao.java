package ca.utoronto.utm.mcs;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.github.cdimascio.dotenv.Dotenv;

import org.bson.Document;

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

}
