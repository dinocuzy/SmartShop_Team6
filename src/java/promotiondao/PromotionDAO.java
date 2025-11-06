package promotiondao;

import model.Promotion;
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

public class PromotionDAO implements IPromotionDAO {
    
    @Override
    public List<Promotion> getAll(boolean includeInactive) {
        List<Promotion> promotions = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT PromotionID, Title, Description, DiscountPercent, DiscountAmount, " +
                  "StartDate, EndDate, IsActive FROM Promotions ORDER BY PromotionID DESC";
        } else {
            sql = "SELECT PromotionID, Title, Description, DiscountPercent, DiscountAmount, " +
                  "StartDate, EndDate, IsActive FROM Promotions WHERE IsActive = 1 ORDER BY PromotionID DESC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all promotions: " + e.getMessage());
            e.printStackTrace();
        }
        return promotions;
    }
    
    @Override
    public Promotion getById(int promotionID) {
        String sql = "SELECT PromotionID, Title, Description, DiscountPercent, DiscountAmount, " +
                     "StartDate, EndDate, IsActive FROM Promotions WHERE PromotionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, promotionID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPromotion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting promotion by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        Date now = new Date();
        String sql = "SELECT PromotionID, Title, Description, DiscountPercent, DiscountAmount, " +
                     "StartDate, EndDate, IsActive FROM Promotions " +
                     "WHERE IsActive = 1 AND StartDate <= ? AND EndDate >= ? ORDER BY PromotionID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(now.getTime()));
            ps.setTimestamp(2, new Timestamp(now.getTime()));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    promotions.add(mapResultSetToPromotion(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active promotions: " + e.getMessage());
            e.printStackTrace();
        }
        return promotions;
    }
    
    @Override
    public int insert(Promotion promotion) {
        String sql = "INSERT INTO Promotions (Title, Description, DiscountPercent, DiscountAmount, " +
                     "StartDate, EndDate, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, promotion.getTitle());
            ps.setString(2, promotion.getDescription());
            ps.setBigDecimal(3, promotion.getDiscountPercent());
            ps.setBigDecimal(4, promotion.getDiscountAmount());
            
            if (promotion.getStartDate() != null) {
                ps.setTimestamp(5, new Timestamp(promotion.getStartDate().getTime()));
            } else {
                ps.setTimestamp(5, null);
            }
            
            if (promotion.getEndDate() != null) {
                ps.setTimestamp(6, new Timestamp(promotion.getEndDate().getTime()));
            } else {
                ps.setTimestamp(6, null);
            }
            
            ps.setBoolean(7, promotion.isActive());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        promotion.setPromotionID(generatedId);
                        System.out.println("Inserted promotion ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting promotion: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(Promotion promotion) {
        String sql = "UPDATE Promotions SET Title = ?, Description = ?, DiscountPercent = ?, " +
                     "DiscountAmount = ?, StartDate = ?, EndDate = ?, IsActive = ? WHERE PromotionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, promotion.getTitle());
            ps.setString(2, promotion.getDescription());
            ps.setBigDecimal(3, promotion.getDiscountPercent());
            ps.setBigDecimal(4, promotion.getDiscountAmount());
            
            if (promotion.getStartDate() != null) {
                ps.setTimestamp(5, new Timestamp(promotion.getStartDate().getTime()));
            } else {
                ps.setTimestamp(5, null);
            }
            
            if (promotion.getEndDate() != null) {
                ps.setTimestamp(6, new Timestamp(promotion.getEndDate().getTime()));
            } else {
                ps.setTimestamp(6, null);
            }
            
            ps.setBoolean(7, promotion.isActive());
            ps.setInt(8, promotion.getPromotionID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated promotion ID: " + promotion.getPromotionID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating promotion: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int promotionID) {
        // Soft delete: set IsActive = 0
        String sql = "UPDATE Promotions SET IsActive = 0 WHERE PromotionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, promotionID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted promotion ID: " + promotionID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting promotion: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Promotion mapResultSetToPromotion(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionID(rs.getInt("PromotionID"));
        promotion.setTitle(rs.getString("Title"));
        promotion.setDescription(rs.getString("Description"));
        
        BigDecimal discountPercent = rs.getBigDecimal("DiscountPercent");
        if (discountPercent != null) {
            promotion.setDiscountPercent(discountPercent);
        }
        
        BigDecimal discountAmount = rs.getBigDecimal("DiscountAmount");
        if (discountAmount != null) {
            promotion.setDiscountAmount(discountAmount);
        }
        
        Timestamp startDate = rs.getTimestamp("StartDate");
        if (startDate != null) {
            promotion.setStartDate(new Date(startDate.getTime()));
        }
        
        Timestamp endDate = rs.getTimestamp("EndDate");
        if (endDate != null) {
            promotion.setEndDate(new Date(endDate.getTime()));
        }
        
        promotion.setActive(rs.getBoolean("IsActive"));
        
        return promotion;
    }
}
