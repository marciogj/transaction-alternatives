package rabbitmq.command;

import stock.ReservationRequest;
import stock.StockService;

import com.google.gson.Gson;

public class StockServiceRabbitMQHandler implements RabbitMQMessageHandler {

	private StockService stockService;

	public StockServiceRabbitMQHandler(StockService stockService) {
		this.stockService = stockService;
	}

	@Override
	public void handle(RabbitMQMessage message) {
		Gson gson = new Gson();
		ReservationRequest reservationRequest = gson.fromJson(message.message,
				ReservationRequest.class);
		switch (message.intent) {
		case "confirm-stock":
			stockService.confirmStock(reservationRequest);
			break;
		case "reserve-stock":
			stockService.reserveStock(reservationRequest);
			break;
		case "return-stock":
			stockService.returnStock(reservationRequest);
			break;
		default:
			throw new RuntimeException("invalid intent: " + message.intent);
		}
	}
}
