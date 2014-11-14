package stock;

public interface StockEventBus {

	void outOfStock(ReservationRequest reservationRequest);

	void stockReserved(ReservationRequest reservationRequest);

	void stockReturned(ReservationRequest reservationRequest);

	void stockConfirmed(ReservationRequest reservationRequest);

}
