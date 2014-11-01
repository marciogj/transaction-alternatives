package udesc.bda.stock.persistance;

import org.jongo.MongoCollection;

import udesc.bda.MongoDBStatic;
import udesc.bda.stock.queue.StockRequest;


public class StockRequestDB {

	private final MongoCollection db;


	public StockRequestDB() {
		db = MongoDBStatic.getCollection("stockRequest");
	}
	
	public StockRequest find(String id) {
		StockRequest request = db.findOne("{_id: '" + id + "'}").as(StockRequest.class);
		return request;
		
	}
	
	public boolean save(StockRequest request) {
		boolean result = true;
		try {
			db.insert(request);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void deleteAll() {
		db.drop();
	}

	public void update(StockRequest request) {
		db.update("{_id: '" + request.getId() + "'}").with(request);
	}

}

