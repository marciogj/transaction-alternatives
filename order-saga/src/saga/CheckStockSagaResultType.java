package saga;

public enum CheckStockSagaResultType {

	READY_FOR_PAYMENT,

	READY_FOR_ROLLBACK,

	WAIT;

}
