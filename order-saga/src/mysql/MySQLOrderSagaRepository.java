package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import saga.OrderResult;
import saga.OrderSagaEntity;
import saga.OrderSagaItemEntity;
import saga.OrderSagaRepository;
import saga.OrderSagaResult;
import saga.PaymentResult;

public class MySQLOrderSagaRepository implements OrderSagaRepository {

	private static final String INSERT_ORDER_SAGA_SQL = "insert into order_saga (order_hash, customer_hash, payment_result, order_result, saga_result, version) values (?, ?, ?, ?, ?, ?)";
	private static final String INSERT_ORDER_SAGA_ITEM_SQL = "insert into order_saga_item (order_hash, item_hash, quantity, price, reserved, out_of_stock, returned, confirmed) values (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_ORDER_SAGA_SQL = "select customer_hash, payment_result, order_result, saga_result, version from order_saga where order_hash = ?";
	private static final String SELECT_ORDER_SAGA_ITEM_SQL = "select item_hash, quantity, price, reserved, out_of_stock, returned, confirmed from order_saga_item where order_hash = ?";

	private static final String FINISH_SAGA_SQL = "update order_saga set saga_result = case when order_result = 'SCHEDULED' then 'COMMIT' else 'ROLLBACK' end where order_hash = ?";
	private static final String SET_ORDER_RESULT_SQL = "update order_saga set order_result = ? where order_hash = ?";
	private static final String SET_ITEM_STOCK_CONFIRMED_SQL = "update order_saga_item set confirmed = ? where order_hash = ? and item_hash = ?";
	private static final String SET_PAYMENT_RESULT_SQL = "update order_saga set payment_result = ? where order_hash = ?";
	private static final String SET_ITEM_STOCK_RETURNED_SQL = "update order_saga_item set returned = ? where order_hash = ? and item_hash = ?";
	private static final String SET_ITEM_OUT_OF_STOCK_SQL = "update order_saga_item set out_of_stock = ? where order_hash = ? and item_hash = ?";
	private static final String SET_ITEM_RESERVED_SQL = "update order_saga_item set reserved = ? where order_hash = ? and item_hash = ?";
	private static final String SELECT_ITEMS_NOT_RESERVED_OR_OUT_OF_STOCK = "select count(order_hash) from order_saga_item where order_hash = ? and reserved = ? and out_of_stock = ?";
	private static final String SELECT_ITEMS_NOT_RETURNED_OR_OUT_OF_STOCK = "select count(order_hash) from order_saga_item where order_hash = ? and returned = ? and out_of_stock = ?";

	private MySQLConnectionPool connectionPool;

	public MySQLOrderSagaRepository(MySQLConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public void save(OrderSagaEntity orderSaga) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement insertOrderSaga = connection
						.prepareStatement(INSERT_ORDER_SAGA_SQL)) {
					insertOrderSaga.setString(1, orderSaga.orderHash);
					insertOrderSaga.setString(2, orderSaga.customerHash);
					PaymentResult paymentResult = orderSaga.paymentResult;
					insertOrderSaga.setString(3,
							paymentResult != null ? paymentResult.toString()
									: null);
					OrderResult orderResult = orderSaga.orderResult;
					insertOrderSaga
							.setString(
									4,
									orderResult != null ? orderResult
											.toString() : null);
					OrderSagaResult sagaResult = orderSaga.sagaResult;
					insertOrderSaga.setString(5,
							sagaResult != null ? sagaResult.toString() : null);
					insertOrderSaga.setInt(6, orderSaga.version);
					insertOrderSaga.execute();
				}
				try (PreparedStatement insertOrderSagaItem = connection
						.prepareStatement(INSERT_ORDER_SAGA_ITEM_SQL)) {
					insertOrderSagaItem.setString(1, orderSaga.orderHash);
					for (OrderSagaItemEntity item : orderSaga.items.values()) {
						insertOrderSagaItem.setString(2, item.itemHash);
						insertOrderSagaItem.setInt(3, item.quantity);
						insertOrderSagaItem.setInt(4, item.price);
						insertOrderSagaItem.setBoolean(5, item.reserved);
						insertOrderSagaItem.setBoolean(6, item.outOfStock);
						insertOrderSagaItem.setBoolean(7, item.returned);
						insertOrderSagaItem.setBoolean(8, item.confirmed);
						insertOrderSagaItem.addBatch();
					}
					insertOrderSagaItem.executeBatch();
				}
				connection.commit();
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private OrderSagaEntity loadByHash(Connection connection,
			String orderSagaHash) {
		OrderSagaEntity orderSagaEntity = new OrderSagaEntity();
		orderSagaEntity.orderHash = orderSagaHash;
		orderSagaEntity.items = new HashMap<>();

		try {
			try (PreparedStatement selectOrderSaga = connection
					.prepareStatement(SELECT_ORDER_SAGA_SQL)) {
				selectOrderSaga.setString(1, orderSagaHash);
				try (ResultSet rs = selectOrderSaga.executeQuery()) {
					if (!rs.next()) {
						return null;
					}
					orderSagaEntity.customerHash = rs.getString(1);
					String paymentResult = rs.getString(2);
					orderSagaEntity.paymentResult = paymentResult != null ? PaymentResult
							.valueOf(paymentResult) : null;
					String orderResult = rs.getString(3);
					orderSagaEntity.orderResult = orderResult != null ? OrderResult
							.valueOf(orderResult) : null;
					String sagaResult = rs.getString(4);
					orderSagaEntity.sagaResult = sagaResult != null ? OrderSagaResult
							.valueOf(sagaResult) : null;
					orderSagaEntity.version = rs.getInt(5);
				}
			}
			try (PreparedStatement selectOrderSagaItem = connection
					.prepareStatement(SELECT_ORDER_SAGA_ITEM_SQL)) {
				selectOrderSagaItem.setString(1, orderSagaHash);
				try (ResultSet rs = selectOrderSagaItem.executeQuery()) {
					while (rs.next()) {
						OrderSagaItemEntity item = new OrderSagaItemEntity();
						item.itemHash = rs.getString(1);
						item.quantity = rs.getInt(2);
						item.price = rs.getInt(3);
						item.reserved = rs.getBoolean(4);
						item.outOfStock = rs.getBoolean(5);
						item.returned = rs.getBoolean(6);
						item.confirmed = rs.getBoolean(7);
						orderSagaEntity.items.put(item.itemHash, item);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return orderSagaEntity;
	}

	@Override
	public void finishSaga(String orderSagaHash) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement replaceOrderSaga = connection
						.prepareStatement(FINISH_SAGA_SQL)) {
					replaceOrderSaga.setString(1, orderSagaHash);
					replaceOrderSaga.execute();
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
	public void setOrderResult(String orderSagaHash, OrderResult orderResult) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement replaceOrderSaga = connection
						.prepareStatement(SET_ORDER_RESULT_SQL)) {
					replaceOrderSaga.setString(1, orderResult.toString());
					replaceOrderSaga.setString(2, orderSagaHash);
					replaceOrderSaga.execute();
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
	public OrderSagaEntity setItemStockConfirmed(String orderSagaHash,
			String itemHash) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement replaceOrderSaga = connection
						.prepareStatement(SET_ITEM_STOCK_CONFIRMED_SQL)) {
					replaceOrderSaga.setBoolean(1, true);
					replaceOrderSaga.setString(2, orderSagaHash);
					replaceOrderSaga.setString(3, itemHash);
					replaceOrderSaga.execute();
				}
				connection.commit();
				return loadByHashIfAllItemsReservedOrOutOfStock(connection,
						orderSagaHash);
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private OrderSagaEntity loadByHashIfAllItemsReservedOrOutOfStock(
			Connection connection, String orderSagaHash) throws SQLException {
		try (PreparedStatement prepareStatement = connection
				.prepareStatement(SELECT_ITEMS_NOT_RESERVED_OR_OUT_OF_STOCK)) {
			prepareStatement.setString(1, orderSagaHash);
			prepareStatement.setBoolean(2, false);
			prepareStatement.setBoolean(3, false);
			try (ResultSet rs = prepareStatement.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					if (count == 0) {
						return loadByHash(connection, orderSagaHash);
					}
				}
			}
		}
		return null;
	}

	private OrderSagaEntity loadByHashIfAllItemsReturnedOrOutOfStock(
			Connection connection, String orderSagaHash) throws SQLException {
		try (PreparedStatement prepareStatement = connection
				.prepareStatement(SELECT_ITEMS_NOT_RETURNED_OR_OUT_OF_STOCK)) {
			prepareStatement.setString(1, orderSagaHash);
			prepareStatement.setBoolean(2, false);
			prepareStatement.setBoolean(3, false);
			ResultSet rs = prepareStatement.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count == 0) {
					return loadByHash(connection, orderSagaHash);
				}
			}
		}
		return null;
	}

	@Override
	public OrderSagaEntity setPaymentResult(String orderHash,
			PaymentResult paymentResult) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement replaceOrderSaga = connection
						.prepareStatement(SET_PAYMENT_RESULT_SQL)) {
					replaceOrderSaga.setString(1, paymentResult.toString());
					replaceOrderSaga.setString(2, orderHash);
					replaceOrderSaga.execute();
				}
				connection.commit();
				return loadByHash(connection, orderHash);
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OrderSagaEntity setItemStockReturned(String orderHash,
			String itemHash) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement replaceOrderSaga = connection
						.prepareStatement(SET_ITEM_STOCK_RETURNED_SQL)) {
					replaceOrderSaga.setBoolean(1, true);
					replaceOrderSaga.setString(2, orderHash);
					replaceOrderSaga.setString(3, itemHash);
					replaceOrderSaga.execute();
				}
				connection.commit();
				return loadByHashIfAllItemsReturnedOrOutOfStock(connection,
						orderHash);
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OrderSagaEntity setItemOutOfStock(String orderHash, String itemHash) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement replaceOrderSaga = connection
						.prepareStatement(SET_ITEM_OUT_OF_STOCK_SQL)) {
					replaceOrderSaga.setBoolean(1, true);
					replaceOrderSaga.setString(2, orderHash);
					replaceOrderSaga.setString(3, itemHash);
					replaceOrderSaga.execute();
				}
				connection.commit();
				return loadByHashIfAllItemsReturnedOrOutOfStock(connection,
						orderHash);
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OrderSagaEntity setStockReserved(String orderHash, String itemHash) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement replaceOrderSaga = connection
						.prepareStatement(SET_ITEM_RESERVED_SQL)) {
					replaceOrderSaga.setBoolean(1, true);
					replaceOrderSaga.setString(2, orderHash);
					replaceOrderSaga.setString(3, itemHash);
					replaceOrderSaga.execute();
				}
				connection.commit();
				return loadByHashIfAllItemsReservedOrOutOfStock(connection,
						orderHash);
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
