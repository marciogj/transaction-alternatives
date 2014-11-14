package rabbitmq.command;

import order.OrderEntity;
import order.OrderService;

import com.google.gson.Gson;

public class OrderServiceRabbitMQHandler implements RabbitMQMessageHandler {

	private OrderService orderService;

	public OrderServiceRabbitMQHandler(OrderService orderService) {
		this.orderService = orderService;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		switch (message.intent) {
		case "place-order":
			Gson gson = new Gson();
			OrderEntity order = gson.fromJson(message.message,
					OrderEntity.class);
			orderService.placeOrder(order);
			break;

		case "cancel-order":
			String orderHash = message.message;
			orderService.cancelOrder(orderHash);
			break;

		default:
			throw new RuntimeException("invalid intent: " + message.intent);
		}
	}
}