package rabbitmq.command;

public interface RabbitMQMessageHandler {

	void handle(RabbitMQMessage message);

}
