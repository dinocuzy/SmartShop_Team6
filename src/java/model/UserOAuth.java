package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng UserOAuth trong database SmartShopDB
 */
public class UserOAuth {
    
    private int userOAuthID;
    private int userID;
    private int providerID;
    private String providerUserID;
    private String accessToken;
    private String refreshToken;
    private Date linkedAt;
    
    // Có thể thêm thông tin từ join
    private String providerName;
    private String userName;
    
    public UserOAuth() {
        this.linkedAt = new Date();
    }
    
    public UserOAuth(int userOAuthID, int userID, int providerID, String providerUserID, 
                    String accessToken, String refreshToken, Date linkedAt) {
        this.userOAuthID = userOAuthID;
        this.userID = userID;
        this.providerID = providerID;
        this.providerUserID = providerUserID;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.linkedAt = linkedAt;
    }
    
    // Getters and Setters
    
    public int getUserOAuthID() {
        return userOAuthID;
    }
    
    public void setUserOAuthID(int userOAuthID) {
        this.userOAuthID = userOAuthID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public int getProviderID() {
        return providerID;
    }
    
    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }
    
    public String getProviderUserID() {
        return providerUserID;
    }
    
    public void setProviderUserID(String providerUserID) {
        this.providerUserID = providerUserID;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public Date getLinkedAt() {
        return linkedAt;
    }
    
    public void setLinkedAt(Date linkedAt) {
        this.linkedAt = linkedAt;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
}

