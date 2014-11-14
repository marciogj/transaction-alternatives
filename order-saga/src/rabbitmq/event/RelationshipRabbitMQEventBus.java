package rabbitmq.event;

import rabbitmq.AbstractRabbitMQMessageSender;
import relationship.RelationshipEntity;
import relationship.RelationshipEventBus;

import com.rabbitmq.client.ConnectionFactory;

public class RelationshipRabbitMQEventBus extends AbstractRabbitMQMessageSender
		implements RelationshipEventBus {

	public RelationshipRabbitMQEventBus(ConnectionFactory factory) {
		super(factory, "relationship-event");
	}

	@Override
	public void customerNotified(RelationshipEntity message) {
		send("customer-notified", message);
	}

}
