package udesc.bda.order.persistance.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import udesc.bda.order.queue.OrderRequest;

public class OrderRequestDAO {

	public boolean save(OrderRequest o, Connection conn) {
		boolean success = true;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "INSERT INTO orders_request (id, stock_request, order_id, status) VALUES (?, ?, ?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, o.getId());
			pstmt.setString(2, o.getStockRequest().getId());
			pstmt.setString(3, o.getOrder().getId());
			pstmt.setString(4, o.getStatus().toString());
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
			String query = "DELETE FROM orders_request";
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

	public boolean update(OrderRequest request, Connection conn) {
		boolean success = true;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			String query = "UPDATE orders_request SET status=? WHERE id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, request.getStatus().toString());
			pstmt.setString(2, request.getId());
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
