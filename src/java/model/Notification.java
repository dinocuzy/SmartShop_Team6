package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng Notifications trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class Notification {
    
    private int notificationID;
    private int userID;
    private String title;
    private String content;
    private boolean isRead;
    private Date createdAt;
    
    // Relationship với User (Many-to-One) - chỉ để reference, không dùng JPA
    private User user;
    
    /**
     * Constructor mặc định không tham số
     */
    public Notification() {
        this.isRead = false;
        this.createdAt = new Date();
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public Notification(int notificationID, int userID, String title, String content, 
                        boolean isRead, Date createdAt) {
        this.notificationID = notificationID;
        this.userID = userID;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
    
    // Getter và Setter methods
    
    public int getNotificationID() {
        return notificationID;
    }
    
    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userID = user.getUserID();
        }
    }
    

    @Override
    public String toString() {
        return "Notification{" +
                "notificationID=" + notificationID +
                ", userID=" + userID +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
