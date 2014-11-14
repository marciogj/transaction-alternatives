package stock;

import java.util.ArrayList;
import java.util.List;

public class StockItemEntity {

	public String hash;
	public int inStockQuantity;
	public List<StockItemReservationEntity> reservations;

	/* Atomic update. */
	public int version;

	public StockItemEntity copy() {
		StockItemEntity newItem = new StockItemEntity();
		newItem.hash = hash;
		newItem.inStockQuantity = inStockQuantity;
		newItem.reservations = new ArrayList<>();
		for (StockItemReservationEntity reservation : reservations) {
			newItem.reservations.add(reservation.copy());
		}
		newItem.version = version + 1;
		return newItem;
	}

}
