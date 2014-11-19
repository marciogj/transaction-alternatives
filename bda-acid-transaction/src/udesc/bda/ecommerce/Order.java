package udesc.bda.ecommerce;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import udesc.bda.Customer;

public class Order {
	private String id;
	private Customer customer;
	private Payment payment;
	private long total;
	private long discount;
	List<Item> items;
	
	public Order() {
		id = UUID.randomUUID().toString();
		items = new ArrayList<Item>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getDiscount() {
		return discount;
	}

	public void setDiscount(long discount) {
		this.discount = discount;
	}
	
	public void add(Item item) {
		items.add(item);
	}

	public List<Item> getItems() {
		return items;
	}
	
	
	
}
