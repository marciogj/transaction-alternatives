package rabbitmq.event;

import rabbitmq.command.RabbitMQMessage;
import rabbitmq.command.RabbitMQMessageHandler;
import stock.ReservationRequest;
import stock.StockEventBus;

import com.google.gson.Gson;

public class StockEventBusRabbitMQHandler implements RabbitMQMessageHandler {

	private StockEventBus stockEventBus;

	public StockEventBusRabbitMQHandler(StockEventBus stockEventBus) {
		this.stockEventBus = stockEventBus;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		Gson gson = new Gson();
		ReservationRequest reservationRequest = gson.fromJson(message.message,
				ReservationRequest.class);
		switch (message.intent) {
		case "out-of-stock":
			stockEventBus.outOfStock(reservationRequest);
			break;
		case "stock-confirmed":
			stockEventBus.stockConfirmed(reservationRequest);
			break;
		case "stock-reserved":
			stockEventBus.stockReserved(reservationRequest);
			break;
		case "stock-returned":
			stockEventBus.stockReturned(reservationRequest);
			break;
		default:
			throw new RuntimeException("invalid intent: " + message.intent);
		}
	}

}
