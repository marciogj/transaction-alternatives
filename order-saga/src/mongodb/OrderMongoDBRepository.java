package mongodb;

import order.OrderEntity;
import order.OrderRepository;

import org.jongo.MongoCollection;

public class OrderMongoDBRepository implements OrderRepository {

	private MongoCollection orderCollection;

	public OrderMongoDBRepository(MongoCollection orderCollection) {
		this.orderCollection = orderCollection;
		orderCollection.ensureIndex("{hash: 1}", "{unique: true}");
	}

	@Override
	public void save(OrderEntity order) {
		orderCollection.save(order);
	}

	@Override
	public void deleteByHash(String orderHash) {
		orderCollection.remove("{hash: #}", orderHash);
	}

}
