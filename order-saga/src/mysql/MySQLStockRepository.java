package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import stock.ReservationRequest;
import stock.StockItemEntity;
import stock.StockItemReservationEntity;
import stock.StockRepository;

public class MySQLStockRepository implements StockRepository {

	private static final String INSERT_STOCK_ITEM_SQL = "insert into stock_item (hash, in_stock_quantity, version) values (?, ?, ?)";
	private static final String INSERT_STOCK_ITEM_RESERVATION_SQL = "insert into stock_item_reservation (item_hash, reservation_hash, quantity) values (?, ?, ?)";
	private static final String RESERVE_ITEM_SQL = INSERT_STOCK_ITEM_RESERVATION_SQL;
	private static final String DELETE_RESERVE_ITEM_SQL = "delete from stock_item_reservation where item_hash = ? and reservation_hash = ?";
	private static final String DECREASE_ITEM_STOCK_SQL = "update stock_item set in_stock_quantity = in_stock_quantity - ? where hash = ? and in_stock_quantity >= ?";
	private static final String INCREATE_STOCK_QUANTITY_SQL = "update stock_item set in_stock_quantity = in_stock_quantity + ? where hash = ?";

	private MySQLConnectionPool connectionPool;

	public MySQLStockRepository(MySQLConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public void save(StockItemEntity item) {
		try {
			Connection connection = connectionPool.connection();
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement insertStockItem = connection
						.prepareStatement(INSERT_STOCK_ITEM_SQL)) {
					insertStockItem.setString(1, item.hash);
					insertStockItem.setInt(2, item.inStockQuantity);
					insertStockItem.setInt(3, item.version);
					insertStockItem.execute();
				}
				try (PreparedStatement insertStockItemReservation = connection
						.prepareStatement(INSERT_STOCK_ITEM_RESERVATION_SQL)) {
					insertStockItemReservation.setString(1, item.hash);
					for (StockItemReservationEntity reservation : item.reservations) {
						insertStockItemReservation.setString(2,
								reservation.hash);
						insertStockItemReservation.setInt(3,
								reservation.quantity);
						insertStockItemReservation.addBatch();
					}
					insertStockItemReservation.executeBatch();
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
	public boolean reserveItem(ReservationRequest reservationRequest) {
		Connection connection = connectionPool.connection();
		try {
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement prepareStatement = connection
						.prepareStatement(DECREASE_ITEM_STOCK_SQL)) {
					prepareStatement.setInt(1, reservationRequest.quantity);
					prepareStatement.setString(2, reservationRequest.itemHash);
					prepareStatement.setInt(3, reservationRequest.quantity);
					int affectedRows = prepareStatement.executeUpdate();
					if (affectedRows < 1) {
						return false;
					}
				}
				try (PreparedStatement prepareStatement = connection
						.prepareStatement(RESERVE_ITEM_SQL)) {
					prepareStatement.setString(1, reservationRequest.itemHash);
					prepareStatement.setString(2,
							reservationRequest.reservationHash);
					prepareStatement.setInt(3, reservationRequest.quantity);
					prepareStatement.execute();
				}
				connection.commit();
				return true;
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void returnStock(ReservationRequest reservationRequest) {
		Connection connection = connectionPool.connection();
		try {
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement prepareStatement = connection
						.prepareStatement(DELETE_RESERVE_ITEM_SQL)) {
					prepareStatement.setString(1, reservationRequest.itemHash);
					prepareStatement.setString(2,
							reservationRequest.reservationHash);
					int affectedRows = prepareStatement.executeUpdate();
					if (affectedRows < 1) {
						return;
					}
				}
				try (PreparedStatement prepareStatement = connection
						.prepareStatement(INCREATE_STOCK_QUANTITY_SQL)) {
					prepareStatement.setInt(1, reservationRequest.quantity);
					prepareStatement.setString(2, reservationRequest.itemHash);
					prepareStatement.execute();
				}
				connection.commit();
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	public void confirmStock(ReservationRequest reservationRequest) {
		Connection connection = connectionPool.connection();
		try {
			try {
				connection.setAutoCommit(false);
				try (PreparedStatement prepareStatement = connection
						.prepareStatement(DELETE_RESERVE_ITEM_SQL)) {
					prepareStatement.setString(1, reservationRequest.itemHash);
					prepareStatement.setString(2,
							reservationRequest.reservationHash);
					prepareStatement.execute();
				}
				connection.commit();
			} finally {
				connectionPool.close(connection);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}

}
