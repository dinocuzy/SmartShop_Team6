package useroauthdao;

import model.UserOAuth;
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
 * Implementation của IUserOAuthDAO
 */
public class UserOAuthDAO implements IUserOAuthDAO {
    
    @Override
    public UserOAuth getByProviderUserID(int providerID, String providerUserID) {
        String sql = "SELECT uo.UserOAuthID, uo.UserID, uo.ProviderID, uo.ProviderUserID, " +
                     "uo.AccessToken, uo.RefreshToken, uo.LinkedAt, " +
                     "op.ProviderName, u.FullName as UserName " +
                     "FROM UserOAuth uo " +
                     "INNER JOIN OAuthProviders op ON uo.ProviderID = op.ProviderID " +
                     "INNER JOIN Users u ON uo.UserID = u.UserID " +
                     "WHERE uo.ProviderID = ? AND uo.ProviderUserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, providerID);
            ps.setString(2, providerUserID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserOAuth(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting UserOAuth by provider user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public UserOAuth getByUserIDAndProvider(int userID, int providerID) {
        String sql = "SELECT uo.UserOAuthID, uo.UserID, uo.ProviderID, uo.ProviderUserID, " +
                     "uo.AccessToken, uo.RefreshToken, uo.LinkedAt, " +
                     "op.ProviderName, u.FullName as UserName " +
                     "FROM UserOAuth uo " +
                     "INNER JOIN OAuthProviders op ON uo.ProviderID = op.ProviderID " +
                     "INNER JOIN Users u ON uo.UserID = u.UserID " +
                     "WHERE uo.UserID = ? AND uo.ProviderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            ps.setInt(2, providerID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserOAuth(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting UserOAuth by user ID and provider: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<UserOAuth> getByUserID(int userID) {
        List<UserOAuth> userOAuths = new ArrayList<>();
        String sql = "SELECT uo.UserOAuthID, uo.UserID, uo.ProviderID, uo.ProviderUserID, " +
                     "uo.AccessToken, uo.RefreshToken, uo.LinkedAt, " +
                     "op.ProviderName, u.FullName as UserName " +
                     "FROM UserOAuth uo " +
                     "INNER JOIN OAuthProviders op ON uo.ProviderID = op.ProviderID " +
                     "INNER JOIN Users u ON uo.UserID = u.UserID " +
                     "WHERE uo.UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userOAuths.add(mapResultSetToUserOAuth(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting UserOAuth by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return userOAuths;
    }
    
    @Override
    public int insert(UserOAuth userOAuth) {
        String sql = "INSERT INTO UserOAuth (UserID, ProviderID, ProviderUserID, AccessToken, RefreshToken, LinkedAt) " +
                     "VALUES (?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, userOAuth.getUserID());
            ps.setInt(2, userOAuth.getProviderID());
            ps.setString(3, userOAuth.getProviderUserID());
            ps.setString(4, userOAuth.getAccessToken());
            ps.setString(5, userOAuth.getRefreshToken());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        userOAuth.setUserOAuthID(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting UserOAuth: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public boolean update(UserOAuth userOAuth) {
        String sql = "UPDATE UserOAuth SET AccessToken = ?, RefreshToken = ? WHERE UserOAuthID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userOAuth.getAccessToken());
            ps.setString(2, userOAuth.getRefreshToken());
            ps.setInt(3, userOAuth.getUserOAuthID());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating UserOAuth: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int userOAuthID) {
        String sql = "DELETE FROM UserOAuth WHERE UserOAuthID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userOAuthID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting UserOAuth: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private UserOAuth mapResultSetToUserOAuth(ResultSet rs) throws SQLException {
        UserOAuth userOAuth = new UserOAuth();
        userOAuth.setUserOAuthID(rs.getInt("UserOAuthID"));
        userOAuth.setUserID(rs.getInt("UserID"));
        userOAuth.setProviderID(rs.getInt("ProviderID"));
        userOAuth.setProviderUserID(rs.getString("ProviderUserID"));
        userOAuth.setAccessToken(rs.getString("AccessToken"));
        userOAuth.setRefreshToken(rs.getString("RefreshToken"));
        
        Timestamp linkedAt = rs.getTimestamp("LinkedAt");
        if (linkedAt != null) {
            userOAuth.setLinkedAt(new Date(linkedAt.getTime()));
        }
        
        // Thông tin từ join
        try {
            userOAuth.setProviderName(rs.getString("ProviderName"));
            userOAuth.setUserName(rs.getString("UserName"));
        } catch (SQLException e) {
            // Các trường này có thể không có trong một số query
        }
        
        return userOAuth;
    }
}

