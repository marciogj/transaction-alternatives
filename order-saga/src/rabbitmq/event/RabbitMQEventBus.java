package rabbitmq.event;

import order.OrderEventBus;
import payment.PaymentEventBus;
import rabbitmq.ConnectionFactories;
import relationship.RelationshipEventBus;
import shipping.ShippingEventBus;
import stock.StockEventBus;

public class RabbitMQEventBus {

	private final OrderRabbitMQEventBus orderRabbitMQEventBus = new OrderRabbitMQEventBus(
			ConnectionFactories.order());
	private final PaymentRabbitMQEventBus paymentRabbitMQEventBus = new PaymentRabbitMQEventBus(
			ConnectionFactories.order());
	private final RelationshipRabbitMQEventBus relationshipRabbitMQEventBus = new RelationshipRabbitMQEventBus(
			ConnectionFactories.order());
	private final ShippingRabbitMQEventBus shippingRabbitMQEventBus = new ShippingRabbitMQEventBus(
			ConnectionFactories.order());
	private final StockRabbitMQEventBus stockRabbitMQEventBus = new StockRabbitMQEventBus(
			ConnectionFactories.order());

	public OrderEventBus orderEventBus() {
		return orderRabbitMQEventBus;
	}

	public PaymentEventBus paymentEventBus() {
		return paymentRabbitMQEventBus;
	}

	public RelationshipEventBus relationshipEventBus() {
		return relationshipRabbitMQEventBus;
	}

	public ShippingEventBus shippingEventBus() {
		return shippingRabbitMQEventBus;
	}

	public StockEventBus stockEventBus() {
		return stockRabbitMQEventBus;
	}

}
