package analyticsdao;

import util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation của IAnalyticsDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class AnalyticsDAO implements IAnalyticsDAO {
    
    @Override
    public BigDecimal getTotalRevenue(Date startDate, Date endDate) {
        String sql = "SELECT ISNULL(SUM(TotalAmount), 0) AS TotalRevenue FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (startDate != null) {
            sql += " AND OrderDate >= ?";
            params.add(startDate);
        }
        if (endDate != null) {
            sql += " AND OrderDate <= ?";
            params.add(endDate);
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("TotalRevenue");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public int getTotalOrders(Date startDate, Date endDate) {
        String sql = "SELECT COUNT(*) AS TotalOrders FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (startDate != null) {
            sql += " AND OrderDate >= ?";
            params.add(startDate);
        }
        if (endDate != null) {
            sql += " AND OrderDate <= ?";
            params.add(endDate);
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalOrders");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int getTotalViews(Date startDate, Date endDate) {
        String sql = "SELECT COUNT(*) AS TotalViews FROM ProductViews WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (startDate != null) {
            sql += " AND ViewedAt >= ?";
            params.add(startDate);
        }
        if (endDate != null) {
            sql += " AND ViewedAt <= ?";
            params.add(endDate);
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalViews");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total views: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public BigDecimal getAvgRevenuePerOrder(Date startDate, Date endDate) {
        String sql = "SELECT ISNULL(AVG(TotalAmount), 0) AS AvgRevenue FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (startDate != null) {
            sql += " AND OrderDate >= ?";
            params.add(startDate);
        }
        if (endDate != null) {
            sql += " AND OrderDate <= ?";
            params.add(endDate);
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("AvgRevenue");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting average revenue per order: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public List<Map<String, Object>> getRevenueByDate(Date startDate, Date endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT CAST(OrderDate AS DATE) AS OrderDate, SUM(TotalAmount) AS Revenue " +
                     "FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (startDate != null) {
            sql += " AND OrderDate >= ?";
            params.add(startDate);
        }
        if (endDate != null) {
            sql += " AND OrderDate <= ?";
            params.add(endDate);
        }
        
        sql += " GROUP BY CAST(OrderDate AS DATE) ORDER BY OrderDate ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    Date date = rs.getDate("OrderDate");
                    item.put("date", sdf.format(date));
                    item.put("revenue", rs.getBigDecimal("Revenue"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getOrdersByDate(Date startDate, Date endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT CAST(OrderDate AS DATE) AS OrderDate, COUNT(*) AS Orders " +
                     "FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (startDate != null) {
            sql += " AND OrderDate >= ?";
            params.add(startDate);
        }
        if (endDate != null) {
            sql += " AND OrderDate <= ?";
            params.add(endDate);
        }
        
        sql += " GROUP BY CAST(OrderDate AS DATE) ORDER BY OrderDate ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    Date date = rs.getDate("OrderDate");
                    item.put("date", sdf.format(date));
                    item.put("orders", rs.getInt("Orders"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getViewsByDate(Date startDate, Date endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT CAST(ViewedAt AS DATE) AS ViewDate, COUNT(*) AS Views " +
                     "FROM ProductViews WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (startDate != null) {
            sql += " AND ViewedAt >= ?";
            params.add(startDate);
        }
        if (endDate != null) {
            sql += " AND ViewedAt <= ?";
            params.add(endDate);
        }
        
        sql += " GROUP BY CAST(ViewedAt AS DATE) ORDER BY ViewDate ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    Date date = rs.getDate("ViewDate");
                    if (date != null) {
                        item.put("date", sdf.format(date));
                        item.put("views", rs.getInt("Views"));
                        result.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting views by date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getRevenueByMonth(Integer year) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT YEAR(OrderDate) AS Year, MONTH(OrderDate) AS Month, SUM(TotalAmount) AS Revenue " +
                     "FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (year != null) {
            sql += " AND YEAR(OrderDate) = ?";
            params.add(year);
        }
        
        sql += " GROUP BY YEAR(OrderDate), MONTH(OrderDate) ORDER BY Year ASC, Month ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    int month = rs.getInt("Month");
                    int yearValue = rs.getInt("Year");
                    item.put("month", String.format("%d/%d", month, yearValue));
                    item.put("revenue", rs.getBigDecimal("Revenue"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by month: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getOrdersByMonth(Integer year) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT YEAR(OrderDate) AS Year, MONTH(OrderDate) AS Month, COUNT(*) AS Orders " +
                     "FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (year != null) {
            sql += " AND YEAR(OrderDate) = ?";
            params.add(year);
        }
        
        sql += " GROUP BY YEAR(OrderDate), MONTH(OrderDate) ORDER BY Year ASC, Month ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    int month = rs.getInt("Month");
                    int yearValue = rs.getInt("Year");
                    item.put("month", String.format("%d/%d", month, yearValue));
                    item.put("orders", rs.getInt("Orders"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by month: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getViewsByMonth(Integer year) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT YEAR(ViewedAt) AS Year, MONTH(ViewedAt) AS Month, COUNT(*) AS Views " +
                     "FROM ProductViews WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (year != null) {
            sql += " AND YEAR(ViewedAt) = ?";
            params.add(year);
        }
        
        sql += " GROUP BY YEAR(ViewedAt), MONTH(ViewedAt) ORDER BY Year ASC, Month ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    int month = rs.getInt("Month");
                    int yearValue = rs.getInt("Year");
                    item.put("month", String.format("%d/%d", month, yearValue));
                    item.put("views", rs.getInt("Views"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting views by month: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getRevenueByYear() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT YEAR(OrderDate) AS Year, SUM(TotalAmount) AS Revenue " +
                     "FROM Orders " +
                     "GROUP BY YEAR(OrderDate) ORDER BY Year ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                int year = rs.getInt("Year");
                item.put("year", String.valueOf(year));
                item.put("revenue", rs.getBigDecimal("Revenue"));
                result.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by year: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getOrdersByYear() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT YEAR(OrderDate) AS Year, COUNT(*) AS Orders " +
                     "FROM Orders " +
                     "GROUP BY YEAR(OrderDate) ORDER BY Year ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                int year = rs.getInt("Year");
                item.put("year", String.valueOf(year));
                item.put("orders", rs.getInt("Orders"));
                result.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by year: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getViewsByYear() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT YEAR(ViewedAt) AS Year, COUNT(*) AS Views " +
                     "FROM ProductViews " +
                     "GROUP BY YEAR(ViewedAt) ORDER BY Year ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                int year = rs.getInt("Year");
                item.put("year", String.valueOf(year));
                item.put("views", rs.getInt("Views"));
                result.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting views by year: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}

