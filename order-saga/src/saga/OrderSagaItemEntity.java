package saga;

public class OrderSagaItemEntity {

	public String itemHash;
	public int quantity;
	public int price;

	/* Activities with stock service. */
	public boolean reserved;
	public boolean outOfStock;
	public boolean returned;
	public boolean confirmed;

	public OrderSagaItemEntity copy() {
		OrderSagaItemEntity newOrderSagaItem = new OrderSagaItemEntity();
		newOrderSagaItem.itemHash = itemHash;
		newOrderSagaItem.quantity = quantity;
		newOrderSagaItem.price = price;
		newOrderSagaItem.reserved = reserved;
		newOrderSagaItem.outOfStock = outOfStock;
		newOrderSagaItem.returned = returned;
		newOrderSagaItem.confirmed = confirmed;
		return newOrderSagaItem;
	}

}
