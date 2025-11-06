package model;

/**
 * Entity class đại diện cho bảng OAuthProviders trong database SmartShopDB
 */
public class OAuthProvider {
    
    private int providerID;
    private String providerName;
    
    public OAuthProvider() {
    }
    
    public OAuthProvider(int providerID, String providerName) {
        this.providerID = providerID;
        this.providerName = providerName;
    }
    
    // Getters and Setters
    
    public int getProviderID() {
        return providerID;
    }
    
    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}

