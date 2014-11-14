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
	private ConnectionFactory factory;
	private String exchangeName;

	public AbstractRabbitMQMessageSender(ConnectionFactory factory,
			String exchangeName) {
		this.factory = factory;
		this.exchangeName = exchangeName;
	}

	public void send(String intent, Object message) {
		System.out.println("> Sending message '" + intent + "' to '"
				+ exchangeName + "'.");
		try {
			Connection connection = factory.newConnection();
			try {
				Channel channel = connection.createChannel();
				try {
					channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE);
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
				} finally {
					channel.close();
				}
			} finally {
				connection.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
