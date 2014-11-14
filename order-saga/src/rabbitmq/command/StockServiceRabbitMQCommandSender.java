package rabbitmq.command;

import rabbitmq.AbstractRabbitMQMessageSender;
import stock.ReservationRequest;
import stock.StockService;

import com.rabbitmq.client.ConnectionFactory;

public class StockServiceRabbitMQCommandSender extends
		AbstractRabbitMQMessageSender implements StockService {

	public StockServiceRabbitMQCommandSender(ConnectionFactory factory) {
		super(factory, "stock-service");
	}

	@Override
	public void reserveStock(ReservationRequest reservationRequest) {
		send("reserve-stock", reservationRequest);
	}

	@Override
	public void returnStock(ReservationRequest reservationRequest) {
		send("return-stock", reservationRequest);
	}

	@Override
	public void confirmStock(ReservationRequest reservationRequest) {
		send("confirm-stock", reservationRequest);
	}

}
