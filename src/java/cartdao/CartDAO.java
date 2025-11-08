package cartdao;

import model.CartItemDB;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của ICartDAO sử dụng JDBC
 */
public class CartDAO implements ICartDAO {
    
    @Override
    public List<CartItemDB> getCartItemsByUser(int userID) {
        String sql = "SELECT ci.CartItemID, ci.UserID, ci.ProductID, ci.Quantity, ci.AddedDate, " +
                     "p.ProductID, p.ProductName, p.Price, p.Stock, p.StockStatus, p.ImageUrl " +
                     "FROM CartItems ci " +
                     "INNER JOIN Products p ON ci.ProductID = p.ProductID " +
                     "WHERE ci.UserID = ? " +
                     "ORDER BY ci.AddedDate DESC";
        
        List<CartItemDB> cartItems = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItemDB cartItem = mapResultSetToCartItemDB(rs);
                    cartItems.add(cartItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart items by user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cartItems;
    }
    
    @Override
    public CartItemDB getCartItemById(int cartItemID) {
        String sql = "SELECT ci.CartItemID, ci.UserID, ci.ProductID, ci.Quantity, ci.AddedDate, " +
                     "p.ProductID, p.ProductName, p.Price, p.Stock, p.StockStatus, p.ImageUrl " +
                     "FROM CartItems ci " +
                     "INNER JOIN Products p ON ci.ProductID = p.ProductID " +
                     "WHERE ci.CartItemID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartItemID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCartItemDB(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart item by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public CartItemDB getCartItemByUserAndProduct(int userID, int productID) {
        String sql = "SELECT ci.CartItemID, ci.UserID, ci.ProductID, ci.Quantity, ci.AddedDate, " +
                     "p.ProductID, p.ProductName, p.Price, p.Stock, p.StockStatus, p.ImageUrl " +
                     "FROM CartItems ci " +
                     "INNER JOIN Products p ON ci.ProductID = p.ProductID " +
                     "WHERE ci.UserID = ? AND ci.ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            ps.setInt(2, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCartItemDB(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart item by user and product: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public int insert(CartItemDB cartItem) {
        String sql = "INSERT INTO CartItems (UserID, ProductID, Quantity, AddedDate) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, cartItem.getUserID());
            ps.setInt(2, cartItem.getProductID());
            ps.setInt(3, cartItem.getQuantity());
            
            if (cartItem.getAddedDate() != null) {
                ps.setTimestamp(4, new Timestamp(cartItem.getAddedDate().getTime()));
            } else {
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            }
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        cartItem.setCartItemID(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting cart item: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public boolean update(CartItemDB cartItem) {
        String sql = "UPDATE CartItems SET Quantity = ?, AddedDate = ? WHERE CartItemID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartItem.getQuantity());
            
            if (cartItem.getAddedDate() != null) {
                ps.setTimestamp(2, new Timestamp(cartItem.getAddedDate().getTime()));
            } else {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            }
            
            ps.setInt(3, cartItem.getCartItemID());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int cartItemID) {
        String sql = "DELETE FROM CartItems WHERE CartItemID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartItemID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting cart item: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int deleteAllByUser(int userID) {
        String sql = "DELETE FROM CartItems WHERE UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Error deleting all cart items by user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public boolean deleteByUserAndProduct(int userID, int productID) {
        String sql = "DELETE FROM CartItems WHERE UserID = ? AND ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            ps.setInt(2, productID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting cart item by user and product: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int countByUser(int userID) {
        String sql = "SELECT COUNT(*) FROM CartItems WHERE UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting cart items by user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet thành CartItemDB object
     */
    private CartItemDB mapResultSetToCartItemDB(ResultSet rs) throws SQLException {
        CartItemDB cartItem = new CartItemDB();
        cartItem.setCartItemID(rs.getInt("CartItemID"));
        cartItem.setUserID(rs.getInt("UserID"));
        cartItem.setProductID(rs.getInt("ProductID"));
        cartItem.setQuantity(rs.getInt("Quantity"));
        
        Timestamp addedDate = rs.getTimestamp("AddedDate");
        if (addedDate != null) {
            cartItem.setAddedDate(new Date(addedDate.getTime()));
        }
        
        // Map Product information
        model.Product product = new model.Product();
        product.setProductID(rs.getInt("ProductID"));
        product.setProductName(rs.getString("ProductName"));
        product.setPrice(rs.getBigDecimal("Price"));
        product.setStock(rs.getInt("Stock"));
        product.setStockStatus(rs.getString("StockStatus"));
        product.setImageUrl(rs.getString("ImageUrl"));
        
        cartItem.setProduct(product);
        
        return cartItem;
    }
}

