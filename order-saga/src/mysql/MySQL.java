package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MySQL {

	private static final int MAX_POOL_SIZE = 1;
	private BlockingQueue<Connection> connections = new ArrayBlockingQueue<>(
			MAX_POOL_SIZE);

	private static MySQL instance;

	public static MySQL getInstance() {
		if (instance == null) {
			instance = new MySQL();
		}
		return instance;
	}

	private MySQL() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost/bda";
			for (int i = 0; i < MAX_POOL_SIZE; i++) {
				Connection conn = DriverManager.getConnection(url, "bda",
						"bda123");
				connections.offer(conn);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void release(Connection conn) {
		connections.offer(conn);
	}

	public Connection waitAndGetConnection() {
		try {
			return connections.poll(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}

}
