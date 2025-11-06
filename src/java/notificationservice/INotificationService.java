package notificationservice;

import model.Notification;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Notification
 */
public interface INotificationService {

    /**
     * Thêm notification mới
     *
     * @param notification Notification object cần thêm
     */
    void addNotification(Notification notification);

    /**
     * Cập nhật notification
     *
     * @param notification Notification object với thông tin đã cập nhật
     */
    void updateNotification(Notification notification);

    /**
     * Xóa notification
     *
     * @param notificationID ID notification cần xóa
     */
    void deleteNotification(int notificationID);

    /**
     * Lấy notification theo ID
     *
     * @param notificationID ID notification
     * @return Notification object hoặc null nếu không tìm thấy
     */
    Notification getNotificationById(int notificationID);

    /**
     * Lấy tất cả notifications của một user
     *
     * @param userID ID user
     * @return Danh sách các Notification
     */
    List<Notification> getNotificationsByUser(int userID);

    /**
     * Lấy các notifications chưa đọc của một user
     *
     * @param userID ID user
     * @return Danh sách các Notification chưa đọc
     */
    List<Notification> getUnreadNotificationsByUser(int userID);

    /**
     * Đánh dấu notification là đã đọc
     *
     * @param notificationID ID notification
     */
    void markAsRead(int notificationID);

    /**
     * Đánh dấu tất cả notifications của user là đã đọc
     *
     * @param userID ID user
     */
    void markAllAsRead(int userID);

    /**
     * Lấy tất cả notifications
     *
     * @return Danh sách tất cả các Notification
     */
    List<Notification> getAllNotifications();
}
