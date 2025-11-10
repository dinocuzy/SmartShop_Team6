package orderstatushistorydao;

import model.OrderStatusHistory;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation của IOrderStatusHistoryDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class OrderStatusHistoryDAO implements IOrderStatusHistoryDAO {
    
    @Override
    public int recordStatusChange(int orderID, String oldStatus, String newStatus, Integer changedBy) {
        String sql = "INSERT INTO OrderStatusHistory (OrderID, OldStatus, NewStatus, ChangedBy, ChangedAt) " +
                     "VALUES (?, ?, ?, ?, SYSDATETIME())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, orderID);
            ps.setString(2, oldStatus);
            ps.setString(3, newStatus);
            if (changedBy != null) {
                ps.setInt(4, changedBy);
            } else {
                ps.setObject(4, null);
            }
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error recording order status change: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    @Override
    public List<OrderStatusHistory> getHistoryByOrderID(int orderID) {
        List<OrderStatusHistory> historyList = new ArrayList<>();
        String sql = "SELECT HistoryID, OrderID, OldStatus, NewStatus, ChangedAt, ChangedBy " +
                     "FROM OrderStatusHistory " +
                     "WHERE OrderID = ? " +
                     "ORDER BY ChangedAt ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderStatusHistory history = mapResultSetToHistory(rs);
                    historyList.add(history);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order status history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historyList;
    }
    
    @Override
    public OrderStatusHistory getById(int historyID) {
        String sql = "SELECT HistoryID, OrderID, OldStatus, NewStatus, ChangedAt, ChangedBy " +
                     "FROM OrderStatusHistory " +
                     "WHERE HistoryID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, historyID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHistory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order status history by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public int deleteByOrderID(int orderID) {
        String sql = "DELETE FROM OrderStatusHistory WHERE OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting order status history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet sang OrderStatusHistory object
     */
    private OrderStatusHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setHistoryID(rs.getInt("HistoryID"));
        history.setOrderID(rs.getInt("OrderID"));
        history.setOldStatus(rs.getString("OldStatus"));
        history.setNewStatus(rs.getString("NewStatus"));
        
        Timestamp changedAt = rs.getTimestamp("ChangedAt");
        if (changedAt != null) {
            history.setChangedAt(new Date(changedAt.getTime()));
        }
        
        Integer changedBy = rs.getInt("ChangedBy");
        if (rs.wasNull()) {
            changedBy = null;
        }
        history.setChangedBy(changedBy);
        
        return history;
    }
}

