package promotionproductdao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của IPromotionProductDAO
 * Sử dụng JDBC để tương tác với bảng PromotionProducts
 */
public class PromotionProductDAO implements IPromotionProductDAO {
    
    @Override
    public boolean addPromotionToProduct(int promotionID, int productID) {
        if (promotionID <= 0 || productID <= 0) {
            return false;
        }
        
        String sql = "INSERT INTO PromotionProducts (PromotionID, ProductID) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, promotionID);
            pstmt.setInt(2, productID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Nếu duplicate (đã có promotion cho product này), bỏ qua
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                return true; // Đã tồn tại, coi như thành công
            }
            System.err.println("Error adding promotion to product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean removePromotionFromProduct(int promotionID, int productID) {
        if (promotionID <= 0 || productID <= 0) {
            return false;
        }
        
        String sql = "DELETE FROM PromotionProducts WHERE PromotionID = ? AND ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, promotionID);
            pstmt.setInt(2, productID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing promotion from product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean removeAllPromotionsFromProduct(int productID) {
        if (productID <= 0) {
            return false;
        }
        
        String sql = "DELETE FROM PromotionProducts WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productID);
            
            int rowsAffected = pstmt.executeUpdate();
            return true; // Luôn return true, kể cả khi không có promotion nào
            
        } catch (SQLException e) {
            System.err.println("Error removing all promotions from product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Integer> getPromotionIDsByProduct(int productID) {
        List<Integer> promotionIDs = new ArrayList<>();
        
        if (productID <= 0) {
            return promotionIDs;
        }
        
        String sql = "SELECT PromotionID FROM PromotionProducts WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    promotionIDs.add(rs.getInt("PromotionID"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting promotion IDs by product: " + e.getMessage());
            e.printStackTrace();
        }
        
        return promotionIDs;
    }
    
    @Override
    public int getActivePromotionIDByProduct(int productID) {
        if (productID <= 0) {
            return 0;
        }
        
        // Lấy promotion đang active và trong khoảng thời gian hiệu lực
        String sql = "SELECT TOP 1 pp.PromotionID " +
                     "FROM PromotionProducts pp " +
                     "INNER JOIN Promotions p ON pp.PromotionID = p.PromotionID " +
                     "WHERE pp.ProductID = ? " +
                     "  AND p.IsActive = 1 " +
                     "  AND p.StartDate <= SYSDATETIME() " +
                     "  AND p.EndDate >= SYSDATETIME() " +
                     "ORDER BY pp.PromotionID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("PromotionID");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting active promotion ID by product: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}

