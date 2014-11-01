package udesc.bda.sql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import udesc.bda.ecommerce.Item;

public class StockDAO {

	public boolean update(List<Item> items, Connection conn) {
		boolean success = true;
		for (Item item : items) {
			success = success && update(item, conn);
		}
		return success;
	}

	private boolean update(Item item, Connection conn) {
		boolean success = true;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "UPDATE stock SET quantity = ? WHERE product_id = ? ";
			pstmt = conn.prepareStatement(query);
			int total = getStockQuantity(item.getProduct().getId(), conn);
			pstmt.setInt(1, total - item.getQuantity());
			pstmt.setString(2, item.getProduct().getId());
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
	
	public int getStockQuantity(String product_id, Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int total = 0;
		try {
			String query = "SELECT quantity FROM stock WHERE product_id = ? ";
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
	
	public boolean save(Item item, Connection conn) {
		boolean success = true;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "INSERT INTO stock (product_id, quantity) VALUES (?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, item.getProduct().getId());
			pstmt.setInt(2, 1000000000);
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
