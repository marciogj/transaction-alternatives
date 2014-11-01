package udesc.bda.order.queue;

import org.jongo.marshall.jackson.oid.Id;

import udesc.bda.CommandEvent;
import udesc.bda.order.model.Order;
import udesc.bda.stock.queue.StockRequest;

public class OrderRequest implements CommandEvent {
	@Id	private String _id;
	private StockRequest stockRequest;
	private Order order;
	private OrderStatus status;	
	
	public OrderRequest() {}
	
	public OrderRequest(Order o, OrderStatus action) {
		super();
		this.order = o;
		this.status = action;
	}
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public OrderStatus getStatus() {
		return status;
	}
	
	public void setStatus(OrderStatus action) {
		this.status = action;
	}

	public StockRequest getStockRequest() {
		return stockRequest;
	}

	public void setStockRequest(StockRequest stockRequest) {
		this.stockRequest = stockRequest;
	}

	public String getId() {
		return _id;
	}

	public void setId(String orderRequestId) {
		_id = orderRequestId;
	} 
	
}


