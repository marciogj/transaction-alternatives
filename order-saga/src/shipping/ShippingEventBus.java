package shipping;

public interface ShippingEventBus {

	void deliveryScheduled(DeliveryRequest deliveryRequest);

}
