package udesc.bda.sql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import udesc.bda.ecommerce.Item;

public class StockDAO {

	public List<Integer> update(List<Item> items, Connection conn) {
		List<Integer> total = new ArrayList<Integer>();
		for (Item item : items) {
			total.add(new Integer(update(item, conn)));
		}
		return total;
	}

	private int update(Item item, Connection conn) {
		int newTotal = -1;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "UPDATE stock SET quantity = ? WHERE product_id = ? ";
			pstmt = conn.prepareStatement(query);
			int total = getStockQuantity(item.getProduct().getId(), conn);
			newTotal = total - item.getQuantity();
			pstmt.setInt(1, newTotal);
			pstmt.setString(2, item.getProduct().getId());
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
