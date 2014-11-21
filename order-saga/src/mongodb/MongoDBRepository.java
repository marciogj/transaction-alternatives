package mongodb;

import java.net.UnknownHostException;

import order.OrderRepository;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import saga.OrderSagaRepository;
import shipping.ShippingRepository;
import stock.StockRepository;
import bda.Repository;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBRepository implements Repository {

	private MongoClient client;
	private Jongo db;

	public MongoDBRepository() {
		try {
			client = new MongoClient();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		DB db = client.getDB("BDA");
		this.db = new Jongo(db);
	}

	@Override
	public OrderSagaRepository orderSagaRepository() {
		MongoCollection orderSagaCollection = db.getCollection("order-saga");
		return new OrderSagaMongoDBRepository(orderSagaCollection);
	}

	@Override
	public OrderRepository orderRepository() {
		MongoCollection orderCollection = db.getCollection("order");
		return new OrderMongoDBRepository(orderCollection);
	}

	@Override
	public ShippingRepository shippingRepository() {
		MongoCollection shippingCollection = db.getCollection("shipping");
		return new ShippingMongoDBRepository(shippingCollection);
	}

	@Override
	public StockRepository stockRepository() {
		MongoCollection stockCollection = db.getCollection("stock");
		return new StockMongoDBRepository(stockCollection);
	}

}
