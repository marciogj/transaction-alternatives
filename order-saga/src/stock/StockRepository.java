package stock;

public interface StockRepository {

	StockItemEntity loadItem(String itemHash);

	void save(StockItemEntity item);

	void replace(StockItemEntity item, StockItemEntity newItem);

}
