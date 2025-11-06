package roledao;

import model.Role;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO implements IRoleDAO {
    
    @Override
    public List<Role> getAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT RoleID, RoleName, Description FROM Roles ORDER BY RoleName ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Role role = new Role();
                role.setRoleID(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
                role.setDescription(rs.getString("Description"));
                roles.add(role);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all roles: " + e.getMessage());
            e.printStackTrace();
        }
        return roles;
    }
    
    @Override
    public Role getById(int roleID) {
        String sql = "SELECT RoleID, RoleName, Description FROM Roles WHERE RoleID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roleID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setRoleID(rs.getInt("RoleID"));
                    role.setRoleName(rs.getString("RoleName"));
                    role.setDescription(rs.getString("Description"));
                    return role;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting role by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Role getByName(String roleName) {
        String sql = "SELECT RoleID, RoleName, Description FROM Roles WHERE RoleName = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roleName);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setRoleID(rs.getInt("RoleID"));
                    role.setRoleName(rs.getString("RoleName"));
                    role.setDescription(rs.getString("Description"));
                    return role;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting role by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public int insert(Role role) {
        String sql = "INSERT INTO Roles (RoleName, Description) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, role.getRoleName());
            ps.setString(2, role.getDescription());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        role.setRoleID(generatedId);
                        System.out.println("Inserted role ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting role: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(Role role) {
        String sql = "UPDATE Roles SET RoleName = ?, Description = ? WHERE RoleID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, role.getRoleName());
            ps.setString(2, role.getDescription());
            ps.setInt(3, role.getRoleID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated role ID: " + role.getRoleID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int roleID) {
        String sql = "DELETE FROM Roles WHERE RoleID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roleID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted role ID: " + roleID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
