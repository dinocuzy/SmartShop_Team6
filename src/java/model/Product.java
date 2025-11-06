package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class đại diện cho bảng Products trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class Product {
    
    private int productID;
    private int categoryID;
    
    // Không phải cột trong database, được JOIN từ bảng Categories
    private String categoryName;
    
    private String productName;
    private String slug;
    private String description;
    private BigDecimal price;
    private String size;
    private String color;
    private boolean isSpecial;
    private int stock;
    private String stockStatus;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;
    
    // Relationship với Category (Many-to-One) - chỉ để reference, không dùng JPA
    private Category category;
    
    /**
     * Constructor mặc định không tham số
     */
    public Product() {
        this.isSpecial = false;
        this.stock = 0;
        this.createdAt = new Date();
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public Product(int productID, int categoryID, String productName, String slug, 
                   String description, BigDecimal price,
                   String size, String color, boolean isSpecial, int stock, 
                   String stockStatus, String imageUrl, Date createdAt, Date updatedAt) {
        this.productID = productID;
        this.categoryID = categoryID;
        this.productName = productName;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.size = size;
        this.color = color;
        this.isSpecial = isSpecial;
        this.stock = stock;
        this.stockStatus = stockStatus;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter và Setter methods
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public int getCategoryID() {
        return categoryID;
    }
    
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    
    public String getCategoryName() {
        if (categoryName == null && category != null) {
            categoryName = category.getCategoryName();
        }
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    /**
     * Set giá sản phẩm với validation
     * @param price Giá sản phẩm (phải >= 0)
     * @throws IllegalArgumentException nếu giá < 0
     */
    public void setPrice(BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public boolean isSpecial() {
        return isSpecial;
    }
    
    public void setSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }
    
    public int getStock() {
        return stock;
    }
    
    /**
     * Set số lượng tồn kho với validation
     * @param stock Số lượng (phải >= 0)
     * @throws IllegalArgumentException nếu stock < 0
     */
    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        this.stock = stock;
    }
    
    public String getStockStatus() {
        return stockStatus;
    }
    
    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
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
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryID = category.getCategoryID();
            this.categoryName = category.getCategoryName();
        }
    }
    
    
    // Backward compatibility: alias methods for isActive (using stockStatus)
    /**
     * Kiểm tra sản phẩm có đang hoạt động không
     * Dựa vào StockStatus: nếu là 'InStock' hoặc null thì là active
     */
    public boolean isActive() {
        return stockStatus == null || "InStock".equalsIgnoreCase(stockStatus);
    }
    
    /**
     * Set trạng thái active (sử dụng StockStatus)
     */
    public void setActive(boolean active) {
        if (active) {
            this.stockStatus = "InStock";
        } else {
            this.stockStatus = "OutOfStock";
        }
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", categoryID=" + categoryID +
                ", productName='" + productName + '\'' +
                ", slug='" + slug + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", stockStatus='" + stockStatus + '\'' +
                ", isSpecial=" + isSpecial +
                '}';
    }
}
