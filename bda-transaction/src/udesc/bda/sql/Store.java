package udesc.bda.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import udesc.bda.ecommerce.Order;
import udesc.bda.sql.dao.ItemDAO;
import udesc.bda.sql.dao.OrderDAO;
import udesc.bda.sql.dao.StockDAO;

public class Store {
	MySQL db;
	
	public Store() {
		db = new MySQL();
	}
	
	public void checkout(Order order) {
		Optional<Connection> maybeAConn = null;
		Connection conn = null;
		try {
			maybeAConn = db.getConnection();
			while (!maybeAConn.isPresent()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
				maybeAConn = db.getConnection();
			}
			conn = maybeAConn.get();
			conn.setAutoCommit(false);
			OrderDAO orderDao = new OrderDAO();
			ItemDAO itemDao = new ItemDAO();
			StockDAO stockDao = new StockDAO();
			
			orderDao.save(order, conn);
			itemDao.save(order.getItems(), order.getId(), conn);
			stockDao.update(order.getItems(), conn);
			
			//Stock
			//FiscalReceipt
			conn.commit();
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.release(conn);
		}
		
		
		
		// - colocar em paralelo uma thread consulta stock para forçar mais locks?
		
		//begin transaction
		//tranform cart into a formal order
		//for each stock item reduce from stock
		//generate invoice (payment)
		//geneate governent fiscal register
		//
		
	}

	
	
	
}
