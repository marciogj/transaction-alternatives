package rabbitmq.event;

import order.OrderEventBus;
import payment.PaymentEventBus;
import relationship.RelationshipEventBus;
import shipping.ShippingEventBus;
import stock.StockEventBus;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQEventBus {

	private ConnectionFactory factory;

	public RabbitMQEventBus() {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
	}

	public OrderEventBus orderEventBus() {
		return new OrderRabbitMQEventBus(factory);
	}

	public PaymentEventBus paymentEventBus() {
		return new PaymentRabbitMQEventBus(factory);
	}

	public RelationshipEventBus relationshipEventBus() {
		return new RelationshipRabbitMQEventBus(factory);
	}

	public ShippingEventBus shippingEventBus() {
		return new ShippingRabbitMQEventBus(factory);
	}

	public StockEventBus stockEventBus() {
		return new StockRabbitMQEventBus(factory);
	}

}
