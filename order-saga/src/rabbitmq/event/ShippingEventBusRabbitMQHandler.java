package rabbitmq.event;

import rabbitmq.command.RabbitMQMessage;
import rabbitmq.command.RabbitMQMessageHandler;
import shipping.DeliveryRequest;
import shipping.ShippingEventBus;

import com.google.gson.Gson;

public class ShippingEventBusRabbitMQHandler implements RabbitMQMessageHandler {

	private ShippingEventBus shippingEventBus;

	public ShippingEventBusRabbitMQHandler(ShippingEventBus shippingEventBus) {
		this.shippingEventBus = shippingEventBus;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		if (!message.intent.equals("delivery-scheduled")) {
			throw new RuntimeException("invalid intent: " + message.intent);
		}
		Gson gson = new Gson();
		DeliveryRequest deliveryRequest = gson.fromJson(message.message,
				DeliveryRequest.class);
		shippingEventBus.deliveryScheduled(deliveryRequest);
	}

}
