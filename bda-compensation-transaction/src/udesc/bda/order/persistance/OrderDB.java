package udesc.bda.order.persistance;

import org.jongo.MongoCollection;

import udesc.bda.MongoDBStatic;
import udesc.bda.order.model.Order;


public class OrderDB {

	private final MongoCollection orderDB;


	public OrderDB() {
		orderDB = MongoDBStatic.getCollection("orders");
	}
	
	public boolean save(Order order) {
		boolean result = true;
		try {
			orderDB.insert(order);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void deleteAll() {
		orderDB.drop();
	}

	public void update(Order order) {
		orderDB.update("{_id: '"+order.getId()+"'}").with(order);
	}


}
