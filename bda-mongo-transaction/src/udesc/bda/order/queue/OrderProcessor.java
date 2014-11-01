package udesc.bda.order.queue;

import java.util.concurrent.BlockingQueue;

import udesc.bda.order.persistance.OrderDB;
import udesc.bda.order.persistance.OrderRequestDB;

public class OrderProcessor implements Runnable {
	private BlockingQueue<OrderRequest> orderQueue;
	private OrderDB orderDB;
	private OrderRequestDB orderRequestDB;
	
	public OrderProcessor(BlockingQueue<OrderRequest> queue) {
		orderQueue = queue;
		orderDB = new OrderDB();
		orderRequestDB = new OrderRequestDB();
	}
	
	public void run() {
		while(true) {
			System.out.println("Checking for order request commands...");
			try {
				OrderRequest orderRequest = orderQueue.take();
				process(orderRequest);
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
	}
	
	private void process(OrderRequest request) {
		System.out.println("Processing a request type of " + (request.getStatus() == OrderStatus.REQUESTED ? "REQUEST" : "COMPENSATION"));
		if (request.getStatus() == OrderStatus.REQUESTED) {
			orderRequestDB.save(request);
			orderDB.save(request.getOrder());
		} else if (request.getStatus() == OrderStatus.COMPENSATE) {
			//request.getOrder().setStatus(Status.CANCELED);
			orderRequestDB.update(request);
		} else if(request.getStatus() == OrderStatus.STOCK_READY) {
			orderRequestDB.update(request);
		}
	}

}
