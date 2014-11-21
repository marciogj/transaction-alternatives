package saga;

import java.util.ArrayList;
import java.util.List;

import order.OrderEntity;
import order.OrderEventBus;
import order.OrderItemEntity;
import payment.PaymentEventBus;
import payment.PaymentRequest;
import relationship.RelationshipEntity;
import relationship.RelationshipEventBus;
import relationship.RelationshipService;
import shipping.DeliveryItemRequest;
import shipping.DeliveryRequest;
import shipping.ShippingEventBus;
import stock.ReservationRequest;
import stock.StockEventBus;

public class OrderSaga implements OrderEventBus, PaymentEventBus,
		RelationshipEventBus, ShippingEventBus, StockEventBus {

	private OrderSagaDependencies dependencies;

	public OrderSaga(OrderSagaDependencies dependencies) {
		this.dependencies = dependencies;
	}

	public void placeOrder(OrderSagaEntity orderSaga) {
		dependencies.metrics().beginOrder(orderSaga.orderHash);
		dependencies.sagaRepository().save(orderSaga);
		OrderEntity order = orderSaga.toOrder();
		dependencies.orderService().placeOrder(order);
	}

	public void orderPlaced(OrderEntity order) {
		String orderHash = order.hash;
		List<OrderItemEntity> items = order.items;
		for (OrderItemEntity item : items) {
			ReservationRequest reservationRequest = new ReservationRequest();
			reservationRequest.reservationHash = orderHash;
			reservationRequest.itemHash = item.hash;
			reservationRequest.quantity = item.quantity;
			dependencies.stockService().reserveStock(reservationRequest);
		}
	}

	public void stockReserved(ReservationRequest reservationRequest) {
		String reservationHash = reservationRequest.reservationHash;
		String itemHash = reservationRequest.itemHash;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity newOrder = sagaRepository.setStockReserved(
				reservationHash, itemHash);

		if (newOrder == null) {
			return;
		}

		boolean readyForPayment = true;
		boolean readyForRollback = true;
		int priceSum = 0;
		for (OrderSagaItemEntity newItem : newOrder.items.values()) {
			readyForPayment &= newItem.reserved;
			readyForRollback &= newItem.reserved || newItem.outOfStock;
			priceSum += newItem.price;
		}

		if (readyForPayment) {
			PaymentRequest paymentRequest = new PaymentRequest();
			paymentRequest.customerHash = newOrder.customerHash;
			paymentRequest.paymentCorrelationHash = newOrder.orderHash;
			paymentRequest.price = priceSum;
			dependencies.paymentService().requestPayment(paymentRequest);
		} else if (readyForRollback) {
			rollbackStockReservation(newOrder);
		}
	}

	public void outOfStock(ReservationRequest reservationRequest) {
		String reservationHash = reservationRequest.reservationHash;
		String itemHash = reservationRequest.itemHash;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity newOrder = sagaRepository.setItemOutOfStock(
				reservationHash, itemHash);

		if (newOrder == null) {
			return;
		}

		boolean readyForRollback = true;
		for (OrderSagaItemEntity newItem : newOrder.items.values()) {
			readyForRollback &= newItem.reserved || newItem.outOfStock;
		}

		if (readyForRollback) {
			rollbackStockReservation(newOrder);
		}
	}

	private void rollbackStockReservation(OrderSagaEntity orderSaga) {
		String orderHash = orderSaga.orderHash;
		boolean anyReturned = false;
		for (OrderSagaItemEntity item : orderSaga.items.values()) {
			if (item.reserved) {
				ReservationRequest reservationRequest = new ReservationRequest();
				reservationRequest.reservationHash = orderHash;
				reservationRequest.itemHash = item.itemHash;
				reservationRequest.quantity = item.quantity;
				dependencies.stockService().returnStock(reservationRequest);
				anyReturned = true;
			}
		}
		if (!anyReturned) {
			// No items were reserved, we can cancel the order
			dependencies.orderService().cancelOrder(orderSaga.orderHash);
		}
	}

	public void stockReturned(ReservationRequest reservationRequest) {
		String reservationHash = reservationRequest.reservationHash;
		String itemHash = reservationRequest.itemHash;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();

		OrderSagaEntity newOrder = sagaRepository.setItemStockReturned(
				reservationHash, itemHash);

		if (newOrder == null) {
			return;
		}

		boolean readyForCancellation = true;
		for (OrderSagaItemEntity newItem : newOrder.items.values()) {
			readyForCancellation &= newItem.returned || newItem.outOfStock;
		}

		if (readyForCancellation) {
			dependencies.orderService().cancelOrder(reservationHash);
		}
	}

	public void paymentRejected(PaymentRequest paymentRequest) {
		String orderHash = paymentRequest.paymentCorrelationHash;
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();

		OrderSagaEntity newOrder = sagaRepository.setPaymentResult(orderHash,
				PaymentResult.REJECTED);

		rollbackStockReservation(newOrder);
	}

	public void paymentAccepted(PaymentRequest paymentRequest) {
		String orderHash = paymentRequest.paymentCorrelationHash;
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();

		OrderSagaEntity newOrder = sagaRepository.setPaymentResult(orderHash,
				PaymentResult.ACCEPTED);

		for (OrderSagaItemEntity newItem : newOrder.items.values()) {
			ReservationRequest reservationRequest = new ReservationRequest();
			reservationRequest.reservationHash = newOrder.orderHash;
			reservationRequest.itemHash = newItem.itemHash;
			reservationRequest.quantity = newItem.quantity;
			dependencies.stockService().confirmStock(reservationRequest);
		}
	}

	public void orderCancelled(String orderHash) {
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		sagaRepository.setOrderResult(orderHash, OrderResult.CANCELLED);
		RelationshipEntity message = new RelationshipEntity();
		message.relationshipCorrelationHash = orderHash;
		message.message = "Order cancelled";
		notifyCustomer(message);
	}

	public void stockConfirmed(ReservationRequest reservationRequest) {
		String reservationHash = reservationRequest.reservationHash;
		String itemHash = reservationRequest.itemHash;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();

		OrderSagaEntity newOrder = sagaRepository.setItemStockConfirmed(
				reservationHash, itemHash);

		if (newOrder == null) {
			return;
		}

		boolean readyForShipping = true;
		for (OrderSagaItemEntity newItem : newOrder.items.values()) {
			readyForShipping &= newItem.confirmed;
		}

		if (readyForShipping) {
			DeliveryRequest deliveryRequest = new DeliveryRequest();
			deliveryRequest.deliveryCorrelationHash = newOrder.orderHash;
			deliveryRequest.customerHash = newOrder.customerHash;
			deliveryRequest.items = new ArrayList<>();
			for (OrderSagaItemEntity newItem : newOrder.items.values()) {
				DeliveryItemRequest deliveryItem = new DeliveryItemRequest();
				deliveryItem.itemHash = newItem.itemHash;
				deliveryItem.quantity = newItem.quantity;
				deliveryRequest.items.add(deliveryItem);
			}
			dependencies.shippingService().requestDelivery(deliveryRequest);
		}
	}

	public void deliveryScheduled(DeliveryRequest deliveryRequest) {
		String deliveryCorrelationHash = deliveryRequest.deliveryCorrelationHash;
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		sagaRepository.setOrderResult(deliveryCorrelationHash,
				OrderResult.SCHEDULED);
		RelationshipEntity message = new RelationshipEntity();
		message.relationshipCorrelationHash = deliveryCorrelationHash;
		message.message = "Shipping scheduled";
		notifyCustomer(message);
	}

	private void notifyCustomer(RelationshipEntity message) {
		dependencies.metrics().endOrder(message.relationshipCorrelationHash);
		RelationshipService relationshipService = dependencies
				.relationshipService();
		relationshipService.notifyCustomer(message);
	}

	public void customerNotified(RelationshipEntity relationshipMessage) {
		String relationshipCorrelationHash = relationshipMessage.relationshipCorrelationHash;
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		sagaRepository.finishSaga(relationshipCorrelationHash);

		// done
	}

}
