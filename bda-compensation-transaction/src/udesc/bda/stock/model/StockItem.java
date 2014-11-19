package udesc.bda.stock.model;

import java.util.UUID;

import org.jongo.marshall.jackson.oid.Id;


public class StockItem {
	@Id	private String _id;
	private String productName;
	private int quantity;
	
	public StockItem() {}

	public StockItem(StockItem aStockItem) {
		_id = aStockItem.getId();
		productName = aStockItem.getProductName();
		quantity = aStockItem.getQuantity();
	}
	
	public StockItem(String name, int quantity) {
		this(name);
		this.quantity = quantity;
	}


	public StockItem(String name) {
		_id = UUID.randomUUID().toString();
		productName = name;
	}
	
	
	public String getId() {
		return _id;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}
