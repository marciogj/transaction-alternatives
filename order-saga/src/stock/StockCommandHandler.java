package stock;

import java.util.List;

public class StockCommandHandler implements StockService {

	private StockRepository repository;
	private StockEventBus eventBus;

	public StockCommandHandler(StockRepository repository,
			StockEventBus eventBus) {
		this.repository = repository;
		this.eventBus = eventBus;
	}

	@Override
	public void reserveStock(ReservationRequest reservationRequest) {
		String itemHash = reservationRequest.itemHash;
		String reservationHash = reservationRequest.reservationHash;
		int quantity = reservationRequest.quantity;
		StockItemEntity item = repository.loadItem(itemHash);

		/* Look if item is already reserved */
		List<StockItemReservationEntity> reservations = item.reservations;
		for (StockItemReservationEntity reservation : reservations) {
			if (reservationHash.equals(reservation.hash)) {
				/* Item already has a reservation for this. */
				return;
			}
		}

		/* Item needs to be reserved. */
		if (item.inStockQuantity < quantity) {
			eventBus.outOfStock(reservationRequest);
			return;
		}

		StockItemEntity newItem = item.copy();
		StockItemReservationEntity reservation = new StockItemReservationEntity();
		reservation.hash = reservationHash;
		reservation.quantity = quantity;
		reservation.confirmed = false;
		newItem.reservations.add(reservation);
		repository.replace(item, newItem);

		eventBus.stockReserved(reservationRequest);
	}

	@Override
	public void returnStock(ReservationRequest reservationRequest) {
		String itemHash = reservationRequest.itemHash;
		String reservationHash = reservationRequest.reservationHash;
		StockItemEntity oldItem = repository.loadItem(itemHash);
		StockItemEntity newItem = oldItem.copy();

		/* Return reserved quantity to stock. */
		List<StockItemReservationEntity> newReservations = newItem.reservations;
		for (StockItemReservationEntity newReservation : newReservations) {
			if (reservationHash.equals(newReservation.hash)) {
				newItem.inStockQuantity += newReservation.quantity;
				newReservation.quantity = 0;
			}
		}
		repository.replace(oldItem, newItem);

		eventBus.stockReturned(reservationRequest);
	}

	@Override
	public void confirmStock(ReservationRequest reservationRequest) {
		String itemHash = reservationRequest.itemHash;
		String reservationHash = reservationRequest.reservationHash;
		StockItemEntity oldItem = repository.loadItem(itemHash);
		StockItemEntity newItem = oldItem.copy();

		/* Return reserved quantity to stock. */
		List<StockItemReservationEntity> newReservations = newItem.reservations;
		for (StockItemReservationEntity newReservation : newReservations) {
			if (reservationHash.equals(newReservation.hash)) {
				newReservation.confirmed = true;
			}
		}
		repository.replace(oldItem, newItem);

		eventBus.stockConfirmed(reservationRequest);
	}

}
