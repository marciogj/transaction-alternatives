package mongodb;

import java.net.UnknownHostException;

import order.OrderRepository;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import saga.OrderSagaRepository;
import shipping.ShippingRepository;
import stock.StockRepository;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBRepository {

	private MongoClient client;
	private Jongo jongo;

	public MongoDBRepository() {
		try {
			client = new MongoClient();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		DB db = client.getDB("BDA");
		jongo = new Jongo(db);
	}

	public OrderSagaRepository orderSagaRepository() {
		MongoCollection orderSagaCollection = jongo.getCollection("order-saga");
		return new OrderSagaMongoDBRepository(orderSagaCollection);
	}

	public OrderRepository orderRepository() {
		MongoCollection orderCollection = jongo.getCollection("order");
		return new OrderMongoDBRepository(orderCollection);
	}

	public ShippingRepository shippingRepository() {
		MongoCollection shippingCollection = jongo.getCollection("shipping");
		return new ShippingMongoDBRepository(shippingCollection);
	}

	public StockRepository stockRepository() {
		MongoCollection stockCollection = jongo.getCollection("stock");
		return new StockMongoDBRepository(stockCollection);
	}

}
