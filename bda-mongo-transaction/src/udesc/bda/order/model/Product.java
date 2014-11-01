package udesc.bda.order.model;

import org.jongo.marshall.jackson.oid.Id;

public class Product {
	@Id private String _id;
	private String name;
	
	public Product(String id, String aName) {
		name = aName;
		_id = id;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return name;
	}
	
	
}
