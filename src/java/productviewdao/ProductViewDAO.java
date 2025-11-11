package productviewdao;

import model.Product;
import model.ProductView;
import productdao.ProductDAO;
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
 * Implementation của IProductViewDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class ProductViewDAO implements IProductViewDAO {
    
    @Override
    public int insert(ProductView view) {
        String sql = "INSERT INTO ProductViews (ProductID, UserID, ViewedAt) " +
                     "VALUES (?, ?, SYSDATETIME())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, view.getProductID());
            if (view.getUserID() != null) {
                ps.setInt(2, view.getUserID());
            } else {
                ps.setObject(2, null);
            }
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        view.setViewID(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting product view: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    @Override
    public int getViewCountByProductID(int productID) {
        String sql = "SELECT COUNT(*) as ViewCount FROM ProductViews WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ViewCount");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting view count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public ProductView getById(int viewID) {
        String sql = "SELECT ViewID, ProductID, UserID, ViewedAt " +
                     "FROM ProductViews " +
                     "WHERE ViewID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, viewID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToView(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product view by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Product> getMostViewedProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT TOP (?) p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, " +
                     "p.Price, p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, " +
                     "p.CreatedAt, p.UpdatedAt, COUNT(pv.ViewID) as ViewCount " +
                     "FROM Products p " +
                     "INNER JOIN ProductViews pv ON p.ProductID = pv.ProductID " +
                     "WHERE p.StockStatus = 'InStock' " +
                     "GROUP BY p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, " +
                     "p.Price, p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, " +
                     "p.CreatedAt, p.UpdatedAt " +
                     "ORDER BY ViewCount DESC, p.ProductID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                ProductDAO productDAO = new ProductDAO();
                while (rs.next()) {
                    Product product = productDAO.getById(rs.getInt("ProductID"));
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting most viewed products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    @Override
    public List<Product> getRecommendedProductsByUserViews(Integer userID, int limit) {
        List<Product> products = new ArrayList<>();
        
        if (userID == null) {
            // Nếu anonymous, trả về sản phẩm được xem nhiều nhất
            return getMostViewedProducts(limit);
        }
        
        // Lấy các sản phẩm mà user đã xem, sau đó lấy các sản phẩm cùng category
        String sql = "SELECT DISTINCT TOP (?) p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, " +
                     "p.Price, p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, " +
                     "p.CreatedAt, p.UpdatedAt " +
                     "FROM Products p " +
                     "INNER JOIN ProductViews pv ON p.ProductID = pv.ProductID " +
                     "WHERE p.StockStatus = 'InStock' " +
                     "AND p.CategoryID IN ( " +
                     "    SELECT DISTINCT p2.CategoryID " +
                     "    FROM Products p2 " +
                     "    INNER JOIN ProductViews pv2 ON p2.ProductID = pv2.ProductID " +
                     "    WHERE pv2.UserID = ? " +
                     "    AND p2.StockStatus = 'InStock' " +
                     ") " +
                     "AND p.ProductID NOT IN ( " +
                     "    SELECT DISTINCT ProductID FROM ProductViews WHERE UserID = ? " +
                     ") " +
                     "GROUP BY p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, " +
                     "p.Price, p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, " +
                     "p.CreatedAt, p.UpdatedAt " +
                     "ORDER BY COUNT(pv.ViewID) DESC, p.ProductID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, userID);
            ps.setInt(3, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                ProductDAO productDAO = new ProductDAO();
                while (rs.next()) {
                    Product product = productDAO.getById(rs.getInt("ProductID"));
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting recommended products by user views: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Nếu không đủ, bổ sung bằng sản phẩm được xem nhiều nhất
        if (products.size() < limit) {
            List<Product> mostViewed = getMostViewedProducts(limit);
            for (Product product : mostViewed) {
                boolean exists = false;
                for (Product p : products) {
                    if (p.getProductID() == product.getProductID()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists && products.size() < limit) {
                    products.add(product);
                }
            }
        }
        
        return products;
    }
    
    @Override
    public List<Product> getRecommendedProductsByCategory(int categoryID, int excludeProductID, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT TOP (?) p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, " +
                     "p.Price, p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, " +
                     "p.CreatedAt, p.UpdatedAt, COUNT(pv.ViewID) as ViewCount " +
                     "FROM Products p " +
                     "LEFT JOIN ProductViews pv ON p.ProductID = pv.ProductID " +
                     "WHERE p.CategoryID = ? AND p.ProductID != ? AND p.StockStatus = 'InStock' " +
                     "GROUP BY p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, " +
                     "p.Price, p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, " +
                     "p.CreatedAt, p.UpdatedAt " +
                     "ORDER BY ViewCount DESC, p.ProductID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, categoryID);
            ps.setInt(3, excludeProductID);
            
            try (ResultSet rs = ps.executeQuery()) {
                ProductDAO productDAO = new ProductDAO();
                while (rs.next()) {
                    Product product = productDAO.getById(rs.getInt("ProductID"));
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting recommended products by category: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * Map ResultSet sang ProductView object
     */
    private ProductView mapResultSetToView(ResultSet rs) throws SQLException {
        ProductView view = new ProductView();
        view.setViewID(rs.getInt("ViewID"));
        view.setProductID(rs.getInt("ProductID"));
        
        Integer userID = rs.getInt("UserID");
        if (rs.wasNull()) {
            userID = null;
        }
        view.setUserID(userID);
        
        Timestamp viewedAt = rs.getTimestamp("ViewedAt");
        if (viewedAt != null) {
            view.setViewedAt(new Date(viewedAt.getTime()));
        }
        
        return view;
    }
}

