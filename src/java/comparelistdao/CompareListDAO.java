package comparelistdao;

import model.CompareList;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Implementation của ICompareListDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class CompareListDAO implements ICompareListDAO {
    
    @Override
    public CompareList getByUserID(int userID) {
        String sql = "SELECT CompareListID, UserID, CreatedAt " +
                     "FROM CompareLists " +
                     "WHERE UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompareList(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting compare list by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public int createForUser(int userID) {
        // Kiểm tra xem đã có chưa
        CompareList existing = getByUserID(userID);
        if (existing != null) {
            return existing.getCompareListID();
        }
        
        String sql = "INSERT INTO CompareLists (UserID, CreatedAt) " +
                     "VALUES (?, SYSDATETIME())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, userID);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating compare list: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    @Override
    public int deleteByUserID(int userID) {
        String sql = "DELETE FROM CompareLists WHERE UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting compare list: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet sang CompareList object
     */
    private CompareList mapResultSetToCompareList(ResultSet rs) throws SQLException {
        CompareList compareList = new CompareList();
        compareList.setCompareListID(rs.getInt("CompareListID"));
        compareList.setUserID(rs.getInt("UserID"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            compareList.setCreatedAt(new Date(createdAt.getTime()));
        }
        
        return compareList;
    }
}

