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
		System.out.println("StockProcessor - " + request.getAction() + " - StockRequest[" + request.getId()+ "] - RequestOrder[" + request.getOrderRequestId()+"]");
		if (request.getAction() == StockAction.WITHDRAW) {
			StockResult result;
			List<StockItem> items = request.getItems();
			List<StockItem> compensationList = new ArrayList<StockItem>();
			for (StockItem item : items) {
				result = stockDB.withdraw(item);
				if (result == StockResult.INSUFICIENT) {
					compensateWithdraws(compensationList, request);
					return StockResult.ERROR;
				}
				//All items will be added to compensation in case a further withdraw fails
				compensationList.add(item); 
			}
			
			request.setStatus(StockStatus.COMPLETED);
			stockRequestDB.update(request);
			//Notify coordinator that everything was fine on stock
			coordinatorQueue.add(request);
		} else if (request.getAction() == StockAction.COMPENSATE) {
			stockDB.compensate(request.getItems());
			request.setStatus(StockStatus.COMPENSATED);
			stockRequestDB.update(request);
			coordinatorQueue.add(request);
		}
		
		return StockResult.SUCCESS;
	}
	
	private void compensateWithdraws(List<StockItem> compensationList, StockRequest oldRequest)  {
		StockRequest request = new StockRequest(compensationList, StockAction.COMPENSATE);
		request.setOrderRequestId(oldRequest.getOrderRequestId());
		stockQueue.add(request);
	}

}
