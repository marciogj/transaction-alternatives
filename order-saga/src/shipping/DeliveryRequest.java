package shipping;

import java.util.List;

public class DeliveryRequest {
	
	public String deliveryCorrelationHash;
	public String customerHash;
	public List<DeliveryItemRequest> items;

}
