package udesc.bda.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;


public class MySQL {
	DataSource dataSource;
	Connection[] connections;
	boolean[] available;
	private static final int MAX_POOL_SIZE = 30;
	int availableCount;
	int inUseCount;

	public MySQL() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost/erp";
			Connection conn;
			int i =0;
			availableCount = MAX_POOL_SIZE;
			inUseCount = 0;
			connections = new Connection[MAX_POOL_SIZE];
			available = new boolean[MAX_POOL_SIZE];
			while(i < 30) {
				conn = DriverManager.getConnection(url, "root", "");
				available[i] = true;
				connections[i] = conn;
				i++;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void release(Connection conn) {
		for(int i=0;i < MAX_POOL_SIZE;i++){
			if (connections[i] == conn) {
				available[i] = true;
				availableCount++;
				inUseCount--;
			}
		}
	}

	public synchronized Optional<Connection> getConnection() throws SQLException {
		for(int i=0;i < MAX_POOL_SIZE;i++){
			if (available[i]) {
				available[i] = false;
				inUseCount++;
				availableCount--;
				printDataSourceStats();
				return Optional.of(connections[i]);
			}
		}
		return Optional.empty();
	}

	private void printDataSourceStats() {
		System.out.println("NumActive: " + inUseCount);
		System.out.println("NumIdle: " + availableCount);
	}

} 

	
		