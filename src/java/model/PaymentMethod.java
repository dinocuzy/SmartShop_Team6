package model;

/**
 * Entity class đại diện cho bảng PaymentMethods trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class PaymentMethod {
    
    private int paymentMethodID;
    private String methodName;
    private String provider;
    private boolean isActive;
    
    /**
     * Constructor mặc định không tham số
     */
    public PaymentMethod() {
        this.isActive = true;
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public PaymentMethod(int paymentMethodID, String methodName, String provider, boolean isActive) {
        this.paymentMethodID = paymentMethodID;
        this.methodName = methodName;
        this.provider = provider;
        this.isActive = isActive;
    }
    
    // Getter và Setter methods
    
    public int getPaymentMethodID() {
        return paymentMethodID;
    }
    
    public void setPaymentMethodID(int paymentMethodID) {
        this.paymentMethodID = paymentMethodID;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    @Override
    public String toString() {
        return "PaymentMethod{" +
                "paymentMethodID=" + paymentMethodID +
                ", methodName='" + methodName + '\'' +
                ", provider='" + provider + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
