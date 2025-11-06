package model;

import java.math.BigDecimal;

/**
 * Model đại diện cho một item trong giỏ hàng
 */
public class CartItem {
    
    private int productID;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;
    private int stock; // Số lượng tồn kho
    private String stockStatus;
    
    public CartItem() {
        this.quantity = 1;
    }
    
    public CartItem(int productID, String productName, String imageUrl, BigDecimal price, int quantity, int stock, String stockStatus) {
        this.productID = productID;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.stockStatus = stockStatus;
    }
    
    /**
     * Tính tổng tiền cho item này
     */
    public BigDecimal getSubtotal() {
        if (price == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public String getStockStatus() {
        return stockStatus;
    }
    
    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }
}

