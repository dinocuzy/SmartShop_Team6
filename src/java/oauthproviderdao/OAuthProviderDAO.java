package oauthproviderdao;

import model.OAuthProvider;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của IOAuthProviderDAO
 */
public class OAuthProviderDAO implements IOAuthProviderDAO {
    
    @Override
    public OAuthProvider getById(int providerID) {
        String sql = "SELECT ProviderID, ProviderName FROM OAuthProviders WHERE ProviderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, providerID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOAuthProvider(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting OAuth provider by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public OAuthProvider getByName(String providerName) {
        String sql = "SELECT ProviderID, ProviderName FROM OAuthProviders WHERE ProviderName = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, providerName);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOAuthProvider(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting OAuth provider by name: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<OAuthProvider> getAll() {
        List<OAuthProvider> providers = new ArrayList<>();
        String sql = "SELECT ProviderID, ProviderName FROM OAuthProviders ORDER BY ProviderID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                providers.add(mapResultSetToOAuthProvider(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all OAuth providers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return providers;
    }
    
    private OAuthProvider mapResultSetToOAuthProvider(ResultSet rs) throws SQLException {
        OAuthProvider provider = new OAuthProvider();
        provider.setProviderID(rs.getInt("ProviderID"));
        provider.setProviderName(rs.getString("ProviderName"));
        return provider;
    }
}

