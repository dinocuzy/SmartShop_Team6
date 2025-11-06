package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class để quản lý kết nối đến SQL Server database
 * Sử dụng cho SmartShopDB
 */
public class DBConnection {
    
    // Thông tin kết nối SQL Server
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=SmartShopDB;encrypt=false;trustServerCertificate=true";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "11012004"; // Thay đổi mật khẩu phù hợp
    
    /**
     * Lấy kết nối đến database
     * @return Connection object
     * @throws SQLException nếu có lỗi kết nối
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Đảm bảo driver được load
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found", e);
        }
        
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Đóng kết nối
     * @param conn Connection cần đóng
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
