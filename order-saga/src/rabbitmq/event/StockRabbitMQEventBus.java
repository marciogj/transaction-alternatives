package rabbitmq.event;

import rabbitmq.AbstractRabbitMQMessageSender;
import stock.ReservationRequest;
import stock.StockEventBus;

import com.rabbitmq.client.ConnectionFactory;

public class StockRabbitMQEventBus extends AbstractRabbitMQMessageSender
		implements StockEventBus {

	public StockRabbitMQEventBus(ConnectionFactory factory) {
		super(factory, "stock-event");
	}

	@Override
	public void outOfStock(ReservationRequest reservationRequest) {
		send("out-of-stock", reservationRequest);
	}

	@Override
	public void stockReserved(ReservationRequest reservationRequest) {
		send("stock-reserved", reservationRequest);
	}

	@Override
	public void stockReturned(ReservationRequest reservationRequest) {
		send("stock-returned", reservationRequest);
	}

	@Override
	public void stockConfirmed(ReservationRequest reservationRequest) {
		send("stock-confirmed", reservationRequest);
	}

}
