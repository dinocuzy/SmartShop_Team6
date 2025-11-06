package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng SupportRequests trong database SmartShopDB
 */
public class SupportRequest {
    
    private int requestID;
    private int userID;
    private String subject;
    private String message;
    private String status;
    private Date createdAt;
    
    // Có thể thêm thông tin từ join
    private String userName;
    private String userEmail;
    
    public SupportRequest() {
        this.status = "Open";
        this.createdAt = new Date();
    }
    
    public SupportRequest(int requestID, int userID, String subject, String message, 
                         String status, Date createdAt) {
        this.requestID = requestID;
        this.userID = userID;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getRequestID() {
        return requestID;
    }
    
    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

