package mongodb;

import org.jongo.MongoCollection;

import saga.OrderResult;
import saga.OrderSagaEntity;
import saga.OrderSagaRepository;
import saga.OrderSagaResult;
import saga.PaymentResult;

public class OrderSagaMongoDBRepository implements OrderSagaRepository {

	private MongoCollection orderSagaCollection;

	public OrderSagaMongoDBRepository(MongoCollection orderSagaCollection) {
		this.orderSagaCollection = orderSagaCollection;
		orderSagaCollection.ensureIndex("{orderHash: 1}", "{unique: true}");
	}

	@Override
	public void save(OrderSagaEntity orderSaga) {
		orderSagaCollection.save(orderSaga);
	}

	private OrderSagaEntity loadByHash(String orderSagaHash) {
		return orderSagaCollection.findOne("{orderHash: #}", orderSagaHash).as(
				OrderSagaEntity.class);
	}

	@Override
	public void finishSaga(String orderSagaHash) {
		int n = orderSagaCollection
				.update("{orderHash: #, orderResult: #}", orderSagaHash,
						OrderResult.SCHEDULED)
				.with("{$set: {sagaResult: #}}", OrderSagaResult.COMMIT).getN();
		if (n < 1) {
			orderSagaCollection.update("{orderHash: #, orderResult: #}",
					orderSagaHash, OrderResult.CANCELLED).with(
					"{$set: {sagaResult: #}}", OrderSagaResult.ROLLBACK);
		}
	}

	@Override
	public void setOrderResult(String orderSagaHash, OrderResult orderResult) {
		orderSagaCollection.update("{orderHash: #}", orderSagaHash).with(
				"{$set: {orderResult: #}}", orderResult);
	}

	@Override
	public OrderSagaEntity setItemStockConfirmed(String orderSagaHash,
			String itemHash) {
		orderSagaCollection.update("{orderHash: #}", orderSagaHash).with(
				"{$set: {items.#.confirmed: #}}", itemHash, true);
		return loadByHash(orderSagaHash);
	}

	@Override
	public OrderSagaEntity setPaymentResult(String orderHash,
			PaymentResult paymentResult) {
		orderSagaCollection.update("{orderHash: #}", orderHash).with(
				"{$set: {paymentResult: #}}", paymentResult);
		return loadByHash(orderHash);
	}

	@Override
	public OrderSagaEntity setItemStockReturned(String orderHash,
			String itemHash) {
		orderSagaCollection.update("{orderHash: #}", orderHash).with(
				"{$set: {items.#.returned: #}}", itemHash, true);
		return loadByHash(orderHash);
	}

	@Override
	public OrderSagaEntity setItemOutOfStock(String orderHash, String itemHash) {
		orderSagaCollection.update("{orderHash: #}", orderHash).with(
				"{$set: {items.#.outOfStock: #}}", itemHash, true);
		return loadByHash(orderHash);
	}

	@Override
	public OrderSagaEntity setStockReserved(String orderHash, String itemHash) {
		orderSagaCollection.update("{orderHash: #}", orderHash).with(
				"{$set: {items.#.reserved: #}}", itemHash, true);
		return loadByHash(orderHash);
	}

}
