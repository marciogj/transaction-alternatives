package bda;

import java.util.ArrayList;
import java.util.HashMap;

import mongodb.MongoDBRepository;
import order.OrderCommandHandler;
import order.OrderEventBus;
import order.OrderRepository;
import order.OrderService;
import payment.PaymentCommandHandler;
import payment.PaymentEventBus;
import payment.PaymentService;
import rabbitmq.command.RabbitMQCommandReceiver;
import rabbitmq.command.RabbitMQCommandSender;
import rabbitmq.event.RabbitMQEventBus;
import rabbitmq.event.RabbitMQEventListener;
import relationship.RelationshipCommandHandler;
import relationship.RelationshipEventBus;
import relationship.RelationshipService;
import saga.OrderSaga;
import saga.OrderSagaDependencies;
import saga.OrderSagaEntity;
import saga.OrderSagaItemEntity;
import saga.OrderSagaMetrics;
import saga.OrderSagaRepository;
import shipping.ShippingCommandHandler;
import shipping.ShippingEventBus;
import shipping.ShippingRepository;
import shipping.ShippingService;
import stock.StockCommandHandler;
import stock.StockEventBus;
import stock.StockItemEntity;
import stock.StockRepository;
import stock.StockService;

public class Main {

	private static OrderSagaMetrics metrics = new OrderSagaMetrics();

	public static void main(String[] args) {
		// Switch implementation:
		Repository repository = new MongoDBRepository();
		// Repository repository = new MySQLRepository();

		RabbitMQCommandSender rabbitMQCommandSender = new RabbitMQCommandSender();
		RabbitMQCommandReceiver rabbitMQCommandReceiver = new RabbitMQCommandReceiver();
		RabbitMQEventBus rabbitMQEventBus = new RabbitMQEventBus();
		RabbitMQEventListener rabbitMQEventListener = new RabbitMQEventListener();

		/* Set up order service command handler. */
		OrderEventBus orderEventBus = rabbitMQEventBus.orderEventBus();
		OrderRepository orderRepository = repository.orderRepository();
		OrderCommandHandler orderCommandHandler = new OrderCommandHandler(
				orderEventBus, orderRepository);
		rabbitMQCommandReceiver.addOrderServiceHandler("main",
				orderCommandHandler);

		/* Set up payment service command handler. */
		PaymentEventBus paymentEventBus = rabbitMQEventBus.paymentEventBus();
		PaymentService paymentCommandHandler = new PaymentCommandHandler(
				paymentEventBus);
		rabbitMQCommandReceiver.addPaymentServiceHandler("main",
				paymentCommandHandler);

		/* Set up relationship service command handler. */
		RelationshipEventBus relationshipEventBus = rabbitMQEventBus
				.relationshipEventBus();
		RelationshipService relationshipCommandHandler = new RelationshipCommandHandler(
				relationshipEventBus);
		rabbitMQCommandReceiver.addRelationshipServiceHandler("main",
				relationshipCommandHandler);

		/* Set up shipping service command handler. */
		ShippingRepository shippingRepository = repository.shippingRepository();
		ShippingEventBus shippingEventBus = rabbitMQEventBus.shippingEventBus();
		ShippingService shippingCommandHandler = new ShippingCommandHandler(
				shippingRepository, shippingEventBus);
		rabbitMQCommandReceiver.addShippingServiceHandler("main",
				shippingCommandHandler);

		/* Set up stock service command handler. */
		StockRepository stockRepository = repository.stockRepository();
		StockEventBus stockEventBus = rabbitMQEventBus.stockEventBus();
		StockService stockCommandHandler = new StockCommandHandler(
				stockRepository, stockEventBus);
		rabbitMQCommandReceiver.addStockServiceHandler("main",
				stockCommandHandler);

		/* Set up saga event listener. */
		OrderSagaDependencies orderSagaDependencies = orderSagaDependencies(
				repository, rabbitMQCommandSender);
		OrderSaga orderSaga = new OrderSaga(orderSagaDependencies);
		rabbitMQEventListener.addOrderEventBusHandler("orderSaga", orderSaga);
		rabbitMQEventListener.addPaymentEventBusHandler("orderSaga", orderSaga);
		rabbitMQEventListener.addRelationshipEventBusHandler("orderSaga",
				orderSaga);
		rabbitMQEventListener
				.addShippingEventBusHandler("orderSaga", orderSaga);
		rabbitMQEventListener.addStockEventBusHandler("orderSaga", orderSaga);

		/* Create 10 items with 100 units. */
		for (int itemId = 0; itemId < 10; itemId++) {
			StockItemEntity stockItem = new StockItemEntity();
			stockItem.hash = "Item" + itemId;
			stockItem.inStockQuantity = 100;
			stockItem.reservations = new ArrayList<>();
			stockRepository.save(stockItem);
		}

		/* Place 1 orders of the items. */
		for (int orderId = 0; orderId < 300; orderId++) {
			OrderSagaEntity orderSagaEntity = new OrderSagaEntity();
			orderSagaEntity.version = 0;
			orderSagaEntity.customerHash = "Customer1";
			orderSagaEntity.orderHash = "Order" + orderId;
			orderSagaEntity.items = new HashMap<>();
			for (int itemId = 0; itemId < 10; itemId++) {
				OrderSagaItemEntity item = new OrderSagaItemEntity();
				item.itemHash = "Item" + itemId;
				item.price = 1;
				item.quantity = 1;
				orderSagaEntity.items.put(item.itemHash, item);
			}
			orderSaga.placeOrder(orderSagaEntity);
		}
	}

	private static OrderSagaDependencies orderSagaDependencies(
			Repository mongoDBRepository,
			RabbitMQCommandSender rabbitMQCommandSender) {
		OrderSagaRepository orderSagaRepository = mongoDBRepository
				.orderSagaRepository();
		OrderService orderService = rabbitMQCommandSender.orderService();
		StockService stockService = rabbitMQCommandSender.stockService();
		PaymentService paymentService = rabbitMQCommandSender.paymentService();
		RelationshipService relationshipService = rabbitMQCommandSender
				.relationshipService();
		ShippingService shippingService = rabbitMQCommandSender
				.shippingService();
		OrderSagaDependencies orderSagaDependencies = new OrderSagaDependencies(
				orderSagaRepository, orderService, stockService,
				paymentService, relationshipService, shippingService, metrics);
		return orderSagaDependencies;
	}

}
