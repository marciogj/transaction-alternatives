package udesc.bda.order.persistance;

import org.jongo.MongoCollection;

import udesc.bda.MongoDBStatic;
import udesc.bda.order.queue.OrderRequest;


public class OrderRequestDB {

	private final MongoCollection db;


	public OrderRequestDB() {
		db = MongoDBStatic.getCollection("ordersRequest");
	}
	
	public boolean save(OrderRequest request) {
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

	public void update(OrderRequest request) {
		db.update("{_id: '"+request.getId()+"'}").with(request);
	}


}
