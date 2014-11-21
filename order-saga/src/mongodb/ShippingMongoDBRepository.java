package mongodb;

import org.jongo.MongoCollection;

import shipping.DeliveryRequest;
import shipping.ShippingRepository;

public class ShippingMongoDBRepository implements ShippingRepository {

	private MongoCollection shippingCollection;

	public ShippingMongoDBRepository(MongoCollection shippingCollection) {
		this.shippingCollection = shippingCollection;
		shippingCollection.ensureIndex("{deliveryCorrelationHash: 1}",
				"{unique: true}");
	}

	@Override
	public void save(DeliveryRequest deliveryRequest) {
		shippingCollection.save(deliveryRequest);
	}

}
