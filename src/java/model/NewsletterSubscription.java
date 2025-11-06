package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng NewsletterSubscriptions trong database SmartShopDB
 */
public class NewsletterSubscription {
    
    private int subscriptionID;
    private String email;
    private Date subscribedAt;
    private boolean isActive;
    
    public NewsletterSubscription() {
        this.isActive = true;
        this.subscribedAt = new Date();
    }
    
    public NewsletterSubscription(int subscriptionID, String email, Date subscribedAt, boolean isActive) {
        this.subscriptionID = subscriptionID;
        this.email = email;
        this.subscribedAt = subscribedAt;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    
    public int getSubscriptionID() {
        return subscriptionID;
    }
    
    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getSubscribedAt() {
        return subscribedAt;
    }
    
    public void setSubscribedAt(Date subscribedAt) {
        this.subscribedAt = subscribedAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}

