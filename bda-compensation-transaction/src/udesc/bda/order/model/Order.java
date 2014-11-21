package udesc.bda.order.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jongo.marshall.jackson.oid.Id;

import udesc.bda.persistance.DBEntity;

public class Order implements DBEntity {
	@Id	private String _id;
	private Customer customer;
	private Payment payment;
	private long total;
	private long discount;
	private List<Item> items;
 	
	public Order() {
		_id = UUID.randomUUID().toString();
		items = new ArrayList<Item>();
	}
	
	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
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



