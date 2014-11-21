package udesc.bda.stock.persistance;

import java.util.List;

import udesc.bda.persistance.Database;
import udesc.bda.stock.model.StockItem;
import udesc.bda.stock.queue.StockResult;

public abstract class StockDatabase implements Database {
	
	public abstract StockResult withdraw(StockItem item);
	
	public abstract void compensate(List<StockItem> itens);

}
