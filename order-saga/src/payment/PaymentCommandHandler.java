package payment;

public class PaymentCommandHandler implements PaymentService {

	private boolean acceptPayment = true;
	private PaymentEventBus eventBus;

	public PaymentCommandHandler(PaymentEventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void requestPayment(PaymentRequest paymentRequest) {
		/* Simulate an external payment service. */
		/* 50% accepted, 50% rejected. */
		if (acceptPayment) {
			eventBus.paymentAccepted(paymentRequest);
		} else {
			eventBus.paymentRejected(paymentRequest);
		}
		acceptPayment = !acceptPayment;
	}

}
