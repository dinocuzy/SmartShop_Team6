package userservice;

import model.User;
import model.Address;
import userdao.IUserDAO;
import userdao.UserDAO;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation của IUserService
 * Chứa business logic và validation cho User
 * Sử dụng UserDAO (JDBC) để truy cập dữ liệu
 */
public class UserService implements IUserService {

    private final IUserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    @Override
    public int addUser(User user) {
        validateUser(user);
        
        // Kiểm tra email đã tồn tại chưa
        User existingUser = userDAO.getByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        int generatedId = userDAO.insert(user);
        if (generatedId <= 0) {
            throw new RuntimeException("Failed to insert user. Generated ID is invalid: " + generatedId);
        }
        return generatedId;
    }

    @Override
    public void updateUser(User user) {
        validateUser(user);
        if (user.getUserID() <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        
        // Kiểm tra email đã tồn tại ở user khác chưa
        User existingUser = userDAO.getByEmail(user.getEmail());
        if (existingUser != null && existingUser.getUserID() != user.getUserID()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        boolean updated = userDAO.update(user);
        if (!updated) {
            throw new RuntimeException("Failed to update user with ID: " + user.getUserID());
        }
    }

    @Override
    public void deleteUser(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        
        // Kiểm tra user có phải Admin không
        User user = userDAO.getById(userID);
        if (user != null) {
            String roleName = user.getRoleName();
            if (roleName != null && roleName.equalsIgnoreCase("Admin")) {
                throw new IllegalArgumentException("Không thể xóa tài khoản Admin");
            }
            // Hoặc kiểm tra RoleID = 1 (thường Admin có RoleID = 1)
            if (user.getRoleID() == 1) {
                throw new IllegalArgumentException("Không thể xóa tài khoản Admin");
            }
        }
        
        boolean deleted = userDAO.delete(userID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete user with ID: " + userID);
        }
    }

    @Override
    public User getUserById(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        return userDAO.getById(userID);
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        // DAO sẽ return null nếu user không tồn tại hoặc có lỗi database
        return userDAO.getByEmail(email.trim());
    }

    @Override
    public List<User> getAllUsers(boolean includeInactive) {
        return userDAO.getAll(includeInactive);
    }

    @Override
    public List<User> getUsersByRole(int roleID, boolean includeInactive) {
        if (roleID <= 0) {
            throw new IllegalArgumentException("Role ID must be greater than 0");
        }
        return userDAO.getByRole(roleID, includeInactive);
    }

    @Override
    public List<User> searchUsers(String keyword, boolean includeInactive) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers(includeInactive);
        }
        return userDAO.search(keyword, includeInactive);
    }

    @Override
    public List<User> getPagedUsers(int pageNumber, int pageSize, String sortBy, String sortOrder, 
                                    String searchKeyword, int roleID, boolean includeInactive) {
        return userDAO.getPagedUsers(pageNumber, pageSize, sortBy, sortOrder, searchKeyword, roleID, includeInactive);
    }

    @Override
    public int countUsers(String searchKeyword, int roleID, boolean includeInactive) {
        return userDAO.count(searchKeyword, roleID, includeInactive);
    }

    @Override
    public Map<User, Address> getPagedUsersWithDefaultAddress(
            int pageNumber, int pageSize, String sortBy, String sortOrder,
            String searchKeyword, int roleID, boolean includeInactive) {
        
        Map<User, Address> userAddressMap = new HashMap<>();
        
        // Validate và set defaults
        if (pageNumber < 1) pageNumber = 1;
        if (pageSize < 1) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "UserID";
        if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "ASC";
        
        // Validate sortBy để tránh SQL injection
        String[] allowedSortColumns = {"UserID", "FullName", "Email", "CreatedAt"};
        boolean isValidSort = false;
        for (String col : allowedSortColumns) {
            if (sortBy.equalsIgnoreCase(col)) {
                isValidSort = true;
                sortBy = col;
                break;
            }
        }
        if (!isValidSort) {
            sortBy = "UserID";
        }
        
        if (!sortOrder.equalsIgnoreCase("DESC")) sortOrder = "ASC";
        
        // Xây dựng WHERE clause
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
        
        // SQL Server pagination với JDBC
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT " +
                     "    u.UserID, u.FullName, u.Email, u.PasswordHash, u.Phone, " +
                     "    u.RoleID, u.CreatedAt, u.IsActive, " +
                     "    r.RoleName, " +
                     "    a.AddressID, a.FullName AS AddressFullName, a.Phone AS AddressPhone, " +
                     "    a.Line1, a.Line2, a.City, a.District, a.Ward, a.Country, a.PostalCode, " +
                     "    a.IsDefault AS AddressIsDefault, a.CreatedAt AS AddressCreatedAt " +
                     "FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                     "LEFT JOIN Addresses a ON u.UserID = a.UserID AND a.IsDefault = 1 " +
                     where + " ORDER BY u." + sortBy + " " + sortOrder +
                     " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            // Set các tham số cho WHERE clause
            for (Object param : params) {
                if (param instanceof String) {
                    ps.setString(paramIndex++, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(paramIndex++, (Integer) param);
                }
            }
            // Set OFFSET và FETCH NEXT
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        // Map User
                        User user = new User();
                        user.setUserID(rs.getInt("UserID"));
                        user.setFullName(rs.getString("FullName"));
                        user.setEmail(rs.getString("Email"));
                        
                        String password = rs.getString("PasswordHash");
                        if (password != null) {
                            user.setPasswordHash(password);
                        }
                        
                        user.setPhone(rs.getString("Phone"));
                        user.setRoleID(rs.getInt("RoleID"));
                        
                        Timestamp createdAt = rs.getTimestamp("CreatedAt");
                        if (createdAt != null) {
                            user.setCreatedAt(new Date(createdAt.getTime()));
                        }
                        
                        user.setActive(rs.getBoolean("IsActive"));
                        
                        // Set roleName
                        String roleName = rs.getString("RoleName");
                        if (roleName != null) {
                            user.setRoleName(roleName);
                        }
                        
                        // Map Address nếu có
                        Address address = null;
                        int addressID = rs.getInt("AddressID");
                        if (!rs.wasNull() && addressID > 0) {
                            address = new Address();
                            address.setAddressID(addressID);
                            address.setFullName(rs.getString("AddressFullName"));
                            address.setPhone(rs.getString("AddressPhone"));
                            address.setLine1(rs.getString("Line1"));
                            address.setLine2(rs.getString("Line2"));
                            address.setCity(rs.getString("City"));
                            address.setDistrict(rs.getString("District"));
                            address.setWard(rs.getString("Ward"));
                            address.setCountry(rs.getString("Country"));
                            address.setPostalCode(rs.getString("PostalCode"));
                            address.setDefault(rs.getBoolean("AddressIsDefault"));
                            
                            Timestamp addressCreatedAt = rs.getTimestamp("AddressCreatedAt");
                            if (addressCreatedAt != null) {
                                address.setCreatedAt(new Date(addressCreatedAt.getTime()));
                            }
                        }
                        
                        userAddressMap.put(user, address);
                    } catch (Exception e) {
                        System.err.println("Error mapping user/address: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting paged users with default address: " + e.getMessage());
            e.printStackTrace();
        }
        
        return userAddressMap;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        // Validate email format
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getRoleID() <= 0) {
            throw new IllegalArgumentException("Role ID must be greater than 0");
        }
    }
}
