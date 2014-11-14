package rabbitmq.command;

import payment.PaymentRequest;
import payment.PaymentService;
import rabbitmq.AbstractRabbitMQMessageSender;

import com.rabbitmq.client.ConnectionFactory;

public class PaymentServiceRabbitMQCommandSender extends
		AbstractRabbitMQMessageSender implements PaymentService {

	public PaymentServiceRabbitMQCommandSender(ConnectionFactory factory) {
		super(factory, "payment-service");
	}

	@Override
	public void requestPayment(PaymentRequest paymentRequest) {
		send("request-payment", paymentRequest);
	}

}
