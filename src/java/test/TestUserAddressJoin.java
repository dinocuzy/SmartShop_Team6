package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DBConnection;

/**
 * Test class để kiểm tra JOIN giữa Users và Addresses
 */
public class TestUserAddressJoin {
    
    public static void main(String[] args) {
        System.out.println("=== TEST JOIN Users và Addresses ===\n");
        
        // Test 1: LEFT JOIN - Lấy tất cả users và địa chỉ (nếu có)
        testLeftJoin();
        
        // Test 2: Đếm số địa chỉ mỗi user
        testCountAddresses();
        
        // Test 3: Lấy địa chỉ mặc định của mỗi user
        testDefaultAddress();
        
        // Test 4: INNER JOIN - Chỉ lấy user có địa chỉ
        testInnerJoin();
        
        // Test 5: Lấy địa chỉ của một user cụ thể
        testGetUserAddresses(6); // Customer 2 có 2 địa chỉ
        
        System.out.println("\n=== ALL TESTS COMPLETED ===");
    }
    
    /**
     * Test 1: LEFT JOIN - Lấy tất cả users và địa chỉ của họ
     */
    private static void testLeftJoin() {
        System.out.println("--- Test 1: LEFT JOIN Users và Addresses ---");
        
        String sql = "SELECT " +
                     "    u.UserID, " +
                     "    u.FullName AS UserName, " +
                     "    u.Email, " +
                     "    a.AddressID, " +
                     "    a.FullName AS AddressFullName, " +
                     "    a.Line1, " +
                     "    a.City, " +
                     "    a.IsDefault " +
                     "FROM Users u " +
                     "LEFT JOIN Addresses a ON u.UserID = a.UserID " +
                     "ORDER BY u.UserID, a.IsDefault DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.printf("UserID: %d | Name: %s | Email: %s | ", 
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("Email"));
                
                int addressID = rs.getInt("AddressID");
                if (rs.wasNull()) {
                    System.out.println("Address: NULL (Không có địa chỉ)");
                } else {
                    System.out.printf("AddressID: %d | Address: %s, %s | IsDefault: %s%n",
                        addressID,
                        rs.getString("Line1"),
                        rs.getString("City"),
                        rs.getBoolean("IsDefault") ? "YES" : "NO");
                }
            }
            System.out.printf("Tổng số records: %d%n%n", count);
            
        } catch (SQLException e) {
            System.err.println("Error in testLeftJoin: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 2: Đếm số địa chỉ mỗi user
     */
    private static void testCountAddresses() {
        System.out.println("--- Test 2: Số lượng địa chỉ mỗi user ---");
        
        String sql = "SELECT " +
                     "    u.UserID, " +
                     "    u.FullName, " +
                     "    u.Email, " +
                     "    COUNT(a.AddressID) AS AddressCount " +
                     "FROM Users u " +
                     "LEFT JOIN Addresses a ON u.UserID = a.UserID " +
                     "GROUP BY u.UserID, u.FullName, u.Email " +
                     "ORDER BY u.UserID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                System.out.printf("UserID: %d | Name: %s | Email: %s | AddressCount: %d%n",
                    rs.getInt("UserID"),
                    rs.getString("FullName"),
                    rs.getString("Email"),
                    rs.getInt("AddressCount"));
            }
            System.out.println();
            
        } catch (SQLException e) {
            System.err.println("Error in testCountAddresses: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 3: Lấy địa chỉ mặc định của mỗi user (giống code trong UserServlet)
     */
    private static void testDefaultAddress() {
        System.out.println("--- Test 3: Địa chỉ mặc định của mỗi user ---");
        
        String sql = "SELECT " +
                     "    u.UserID, " +
                     "    u.FullName AS UserName, " +
                     "    u.Email, " +
                     "    a.AddressID, " +
                     "    a.FullName AS AddressFullName, " +
                     "    a.Line1 + ', ' + ISNULL(a.District + ', ', '') + a.City + ', ' + a.Country AS FullAddress, " +
                     "    a.IsDefault " +
                     "FROM Users u " +
                     "LEFT JOIN Addresses a ON u.UserID = a.UserID AND a.IsDefault = 1 " +
                     "ORDER BY u.UserID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                System.out.printf("UserID: %d | Name: %s | Email: %s | ", 
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("Email"));
                
                int addressID = rs.getInt("AddressID");
                if (rs.wasNull()) {
                    System.out.println("Default Address: NULL (Không có địa chỉ mặc định)");
                } else {
                    System.out.printf("Default Address: %s%n", rs.getString("FullAddress"));
                }
            }
            System.out.println();
            
        } catch (SQLException e) {
            System.err.println("Error in testDefaultAddress: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 4: INNER JOIN - Chỉ lấy user có địa chỉ
     */
    private static void testInnerJoin() {
        System.out.println("--- Test 4: INNER JOIN (chỉ user có địa chỉ) ---");
        
        String sql = "SELECT " +
                     "    u.UserID, " +
                     "    u.FullName AS UserName, " +
                     "    u.Email, " +
                     "    a.AddressID, " +
                     "    a.Line1 + ', ' + a.City AS Address " +
                     "FROM Users u " +
                     "INNER JOIN Addresses a ON u.UserID = a.UserID " +
                     "ORDER BY u.UserID, a.IsDefault DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.printf("UserID: %d | Name: %s | Email: %s | Address: %s%n",
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("Email"),
                    rs.getString("Address"));
            }
            System.out.printf("Tổng số records: %d%n%n", count);
            
        } catch (SQLException e) {
            System.err.println("Error in testInnerJoin: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 5: Lấy tất cả địa chỉ của một user cụ thể (giống code trong AddressDAO)
     */
    private static void testGetUserAddresses(int userID) {
        System.out.printf("--- Test 5: Lấy tất cả địa chỉ của UserID = %d ---%n", userID);
        
        String sql = "SELECT * FROM Addresses WHERE UserID = ? ORDER BY IsDefault DESC, CreatedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.printf("AddressID: %d | FullName: %s | Phone: %s | " +
                                     "Address: %s, %s, %s | IsDefault: %s%n",
                        rs.getInt("AddressID"),
                        rs.getString("FullName"),
                        rs.getString("Phone"),
                        rs.getString("Line1"),
                        rs.getString("City"),
                        rs.getString("Country"),
                        rs.getBoolean("IsDefault") ? "YES" : "NO");
                }
                
                if (count == 0) {
                    System.out.printf("UserID %d không có địa chỉ nào%n", userID);
                } else {
                    System.out.printf("Tổng số địa chỉ: %d%n", count);
                }
            }
            System.out.println();
            
        } catch (SQLException e) {
            System.err.println("Error in testGetUserAddresses: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
