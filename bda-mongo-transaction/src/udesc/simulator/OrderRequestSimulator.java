package udesc.simulator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import udesc.bda.order.model.Customer;
import udesc.bda.order.model.Item;
import udesc.bda.order.model.Order;
import udesc.bda.order.model.Payment;
import udesc.bda.order.model.Product;
import udesc.bda.order.queue.OrderRequest;
import udesc.bda.order.queue.OrderStatus;
import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.queue.StockAction;
import udesc.bda.stock.queue.StockRequest;


public class OrderRequestSimulator implements Runnable {
	private BlockingQueue<StockRequest> stockQueue;
	private BlockingQueue<OrderRequest> orderQueue;
	
	
	private List<StockItem> registeredItems;
	private long id = 1000;
	
	public OrderRequestSimulator(BlockingQueue<StockRequest> sqm, BlockingQueue<OrderRequest> oqm, List<StockItem> items) {
		orderQueue = oqm;
		stockQueue = sqm;
		registeredItems = items;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Order order = new Order();
				String customerName = "Customer " + id++;
				order.setCustomer(new Customer(customerName));
				order.setPayment(new Payment("1234 5678 9101 1121", customerName));

				System.out.println("Customer " + customerName + " starting a new order");
				List<StockItem> itemsToWidraw = new ArrayList<StockItem>();
				StockItem item = null;
				for (StockItem stockItem : registeredItems) {
					Product product = new Product(stockItem.getId(), stockItem.getProductName());
					int quantity = 10;
					int price = 2000;
					order.add(new Item(product, price, quantity));
					item = new StockItem(stockItem);
					item.setQuantity(quantity);
					itemsToWidraw.add(item);
				}
				StockRequest stockRequest = new StockRequest(itemsToWidraw, StockAction.WITHDRAW);
				OrderRequest orderRequest = new OrderRequest(order, OrderStatus.REQUESTED);
				orderRequest.setStockRequest(stockRequest);
				stockRequest.setOrderRequestId(orderRequest.getId());
				
				//a failure here would fuck it up...
				orderQueue.add(orderRequest);
				stockQueue.add(stockRequest);
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
		
	}

}
