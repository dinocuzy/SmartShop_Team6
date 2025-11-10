package comparelistitemdao;

import model.CompareListItem;
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

/**
 * Implementation của ICompareListItemDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class CompareListItemDAO implements ICompareListItemDAO {
    
    @Override
    public int addProduct(int compareListID, int productID) {
        // Kiểm tra xem đã có chưa
        if (isProductInList(compareListID, productID)) {
            return -1; // Đã tồn tại
        }
        
        String sql = "INSERT INTO CompareListItems (CompareListID, ProductID, AddedAt) " +
                     "VALUES (?, ?, SYSDATETIME())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, compareListID);
            ps.setInt(2, productID);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding product to compare list: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    @Override
    public int removeProduct(int compareListID, int productID) {
        String sql = "DELETE FROM CompareListItems WHERE CompareListID = ? AND ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, compareListID);
            ps.setInt(2, productID);
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing product from compare list: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public List<CompareListItem> getItemsByCompareListID(int compareListID) {
        List<CompareListItem> items = new ArrayList<>();
        String sql = "SELECT ci.CompareListItemID, ci.CompareListID, ci.ProductID, ci.AddedAt, " +
                     "p.ProductName, p.ImageUrl " +
                     "FROM CompareListItems ci " +
                     "INNER JOIN Products p ON ci.ProductID = p.ProductID " +
                     "WHERE ci.CompareListID = ? " +
                     "ORDER BY ci.AddedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, compareListID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CompareListItem item = mapResultSetToItem(rs);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting compare list items: " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
    
    @Override
    public boolean isProductInList(int compareListID, int productID) {
        String sql = "SELECT COUNT(*) as Count FROM CompareListItems " +
                     "WHERE CompareListID = ? AND ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, compareListID);
            ps.setInt(2, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking product in compare list: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int clearList(int compareListID) {
        String sql = "DELETE FROM CompareListItems WHERE CompareListID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, compareListID);
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error clearing compare list: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet sang CompareListItem object
     */
    private CompareListItem mapResultSetToItem(ResultSet rs) throws SQLException {
        CompareListItem item = new CompareListItem();
        item.setCompareListItemID(rs.getInt("CompareListItemID"));
        item.setCompareListID(rs.getInt("CompareListID"));
        item.setProductID(rs.getInt("ProductID"));
        
        Timestamp addedAt = rs.getTimestamp("AddedAt");
        if (addedAt != null) {
            item.setAddedAt(new Date(addedAt.getTime()));
        }
        
        // Thông tin từ JOIN
        item.setProductName(rs.getString("ProductName"));
        item.setProductImageUrl(rs.getString("ImageUrl"));
        
        return item;
    }
}

