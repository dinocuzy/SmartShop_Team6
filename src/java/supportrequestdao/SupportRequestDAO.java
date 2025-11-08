package supportrequestdao;

import model.SupportRequest;
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
 * Implementation của ISupportRequestDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class SupportRequestDAO implements ISupportRequestDAO {
    
    @Override
    public int insert(SupportRequest request) {
        String sql = "INSERT INTO SupportRequests (UserID, Subject, Message, Status, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, SYSDATETIME())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, request.getUserID());
            ps.setString(2, request.getSubject());
            ps.setString(3, request.getMessage());
            ps.setString(4, request.getStatus() != null ? request.getStatus() : "Open");
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        request.setRequestID(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting support request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    @Override
    public SupportRequest getById(int requestID) {
        String sql = "SELECT sr.RequestID, sr.UserID, sr.Subject, sr.Message, sr.Status, sr.CreatedAt, " +
                     "u.FullName as UserName, u.Email as UserEmail " +
                     "FROM SupportRequests sr " +
                     "INNER JOIN Users u ON sr.UserID = u.UserID " +
                     "WHERE sr.RequestID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, requestID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequest(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting support request by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<SupportRequest> getByUserID(int userID) {
        List<SupportRequest> requests = new ArrayList<>();
        String sql = "SELECT sr.RequestID, sr.UserID, sr.Subject, sr.Message, sr.Status, sr.CreatedAt, " +
                     "u.FullName as UserName, u.Email as UserEmail " +
                     "FROM SupportRequests sr " +
                     "INNER JOIN Users u ON sr.UserID = u.UserID " +
                     "WHERE sr.UserID = ? " +
                     "ORDER BY sr.CreatedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupportRequest request = mapResultSetToRequest(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting support requests by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }
    
    @Override
    public List<SupportRequest> getAll() {
        List<SupportRequest> requests = new ArrayList<>();
        String sql = "SELECT sr.RequestID, sr.UserID, sr.Subject, sr.Message, sr.Status, sr.CreatedAt, " +
                     "u.FullName as UserName, u.Email as UserEmail " +
                     "FROM SupportRequests sr " +
                     "INNER JOIN Users u ON sr.UserID = u.UserID " +
                     "ORDER BY sr.CreatedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                SupportRequest request = mapResultSetToRequest(rs);
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all support requests: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }
    
    @Override
    public List<SupportRequest> getByStatus(String status) {
        List<SupportRequest> requests = new ArrayList<>();
        String sql = "SELECT sr.RequestID, sr.UserID, sr.Subject, sr.Message, sr.Status, sr.CreatedAt, " +
                     "u.FullName as UserName, u.Email as UserEmail " +
                     "FROM SupportRequests sr " +
                     "INNER JOIN Users u ON sr.UserID = u.UserID " +
                     "WHERE sr.Status = ? " +
                     "ORDER BY sr.CreatedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupportRequest request = mapResultSetToRequest(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting support requests by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }
    
    @Override
    public boolean update(SupportRequest request) {
        String sql = "UPDATE SupportRequests SET Subject = ?, Message = ?, Status = ? " +
                     "WHERE RequestID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, request.getSubject());
            ps.setString(2, request.getMessage());
            ps.setString(3, request.getStatus());
            ps.setInt(4, request.getRequestID());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating support request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int requestID) {
        String sql = "DELETE FROM SupportRequests WHERE RequestID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, requestID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting support request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Map ResultSet sang SupportRequest object
     */
    private SupportRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        SupportRequest request = new SupportRequest();
        request.setRequestID(rs.getInt("RequestID"));
        request.setUserID(rs.getInt("UserID"));
        request.setSubject(rs.getString("Subject"));
        request.setMessage(rs.getString("Message"));
        request.setStatus(rs.getString("Status"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            request.setCreatedAt(new Date(createdAt.getTime()));
        }
        
        // Thông tin từ JOIN
        request.setUserName(rs.getString("UserName"));
        request.setUserEmail(rs.getString("UserEmail"));
        
        return request;
    }
}

