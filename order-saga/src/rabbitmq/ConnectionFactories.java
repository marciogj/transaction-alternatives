package rabbitmq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.ConnectionFactory;

public class ConnectionFactories {

	private static ConnectionFactory factory = factory();

	private static ConnectionFactory factory() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		ExecutorService executor = Executors.newWorkStealingPool();
		factory.setSharedExecutor(executor);
		return factory;
	}

	public static ConnectionFactory order() {
		return factory;
	}

}
