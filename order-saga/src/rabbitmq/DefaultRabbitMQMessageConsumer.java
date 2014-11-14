package rabbitmq;

import java.io.IOException;
import java.util.Map;

import rabbitmq.command.RabbitMQMessage;
import rabbitmq.command.RabbitMQMessageHandler;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class DefaultRabbitMQMessageConsumer {

	private static final String EXCHANGE_TYPE = "fanout";
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private String exchangeName;
	private String queueName;
	private String consumerName;

	public DefaultRabbitMQMessageConsumer(ConnectionFactory factory,
			String exchangeName, String consumerName) {
		this.factory = factory;
		this.exchangeName = exchangeName;
		this.consumerName = consumerName;
		queueName = exchangeName + "-" + consumerName;
	}

	public void init(RabbitMQMessageHandler rabbitMQMessageHandler) {
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE);
			boolean durable = true;
			boolean exclusive = false;
			boolean autoDelete = false;
			Map<String, Object> arguments = null;
			channel.queueDeclare(queueName, durable, exclusive, autoDelete,
					arguments);
			channel.queueBind(queueName, exchangeName, "");
			channel.basicConsume(queueName, false,
					new DefaultConsumer(channel) {
						@Override
						public void handleDelivery(String consumerTag,
								Envelope envelope, BasicProperties properties,
								byte[] body) throws IOException {
							Gson gson = new Gson();
							String messageBody = new String(body, "UTF-8");
							RabbitMQMessage message = gson.fromJson(
									messageBody, RabbitMQMessage.class);

							System.out.println("> Handling message '"
									+ message.intent + "' at '" + exchangeName
									+ "' by '" + consumerName + "'.");

							rabbitMQMessageHandler.handle(message);

							long deliveryTag = envelope.getDeliveryTag();
							boolean multiple = false;
							channel.basicAck(deliveryTag, multiple);
						}
					});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			if (channel != null) {
				channel.queueUnbind(queueName, exchangeName, "");
			}
		} catch (IOException e) {
			;
		}
		try {
			if (channel != null) {
				channel.close();
				channel = null;
			}
		} catch (IOException e) {
			;
		}
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (IOException e) {
			;
		}
	}

}
