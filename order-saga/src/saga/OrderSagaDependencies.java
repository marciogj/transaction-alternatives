package saga;

import order.OrderService;
import payment.PaymentService;
import relationship.RelationshipService;
import shipping.ShippingService;
import stock.StockService;

public class OrderSagaDependencies {

	private OrderSagaRepository orderSagaRepository;
	private OrderService orderService;
	private StockService stockService;
	private PaymentService paymentService;
	private RelationshipService relationshipService;
	private ShippingService shippingService;
	private OrderSagaMetrics metrics;

	public OrderSagaDependencies(
			OrderSagaRepository orderSagaRepository,
			OrderService orderService, 
			StockService stockService,
			PaymentService paymentService,
			RelationshipService relationshipService,
			ShippingService shippingService,
			OrderSagaMetrics metrics) {
		this.orderSagaRepository = orderSagaRepository;
		this.orderService = orderService;
		this.stockService = stockService;
		this.paymentService = paymentService;
		this.relationshipService = relationshipService;
		this.shippingService = shippingService;
		this.metrics = metrics;
	}

	public OrderSagaRepository sagaRepository() {
		return orderSagaRepository;
	}

	public OrderService orderService() {
		return orderService;
	}

	public StockService stockService() {
		return stockService;
	}

	public PaymentService paymentService() {
		return paymentService;
	}

	public RelationshipService relationshipService() {
		return relationshipService;
	}

	public ShippingService shippingService() {
		return shippingService;
	}

	public OrderSagaMetrics metrics() {
		return metrics;
	}

}
