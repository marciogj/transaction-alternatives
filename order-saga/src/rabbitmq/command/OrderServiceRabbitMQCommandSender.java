package rabbitmq.command;

import order.OrderEntity;
import order.OrderService;
import rabbitmq.AbstractRabbitMQMessageSender;

import com.rabbitmq.client.ConnectionFactory;

public class OrderServiceRabbitMQCommandSender extends
		AbstractRabbitMQMessageSender implements OrderService {

	public OrderServiceRabbitMQCommandSender(ConnectionFactory factory) {
		super(factory, "order-service");
	}

	@Override
	public void placeOrder(OrderEntity order) {
		send("place-order", order);
	}

	@Override
	public void cancelOrder(String orderHash) {
		send("cancel-order", orderHash);
	}

}
