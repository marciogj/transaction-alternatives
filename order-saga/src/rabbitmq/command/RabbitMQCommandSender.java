package rabbitmq.command;

import order.OrderService;
import payment.PaymentService;
import rabbitmq.ConnectionFactories;
import relationship.RelationshipService;
import shipping.ShippingService;
import stock.StockService;

public class RabbitMQCommandSender {

	private final OrderServiceRabbitMQCommandSender orderServiceRabbitMQCommandSender = new OrderServiceRabbitMQCommandSender(
			ConnectionFactories.order());
	private final StockServiceRabbitMQCommandSender stockServiceRabbitMQCommandSender = new StockServiceRabbitMQCommandSender(
			ConnectionFactories.order());
	private final PaymentServiceRabbitMQCommandSender paymentServiceRabbitMQCommandSender = new PaymentServiceRabbitMQCommandSender(
			ConnectionFactories.order());
	private final RelationshipServiceRabbitMQCommandSender relationshipServiceRabbitMQCommandSender = new RelationshipServiceRabbitMQCommandSender(
			ConnectionFactories.order());
	private final ShippingServiceRabbitMQCommandSender shippingServiceRabbitMQCommandSender = new ShippingServiceRabbitMQCommandSender(
			ConnectionFactories.order());

	public RabbitMQCommandSender() {
	}

	public OrderService orderService() {
		return orderServiceRabbitMQCommandSender;
	}

	public StockService stockService() {
		return stockServiceRabbitMQCommandSender;
	}

	public PaymentService paymentService() {
		return paymentServiceRabbitMQCommandSender;
	}

	public RelationshipService relationshipService() {
		return relationshipServiceRabbitMQCommandSender;
	}

	public ShippingService shippingService() {
		return shippingServiceRabbitMQCommandSender;
	}

}
