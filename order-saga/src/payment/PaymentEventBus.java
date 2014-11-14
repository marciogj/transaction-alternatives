package payment;

public interface PaymentEventBus {

	void paymentAccepted(PaymentRequest paymentRequest);

	void paymentRejected(PaymentRequest paymentRequest);

}
