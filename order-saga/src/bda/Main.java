package bda;

import java.util.ArrayList;

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

	public static void main(String[] args) {
		MongoDBRepository mongoDBRepository = new MongoDBRepository();
		RabbitMQCommandSender rabbitMQCommandSender = new RabbitMQCommandSender();
		RabbitMQCommandReceiver rabbitMQCommandReceiver = new RabbitMQCommandReceiver();
		RabbitMQEventBus rabbitMQEventBus = new RabbitMQEventBus();
		RabbitMQEventListener rabbitMQEventListener = new RabbitMQEventListener();

		/* Set up order service command handler. */
		OrderEventBus orderEventBus = rabbitMQEventBus.orderEventBus();
		OrderRepository orderRepository = mongoDBRepository.orderRepository();
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
		ShippingRepository shippingRepository = mongoDBRepository
				.shippingRepository();
		ShippingEventBus shippingEventBus = rabbitMQEventBus.shippingEventBus();
		ShippingService shippingCommandHandler = new ShippingCommandHandler(
				shippingRepository, shippingEventBus);
		rabbitMQCommandReceiver.addShippingServiceHandler("main",
				shippingCommandHandler);

		/* Set up stock service command handler. */
		StockRepository stockRepository = mongoDBRepository.stockRepository();
		StockEventBus stockEventBus = rabbitMQEventBus.stockEventBus();
		StockService stockCommandHandler = new StockCommandHandler(
				stockRepository, stockEventBus);
		rabbitMQCommandReceiver.addStockServiceHandler("main",
				stockCommandHandler);

		/* Set up saga event listener. */
		OrderSagaDependencies orderSagaDependencies = orderSagaDependencies(
				mongoDBRepository, rabbitMQCommandSender);
		OrderSaga orderSaga = new OrderSaga(orderSagaDependencies);
		rabbitMQEventListener.addOrderEventBusHandler("orderSaga", orderSaga);
		rabbitMQEventListener.addPaymentEventBusHandler("orderSaga", orderSaga);
		rabbitMQEventListener.addRelationshipEventBusHandler("orderSaga",
				orderSaga);
		rabbitMQEventListener
				.addShippingEventBusHandler("orderSaga", orderSaga);
		rabbitMQEventListener.addStockEventBusHandler("orderSaga", orderSaga);

		StockItemEntity stockItem0 = new StockItemEntity();
		stockItem0.hash = "Item0";
		stockItem0.inStockQuantity = 10;
		stockItem0.reservations = new ArrayList<>();
		stockRepository.save(stockItem0);
		StockItemEntity stockItem1 = new StockItemEntity();
		stockItem1.hash = "Item1";
		stockItem1.inStockQuantity = 10;
		stockItem1.reservations = new ArrayList<>();
		stockRepository.save(stockItem1);
		StockItemEntity stockItem2 = new StockItemEntity();
		stockItem2.hash = "Item2";
		stockItem2.inStockQuantity = 10;
		stockItem2.reservations = new ArrayList<>();
		stockRepository.save(stockItem2);

		/* Place order. */
		OrderSagaEntity orderSagaEntity = new OrderSagaEntity();
		orderSagaEntity.version = 0;
		orderSagaEntity.customerHash = "Customer1";
		orderSagaEntity.orderHash = "Order1";
		orderSagaEntity.items = new ArrayList<>();
		OrderSagaItemEntity item0 = new OrderSagaItemEntity();
		item0.itemHash = "Item0";
		item0.price = 150;
		item0.quantity = 2;
		orderSagaEntity.items.add(item0);
		OrderSagaItemEntity item1 = new OrderSagaItemEntity();
		item1.itemHash = "Item1";
		item1.price = 100;
		item1.quantity = 3;
		orderSagaEntity.items.add(item1);
		OrderSagaItemEntity item2 = new OrderSagaItemEntity();
		item2.itemHash = "Item2";
		item2.price = 50;
		item2.quantity = 7;
		orderSagaEntity.items.add(item2);
		orderSaga.placeOrder(orderSagaEntity);
	}

	private static OrderSagaDependencies orderSagaDependencies(
			MongoDBRepository mongoDBRepository,
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
				paymentService, relationshipService, shippingService);
		return orderSagaDependencies;
	}

}
