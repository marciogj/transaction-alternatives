package udesc.bda.order.persistance.mongo;

import org.jongo.MongoCollection;

import udesc.bda.MongoDBStatic;
import udesc.bda.order.queue.OrderRequest;
import udesc.bda.persistance.DBEntity;
import udesc.bda.persistance.Database;


public class OrderRequestDB implements Database {

	private final MongoCollection db;


	public OrderRequestDB() {
		db = MongoDBStatic.getCollection("ordersRequest");
	}
	
	public boolean save(DBEntity request) {
		boolean result = true;
		try {
			db.insert((OrderRequest)request);
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
		OrderRequest request = (OrderRequest) o;
		db.update("{_id: '"+request.getId()+"'}").with(request);
		return true;
	}


}
