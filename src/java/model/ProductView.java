package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng ProductViews trong database SmartShopDB
 */
public class ProductView {
    
    private int viewID;
    private int productID;
    private Integer userID; // NULL nếu anonymous
    private Date viewedAt;
    
    // Có thể thêm thông tin từ join
    private String productName;
    
    public ProductView() {
        this.viewedAt = new Date();
    }
    
    public ProductView(int viewID, int productID, Integer userID, Date viewedAt) {
        this.viewID = viewID;
        this.productID = productID;
        this.userID = userID;
        this.viewedAt = viewedAt;
    }
    
    // Getters and Setters
    
    public int getViewID() {
        return viewID;
    }
    
    public void setViewID(int viewID) {
        this.viewID = viewID;
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
    
    public Date getViewedAt() {
        return viewedAt;
    }
    
    public void setViewedAt(Date viewedAt) {
        this.viewedAt = viewedAt;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
}

