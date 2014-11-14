package saga;

public interface OrderSagaRepository {

	void save(OrderSagaEntity orderSaga);
	
	OrderSagaEntity loadByHash(String orderSagaHash);

	void replace(OrderSagaEntity oldOrder, OrderSagaEntity newOrder);
	
}
