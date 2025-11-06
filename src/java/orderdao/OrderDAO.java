package orderdao;

import model.Order;
import util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO implements IOrderDAO {
    
    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderStatus, o.OrderDate, " +
                     "u.FullName AS UserName, p.PaymentStatus " +
                     "FROM Orders o " +
                     "LEFT JOIN Users u ON o.UserID = u.UserID " +
                     "LEFT JOIN Payments p ON o.OrderID = p.OrderID " +
                     "ORDER BY o.OrderID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setUserName(rs.getString("UserName"));
                order.setPaymentStatus(rs.getString("PaymentStatus"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    
    @Override
    public Order getById(int orderID) {
        String sql = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderStatus, o.OrderDate, " +
                     "u.FullName AS UserName, p.PaymentStatus " +
                     "FROM Orders o " +
                     "LEFT JOIN Users u ON o.UserID = u.UserID " +
                     "LEFT JOIN Payments p ON o.OrderID = p.OrderID " +
                     "WHERE o.OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setUserName(rs.getString("UserName"));
                    order.setPaymentStatus(rs.getString("PaymentStatus"));
                    return order;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Order> getByUser(int userID) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderStatus, o.OrderDate, " +
                     "u.FullName AS UserName, p.PaymentStatus " +
                     "FROM Orders o " +
                     "LEFT JOIN Users u ON o.UserID = u.UserID " +
                     "LEFT JOIN Payments p ON o.OrderID = p.OrderID " +
                     "WHERE o.UserID = ? ORDER BY o.OrderID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setUserName(rs.getString("UserName"));
                    order.setPaymentStatus(rs.getString("PaymentStatus"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by user: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    
    @Override
    public List<Order> getByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderStatus, o.OrderDate, " +
                     "u.FullName AS UserName, p.PaymentStatus " +
                     "FROM Orders o " +
                     "LEFT JOIN Users u ON o.UserID = u.UserID " +
                     "LEFT JOIN Payments p ON o.OrderID = p.OrderID " +
                     "WHERE o.OrderStatus = ? ORDER BY o.OrderID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setUserName(rs.getString("UserName"));
                    order.setPaymentStatus(rs.getString("PaymentStatus"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by status: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    
    @Override
    public List<Order> getPagedOrders(int pageNumber, int pageSize, String sortBy, String sortOrder,
                                      String searchKeyword, String status, int userID) {
        List<Order> orders = new ArrayList<>();
        
        // Validate và set defaults
        if (pageNumber < 1) pageNumber = 1;
        if (pageSize < 1) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "OrderID";
        if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "ASC";
        
        // Validate sortBy để tránh SQL injection
        String[] allowedColumns = {"OrderID", "OrderDate", "TotalAmount", "OrderStatus"};
        boolean isValid = false;
        for (String col : allowedColumns) {
            if (sortBy.equalsIgnoreCase(col)) {
                sortBy = col;
                isValid = true;
                break;
            }
        }
        if (!isValid) sortBy = "OrderID";
        
        if (!sortOrder.equalsIgnoreCase("DESC")) sortOrder = "ASC";
        
        // Build WHERE clause
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            whereClause.append("u.FullName LIKE ?");
            params.add("%" + searchKeyword.trim() + "%");
        }
        
        if (status != null && !status.trim().isEmpty()) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("o.OrderStatus = ?");
            params.add(status.trim());
        }
        
        if (userID > 0) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("o.UserID = ?");
            params.add(userID);
        }
        
        String where = whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "";
        
        // Build SQL với OFFSET/FETCH (SQL Server pagination)
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT o.OrderID, o.UserID, o.TotalAmount, o.OrderStatus, o.OrderDate, " +
                     "u.FullName AS UserName, p.PaymentStatus " +
                     "FROM Orders o " +
                     "LEFT JOIN Users u ON o.UserID = u.UserID " +
                     "LEFT JOIN Payments p ON o.OrderID = p.OrderID " +
                     where + " ORDER BY o." + sortBy + " " + sortOrder +
                     " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    ps.setString(paramIndex++, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(paramIndex++, (Integer) param);
                }
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setUserName(rs.getString("UserName"));
                    order.setPaymentStatus(rs.getString("PaymentStatus"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting paged orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    @Override
    public int count(String searchKeyword, String status, int userID) {
        // Build WHERE clause
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            whereClause.append("u.FullName LIKE ?");
            params.add("%" + searchKeyword.trim() + "%");
        }
        
        if (status != null && !status.trim().isEmpty()) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("o.OrderStatus = ?");
            params.add(status.trim());
        }
        
        if (userID > 0) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("o.UserID = ?");
            params.add(userID);
        }
        
        String where = whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "";
        String sql = "SELECT COUNT(*) FROM Orders o LEFT JOIN Users u ON o.UserID = u.UserID " + where;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    ps.setString(paramIndex++, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(paramIndex++, (Integer) param);
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int insert(Order order) {
        String sql = "INSERT INTO Orders (UserID, TotalAmount, OrderStatus, OrderDate) " +
                     "VALUES (?, ?, ?, GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, order.getUserID());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getOrderStatus());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        order.setOrderID(generatedId);
                        System.out.println("Inserted order ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(Order order) {
        String sql = "UPDATE Orders SET UserID = ?, TotalAmount = ?, OrderStatus = ? WHERE OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, order.getUserID());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getOrderStatus());
            ps.setInt(4, order.getOrderID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated order ID: " + order.getOrderID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int orderID) {
        String sql = "DELETE FROM Orders WHERE OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted order ID: " + orderID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("OrderID"));
        order.setUserID(rs.getInt("UserID"));
        
        BigDecimal totalAmount = rs.getBigDecimal("TotalAmount");
        if (totalAmount != null) {
            order.setTotalAmount(totalAmount);
        }
        
        order.setOrderStatus(rs.getString("OrderStatus"));
        
        Timestamp orderDate = rs.getTimestamp("OrderDate");
        if (orderDate != null) {
            order.setOrderDate(new Date(orderDate.getTime()));
        }
        
        return order;
    }
}
