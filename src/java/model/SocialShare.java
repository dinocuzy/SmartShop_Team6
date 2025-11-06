package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng SocialShares trong database SmartShopDB
 */
public class SocialShare {
    
    private int shareID;
    private int productID;
    private Integer userID; // NULL nếu anonymous
    private String platform; // Facebook, Twitter, Instagram, Zalo, etc.
    private Date sharedAt;
    
    // Có thể thêm thông tin từ join
    private String productName;
    
    public SocialShare() {
        this.sharedAt = new Date();
    }
    
    public SocialShare(int shareID, int productID, Integer userID, String platform, Date sharedAt) {
        this.shareID = shareID;
        this.productID = productID;
        this.userID = userID;
        this.platform = platform;
        this.sharedAt = sharedAt;
    }
    
    // Getters and Setters
    
    public int getShareID() {
        return shareID;
    }
    
    public void setShareID(int shareID) {
        this.shareID = shareID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public Integer getUserID() {
        return userID;
    }
    
    public void setUserID(Integer userID) {
        this.userID = userID;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public Date getSharedAt() {
        return sharedAt;
    }
    
    public void setSharedAt(Date sharedAt) {
        this.sharedAt = sharedAt;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
}

