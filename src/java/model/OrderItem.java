package model;

import java.math.BigDecimal;

/**
 * Entity class đại diện cho bảng OrderItems trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class OrderItem {
    
    private int orderItemID;
    private int orderID;
    private int productID;
    
    // Không phải cột trong database, được JOIN từ bảng Products
    private String productName;
    
    private int quantity;
    private BigDecimal unitPrice;
    
    // Relationships - chỉ để reference, không dùng JPA
    private Order order;
    private Product product;
    
    /**
     * Constructor mặc định không tham số
     */
    public OrderItem() {
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public OrderItem(int orderItemID, int orderID, int productID, int quantity, BigDecimal unitPrice) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    // Getter và Setter methods
    
    public int getOrderItemID() {
        return orderItemID;
    }
    
    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }
    
    public int getOrderID() {
        return orderID;
    }
    
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
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
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            this.orderID = order.getOrderID();
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
        }
    }
    
    /**
     * Tính tổng tiền của order item này
     */
    public BigDecimal getSubtotal() {
        if (unitPrice != null && quantity > 0) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemID=" + orderItemID +
                ", orderID=" + orderID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
