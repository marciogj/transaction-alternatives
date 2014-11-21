package udesc.bda.order.persistance.mysql;

import java.sql.Connection;

import udesc.bda.order.queue.OrderRequest;
import udesc.bda.persistance.DBEntity;
import udesc.bda.persistance.Database;
import udesc.bda.persistance.MySQL;


public class OrderRequestDB  implements Database {
	protected Connection conn;
	OrderRequestDAO dao;
	
	public OrderRequestDB() {
		MySQL db = MySQL.getInstance();
		conn = db.waitAndGetConnection();
		dao = new OrderRequestDAO();
	}
	
	public boolean save(DBEntity o) {
		return dao.save((OrderRequest) o, conn);
	}

	public void deleteAll() {
		dao.deleteAll(conn);
	}

	public boolean update(DBEntity o) {
		return dao.update((OrderRequest) o, conn);
	}


}
