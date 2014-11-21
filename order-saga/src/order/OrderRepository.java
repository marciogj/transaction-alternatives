package order;

public interface OrderRepository {

	void save(OrderEntity order);

	void deleteByHash(String orderHash);

}
