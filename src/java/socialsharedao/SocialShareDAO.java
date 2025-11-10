package socialsharedao;

import model.SocialShare;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Implementation của ISocialShareDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class SocialShareDAO implements ISocialShareDAO {
    
    @Override
    public int insert(SocialShare share) {
        String sql = "INSERT INTO SocialShares (ProductID, UserID, Platform, SharedAt) " +
                     "VALUES (?, ?, ?, SYSDATETIME())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, share.getProductID());
            if (share.getUserID() != null) {
                ps.setInt(2, share.getUserID());
            } else {
                ps.setObject(2, null);
            }
            ps.setString(3, share.getPlatform());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        share.setShareID(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting social share: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    @Override
    public int getShareCountByProductID(int productID) {
        String sql = "SELECT COUNT(*) as ShareCount FROM SocialShares WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ShareCount");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting share count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public SocialShare getById(int shareID) {
        String sql = "SELECT ShareID, ProductID, UserID, Platform, SharedAt " +
                     "FROM SocialShares " +
                     "WHERE ShareID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, shareID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShare(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting social share by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Map ResultSet sang SocialShare object
     */
    private SocialShare mapResultSetToShare(ResultSet rs) throws SQLException {
        SocialShare share = new SocialShare();
        share.setShareID(rs.getInt("ShareID"));
        share.setProductID(rs.getInt("ProductID"));
        
        Integer userID = rs.getInt("UserID");
        if (rs.wasNull()) {
            userID = null;
        }
        share.setUserID(userID);
        
        share.setPlatform(rs.getString("Platform"));
        
        Timestamp sharedAt = rs.getTimestamp("SharedAt");
        if (sharedAt != null) {
            share.setSharedAt(new Date(sharedAt.getTime()));
        }
        
        return share;
    }
}

