package rabbitmq.command;

import shipping.DeliveryRequest;
import shipping.ShippingService;

import com.google.gson.Gson;

public class ShippingServiceRabbitMQHandler implements RabbitMQMessageHandler {

	private ShippingService shippingService;

	public ShippingServiceRabbitMQHandler(ShippingService shippingService) {
		this.shippingService = shippingService;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		if (!message.intent.equals("request-delivery")) {
			throw new RuntimeException("invalid intent: " + message.intent);
		}
		Gson gson = new Gson();
		DeliveryRequest deliveryRequest = gson.fromJson(message.message,
				DeliveryRequest.class);
		shippingService.requestDelivery(deliveryRequest);
	}

}
