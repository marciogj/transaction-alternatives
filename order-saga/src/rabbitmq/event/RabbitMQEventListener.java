package rabbitmq.event;

import order.OrderEventBus;
import payment.PaymentEventBus;
import rabbitmq.DefaultRabbitMQMessageConsumer;
import relationship.RelationshipEventBus;
import shipping.ShippingEventBus;
import stock.StockEventBus;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQEventListener {

	private ConnectionFactory factory;

	public RabbitMQEventListener() {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
	}

	public DefaultRabbitMQMessageConsumer addOrderEventBusHandler(String name,
			OrderEventBus orderEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "order-event", name);
		consumer.init(new OrderEventBusRabbitMQHandler(orderEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addPaymentEventBusHandler(
			String name, PaymentEventBus paymentEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "payment-event", name);
		consumer.init(new PaymentEventBusRabbitMQHandler(paymentEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addRelationshipEventBusHandler(
			String name, RelationshipEventBus relationshipEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "relationship-event", name);
		consumer.init(new RelationshipEventBusRabbitMQHandler(
				relationshipEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addShippingEventBusHandler(
			String name, ShippingEventBus shippingEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "shipping-event", name);
		consumer.init(new ShippingEventBusRabbitMQHandler(shippingEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addStockEventBusHandler(String name,
			StockEventBus stockEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				factory, "stock-event", name);
		consumer.init(new StockEventBusRabbitMQHandler(stockEventBus));
		return consumer;
	}

}
