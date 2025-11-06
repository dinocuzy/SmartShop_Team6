package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng CompareListItems trong database SmartShopDB
 */
public class CompareListItem {
    
    private int compareListItemID;
    private int compareListID;
    private int productID;
    private Date addedAt;
    
    // Có thể thêm thông tin từ join
    private String productName;
    private String productImageUrl;
    
    public CompareListItem() {
        this.addedAt = new Date();
    }
    
    public CompareListItem(int compareListItemID, int compareListID, int productID, Date addedAt) {
        this.compareListItemID = compareListItemID;
        this.compareListID = compareListID;
        this.productID = productID;
        this.addedAt = addedAt;
    }
    
    // Getters and Setters
    
    public int getCompareListItemID() {
        return compareListItemID;
    }
    
    public void setCompareListItemID(int compareListItemID) {
        this.compareListItemID = compareListItemID;
    }
    
    public int getCompareListID() {
        return compareListID;
    }
    
    public void setCompareListID(int compareListID) {
        this.compareListID = compareListID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public Date getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
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
}

