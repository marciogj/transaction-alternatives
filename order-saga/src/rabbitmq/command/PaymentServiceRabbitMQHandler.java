package rabbitmq.command;

import payment.PaymentRequest;
import payment.PaymentService;

import com.google.gson.Gson;

public class PaymentServiceRabbitMQHandler implements RabbitMQMessageHandler {

	private PaymentService paymentService;

	public PaymentServiceRabbitMQHandler(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		if (message.intent.equals("request-payment")) {
			Gson gson = new Gson();
			PaymentRequest paymentRequest = gson.fromJson(message.message,
					PaymentRequest.class);
			paymentService.requestPayment(paymentRequest);
		} else {
			throw new RuntimeException("invalid intent: " + message.intent);
		}
	}

}
