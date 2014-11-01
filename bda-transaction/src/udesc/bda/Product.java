package udesc.bda;

public class Product {
	private String id;
	private String name;
	
	public Product(String aName) {
		name = aName;
		id = ""+aName.hashCode();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	
}
