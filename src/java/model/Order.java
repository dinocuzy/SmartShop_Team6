package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class đại diện cho bảng Orders trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class Order {
    
    private int orderID;
    private int userID;
    
    // Không phải cột trong database, được JOIN từ bảng Users
    private String userName;
    
    // Không phải cột trong database, được JOIN từ bảng Payments
    private String paymentStatus;
    
    private Integer billingAddressID;
    private Integer shippingAddressID;
    private String orderStatus;
    private Date orderDate;
    private BigDecimal totalAmount;
    private String note;
    
    // Relationships - chỉ để reference, không dùng JPA
    private User user;
    private Address billingAddress;
    private Address shippingAddress;
    
    /**
     * Constructor mặc định không tham số
     */
    public Order() {
        this.orderDate = new Date();
        this.totalAmount = BigDecimal.ZERO;
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public Order(int orderID, int userID, Integer billingAddressID, Integer shippingAddressID,
                 String orderStatus, Date orderDate, BigDecimal totalAmount, String note) {
        this.orderID = orderID;
        this.userID = userID;
        this.billingAddressID = billingAddressID;
        this.shippingAddressID = shippingAddressID;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.note = note;
    }
    
    // Getter và Setter methods
    
    public int getOrderID() {
        return orderID;
    }
    
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public String getUserName() {
        if (userName == null && user != null) {
            userName = user.getFullName();
        }
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Integer getBillingAddressID() {
        return billingAddressID;
    }
    
    public void setBillingAddressID(Integer billingAddressID) {
        this.billingAddressID = billingAddressID;
    }
    
    public Integer getShippingAddressID() {
        return shippingAddressID;
    }
    
    public void setShippingAddressID(Integer shippingAddressID) {
        this.shippingAddressID = shippingAddressID;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userID = user.getUserID();
            this.userName = user.getFullName();
        }
    }
    
    public Address getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
        if (billingAddress != null) {
            this.billingAddressID = billingAddress.getAddressID();
        }
    }
    
    public Address getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
        if (shippingAddress != null) {
            this.shippingAddressID = shippingAddress.getAddressID();
        }
    }
    
    
    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", userID=" + userID +
                ", orderStatus='" + orderStatus + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
