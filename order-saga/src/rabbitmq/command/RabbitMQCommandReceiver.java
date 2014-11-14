package rabbitmq.command;

import order.OrderService;
import payment.PaymentService;
import rabbitmq.DefaultRabbitMQMessageConsumer;
import relationship.RelationshipService;
import shipping.ShippingService;
import stock.StockService;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQCommandReceiver {

	private ConnectionFactory factory;

	public RabbitMQCommandReceiver() {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
	}

	public DefaultRabbitMQMessageConsumer addOrderServiceHandler(String name,
			OrderService orderService) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "order-service", name);
		consumer.init(new OrderServiceRabbitMQHandler(orderService));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addPaymentServiceHandler(String name,
			PaymentService paymentService) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "payment-service", name);
		consumer.init(new PaymentServiceRabbitMQHandler(paymentService));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addRelationshipServiceHandler(
			String name, RelationshipService relationshipService) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "relationship-service", name);
		consumer.init(new RelationshipServiceRabbitMQHandler(
				relationshipService));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addShippingServiceHandler(
			String name, ShippingService shippingService) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "shipping-service", name);
		consumer.init(new ShippingServiceRabbitMQHandler(shippingService));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addStockServiceHandler(String name,
			StockService stockService) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "stock-service", name);
		consumer.init(new StockServiceRabbitMQHandler(stockService));
		return consumer;
	}

}
