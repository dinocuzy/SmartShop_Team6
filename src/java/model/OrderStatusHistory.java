package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng OrderStatusHistory trong database SmartShopDB
 */
public class OrderStatusHistory {
    
    private int historyID;
    private int orderID;
    private String oldStatus;
    private String newStatus;
    private Date changedAt;
    private Integer changedBy; // UserID who changed the status
    
    public OrderStatusHistory() {
        this.changedAt = new Date();
    }
    
    public OrderStatusHistory(int historyID, int orderID, String oldStatus, 
                             String newStatus, Date changedAt, Integer changedBy) {
        this.historyID = historyID;
        this.orderID = orderID;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
    }
    
    // Getters and Setters
    
    public int getHistoryID() {
        return historyID;
    }
    
    public void setHistoryID(int historyID) {
        this.historyID = historyID;
    }
    
    public int getOrderID() {
        return orderID;
    }
    
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public String getOldStatus() {
        return oldStatus;
    }
    
    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
    
    public Date getChangedAt() {
        return changedAt;
    }
    
    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }
    
    public Integer getChangedBy() {
        return changedBy;
    }
    
    public void setChangedBy(Integer changedBy) {
        this.changedBy = changedBy;
    }
}

