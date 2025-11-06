package userdao;

import model.User;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDAO implements IUserDAO {
    
    @Override
    public List<User> getAll(boolean includeInactive) {
        List<User> users = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT u.UserID, u.RoleID, u.Email, u.PasswordHash, u.FullName, u.Phone, " +
                  "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                  "LEFT JOIN Roles r ON u.RoleID = r.RoleID ORDER BY u.UserID ASC";
        } else {
            sql = "SELECT u.UserID, u.RoleID, u.Email, u.Password, u.FullName, u.Phone, " +
                  "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                  "LEFT JOIN Roles r ON u.RoleID = r.RoleID WHERE u.IsActive = 1 ORDER BY u.UserID ASC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                user.setRoleName(rs.getString("RoleName"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    @Override
    public User getById(int userID) {
        String sql = "SELECT u.UserID, u.RoleID, u.Email, u.PasswordHash, u.FullName, u.Phone, " +
                     "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID WHERE u.UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    user.setRoleName(rs.getString("RoleName"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public User getByEmail(String email) {
        String sql = "SELECT u.UserID, u.RoleID, u.Email, u.PasswordHash, u.FullName, u.Phone, " +
                     "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID WHERE u.Email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    user.setRoleName(rs.getString("RoleName"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<User> getByRole(int roleID, boolean includeInactive) {
        List<User> users = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT u.UserID, u.RoleID, u.Email, u.Password, u.FullName, u.Phone, " +
                  "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                  "LEFT JOIN Roles r ON u.RoleID = r.RoleID WHERE u.RoleID = ? ORDER BY u.UserID ASC";
        } else {
            sql = "SELECT u.UserID, u.RoleID, u.Email, u.Password, u.FullName, u.Phone, " +
                  "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                  "LEFT JOIN Roles r ON u.RoleID = r.RoleID WHERE u.RoleID = ? AND u.IsActive = 1 ORDER BY u.UserID ASC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roleID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    user.setRoleName(rs.getString("RoleName"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting users by role: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    @Override
    public List<User> search(String keyword, boolean includeInactive) {
        List<User> users = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT u.UserID, u.RoleID, u.Email, u.Password, u.FullName, u.Phone, " +
                  "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                  "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                  "WHERE u.FullName LIKE ? OR u.Email LIKE ? ORDER BY u.UserID ASC";
        } else {
            sql = "SELECT u.UserID, u.RoleID, u.Email, u.Password, u.FullName, u.Phone, " +
                  "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                  "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                  "WHERE (u.FullName LIKE ? OR u.Email LIKE ?) AND u.IsActive = 1 ORDER BY u.UserID ASC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    user.setRoleName(rs.getString("RoleName"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    @Override
    public List<User> getPagedUsers(int pageNumber, int pageSize, String sortBy, String sortOrder,
                                     String searchKeyword, int roleID, boolean includeInactive) {
        List<User> users = new ArrayList<>();
        
        // Validate và set defaults
        if (pageNumber < 1) pageNumber = 1;
        if (pageSize < 1) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "UserID";
        if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "ASC";
        
        // Validate sortBy để tránh SQL injection
        String[] allowedColumns = {"UserID", "FullName", "Email", "CreatedAt"};
        boolean isValid = false;
        for (String col : allowedColumns) {
            if (sortBy.equalsIgnoreCase(col)) {
                sortBy = col;
                isValid = true;
                break;
            }
        }
        if (!isValid) sortBy = "UserID";
        
        if (!sortOrder.equalsIgnoreCase("DESC")) sortOrder = "ASC";
        
        // Build WHERE clause
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        if (!includeInactive) {
            whereClause.append("u.IsActive = 1");
        }
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("(u.FullName LIKE ? OR u.Email LIKE ?)");
            params.add("%" + searchKeyword.trim() + "%");
            params.add("%" + searchKeyword.trim() + "%");
        }
        
        if (roleID > 0) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("u.RoleID = ?");
            params.add(roleID);
        }
        
        String where = whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "";
        
        // Build SQL với OFFSET/FETCH (SQL Server pagination)
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT u.UserID, u.RoleID, u.Email, u.PasswordHash, u.FullName, u.Phone, " +
                     "u.IsActive, u.CreatedAt, r.RoleName FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                     where + " ORDER BY u." + sortBy + " " + sortOrder +
                     " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    ps.setString(paramIndex++, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(paramIndex++, (Integer) param);
                }
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    user.setRoleName(rs.getString("RoleName"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting paged users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    @Override
    public int count(String searchKeyword, int roleID, boolean includeInactive) {
        // Build WHERE clause
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        if (!includeInactive) {
            whereClause.append("u.IsActive = 1");
        }
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("(u.FullName LIKE ? OR u.Email LIKE ?)");
            params.add("%" + searchKeyword.trim() + "%");
            params.add("%" + searchKeyword.trim() + "%");
        }
        
        if (roleID > 0) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("u.RoleID = ?");
            params.add(roleID);
        }
        
        String where = whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "";
        String sql = "SELECT COUNT(*) FROM Users u " + where;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    ps.setString(paramIndex++, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(paramIndex++, (Integer) param);
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int insert(User user) {
        String sql = "INSERT INTO Users (RoleID, Email, PasswordHash, FullName, Phone, IsActive, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, user.getRoleID());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setBoolean(6, user.isActive());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        user.setUserID(generatedId);
                        System.out.println("Inserted user ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(User user) {
        String sql = "UPDATE Users SET RoleID = ?, Email = ?, PasswordHash = ?, FullName = ?, Phone = ?, IsActive = ? " +
                     "WHERE UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, user.getRoleID());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setBoolean(6, user.isActive());
            ps.setInt(7, user.getUserID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated user ID: " + user.getUserID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int userID) {
        // Soft delete: set IsActive = 0
        String sql = "UPDATE Users SET IsActive = 0 WHERE UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted user ID: " + userID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setRoleID(rs.getInt("RoleID"));
        user.setEmail(rs.getString("Email"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setFullName(rs.getString("FullName"));
        user.setPhone(rs.getString("Phone"));
        user.setActive(rs.getBoolean("IsActive"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            user.setCreatedAt(new Date(createdAt.getTime()));
        }
        
        return user;
    }
}
