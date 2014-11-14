package payment;

import java.util.Random;

public class PaymentCommandHandler implements PaymentService {

	private Random random = new Random();
	private PaymentEventBus eventBus;
	
	public PaymentCommandHandler(PaymentEventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	@Override
	public void requestPayment(PaymentRequest paymentRequest) {
		/* Simulate an external payment service. */
		if (random.nextBoolean()) {
			eventBus.paymentAccepted(paymentRequest);
		} else {
			eventBus.paymentRejected(paymentRequest);
		}
	}
	
}
