package udesc.bda.stock.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import udesc.bda.CommandEvent;
import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.persistance.StockDB;
import udesc.bda.stock.persistance.StockRequestDB;

//Single entry point to process stock database
public class StockProcessor implements Runnable {
	private BlockingQueue<StockRequest> stockQueue;
	private BlockingQueue<CommandEvent> coordinatorQueue;
	private StockDB stockDB;
	private StockRequestDB stockRequestDB;

	public StockProcessor(BlockingQueue<StockRequest> rq, BlockingQueue<CommandEvent> cq) {
		stockQueue = rq;
		coordinatorQueue = cq;
		stockDB = new StockDB();
		stockRequestDB = new StockRequestDB();
	}

	public void run() {
		while(true) {
			System.out.println("Checking for stock request commands...");
			try {
				StockRequest stockRequest = stockQueue.take();
				stockRequest.setStatus(StockStatus.REQUESTED);
				stockRequestDB.save(stockRequest);
				StockResult result;
				result = process(stockRequest);
				if (result != StockResult.SUCCESS) {
					//inform coordinator
					stockRequest.setStatus(StockStatus.FAILED);
					stockRequestDB.update(stockRequest);
					coordinatorQueue.add(stockRequest);
				}
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
	}

	private StockResult process(StockRequest request) {
		System.out.println("Processing a stock request type of " + (request.getAction() == StockAction.WITHDRAW ? "WITHDRAW" : "COMPENSATION"));
		if (request.getAction() == StockAction.WITHDRAW) {
			StockResult result;
			List<StockItem> items = request.getItems();
			List<StockItem> compensationList = new ArrayList<StockItem>();
			for (StockItem item : items) {
				result = stockDB.withdraw(item);
				if (result == StockResult.INSUFICIENT) {
					compensateWithdraws(compensationList);
					return StockResult.ERROR;
				}
				//All items will be added to compensation in case a further withdraw fails
				compensationList.add(item); 
			}
		} else if (request.getAction() == StockAction.COMPENSATE) {
			stockDB.compensate(request.getItems());
		}
		request.setStatus(StockStatus.COMPLETED);
		stockRequestDB.update(request);
		//Notify coordinator that everything was fine on stock
		coordinatorQueue.add(request);
		return StockResult.SUCCESS;
	}
	
	private void compensateWithdraws(List<StockItem> compensationList)  {
		stockQueue.add(new StockRequest(compensationList, StockAction.COMPENSATE));
	}

}
