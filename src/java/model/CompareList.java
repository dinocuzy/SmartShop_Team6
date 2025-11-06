package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng CompareLists trong database SmartShopDB
 */
public class CompareList {
    
    private int compareListID;
    private int userID;
    private Date createdAt;
    
    public CompareList() {
        this.createdAt = new Date();
    }
    
    public CompareList(int compareListID, int userID, Date createdAt) {
        this.compareListID = compareListID;
        this.userID = userID;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getCompareListID() {
        return compareListID;
    }
    
    public void setCompareListID(int compareListID) {
        this.compareListID = compareListID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

