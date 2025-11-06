package model;

/**
 * Entity class đại diện cho bảng PromotionProducts trong database SmartShopDB
 * Đây là bảng trung gian (junction table) giữa Promotions và Products
 */
public class PromotionProduct {
    
    private int promotionProductID;
    private int promotionID;
    private int productID;
    
    // Có thể thêm thông tin từ join
    private String promotionTitle;
    private String productName;
    
    public PromotionProduct() {
    }
    
    public PromotionProduct(int promotionProductID, int promotionID, int productID) {
        this.promotionProductID = promotionProductID;
        this.promotionID = promotionID;
        this.productID = productID;
    }
    
    // Getters and Setters
    
    public int getPromotionProductID() {
        return promotionProductID;
    }
    
    public void setPromotionProductID(int promotionProductID) {
        this.promotionProductID = promotionProductID;
    }
    
    public int getPromotionID() {
        return promotionID;
    }
    
    public void setPromotionID(int promotionID) {
        this.promotionID = promotionID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public String getPromotionTitle() {
        return promotionTitle;
    }
    
    public void setPromotionTitle(String promotionTitle) {
        this.promotionTitle = promotionTitle;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
}

