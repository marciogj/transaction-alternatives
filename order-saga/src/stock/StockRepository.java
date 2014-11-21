package stock;

public interface StockRepository {

	void save(StockItemEntity item);

	boolean reserveItem(ReservationRequest reservationRequest);

	void returnStock(ReservationRequest reservationRequest);

	void confirmStock(ReservationRequest reservationRequest);

}
