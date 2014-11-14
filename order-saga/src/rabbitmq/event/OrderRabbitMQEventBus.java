package rabbitmq.event;

import order.OrderEntity;
import order.OrderEventBus;
import rabbitmq.AbstractRabbitMQMessageSender;

import com.rabbitmq.client.ConnectionFactory;

public class OrderRabbitMQEventBus extends AbstractRabbitMQMessageSender
		implements OrderEventBus {

	public OrderRabbitMQEventBus(ConnectionFactory factory) {
		super(factory, "order-event");
	}

	@Override
	public void orderPlaced(OrderEntity order) {
		send("order-placed", order);
	}

	@Override
	public void orderCancelled(String orderHash) {
		send("order-cancelled", orderHash);
	}

}
