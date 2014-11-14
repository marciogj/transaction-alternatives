package rabbitmq.event;

import order.OrderEntity;
import order.OrderEventBus;
import rabbitmq.command.RabbitMQMessage;
import rabbitmq.command.RabbitMQMessageHandler;

import com.google.gson.Gson;

public class OrderEventBusRabbitMQHandler implements RabbitMQMessageHandler {

	private OrderEventBus orderEventBus;

	public OrderEventBusRabbitMQHandler(OrderEventBus orderEventBus) {
		this.orderEventBus = orderEventBus;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		Gson gson = new Gson();
		switch (message.intent) {
		case "order-cancelled":
			String orderHash = message.message;
			orderEventBus.orderCancelled(orderHash);
			break;
		case "order-placed":
			OrderEntity order = gson.fromJson(message.message,
					OrderEntity.class);
			orderEventBus.orderPlaced(order);
			break;
		default:
			System.err.println("invalid intent: " + message.intent);
		}
	}

}
