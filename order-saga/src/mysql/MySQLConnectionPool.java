package mysql;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnectionPool {

	public Connection connection() {
		return MySQL.getInstance().waitAndGetConnection();
	}

	public void close(Connection connection) {
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		MySQL.getInstance().release(connection);
	}

}
