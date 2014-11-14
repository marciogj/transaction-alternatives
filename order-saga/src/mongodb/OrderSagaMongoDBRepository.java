package mongodb;

import org.jongo.MongoCollection;

import saga.OrderSagaEntity;
import saga.OrderSagaRepository;

import com.mongodb.DuplicateKeyException;
import com.mongodb.WriteResult;

public class OrderSagaMongoDBRepository implements OrderSagaRepository {

	private MongoCollection orderSagaCollection;

	public OrderSagaMongoDBRepository(MongoCollection orderSagaCollection) {
		this.orderSagaCollection = orderSagaCollection;
		orderSagaCollection.ensureIndex("{orderHash: 1}", "{unique: true}");
	}

	@Override
	public void save(OrderSagaEntity orderSaga) {
		try {
			orderSagaCollection.save(orderSaga);
		} catch (DuplicateKeyException e) {
			System.out.println("> Duplicated OrderSagaEntity, won't write.");
			// ok
		}
	}

	@Override
	public OrderSagaEntity loadByHash(String orderSagaHash) {
		return orderSagaCollection.findOne("{orderHash: #}", orderSagaHash).as(
				OrderSagaEntity.class);
	}

	@Override
	public void replace(OrderSagaEntity oldOrder, OrderSagaEntity newOrder) {
		if (newOrder.version <= oldOrder.version) {
			throw new RuntimeException();
		}
		WriteResult wr = orderSagaCollection.update(
				"{orderHash: #, version: #}", oldOrder.orderHash,
				oldOrder.version).with(newOrder);
		System.out.println("> Replaced? " + wr.getN());
	}

}
