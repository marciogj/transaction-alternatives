package udesc.bda.stock.queue;

import java.util.List;
import java.util.UUID;

import org.jongo.marshall.jackson.oid.Id;

import udesc.bda.CommandEvent;
import udesc.bda.persistance.DBEntity;
import udesc.bda.stock.model.StockItem;

public class StockRequest implements CommandEvent, DBEntity {
	@Id String _id;
	private String orderRequestId;
	private List<StockItem> itens;
	private StockAction action;	
	private StockStatus status;
	
	public static StockRequest withdrawRequest(List<StockItem> itens) {
		return new StockRequest(itens, StockAction.WITHDRAW);
	}
	
	public StockRequest(){}
	
	public StockRequest(List<StockItem> item, StockAction action) {
		_id = UUID.randomUUID().toString();
		this.itens = item;
		this.action = action;
	}
	
	public  List<StockItem> getItems() {
		return itens;
	}
	
	public StockAction getAction() {
		return action;
	}
	
	public void setAction(StockAction action) {
		this.action = action;
	}

	public String getId() {
		return _id;
	}

	public StockStatus getStatus() {
		return status;
	}

	public void setStatus(StockStatus status) {
		this.status = status;
	}

	public String getOrderRequestId() {
		return orderRequestId;
	}

	public void setOrderRequestId(String orderRequestId) {
		this.orderRequestId = orderRequestId;
	}

}


