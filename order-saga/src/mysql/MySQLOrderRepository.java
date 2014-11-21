package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import order.OrderEntity;
import order.OrderItemEntity;
import order.OrderRepository;

public class MySQLOrderRepository implements OrderRepository {

	private static final String INSERT_ORDER_SQL = "insert into `order` (hash) values (?)";
	private static final String INSERT_ORDER_ITEM_SQL = "insert into order_item (order_hash, item_hash, quantity) values (?, ?, ?)";
	private static final String DELETE_ORDER_SQL = "delete from `order` where hash = ?";
	private static final String DELETE_ORDER_ITEM_SQL = "delete from order_item where order_hash = ?";
	
	private MySQLConnectionPool connectionPool;

	public MySQLOrderRepository(MySQLConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public void save(OrderEntity order) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement insertOrder = connection
						.prepareStatement(INSERT_ORDER_SQL)) {
					insertOrder.setString(1, order.hash);
					insertOrder.execute();
				}
				try (PreparedStatement insertItem = connection
						.prepareStatement(INSERT_ORDER_ITEM_SQL)) {
					insertItem.setString(1, order.hash);
					for (OrderItemEntity item : order.items) {
						insertItem.setString(2, item.hash);
						insertItem.setInt(3, item.quantity);
						insertItem.addBatch();
					}
					insertItem.executeBatch();
				}
				connection.commit();
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteByHash(String orderHash) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement deleteOrderItem = connection
						.prepareStatement(DELETE_ORDER_ITEM_SQL)) {
					deleteOrderItem.setString(1, orderHash);
					deleteOrderItem.execute();
				}
				try (PreparedStatement deleteOrder = connection
						.prepareStatement(DELETE_ORDER_SQL)) {
					deleteOrder.setString(1, orderHash);
					deleteOrder.execute();
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
