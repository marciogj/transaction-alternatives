package rabbitmq.event;

import com.rabbitmq.client.ConnectionFactory;

import payment.PaymentEventBus;
import payment.PaymentRequest;
import rabbitmq.AbstractRabbitMQMessageSender;

public class PaymentRabbitMQEventBus extends AbstractRabbitMQMessageSender implements PaymentEventBus {

	public PaymentRabbitMQEventBus(ConnectionFactory factory) {
		super(factory, "payment-event");
	}

	@Override
	public void paymentAccepted(PaymentRequest paymentRequest) {
		send("payment-accepted", paymentRequest);
	}

	@Override
	public void paymentRejected(PaymentRequest paymentRequest) {
		send("payment-rejected", paymentRequest);
	}

}
