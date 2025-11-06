package model;

/**
 * Entity class đại diện cho bảng Roles trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class Role {
    
    private int roleID;
    private String roleName;
    private String description;
    
    /**
     * Constructor mặc định không tham số
     */
    public Role() {
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public Role(int roleID, String roleName, String description) {
        this.roleID = roleID;
        this.roleName = roleName;
        this.description = description;
    }
    
    // Getter và Setter methods
    
    public int getRoleID() {
        return roleID;
    }
    
    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "roleID=" + roleID +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
