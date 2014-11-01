package udesc.bda.order.model;

public class Customer {
	private String id;
	private String name;
	
	public Customer(String aName) {
		name = aName;
		id = name.hashCode()+"";
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

}
