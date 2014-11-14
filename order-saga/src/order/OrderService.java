package order;

public interface OrderService {

	void placeOrder(OrderEntity order);

	void cancelOrder(String orderHash);

}