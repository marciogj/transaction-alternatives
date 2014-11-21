package stock;

import java.util.List;

public class StockItemEntity {

	public String hash;
	public int inStockQuantity;
	public List<StockItemReservationEntity> reservations;

	/* Atomic update. */
	public int version;

}
