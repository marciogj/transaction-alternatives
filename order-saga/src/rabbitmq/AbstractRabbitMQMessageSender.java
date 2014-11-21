package rabbitmq;

import java.io.IOException;

import rabbitmq.command.RabbitMQMessage;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class AbstractRabbitMQMessageSender {

	private static final String EXCHANGE_TYPE = "fanout";
	private String exchangeName;
	private Connection connection;
	private Channel channel;

	public AbstractRabbitMQMessageSender(ConnectionFactory factory,
			String exchangeName) {
		this.exchangeName = exchangeName;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void send(String intent, Object message) {
		try {
			Gson gson = new Gson();
			RabbitMQMessage mQMessage = new RabbitMQMessage();
			mQMessage.intent = intent;
			mQMessage.message = message instanceof String ? (String) message
					: gson.toJson(message);
			String messageJSon = gson.toJson(mQMessage);
			byte[] serializedMessage = messageJSon.getBytes("UTF-8");
			BasicProperties properties = null;
			String routingKey = "";
			channel.basicPublish(exchangeName, routingKey, properties,
					serializedMessage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
