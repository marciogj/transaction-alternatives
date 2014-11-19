package udesc.bda.ecommerce;

import java.util.UUID;

public class Payment {
	private String id;
	private String cardNumber;
	private String ownerName;
	
	public Payment(String card, String owner) {
		id = UUID.randomUUID().toString();
		cardNumber = card;
		ownerName = owner;
	}

	public String getId() {
		return id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public String getOwnerName() {
		return ownerName;
	}
	
	
}
