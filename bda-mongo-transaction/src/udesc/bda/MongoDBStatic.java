package udesc.bda;

import java.net.UnknownHostException;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBStatic {
	public static final String DB_HOST = "127.0.0.1";
	public static final int DB_PORT = 27017;
	public static final String DB_NAME = "bda";
//	public static final String DB_USER = "";
//	public static final String DB_PWD = "";
	
	private static DB db = null;
	
	public static final DB getMongoDB() {
		if (db != null) {
			return db;
		}
		MongoClient mongoClient;
		try {	
			mongoClient = new MongoClient( DB_HOST , DB_PORT );
			db = mongoClient.getDB(DB_NAME);
//			boolean auth = db.authenticate("", "".toCharArray());
//			if (!auth) {
//				throw new RuntimeException("User/Password does not match for database " + DB_NAME);
//			}
		} catch (UnknownHostException e) {
			throw new RuntimeException("DB Host is not possible to reach: " + e.getMessage());
		}
		
		return db;
	}
	
//	public static DBCollection  getCollection(String name) {
//		return getMongoDB().getCollection(name);
//	}

	public static MongoCollection getCollection(String name) {
		Jongo jongo = new Jongo(getMongoDB());
		return jongo.getCollection(name);
	}
	
}
