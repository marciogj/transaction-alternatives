package order;

public interface OrderEventBus {

	void orderPlaced(OrderEntity order);

	void orderCancelled(String orderHash);

}
