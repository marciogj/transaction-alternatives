package relationship;

public class RelationshipCommandHandler implements RelationshipService {
	
	private RelationshipEventBus eventBus;

	public RelationshipCommandHandler(RelationshipEventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void notifyCustomer(RelationshipEntity message) {
		
		/* Notify customer somehow... */
		
		eventBus.customerNotified(message);
	}
	
}
