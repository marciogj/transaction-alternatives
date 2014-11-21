package udesc.bda.stock.persistance.mongo;

import org.jongo.MongoCollection;

import udesc.bda.MongoDBStatic;
import udesc.bda.persistance.DBEntity;
import udesc.bda.persistance.Database;
import udesc.bda.stock.queue.StockRequest;


public class StockRequestDB implements Database {

	private final MongoCollection db;


	public StockRequestDB() {
		db = MongoDBStatic.getCollection("stockRequest");
	}
	
	public StockRequest find(String id) {
		StockRequest request = db.findOne("{_id: '" + id + "'}").as(StockRequest.class);
		return request;
		
	}
	
	public boolean save(DBEntity request) {
		boolean result = true;
		try {
			db.insert((StockRequest)request);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void deleteAll() {
		db.drop();
	}

	public boolean update(DBEntity o) {
		StockRequest request = (StockRequest) o;
		return db.update("{_id: '" + request.getId() + "'}").with(request) != null;
	}

}

