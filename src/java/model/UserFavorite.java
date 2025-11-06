package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng UserFavorites trong database SmartShopDB
 */
public class UserFavorite {
    
    private int favoriteID;
    private int userID;
    private int productID;
    private Date createdAt;
    
    // Có thể thêm thông tin từ join
    private String productName;
    private String productImageUrl;
    private java.math.BigDecimal productPrice;
    
    public UserFavorite() {
        this.createdAt = new Date();
    }
    
    public UserFavorite(int favoriteID, int userID, int productID, Date createdAt) {
        this.favoriteID = favoriteID;
        this.userID = userID;
        this.productID = productID;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getFavoriteID() {
        return favoriteID;
    }
    
    public void setFavoriteID(int favoriteID) {
        this.favoriteID = favoriteID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductImageUrl() {
        return productImageUrl;
    }
    
    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }
    
    public java.math.BigDecimal getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(java.math.BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
}

