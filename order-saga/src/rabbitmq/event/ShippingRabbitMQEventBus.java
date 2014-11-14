package rabbitmq.event;

import rabbitmq.AbstractRabbitMQMessageSender;
import shipping.DeliveryRequest;
import shipping.ShippingEventBus;

import com.rabbitmq.client.ConnectionFactory;

public class ShippingRabbitMQEventBus extends AbstractRabbitMQMessageSender
		implements ShippingEventBus {

	public ShippingRabbitMQEventBus(ConnectionFactory factory) {
		super(factory, "shipping-event");
	}

	@Override
	public void deliveryScheduled(DeliveryRequest deliveryRequest) {
		send("delivery-scheduled", deliveryRequest);
	}

}
