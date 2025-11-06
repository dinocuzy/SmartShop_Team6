package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng Categories trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class Category {
    
    private int categoryID;
    private String categoryName;
    private String description;
    private String imageUrl;
    private Date createdAt;
    
    /**
     * Constructor mặc định không tham số
     */
    public Category() {
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public Category(int categoryID, String categoryName, String description, String imageUrl, Date createdAt) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }
    
    // Getter và Setter methods
    
    public int getCategoryID() {
        return categoryID;
    }
    
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    
    @Override
    public String toString() {
        return "Category{" +
                "categoryID=" + categoryID +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
