package orderitemdao;

import model.OrderItem;
import util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO implements IOrderItemDAO {
    
    @Override
    public List<OrderItem> getAll() {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT OrderItemID, OrderID, ProductID, Quantity, UnitPrice FROM OrderItems ORDER BY OrderItemID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all order items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    @Override
    public OrderItem getById(int orderItemID) {
        String sql = "SELECT OrderItemID, OrderID, ProductID, Quantity, UnitPrice FROM OrderItems WHERE OrderItemID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderItemID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrderItem(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order item by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<OrderItem> getByOrder(int orderID) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.OrderItemID, oi.OrderID, oi.ProductID, oi.Quantity, oi.UnitPrice, " +
                     "p.ProductName FROM OrderItems oi LEFT JOIN Products p ON oi.ProductID = p.ProductID " +
                     "WHERE oi.OrderID = ? ORDER BY oi.OrderItemID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = mapResultSetToOrderItem(rs);
                    item.setProductName(rs.getString("ProductName"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items by order: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    @Override
    public List<OrderItem> getByProduct(int productID) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT OrderItemID, OrderID, ProductID, Quantity, UnitPrice FROM OrderItems WHERE ProductID = ? ORDER BY OrderItemID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToOrderItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items by product: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    @Override
    public int insert(OrderItem orderItem) {
        String sql = "INSERT INTO OrderItems (OrderID, ProductID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, orderItem.getOrderID());
            ps.setInt(2, orderItem.getProductID());
            ps.setInt(3, orderItem.getQuantity());
            ps.setBigDecimal(4, orderItem.getUnitPrice());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        orderItem.setOrderItemID(generatedId);
                        System.out.println("Inserted order item ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order item: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(OrderItem orderItem) {
        String sql = "UPDATE OrderItems SET OrderID = ?, ProductID = ?, Quantity = ?, UnitPrice = ? WHERE OrderItemID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderItem.getOrderID());
            ps.setInt(2, orderItem.getProductID());
            ps.setInt(3, orderItem.getQuantity());
            ps.setBigDecimal(4, orderItem.getUnitPrice());
            ps.setInt(5, orderItem.getOrderItemID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated order item ID: " + orderItem.getOrderItemID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int orderItemID) {
        String sql = "DELETE FROM OrderItems WHERE OrderItemID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderItemID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted order item ID: " + orderItemID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean deleteByOrder(int orderID) {
        String sql = "DELETE FROM OrderItems WHERE OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted order items for order ID: " + orderID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order items by order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setOrderItemID(rs.getInt("OrderItemID"));
        item.setOrderID(rs.getInt("OrderID"));
        item.setProductID(rs.getInt("ProductID"));
        item.setQuantity(rs.getInt("Quantity"));
        
        BigDecimal unitPrice = rs.getBigDecimal("UnitPrice");
        if (unitPrice != null) {
            item.setUnitPrice(unitPrice);
        }
        
        return item;
    }
}
