package model;

import java.util.Date;

/**
 * Entity class đại diện cho bảng Users trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class User {
    
    private int userID;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private int roleID;
    
    // Không phải cột trong database, được JOIN từ bảng Roles
    private String roleName;
    
    private Date createdAt;
    private boolean isActive;
    
    // Relationship với Role (Many-to-One) - chỉ để reference, không dùng JPA
    private Role role;
    
    /**
     * Constructor mặc định không tham số
     */
    public User() {
        this.isActive = true;
        this.createdAt = new Date();
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public User(int userID, String fullName, String email, String passwordHash, 
                String phone, int roleID, Date createdAt, boolean isActive) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.roleID = roleID;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }
    
    // Getter và Setter methods
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public int getRoleID() {
        return roleID;
    }
    
    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }
    
    public String getRoleName() {
        if (roleName == null && role != null) {
            roleName = role.getRoleName();
        }
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
        if (role != null) {
            this.roleID = role.getRoleID();
            this.roleName = role.getRoleName();
        }
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    
    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roleID=" + roleID +
                ", isActive=" + isActive +
                '}';
    }
}
