package mongodb;

import order.OrderEntity;
import order.OrderRepository;

import org.jongo.MongoCollection;

import com.mongodb.DuplicateKeyException;

public class OrderMongoDBRepository implements OrderRepository {

	private MongoCollection orderCollection;

	public OrderMongoDBRepository(MongoCollection orderCollection) {
		this.orderCollection = orderCollection;
		orderCollection.ensureIndex("{hash: 1}", "{unique: true}");
	}

	@Override
	public void save(OrderEntity order) {
		try {
			orderCollection.save(order);
		} catch (DuplicateKeyException e) {
			System.out.println("> Duplicated OrderEntity, won't write.");
			// ok
		}
	}

	@Override
	public void deleteByHash(String orderHash) {
		orderCollection.remove("{hash: #}", orderHash);
	}

	@Override
	public OrderEntity loadByHash(String orderHash) {
		return orderCollection.findOne("{hash: #}", orderHash).as(
				OrderEntity.class);
	}

}
