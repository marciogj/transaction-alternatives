package order;

public class OrderCommandHandler implements OrderService {
	
	private OrderEventBus eventBus;
	private OrderRepository repository;

	public OrderCommandHandler(OrderEventBus eventBus, OrderRepository repository) {
		this.repository = repository;
		this.eventBus = eventBus;
	}

	@Override
	public void placeOrder(OrderEntity order) {
		repository.save(order);
		eventBus.orderPlaced(order);
	}
	
	@Override
	public void cancelOrder(String orderHash) {
		repository.deleteByHash(orderHash);
		eventBus.orderCancelled(orderHash);
	}
}
