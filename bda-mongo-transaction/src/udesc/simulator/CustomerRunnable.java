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

public class CustomerRunnable implements Runnable {
	private BlockingQueue<StockRequest> stockQueue;
	private BlockingQueue<OrderRequest> orderQueue;
	private List<StockItem> registeredItems;
	private long totalOrders;   
	private Customer customer;
	
	private BlockingQueue<Integer> counterQueue;
	
	CustomerRunnable(Customer c,BlockingQueue<StockRequest> sqm, BlockingQueue<OrderRequest> oqm, List<StockItem> items, BlockingQueue<Integer> cq, int orders) {
		orderQueue = oqm;
		stockQueue = sqm;
		registeredItems = items;
		counterQueue = cq;
		customer = c;
		totalOrders = orders;
	}
	
	public void run() {
		
		while (totalOrders-- > 0) {
			try {
				long start = System.nanoTime();
				System.out.println(start + " - " + customer.getName() + " starting order #" + totalOrders);
				requestOrder();
				long end = System.nanoTime();
				System.out.println(end + " - " + customer.getName() + " order #" + totalOrders + " took " + (end-start) + " nanoseconds");
				Thread.sleep(500); //long) Math.random()*500
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		counterQueue.add(new Integer(1));
	}
	
	public void requestOrder() {
		List<StockItem> itemsToWidraw = new ArrayList<StockItem>();
		int quantity = 3;
		for (StockItem stockItem : registeredItems) {
			StockItem requestItem = new StockItem(stockItem);
			requestItem.setQuantity(quantity++);
			itemsToWidraw.add(requestItem);
		}
		Order order = request(customer, itemsToWidraw); 
		
		StockRequest stockRequest = new StockRequest(itemsToWidraw, StockAction.WITHDRAW);
		OrderRequest orderRequest = new OrderRequest(order, OrderStatus.REQUESTED);
		orderRequest.setStockRequest(stockRequest);
		stockRequest.setOrderRequestId(orderRequest.getId());
		
		//a failure here would fuck it up...
		orderQueue.add(orderRequest);
		stockQueue.add(stockRequest);
	}


	public Order request(Customer c, List<StockItem> items) {
		Order order = new Order();
		order.setCustomer(c);
		order.setPayment(new Payment("1234 5678 7890 4457", c.getName()));
		int price = 10000;
		for (StockItem stockItem : registeredItems) {
			order.add(itemFromStock(stockItem, price, stockItem.getQuantity()));
		}
		
		return order;
	}
	
	private Item itemFromStock(StockItem stockItem, int quantity, int price) {
		Product product = new Product(stockItem.getId(), stockItem.getProductName());
		return new Item(product, price, quantity);
	}

}
