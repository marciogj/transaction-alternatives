package stock;

public class StockItemReservationEntity {

	public String hash;
	public int quantity;

	public StockItemReservationEntity copy() {
		StockItemReservationEntity newReservation = new StockItemReservationEntity();
		newReservation.hash = hash;
		newReservation.quantity = quantity;
		return newReservation;
	}

}
