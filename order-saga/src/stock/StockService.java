package stock;

public interface StockService {

	void reserveStock(ReservationRequest reservationRequest);

	void returnStock(ReservationRequest reservationRequest);

	void confirmStock(ReservationRequest reservationRequest);

}