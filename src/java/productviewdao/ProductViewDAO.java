package productviewdao;

import model.ProductView;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Implementation của IProductViewDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class ProductViewDAO implements IProductViewDAO {
    
    @Override
    public int insert(ProductView view) {
        String sql = "INSERT INTO ProductViews (ProductID, UserID, ViewedAt) " +
                     "VALUES (?, ?, SYSDATETIME())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, view.getProductID());
            if (view.getUserID() != null) {
                ps.setInt(2, view.getUserID());
            } else {
                ps.setObject(2, null);
            }
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        view.setViewID(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting product view: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    @Override
    public int getViewCountByProductID(int productID) {
        String sql = "SELECT COUNT(*) as ViewCount FROM ProductViews WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ViewCount");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting view count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public ProductView getById(int viewID) {
        String sql = "SELECT ViewID, ProductID, UserID, ViewedAt " +
                     "FROM ProductViews " +
                     "WHERE ViewID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, viewID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToView(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product view by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Map ResultSet sang ProductView object
     */
    private ProductView mapResultSetToView(ResultSet rs) throws SQLException {
        ProductView view = new ProductView();
        view.setViewID(rs.getInt("ViewID"));
        view.setProductID(rs.getInt("ProductID"));
        
        Integer userID = rs.getInt("UserID");
        if (rs.wasNull()) {
            userID = null;
        }
        view.setUserID(userID);
        
        Timestamp viewedAt = rs.getTimestamp("ViewedAt");
        if (viewedAt != null) {
            view.setViewedAt(new Date(viewedAt.getTime()));
        }
        
        return view;
    }
}

