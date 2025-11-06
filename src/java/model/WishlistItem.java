package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class đại diện cho bảng WishlistItems trong database SmartShopDB
 */
public class WishlistItem {
    
    private int wishlistItemID;
    private int wishlistID;
    private int productID;
    private Date addedAt;
    
    // Transient fields - không phải cột trong DB, được JOIN từ bảng Products
    private String productName;
    private String productImageUrl;
    private BigDecimal productPrice;
    
    // Relationships - chỉ để reference, không dùng JPA
    private Wishlist wishlist;
    private Product product;
    
    public WishlistItem() {
        this.addedAt = new Date();
    }
    
    public WishlistItem(int wishlistItemID, int wishlistID, int productID, Date addedAt) {
        this.wishlistItemID = wishlistItemID;
        this.wishlistID = wishlistID;
        this.productID = productID;
        this.addedAt = addedAt;
    }
    
    // Getters and Setters
    
    public int getWishlistItemID() {
        return wishlistItemID;
    }
    
    public void setWishlistItemID(int wishlistItemID) {
        this.wishlistItemID = wishlistItemID;
    }
    
    public int getWishlistID() {
        return wishlistID;
    }
    
    public void setWishlistID(int wishlistID) {
        this.wishlistID = wishlistID;
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
        if (productName == null && product != null) {
            productName = product.getProductName();
        }
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductImageUrl() {
        if (productImageUrl == null && product != null) {
            productImageUrl = product.getImageUrl();
        }
        return productImageUrl;
    }
    
    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }
    
    public BigDecimal getProductPrice() {
        if (productPrice == null && product != null) {
            productPrice = product.getPrice();
        }
        return productPrice;
    }
    
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }
    
    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
        if (wishlist != null) {
            this.wishlistID = wishlist.getWishlistID();
        }
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productID = product.getProductID();
            this.productName = product.getProductName();
            this.productImageUrl = product.getImageUrl();
            this.productPrice = product.getPrice();
        }
    }
    
    
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = new Date();
        }
    }
}

