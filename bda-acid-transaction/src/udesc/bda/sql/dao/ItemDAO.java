package udesc.bda.sql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import udesc.bda.ecommerce.Item;

public class ItemDAO {
	
	public boolean save(List<Item> items, String order_id, Connection conn) {
		boolean success = true;
		for (Item item : items) {
			success = success && save(item, order_id, conn);
		}
		return success;
	}
	
	public boolean save(Item item, String order_id, Connection conn) {
		boolean success = true;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "INSERT INTO items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, order_id);
			pstmt.setString(2, item.getProduct().getId());
			pstmt.setInt(3, item.getQuantity());
			pstmt.setInt(4, item.getUnitPrice());
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
			String query = "DELETE FROM items";
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
