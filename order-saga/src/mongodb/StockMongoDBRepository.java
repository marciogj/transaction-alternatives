package mongodb;

import org.jongo.MongoCollection;

import stock.ReservationRequest;
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
	public void save(StockItemEntity item) {
		try {
			stockCollection.save(item);
		} catch (DuplicateKeyException e) {
			// ok
		}
	}

	public void replace(StockItemEntity oldItem, StockItemEntity newItem) {
		int n = stockCollection
				.update("{hash: #, version: #}", oldItem.hash, oldItem.version)
				.with(newItem).getN();
		if (n < 1) {
			throw new RuntimeException("conflict");
		}
	}

	@Override
	public boolean reserveItem(ReservationRequest reservationRequest) {
		int n = stockCollection
				.update("{hash: #, inStockQuantity: {$gte:#}}",
						reservationRequest.itemHash,
						reservationRequest.quantity)
				.with("{$addToSet: {reservations: {hash: #, quantity: #}}, $inc: {inStockQuantity: #}}",
						reservationRequest.reservationHash,
						reservationRequest.quantity,
						-reservationRequest.quantity).getN();
		return n >= 1;
	}

	@Override
	public void returnStock(ReservationRequest reservationRequest) {
		stockCollection
				.update("{hash: #, reservations: {$in:[{hash: #, quantity: #}]}}",
						reservationRequest.itemHash,
						reservationRequest.reservationHash,
						reservationRequest.quantity)
				.with("{$pull: {reservations: {hash: #}}, $inc: {inStockQuantity: #}}",
						reservationRequest.reservationHash,
						reservationRequest.quantity);
	}

	@Override
	public void confirmStock(ReservationRequest reservationRequest) {
		stockCollection
				.update("{hash: #, reservations: {$in:[{hash: #, quantity: #}]}}",
						reservationRequest.itemHash,
						reservationRequest.reservationHash,
						reservationRequest.quantity).with(
						"{$pull: {reservations: {hash: #}}}",
						reservationRequest.reservationHash);
	}

}
