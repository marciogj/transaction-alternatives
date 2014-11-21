package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import shipping.DeliveryItemRequest;
import shipping.DeliveryRequest;
import shipping.ShippingRepository;

public class MySQLShippingRepository implements ShippingRepository {

	private static final String INSERT_DELIVERY_REQUEST_SQL = "insert into delivery_request (delivery_correlation_hash, customer_hash) values (?, ?)";
	private static final String INSERT_DELIVERY_REQUEST_ITEM_SQL = "insert into delivery_request_item (delivery_correlation_hash, item_hash, quantity) values (?, ?, ?)";

	private MySQLConnectionPool connectionPool;

	public MySQLShippingRepository(MySQLConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public void save(DeliveryRequest deliveryRequest) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement insertDeliveryRequest = connection
						.prepareStatement(INSERT_DELIVERY_REQUEST_SQL)) {
					insertDeliveryRequest.setString(1,
							deliveryRequest.deliveryCorrelationHash);
					insertDeliveryRequest.setString(2,
							deliveryRequest.customerHash);
					insertDeliveryRequest.execute();
				}
				try (PreparedStatement insertDeliveryRequestItem = connection
						.prepareStatement(INSERT_DELIVERY_REQUEST_ITEM_SQL)) {
					insertDeliveryRequestItem.setString(1,
							deliveryRequest.deliveryCorrelationHash);
					for (DeliveryItemRequest item : deliveryRequest.items) {
						insertDeliveryRequestItem.setString(2, item.itemHash);
						insertDeliveryRequestItem.setInt(3, item.quantity);
						insertDeliveryRequestItem.addBatch();
					}
					insertDeliveryRequestItem.executeBatch();
				}
				connection.commit();
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
