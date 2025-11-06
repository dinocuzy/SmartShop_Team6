package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class đại diện cho bảng Payments trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class Payment {
    
    private int paymentID;
    private int orderID;
    private int paymentMethodID;
    
    // Không phải cột trong database, được JOIN từ bảng PaymentMethods
    private String methodName;
    
    private BigDecimal amount;
    private String paymentStatus;
    private Date paymentDate;
    private String transactionCode;
    
    // Relationships - chỉ để reference, không dùng JPA
    private Order order;
    private PaymentMethod paymentMethod;
    
    /**
     * Constructor mặc định không tham số
     */
    public Payment() {
        this.paymentDate = new Date();
        this.amount = BigDecimal.ZERO;
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public Payment(int paymentID, int orderID, int paymentMethodID, BigDecimal amount,
                   String paymentStatus, Date paymentDate, String transactionCode) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.paymentMethodID = paymentMethodID;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.transactionCode = transactionCode;
    }
    
    // Getter và Setter methods
    
    public int getPaymentID() {
        return paymentID;
    }
    
    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }
    
    public int getOrderID() {
        return orderID;
    }
    
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public int getPaymentMethodID() {
        return paymentMethodID;
    }
    
    public void setPaymentMethodID(int paymentMethodID) {
        this.paymentMethodID = paymentMethodID;
    }
    
    public String getMethodName() {
        if (methodName == null && paymentMethod != null) {
            methodName = paymentMethod.getMethodName();
        }
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Date getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getTransactionCode() {
        return transactionCode;
    }
    
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
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
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        if (paymentMethod != null) {
            this.paymentMethodID = paymentMethod.getPaymentMethodID();
            this.methodName = paymentMethod.getMethodName();
        }
    }
    
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + paymentID +
                ", orderID=" + orderID +
                ", amount=" + amount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
