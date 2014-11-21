package udesc.bda.order.persistance.mongo;

import org.jongo.MongoCollection;

import udesc.bda.MongoDBStatic;
import udesc.bda.order.model.Order;
import udesc.bda.persistance.DBEntity;
import udesc.bda.persistance.Database;


public class OrderDB implements Database{

	private final MongoCollection orderDB;


	public OrderDB() {
		orderDB = MongoDBStatic.getCollection("orders");
	}
	
	@Override
	public boolean save(DBEntity order) {
		boolean result = true;
		try {
			orderDB.insert((Order)order);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void deleteAll() {
		orderDB.drop();
	}

	@Override
	public boolean update(DBEntity o) {
		Order order = (Order) o;
		orderDB.update("{_id: '"+order.getId()+"'}").with(order);
		return true;
	}


}
