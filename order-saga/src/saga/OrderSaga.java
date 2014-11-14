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
		int quantity = reservationRequest.quantity;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository.loadByHash(reservationHash);
		OrderSagaEntity newOrder = oldOrder.copy();
		boolean readyForPayment = true;
		boolean readyForRollback = true;
		int priceSum = 0;
		for (OrderSagaItemEntity newItem : newOrder.items) {
			if (newItem.itemHash.equals(itemHash)
					&& newItem.quantity == quantity) {
				newItem.reserved = true;
			}
			System.out.println("> Item reserved? " + newItem.reserved);
			readyForPayment &= newItem.reserved;
			readyForRollback &= newItem.reserved || newItem.outOfStock;
			priceSum += newItem.price;
		}
		sagaRepository.replace(oldOrder, newOrder);

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
		int quantity = reservationRequest.quantity;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository.loadByHash(reservationHash);
		OrderSagaEntity newOrder = oldOrder.copy();
		boolean readyForRollback = true;
		for (OrderSagaItemEntity newItem : newOrder.items) {
			if (newItem.itemHash.equals(itemHash)
					&& newItem.quantity == quantity) {
				newItem.outOfStock = true;
			}
			readyForRollback &= newItem.reserved || newItem.outOfStock;
		}
		sagaRepository.replace(oldOrder, newOrder);

		if (readyForRollback) {
			rollbackStockReservation(newOrder);
		}
	}

	private void rollbackStockReservation(OrderSagaEntity orderSaga) {
		String orderHash = orderSaga.orderHash;
		for (OrderSagaItemEntity item : orderSaga.items) {
			if (item.reserved) {
				ReservationRequest reservationRequest = new ReservationRequest();
				reservationRequest.reservationHash = orderHash;
				reservationRequest.itemHash = item.itemHash;
				reservationRequest.quantity = item.quantity;
				dependencies.stockService().returnStock(reservationRequest);
			}
		}
	}

	public void stockReturned(ReservationRequest reservationRequest) {
		String reservationHash = reservationRequest.reservationHash;
		String itemHash = reservationRequest.itemHash;
		int quantity = reservationRequest.quantity;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository.loadByHash(reservationHash);
		OrderSagaEntity newOrder = oldOrder.copy();
		boolean readyForCancellation = true;
		for (OrderSagaItemEntity newItem : newOrder.items) {
			if (newItem.itemHash.equals(itemHash)
					&& newItem.quantity == quantity) {
				newItem.returned = true;
			}
			readyForCancellation &= newItem.returned || newItem.outOfStock;
		}
		sagaRepository.replace(oldOrder, newOrder);

		if (readyForCancellation) {
			dependencies.orderService().cancelOrder(reservationHash);
		}
	}

	public void paymentRejected(PaymentRequest paymentRequest) {
		String orderHash = paymentRequest.paymentCorrelationHash;
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository.loadByHash(orderHash);
		OrderSagaEntity newOrder = oldOrder.copy();

		newOrder.paymentResult = PaymentResult.REJECTED;
		sagaRepository.save(newOrder);

		rollbackStockReservation(newOrder);
	}

	public void paymentAccepted(PaymentRequest paymentRequest) {
		String orderHash = paymentRequest.paymentCorrelationHash;
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository.loadByHash(orderHash);
		OrderSagaEntity newOrder = oldOrder.copy();

		newOrder.paymentResult = PaymentResult.ACCEPTED;
		sagaRepository.save(newOrder);

		for (OrderSagaItemEntity newItem : newOrder.items) {
			ReservationRequest reservationRequest = new ReservationRequest();
			reservationRequest.reservationHash = newOrder.orderHash;
			reservationRequest.itemHash = newItem.itemHash;
			reservationRequest.quantity = newItem.quantity;
			dependencies.stockService().confirmStock(reservationRequest);
		}
	}

	public void orderCancelled(String orderHash) {
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository.loadByHash(orderHash);
		OrderSagaEntity newOrder = oldOrder.copy();
		newOrder.orderResult = OrderResult.CANCELLED;
		sagaRepository.replace(oldOrder, newOrder);
		RelationshipEntity message = new RelationshipEntity();
		message.relationshipCorrelationHash = newOrder.orderHash;
		message.message = "Order cancelled";
		dependencies.relationshipService().notifyCustomer(message);
	}

	public void stockConfirmed(ReservationRequest reservationRequest) {
		String reservationHash = reservationRequest.reservationHash;
		String itemHash = reservationRequest.itemHash;
		int quantity = reservationRequest.quantity;

		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository.loadByHash(reservationHash);
		OrderSagaEntity newOrder = oldOrder.copy();
		boolean readyForShipping = true;
		for (OrderSagaItemEntity newItem : newOrder.items) {
			if (newItem.itemHash.equals(itemHash)
					&& newItem.quantity == quantity) {
				newItem.confirmed = true;
			}
			System.out.println("> Item confirmed? " + newItem.confirmed);
			readyForShipping &= newItem.confirmed;
		}
		sagaRepository.replace(oldOrder, newOrder);

		if (readyForShipping) {
			DeliveryRequest deliveryRequest = new DeliveryRequest();
			deliveryRequest.deliveryCorrelationHash = newOrder.orderHash;
			deliveryRequest.customerHash = newOrder.customerHash;
			deliveryRequest.items = new ArrayList<>();
			for (OrderSagaItemEntity newItem : newOrder.items) {
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
		OrderSagaEntity oldOrder = sagaRepository
				.loadByHash(deliveryCorrelationHash);
		OrderSagaEntity newOrder = oldOrder.copy();
		newOrder.orderResult = OrderResult.SCHEDULED;
		sagaRepository.replace(oldOrder, newOrder);
		RelationshipService relationshipService = dependencies
				.relationshipService();
		RelationshipEntity message = new RelationshipEntity();
		message.relationshipCorrelationHash = deliveryCorrelationHash;
		message.message = "Shipping scheduled";
		relationshipService.notifyCustomer(message);
	}

	public void customerNotified(RelationshipEntity relationshipMessage) {
		String relationshipCorrelationHash = relationshipMessage.relationshipCorrelationHash;
		OrderSagaRepository sagaRepository = dependencies.sagaRepository();
		OrderSagaEntity oldOrder = sagaRepository
				.loadByHash(relationshipCorrelationHash);
		OrderSagaEntity newOrder = oldOrder.copy();
		if (OrderResult.CANCELLED.equals(newOrder.orderResult)) {
			newOrder.sagaResult = OrderSagaResult.ROLLBACK;
		} else {
			newOrder.sagaResult = OrderSagaResult.COMMIT;
		}
		sagaRepository.replace(oldOrder, newOrder);

		// done
	}

}
