package udesc.bda.stock.persistance;

import java.util.List;

import org.jongo.MongoCollection;

import udesc.bda.MongoDBStatic;
import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.queue.StockResult;


public class StockDB {

	private final MongoCollection stock;


	public StockDB() {
		stock = MongoDBStatic.getCollection("stock");
	}
	
	public StockResult withdraw(StockItem item) {
		StockResult result = StockResult.INSUFICIENT;
		StockItem dbItem = stock.findOne("{_id: '" + item.getId() + "'}").as(StockItem.class);
		if (dbItem == null) {
			System.err.println("Could not find item with id " + item.getId());
			return StockResult.ERROR;
		}
		int stockQuantity = dbItem.getQuantity();
		System.out.println("  StockDB - Withdrwaing " + item.getQuantity() + " from stock ("+stockQuantity+") - " + item.getProductName());
		if ( stockQuantity >= item.getQuantity()) {
			result = StockResult.SUCCESS;
			dbItem.setQuantity(stockQuantity - item.getQuantity());
			stock.update("{_id: '"+item.getId()+"'}").with(dbItem);
		} 
		return result;
		
	}
	
	public int getStockQuantity(String product_id) {
		int total = 0;
		try {
			StockItem item = stock.findOne("{_id: '" + product_id + "'}").as(StockItem.class);
			total = item.getQuantity();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return total;
	}
	
	public boolean save(StockItem item) {
		boolean result = true;
		try {
			stock.insert(item);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void deleteAll() {
		stock.drop();
	}

	public synchronized void compensate(List<StockItem> itens) {
		for (StockItem item : itens) {
			StockItem dbItem = stock.findOne("{_id: '" + item.getId() + "'}").as(StockItem.class);
			dbItem.setQuantity(dbItem.getQuantity() + item.getQuantity());
			stock.update("{_id: '"+item.getId()+"'}").with(dbItem);
		}
		
	}

}


