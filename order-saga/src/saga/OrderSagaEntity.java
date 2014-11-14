package saga;

import java.util.ArrayList;
import java.util.List;

import order.OrderEntity;
import order.OrderItemEntity;

public class OrderSagaEntity {

	public String orderHash;
	public String customerHash;
	public List<OrderSagaItemEntity> items;

	/* Activities. */
	public PaymentResult paymentResult;
	public OrderResult orderResult;
	public OrderSagaResult sagaResult;

	/* Atomic item update. */
	public int version;

	public OrderSagaEntity copy() {
		OrderSagaEntity newOrderSagaEntity = new OrderSagaEntity();
		newOrderSagaEntity.orderHash = orderHash;
		newOrderSagaEntity.items = new ArrayList<>();
		for (OrderSagaItemEntity item : items) {
			OrderSagaItemEntity newItem = item.copy();
			newOrderSagaEntity.items.add(newItem);
		}
		newOrderSagaEntity.paymentResult = paymentResult;
		newOrderSagaEntity.orderResult = orderResult;
		newOrderSagaEntity.sagaResult = sagaResult;
		newOrderSagaEntity.version = version + 1;
		return newOrderSagaEntity;
	}

	public OrderEntity toOrder() {
		OrderEntity order = new OrderEntity();
		order.hash = orderHash;
		order.items = new ArrayList<>();
		for (OrderSagaItemEntity item : items) {
			OrderItemEntity orderItem = new OrderItemEntity();
			orderItem.hash = item.itemHash;
			orderItem.quantity = item.quantity;
			order.items.add(orderItem);
		}
		return order;
	}

}
