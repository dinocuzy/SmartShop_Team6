package notificationservice;

import model.Notification;
import notificationdao.INotificationDAO;
import notificationdao.NotificationDAO;
import java.util.List;

/**
 * Implementation của INotificationService
 * Chứa business logic cho Notification
 * Sử dụng NotificationDAO (JDBC) để truy cập dữ liệu
 */
public class NotificationService implements INotificationService {
    
    private final INotificationDAO notificationDAO;
    
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }
    
    @Override
    public void addNotification(Notification notification) {
        validateNotification(notification);
        notificationDAO.insert(notification);
    }
    
    @Override
    public void updateNotification(Notification notification) {
        validateNotification(notification);
        if (notification.getNotificationID() <= 0) {
            throw new IllegalArgumentException("Notification ID must be greater than 0");
        }
        boolean updated = notificationDAO.update(notification);
        if (!updated) {
            throw new RuntimeException("Failed to update notification with ID: " + notification.getNotificationID());
        }
    }
    
    @Override
    public void deleteNotification(int notificationID) {
        if (notificationID <= 0) {
            throw new IllegalArgumentException("Notification ID must be greater than 0");
        }
        boolean deleted = notificationDAO.delete(notificationID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete notification with ID: " + notificationID);
        }
    }
    
    @Override
    public Notification getNotificationById(int notificationID) {
        if (notificationID <= 0) {
            throw new IllegalArgumentException("Notification ID must be greater than 0");
        }
        return notificationDAO.getById(notificationID);
    }
    
    @Override
    public List<Notification> getNotificationsByUser(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        return notificationDAO.getByUser(userID);
    }
    
    @Override
    public List<Notification> getUnreadNotificationsByUser(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        return notificationDAO.getUnreadByUser(userID);
    }
    
    @Override
    public void markAsRead(int notificationID) {
        if (notificationID <= 0) {
            throw new IllegalArgumentException("Notification ID must be greater than 0");
        }
        boolean marked = notificationDAO.markAsRead(notificationID);
        if (!marked) {
            throw new RuntimeException("Failed to mark notification as read with ID: " + notificationID);
        }
    }
    
    @Override
    public void markAllAsRead(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        notificationDAO.markAllAsRead(userID);
    }
    
    @Override
    public List<Notification> getAllNotifications() {
        return notificationDAO.getAll();
    }
    
    private void validateNotification(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null");
        }
        if (notification.getUserID() <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (notification.getContent() == null || notification.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required");
        }
    }
}
