package udesc.simulator;
import java.util.concurrent.BlockingQueue;

import udesc.bda.CommandEvent;
import udesc.bda.order.queue.OrderRequest;
import udesc.bda.order.queue.OrderStatus;
import udesc.bda.stock.queue.StockRequest;
import udesc.bda.stock.queue.StockStatus;


public class CoordinatorSimulator implements Runnable {
	private BlockingQueue<CommandEvent> eventQueue;
	private BlockingQueue<OrderRequest> orderQueue;
	
	public CoordinatorSimulator(BlockingQueue<CommandEvent> eq, BlockingQueue<OrderRequest> oq) {
		eventQueue = eq;
		orderQueue = oq;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				CommandEvent event = eventQueue.take();
				System.out.println("Coordinator - Got a event type " + event.getClass().getCanonicalName());
				
				if (event instanceof StockRequest) {
					StockRequest sr = (StockRequest) event;
					String orderRequestId = sr.getOrderRequestId();
					OrderRequest orderRequest = new OrderRequest();
					orderRequest.setId(orderRequestId);
					System.out.println("    Coordinator - StockRequest type "+sr.getAction().toString()+" - ID[" + sr.getId()+"]");
					if (sr.getStatus() == StockStatus.COMPLETED) {
						
						orderRequest.setStatus(OrderStatus.STOCK_READY);
						orderQueue.add(orderRequest);
					}
					if (sr.getStatus() == StockStatus.COMPENSATED) {
						orderRequest.setStatus(OrderStatus.COMPENSATED);
						orderQueue.add(orderRequest);
					}
				}
				

				
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
		
	}

}
