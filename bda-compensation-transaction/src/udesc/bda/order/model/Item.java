package udesc.bda.order.model;


public class Item {
	private Product product;
	private int quantity;
	private int unit_price;

	public Item(Product p, int unit_price, int qtd) {
		product = p;
		quantity = qtd;
	}

	public Product getProduct() {
		return product;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public int getUnitPrice(){
		return unit_price;
	}

}
