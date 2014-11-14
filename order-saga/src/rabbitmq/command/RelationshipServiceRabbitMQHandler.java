package rabbitmq.command;

import relationship.RelationshipEntity;
import relationship.RelationshipService;

import com.google.gson.Gson;

public class RelationshipServiceRabbitMQHandler implements
		RabbitMQMessageHandler {

	private RelationshipService relationshipService;

	public RelationshipServiceRabbitMQHandler(
			RelationshipService relationshipService) {
		this.relationshipService = relationshipService;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		switch (message.intent) {
		case "notify-customer":
			Gson gson = new Gson();
			RelationshipEntity relationshipMessage = gson.fromJson(
					message.message, RelationshipEntity.class);
			relationshipService.notifyCustomer(relationshipMessage);
			break;

		default:
			throw new RuntimeException("invalid intent: " + message.intent);
		}
	}
}
