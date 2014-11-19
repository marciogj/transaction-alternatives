package udesc.simulator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import udesc.bda.CommandEvent;
import udesc.bda.order.model.Customer;
import udesc.bda.order.persistance.OrderDB;
import udesc.bda.order.persistance.OrderRequestDB;
import udesc.bda.order.queue.OrderProcessor;
import udesc.bda.order.queue.OrderRequest;
import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.persistance.StockDB;
import udesc.bda.stock.persistance.StockRequestDB;
import udesc.bda.stock.queue.StockProcessor;
import udesc.bda.stock.queue.StockRequest;


public class MainMeasureNoSQL {
	private static BlockingQueue<StockRequest> stockQueue = new LinkedBlockingQueue<StockRequest>();
	private static BlockingQueue<OrderRequest> orderQueue = new LinkedBlockingQueue<OrderRequest>();
	private static BlockingQueue<CommandEvent> coordQueue = new LinkedBlockingQueue<CommandEvent>();;
	
	private static List<StockItem> registeredItems = new ArrayList<StockItem>();

	private static final int threads = 80;
	private static final int orders = 375;
	private static final int stock_quantity = 10000;
	private static BlockingQueue<Integer> counter = new ArrayBlockingQueue<Integer>(threads);
	
	public static void main(String[] args) {
		cleanUp();
		prepareStock(stock_quantity);
		long start = System.nanoTime();
		startStockProcessor();
		startOrderProcessor();
		for (int i=0; i < threads; i++) {
			new Thread(new CustomerRunnable(new Customer("Customer#"+i),stockQueue, orderQueue, registeredItems, counter, orders)).start();
		}
		
		startCoordinatorSimulator();
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
	
	private static void startCoordinatorSimulator() {
		new Thread(new CoordinatorSimulator(coordQueue, orderQueue)).start();
	}

	private static void startOrderProcessor() {
		new Thread(new OrderProcessor(orderQueue)).start();
	}

	private static void startStockProcessor() {
	  new Thread(new StockProcessor(stockQueue, coordQueue)).start();
	}
	
	private static void prepareStock(int quantity) {
		StockDB db = new StockDB();
		registeredItems.add(new StockItem("Complexity: A Guided Tour", quantity));
		registeredItems.add(new StockItem("Diversity and Complexity", quantity));
		registeredItems.add(new StockItem("Introducing Fractals: A Graphic Guide", quantity));
		registeredItems.add(new StockItem("Chaos: Making a New Science", quantity));
		registeredItems.add(new StockItem("Refactoring: Improving the Design of Existing Code", quantity));
		registeredItems.add(new StockItem("NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence", quantity));
		
		for (StockItem stockItem : registeredItems) {
			db.save(stockItem);
		}
	}
	
	private static void cleanUp() {
		StockDB stockDB = new StockDB();
		OrderDB orderDB = new OrderDB();
		stockDB.deleteAll();
		orderDB.deleteAll();
		
		StockRequestDB srDB = new StockRequestDB();
		OrderRequestDB orDB = new OrderRequestDB();
		srDB.deleteAll();
		orDB.deleteAll();
	}

}
