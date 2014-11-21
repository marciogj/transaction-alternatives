package udesc.bda.stock.persistance.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import udesc.bda.stock.model.StockItem;

public class StockDAO {

	public List<Integer> update(List<StockItem> items, Connection conn) {
		List<Integer> total = new ArrayList<Integer>();
		for (StockItem item : items) {
			total.add(new Integer(update(item, conn)));
		}
		return total;
	}

	public int update(StockItem item, Connection conn) {
		int newTotal = -1;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "UPDATE stock SET quantity = ? WHERE id = ? ";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, item.getQuantity());
			pstmt.setString(2, item.getId());
			pstmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (rset != null) rset.close();   } catch(Exception e) { e.printStackTrace(); }
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { e.printStackTrace(); }
		}

		return newTotal;
	}
	
	public int getStockQuantity(String product_id, Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int total = 0;
		try {
			String query = "SELECT quantity FROM stock WHERE id = ? ";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, product_id);
			ResultSet rs = pstmt.executeQuery();
		
			if (rs.next()) {
				total = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (rset != null) rset.close();   } catch(Exception e) { e.printStackTrace(); }
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { e.printStackTrace(); }
		}

		return total;
	}
	
	public boolean save(StockItem item, Connection conn) {
		boolean success = true;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "INSERT INTO stock (id, quantity) VALUES (?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, item.getId());
			pstmt.setInt(2, item.getQuantity());
			pstmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success = false;
		} finally {
			try { if (rset != null) rset.close();   } catch(Exception e) { e.printStackTrace(); }
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { e.printStackTrace(); }
		}

		return success;
	}

	public boolean deleteAll(Connection conn) {
		boolean success = true;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "DELETE FROM stock";
			pstmt = conn.prepareStatement(query);
			pstmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success = false;
		} finally {
			try { if (rset != null) rset.close();   } catch(Exception e) { e.printStackTrace(); }
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { e.printStackTrace(); }
		}

		return success;
	}

}
