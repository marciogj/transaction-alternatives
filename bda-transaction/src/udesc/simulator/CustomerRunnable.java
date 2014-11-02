package udesc.simulator;

import java.util.concurrent.BlockingQueue;

import udesc.bda.Customer;
import udesc.bda.Product;
import udesc.bda.ecommerce.Item;
import udesc.bda.ecommerce.Order;
import udesc.bda.ecommerce.Payment;
import udesc.bda.sql.Store;

public class CustomerRunnable implements Runnable {
	private long totalOrders;   
	private Customer customer;
	private Store store;
	private BlockingQueue<Integer> counter;
	
	CustomerRunnable(Customer c, Store s, BlockingQueue<Integer> q, int ordersToDo) {
		customer = c;
		store = s;
		counter = q;
		totalOrders = ordersToDo;
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
		counter.add(new Integer(1));
	}
	
	public void requestOrder() {
		Order order = request(customer); 
		store.checkout(order);
	}
	
	public static  Order request(Customer c) {
		Order order = new Order();
		order.setCustomer(c);
		int quantity = 3;
		int price = 10000;
		order.setPayment(new Payment("1234 5678 7890 4457", c.getName()));
		order.add(new Item(new Product("Complexity: A Guided Tour"), price, quantity++));
		order.add(new Item(new Product("Diversity and Complexity"), price, quantity++));
		order.add(new Item(new Product("Introducing Fractals: A Graphic Guide"), price, quantity++));
		order.add(new Item(new Product("Chaos: Making a New Science"), price, quantity++));
		order.add(new Item(new Product("Refactoring: Improving the Design of Existing Code"), price, quantity++));
		order.add(new Item(new Product("NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence "), price, quantity++));
		return order;
	}

}
