package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng Wishlists trong database SmartShopDB
 */
public class Wishlist {
    
    private int wishlistID;
    private int userID;
    private Date createdAt;
    
    public Wishlist() {
        this.createdAt = new Date();
    }
    
    public Wishlist(int wishlistID, int userID, Date createdAt) {
        this.wishlistID = wishlistID;
        this.userID = userID;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getWishlistID() {
        return wishlistID;
    }
    
    public void setWishlistID(int wishlistID) {
        this.wishlistID = wishlistID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

