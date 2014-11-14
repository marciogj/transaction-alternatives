package rabbitmq.command;

import com.rabbitmq.client.ConnectionFactory;

import rabbitmq.AbstractRabbitMQMessageSender;
import relationship.RelationshipEntity;
import relationship.RelationshipService;

public class RelationshipServiceRabbitMQCommandSender extends
		AbstractRabbitMQMessageSender implements RelationshipService {

	public RelationshipServiceRabbitMQCommandSender(ConnectionFactory factory) {
		super(factory, "relationship-service");
	}

	@Override
	public void notifyCustomer(RelationshipEntity message) {
		send("notify-customer", message);
	}

}
