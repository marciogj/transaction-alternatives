package udesc.simulator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import udesc.bda.Customer;
import udesc.bda.ecommerce.Item;
import udesc.bda.ecommerce.Order;
import udesc.bda.sql.MySQL;
import udesc.bda.sql.Store;
import udesc.bda.sql.dao.ItemDAO;
import udesc.bda.sql.dao.OrderDAO;
import udesc.bda.sql.dao.StockDAO;


public class MainMeasureRDMS {
	private static final int threads = 80;
	private static final int orders = 200;
	private static final int stock_quantity = 1000;
	private static BlockingQueue<Integer> counter = new ArrayBlockingQueue<Integer>(threads);

	public static void main(String[] args) {
		cleanUp();
		prepareStock(stock_quantity);
		Store s = new Store();
		for (int i=0; i < threads; i++) {
			new Thread(new CustomerRunnable(new Customer("Customer#"+i), s, counter, orders)).start();
		}

		long start = System.nanoTime();
		int count = 0;
		while(count < threads) {
			try {
				counter.take();
				System.out.println("--> Thread " + count + " is done :)");
				count++;
			} catch (InterruptedException e) {
				Thread.interrupted();
			}

		}
		long totalTime = System.nanoTime() - start;
		System.err.println("########### Total Time " + totalTime + " ##############");
		System.exit(0);
	}

	public static void prepareStock(int quantity) {
		MySQL db = new MySQL();
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
			StockDAO dao = new StockDAO();
			dao.deleteAll(conn);

			Customer c = new Customer("test");
			Order o = CustomerRunnable.request(c);
			List<Item> items = o.getItems();
			for (Item item : items) {
				item.setQuantity(quantity);
				dao.save(item, conn);
			}

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void cleanUp(){
		MySQL db = new MySQL();
		Optional<Connection> maybeAConn = null;
		Connection conn = null;
		try {
			maybeAConn = db.getConnection();
			conn = maybeAConn.get();
			StockDAO stockDAO = new StockDAO();
			stockDAO.deleteAll(conn);
			
			OrderDAO orderDAO = new OrderDAO();
			orderDAO.deleteAll(conn);
			
			ItemDAO itemDAO = new ItemDAO();
			itemDAO.deleteAll(conn);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
