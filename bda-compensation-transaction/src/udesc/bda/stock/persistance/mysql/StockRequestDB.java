package udesc.bda.stock.persistance.mysql;

import java.sql.Connection;

import udesc.bda.persistance.DBEntity;
import udesc.bda.persistance.Database;
import udesc.bda.persistance.MySQL;
import udesc.bda.stock.queue.StockRequest;


public class StockRequestDB  implements Database {
	protected Connection conn;
	StockRequestDAO dao;
	
	public StockRequestDB() {
		MySQL db = MySQL.getInstance();
		conn = db.waitAndGetConnection();
		dao = new StockRequestDAO();
	}
	
	public boolean save(DBEntity o) {
		return dao.save((StockRequest)o, conn);
	}

	public void deleteAll() {
		dao.deleteAll(conn);
	}

	public boolean update(DBEntity request) {
		dao.update((StockRequest)request, conn);
		return true;
	}

}

