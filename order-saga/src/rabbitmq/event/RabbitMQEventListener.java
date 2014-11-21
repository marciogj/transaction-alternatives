package rabbitmq.event;

import order.OrderEventBus;
import payment.PaymentEventBus;
import rabbitmq.ConnectionFactories;
import rabbitmq.DefaultRabbitMQMessageConsumer;
import relationship.RelationshipEventBus;
import shipping.ShippingEventBus;
import stock.StockEventBus;

public class RabbitMQEventListener {

	public RabbitMQEventListener() {
	}

	public DefaultRabbitMQMessageConsumer addOrderEventBusHandler(String name,
			OrderEventBus orderEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				ConnectionFactories.order(), "order-event", name);
		consumer.init(new OrderEventBusRabbitMQHandler(orderEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addPaymentEventBusHandler(
			String name, PaymentEventBus paymentEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				ConnectionFactories.order(), "payment-event", name);
		consumer.init(new PaymentEventBusRabbitMQHandler(paymentEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addRelationshipEventBusHandler(
			String name, RelationshipEventBus relationshipEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				ConnectionFactories.order(), "relationship-event", name);
		consumer.init(new RelationshipEventBusRabbitMQHandler(
				relationshipEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addShippingEventBusHandler(
			String name, ShippingEventBus shippingEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				ConnectionFactories.order(), "shipping-event", name);
		consumer.init(new ShippingEventBusRabbitMQHandler(shippingEventBus));
		return consumer;
	}

	public DefaultRabbitMQMessageConsumer addStockEventBusHandler(String name,
			StockEventBus stockEventBus) {
		DefaultRabbitMQMessageConsumer consumer = new DefaultRabbitMQMessageConsumer(
				ConnectionFactories.order(), "stock-event", name);
		consumer.init(new StockEventBusRabbitMQHandler(stockEventBus));
		return consumer;
	}

}
