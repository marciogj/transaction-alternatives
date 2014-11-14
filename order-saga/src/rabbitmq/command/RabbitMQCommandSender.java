package rabbitmq.command;

import order.OrderService;
import payment.PaymentService;
import relationship.RelationshipService;
import shipping.ShippingService;
import stock.StockService;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQCommandSender {

	private ConnectionFactory factory;

	public RabbitMQCommandSender() {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
	}

	public OrderService orderService() {
		return new OrderServiceRabbitMQCommandSender(factory);
	}

	public StockService stockService() {
		return new StockServiceRabbitMQCommandSender(factory);
	}

	public PaymentService paymentService() {
		return new PaymentServiceRabbitMQCommandSender(factory);
	}

	public RelationshipService relationshipService() {
		return new RelationshipServiceRabbitMQCommandSender(factory);
	}

	public ShippingService shippingService() {
		return new ShippingServiceRabbitMQCommandSender(factory);
	}

}
