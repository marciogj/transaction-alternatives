package bda;

import order.OrderRepository;
import saga.OrderSagaRepository;
import shipping.ShippingRepository;
import stock.StockRepository;

public interface Repository {

	OrderSagaRepository orderSagaRepository();

	OrderRepository orderRepository();

	ShippingRepository shippingRepository();

	StockRepository stockRepository();

}