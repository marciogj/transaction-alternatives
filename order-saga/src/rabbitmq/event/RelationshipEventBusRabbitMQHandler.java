package rabbitmq.event;

import rabbitmq.command.RabbitMQMessage;
import rabbitmq.command.RabbitMQMessageHandler;
import relationship.RelationshipEntity;
import relationship.RelationshipEventBus;

import com.google.gson.Gson;

public class RelationshipEventBusRabbitMQHandler implements
		RabbitMQMessageHandler {

	private RelationshipEventBus relationshipEventBus;

	public RelationshipEventBusRabbitMQHandler(
			RelationshipEventBus relationshipEventBus) {
		this.relationshipEventBus = relationshipEventBus;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		if (!message.intent.equals("customer-notified")) {
			throw new RuntimeException("invalid intent: " + message.intent);
		}
		Gson gson = new Gson();
		RelationshipEntity relationshipMessage = gson.fromJson(message.message,
				RelationshipEntity.class);
		relationshipEventBus.customerNotified(relationshipMessage);
	}

}
