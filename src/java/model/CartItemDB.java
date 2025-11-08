package model;

import java.util.Date;

/**
 * Model đại diện cho CartItem trong database (bảng CartItems)
 * Khác với CartItem dùng cho session - model này có thêm CartItemID và AddedDate
 */
public class CartItemDB {
    
    private int cartItemID;
    private int userID;
    private int productID;
    private int quantity;
    private Date addedDate;
    
    // Relationship với Product (để lấy thông tin sản phẩm)
    private Product product;
    
    public CartItemDB() {
        this.addedDate = new Date();
    }
    
    public CartItemDB(int userID, int productID, int quantity) {
        this.userID = userID;
        this.productID = productID;
        this.quantity = quantity;
        this.addedDate = new Date();
    }
    
    public CartItemDB(int cartItemID, int userID, int productID, int quantity, Date addedDate) {
        this.cartItemID = cartItemID;
        this.userID = userID;
        this.productID = productID;
        this.quantity = quantity;
        this.addedDate = addedDate;
    }
    
    // Getters and Setters
    
    public int getCartItemID() {
        return cartItemID;
    }
    
    public void setCartItemID(int cartItemID) {
        this.cartItemID = cartItemID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public Date getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}

