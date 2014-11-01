import java.util.List;
import java.util.concurrent.BlockingQueue;

import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.queue.StockRequest;


public class StockRequestSimulator implements Runnable {
	private BlockingQueue<StockRequest> stockQueue;
	private List<StockItem> registeredItems;
	
	public StockRequestSimulator(BlockingQueue<StockRequest> sq, List<StockItem> items) {
		stockQueue = sq;
		registeredItems = items;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				System.out.println("Enqueuing withdraws...");
				for (StockItem stockItem : registeredItems) {
					stockItem.setQuantity(10);
				}
				stockQueue.add(StockRequest.withdrawRequest(registeredItems));
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
		
	}

}
