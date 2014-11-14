package mongodb;

import org.jongo.MongoCollection;

import stock.StockItemEntity;
import stock.StockRepository;

import com.mongodb.DuplicateKeyException;

public class StockMongoDBRepository implements StockRepository {

	private MongoCollection stockCollection;

	public StockMongoDBRepository(MongoCollection stockCollection) {
		this.stockCollection = stockCollection;
		stockCollection.ensureIndex("{hash: 1}", "{unique: true}");
	}

	@Override
	public StockItemEntity loadItem(String itemHash) {
		System.out.println("> Loading item '" + itemHash
				+ "' from StockRepository.");
		return stockCollection.findOne("{hash: #}", itemHash).as(
				StockItemEntity.class);
	}

	@Override
	public void save(StockItemEntity item) {
		try {
			stockCollection.save(item);
		} catch (DuplicateKeyException e) {
			System.out.println("> Duplicated StockItemEntity, won't write.");
			// ok
		}
	}

	@Override
	public void replace(StockItemEntity item, StockItemEntity newItem) {
		stockCollection
				.update("{hash: #, version: #}", item.hash, item.version).with(
						item);
	}

}
