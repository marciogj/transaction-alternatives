package udesc.bda.stock.persistance.mysql;

import java.sql.Connection;
import java.util.List;

import udesc.bda.persistance.DBEntity;
import udesc.bda.persistance.MySQL;
import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.persistance.StockDatabase;
import udesc.bda.stock.queue.StockResult;


public class StockDB extends StockDatabase {
	protected Connection conn;
	StockDAO dao;
	
	public StockDB() {
		MySQL db = MySQL.getInstance();
		conn = db.waitAndGetConnection();
		dao = new StockDAO();
	}	
	
	
	public StockResult withdraw(StockItem item) {
		StockResult result = StockResult.INSUFICIENT;
		int stockQuantity = dao.getStockQuantity(item.getId(), conn);
	
		System.out.println("  StockDB - Withdrwaing " + item.getQuantity() + " from stock ("+stockQuantity+") - " + item.getProductName());
		if ( stockQuantity >= item.getQuantity()) {
			result = StockResult.SUCCESS;
			int newStockvalue =stockQuantity - item.getQuantity(); 
			item.setQuantity(newStockvalue);
			dao.update(item, conn);
		} 
		return result;
		
	}
	
	public int getStockQuantity(String id) {
		int total = 0;
		try {
			total = dao.getStockQuantity(id, conn);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return total;
	}
	
	public boolean save(DBEntity item) {
		boolean result = true;
		try {
			dao.save((StockItem)item, conn);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void deleteAll() {
		dao.deleteAll(conn);
	}

	public synchronized void compensate(List<StockItem> itens) {
		for (StockItem item : itens) {
			int dbTotal = dao.getStockQuantity(item.getId(), conn);
			item.setQuantity(dbTotal + item.getQuantity());
			dao.update(item, conn);
		}
		
	}

	@Override
	public boolean update(DBEntity o) {
		throw new RuntimeException("Not implemented");
	}

}


