package saga;

public interface OrderSagaRepository {

	void save(OrderSagaEntity orderSaga);

	// OrderSagaEntity loadByHash(String orderSagaHash);

	void finishSaga(String orderSagaHash); // Set ROLLBACK if CANCELLED, set
											// COMMIT if OK

	void setOrderResult(String orderSagaHash, OrderResult orderResult);

	// may return null if not interesting

	OrderSagaEntity setItemStockConfirmed(String orderSagaHash, String itemHash);

	OrderSagaEntity setPaymentResult(String orderHash,
			PaymentResult paymentResult);

	OrderSagaEntity setItemStockReturned(String orderHash, String itemHash);

	OrderSagaEntity setItemOutOfStock(String orderHash, String itemHash);

	OrderSagaEntity setStockReserved(String orderHash, String itemHash);
}
