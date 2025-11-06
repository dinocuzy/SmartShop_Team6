package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng ProductImages trong database SmartShopDB
 */
public class ProductImage {
    
    private int imageID;
    private int productID;
    private String imageUrl;
    private int sortOrder;
    private Date createdAt;
    
    public ProductImage() {
        this.sortOrder = 0;
        this.createdAt = new Date();
    }
    
    public ProductImage(int imageID, int productID, String imageUrl, int sortOrder, Date createdAt) {
        this.imageID = imageID;
        this.productID = productID;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getImageID() {
        return imageID;
    }
    
    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

