package rabbitmq.event;

import payment.PaymentEventBus;
import payment.PaymentRequest;
import rabbitmq.command.RabbitMQMessage;
import rabbitmq.command.RabbitMQMessageHandler;

import com.google.gson.Gson;

public class PaymentEventBusRabbitMQHandler implements RabbitMQMessageHandler {

	private PaymentEventBus paymentEventBus;

	public PaymentEventBusRabbitMQHandler(PaymentEventBus paymentEventBus) {
		this.paymentEventBus = paymentEventBus;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		Gson gson = new Gson();
		PaymentRequest paymentRequest = gson.fromJson(message.message,
				PaymentRequest.class);
		switch (message.intent) {
		case "payment-accepted":
			paymentEventBus.paymentAccepted(paymentRequest);
			break;
		case "payment-rejected":
			paymentEventBus.paymentRejected(paymentRequest);
			break;
		default:
			throw new RuntimeException("invalid intent: " + message.intent);
		}
	}

}
