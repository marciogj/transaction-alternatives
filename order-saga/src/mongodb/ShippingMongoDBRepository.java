package mongodb;

import org.jongo.MongoCollection;

import shipping.DeliveryRequest;
import shipping.ShippingRepository;

import com.mongodb.DuplicateKeyException;

public class ShippingMongoDBRepository implements ShippingRepository {

	private MongoCollection shippingCollection;

	public ShippingMongoDBRepository(MongoCollection shippingCollection) {
		this.shippingCollection = shippingCollection;
		shippingCollection.ensureIndex("{deliveryCorrelationHash: 1}",
				"{unique: true}");
	}

	@Override
	public void save(DeliveryRequest deliveryRequest) {
		try {
			shippingCollection.save(deliveryRequest);
		} catch (DuplicateKeyException e) {
			System.out.println("> Duplicated DeliveryRequest, won't write.");
			// ok
		}
	}

}
