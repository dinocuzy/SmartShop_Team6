package notificationdao;

import model.Notification;
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

public class NotificationDAO implements INotificationDAO {
    
    @Override
    public List<Notification> getAll() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT NotificationID, UserID, Title, Content, IsRead, CreatedAt FROM Notifications ORDER BY NotificationID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }
    
    @Override
    public Notification getById(int notificationID) {
        String sql = "SELECT NotificationID, UserID, Title, Content, IsRead, CreatedAt FROM Notifications WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, notificationID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotification(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting notification by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Notification> getByUser(int userID) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT NotificationID, UserID, Title, Content, IsRead, CreatedAt FROM Notifications " +
                     "WHERE UserID = ? ORDER BY CreatedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting notifications by user: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }
    
    @Override
    public List<Notification> getUnreadByUser(int userID) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT NotificationID, UserID, Title, Content, IsRead, CreatedAt FROM Notifications " +
                     "WHERE UserID = ? AND IsRead = 0 ORDER BY CreatedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting unread notifications by user: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }
    
    @Override
    public int insert(Notification notification) {
        String sql = "INSERT INTO Notifications (UserID, Title, Content, IsRead, CreatedAt) VALUES (?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, notification.getUserID());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getContent());
            ps.setBoolean(4, notification.isRead());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        notification.setNotificationID(generatedId);
                        System.out.println("Inserted notification ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting notification: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(Notification notification) {
        String sql = "UPDATE Notifications SET UserID = ?, Title = ?, Content = ?, IsRead = ? WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, notification.getUserID());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getContent());
            ps.setBoolean(4, notification.isRead());
            ps.setInt(5, notification.getNotificationID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated notification ID: " + notification.getNotificationID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating notification: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int notificationID) {
        String sql = "DELETE FROM Notifications WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, notificationID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted notification ID: " + notificationID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean markAsRead(int notificationID) {
        String sql = "UPDATE Notifications SET IsRead = 1 WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, notificationID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Marked notification as read ID: " + notificationID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean markAllAsRead(int userID) {
        String sql = "UPDATE Notifications SET IsRead = 1 WHERE UserID = ? AND IsRead = 0";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Marked all notifications as read for user ID: " + userID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationID(rs.getInt("NotificationID"));
        notification.setUserID(rs.getInt("UserID"));
        notification.setTitle(rs.getString("Title"));
        notification.setContent(rs.getString("Content"));
        notification.setRead(rs.getBoolean("IsRead"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            notification.setCreatedAt(new Date(createdAt.getTime()));
        }
        
        return notification;
    }
}
