import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import udesc.bda.CommandEvent;
import udesc.bda.order.persistance.OrderDB;
import udesc.bda.order.persistance.OrderRequestDB;
import udesc.bda.order.queue.OrderProcessor;
import udesc.bda.order.queue.OrderRequest;
import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.persistance.StockDB;
import udesc.bda.stock.persistance.StockRequestDB;
import udesc.bda.stock.queue.StockProcessor;
import udesc.bda.stock.queue.StockRequest;
import udesc.simulator.CoordinatorSimulator;
import udesc.simulator.OrderRequestSimulator;


public class Main {
	private static BlockingQueue<StockRequest> stockQueue = new LinkedBlockingQueue<StockRequest>();
	private static BlockingQueue<OrderRequest> orderQueue = new LinkedBlockingQueue<OrderRequest>();
	private static BlockingQueue<CommandEvent> coordQueue = new LinkedBlockingQueue<CommandEvent>();;
	
	private static List<StockItem> registeredItems = new ArrayList<StockItem>();

	public static void main(String[] args) {
		cleanUp();
		prepareStock();
		startStockProcessor();
		startOrderProcessor();
		startOrderSimulator();
		startCoordinatorSimulator();
		//startStockRequestSimulator();

	}
	
	private static void startCoordinatorSimulator() {
		new Thread(new CoordinatorSimulator(coordQueue, orderQueue)).start();
	}

	private static void startOrderSimulator() {
		new Thread(new OrderRequestSimulator(stockQueue, orderQueue, registeredItems)).start();
	}

	private static void startOrderProcessor() {
		new Thread(new OrderProcessor(orderQueue)).start();
	}

//	private static void startStockRequestSimulator() {
//		new Thread(new StockRequestSimulator(stockQueue, registeredItems)).start();
//		
//	}

	private static void startStockProcessor() {
	  new Thread(new StockProcessor(stockQueue, coordQueue)).start();
	}
	
	private static void prepareStock() {
		StockDB db = new StockDB();
		int quantity = 50;
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
