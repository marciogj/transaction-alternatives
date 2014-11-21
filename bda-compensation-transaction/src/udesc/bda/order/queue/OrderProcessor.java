package udesc.bda.order.queue;

import java.util.concurrent.BlockingQueue;

import udesc.bda.order.persistance.mongo.OrderDB;
import udesc.bda.order.persistance.mongo.OrderRequestDB;
import udesc.bda.persistance.DBFactory;
import udesc.bda.persistance.Database;

public class OrderProcessor implements Runnable {
	private BlockingQueue<OrderRequest> orderQueue;
	private Database orderDB;
	private Database orderRequestDB;
	
	public OrderProcessor(BlockingQueue<OrderRequest> queue) {
		orderQueue = queue;
		orderDB = new OrderDB();
		orderRequestDB = new OrderRequestDB();
		
		orderDB = DBFactory.getOrderDB();
		orderRequestDB = DBFactory.getOrderRequestDB();
	}
	
	public void run() {
		while(true) {
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
		System.out.println("OrderProcessor - " + request.getStatus() + " - OrderRequestId["+request.getId()+"]");
		if (request.getStatus() == OrderStatus.REQUESTED) {
			orderRequestDB.save(request);
			orderDB.save(request.getOrder());
		} else if (request.getStatus() == OrderStatus.COMPENSATED) {
			//request.getOrder().setStatus(Status.CANCELED);
			orderRequestDB.update(request);
			//
		} else if(request.getStatus() == OrderStatus.STOCK_READY) {
			orderRequestDB.update(request);
		}
	}

}
