package udesc.bda.order.persistance.mysql;

import java.sql.Connection;

import udesc.bda.order.model.Order;
import udesc.bda.persistance.DBEntity;
import udesc.bda.persistance.Database;
import udesc.bda.persistance.MySQL;


public class OrderDB  implements Database {
	protected Connection conn;
	OrderDAO dao;
	
	public OrderDB() {
		MySQL db = MySQL.getInstance();
		conn = db.waitAndGetConnection();
		dao = new OrderDAO();
	}
	
	@Override
	public boolean save(DBEntity order) {
		boolean result = true;
		try {
			dao.save((Order) order, conn);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void deleteAll() {
		dao.deleteAll(conn);
	}

	@Override
	public boolean update(DBEntity o) {
		throw new RuntimeException("Not implemented!");
	}


}
