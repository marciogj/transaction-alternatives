package shipping;

public class ShippingCommandHandler implements ShippingService {

	private ShippingRepository repository;
	private ShippingEventBus eventBus;

	public ShippingCommandHandler(ShippingRepository repository, ShippingEventBus eventBus) {
		this.repository = repository;
		this.eventBus = eventBus;
	}
	
	@Override
	public void requestDelivery(DeliveryRequest deliveryRequest) {
		repository.save(deliveryRequest);
		eventBus.deliveryScheduled(deliveryRequest);
	}
	
}
