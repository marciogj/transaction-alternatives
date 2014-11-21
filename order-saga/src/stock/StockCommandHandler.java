package stock;

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
		if (repository.reserveItem(reservationRequest)) {
			eventBus.stockReserved(reservationRequest);
		} else {
			eventBus.outOfStock(reservationRequest);
		}
	}

	@Override
	public void returnStock(ReservationRequest reservationRequest) {
		repository.returnStock(reservationRequest);
		eventBus.stockReturned(reservationRequest);
	}

	@Override
	public void confirmStock(ReservationRequest reservationRequest) {
		repository.confirmStock(reservationRequest);
		eventBus.stockConfirmed(reservationRequest);
	}

}
