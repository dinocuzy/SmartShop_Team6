package notificationdao;

import model.Notification;
import java.util.List;

public interface INotificationDAO {
    List<Notification> getAll();
    Notification getById(int notificationID);
    List<Notification> getByUser(int userID);
    List<Notification> getUnreadByUser(int userID);
    int insert(Notification notification);
    boolean update(Notification notification);
    boolean delete(int notificationID);
    boolean markAsRead(int notificationID);
    boolean markAllAsRead(int userID);
}
