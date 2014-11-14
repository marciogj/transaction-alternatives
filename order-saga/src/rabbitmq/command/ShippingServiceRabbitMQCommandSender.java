package rabbitmq.command;

import rabbitmq.AbstractRabbitMQMessageSender;
import shipping.DeliveryRequest;
import shipping.ShippingService;

import com.rabbitmq.client.ConnectionFactory;

public class ShippingServiceRabbitMQCommandSender extends
		AbstractRabbitMQMessageSender implements ShippingService {

	public ShippingServiceRabbitMQCommandSender(ConnectionFactory factory) {
		super(factory, "shipping-service");
	}

	@Override
	public void requestDelivery(DeliveryRequest deliveryRequest) {
		send("request-delivery", deliveryRequest);
	}

}
