package productdao;

import model.Product;
import util.DBConnection;
import java.math.BigDecimal;
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
 * Implementation của IProductDAO
 * Sử dụng JDBC để tương tác với SQL Server database
 */
public class ProductDAO implements IProductDAO {
    
    @Override
    public List<Product> getAll(boolean includeInactive) {
        List<Product> products = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                  "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                  "FROM Products ORDER BY ProductID ASC";
        } else {
            sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                  "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                  "FROM Products WHERE StockStatus = 'InStock' ORDER BY ProductID ASC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    @Override
    public Product getById(int productID) {
        String sql = "SELECT p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, p.Price, " +
                     "p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, " +
                     "p.CreatedAt, p.UpdatedAt, c.CategoryName " +
                     "FROM Products p " +
                     "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                     "WHERE p.ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    // Set categoryName từ JOIN
                    String categoryName = rs.getString("CategoryName");
                    if (categoryName != null) {
                        product.setCategoryName(categoryName);
                    }
                    return product;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public int insert(Product product) {
        String sql = "INSERT INTO Products (CategoryID, ProductName, Slug, Description, Price, " +
                     "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, product.getCategoryID());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getSlug());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setString(6, product.getSize());
            ps.setString(7, product.getColor());
            ps.setBoolean(8, product.isSpecial());
            ps.setInt(9, product.getStock());
            
            // Set default StockStatus nếu null
            String stockStatus = product.getStockStatus();
            if (stockStatus == null || stockStatus.isEmpty()) {
                stockStatus = product.getStock() > 0 ? "InStock" : "OutOfStock";
            }
            ps.setString(10, stockStatus);
            
            ps.setString(11, product.getImageUrl());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        product.setProductID(generatedId);
                        System.out.println("Inserted product ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public boolean update(Product product) {
        String sql = "UPDATE Products SET CategoryID = ?, ProductName = ?, Slug = ?, Description = ?, " +
                     "Price = ?, Size = ?, Color = ?, IsSpecial = ?, Stock = ?, " +
                     "StockStatus = ?, ImageUrl = ?, UpdatedAt = GETDATE() WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, product.getCategoryID());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getSlug());
            ps.setString(4, product.getDescription());
            ps.setBigDecimal(5, product.getPrice());
            ps.setString(6, product.getSize());
            ps.setString(7, product.getColor());
            ps.setBoolean(8, product.isSpecial());
            ps.setInt(9, product.getStock());
            ps.setString(10, product.getStockStatus());
            ps.setString(11, product.getImageUrl());
            ps.setInt(12, product.getProductID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated product ID: " + product.getProductID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean delete(int productID) {
        String sql = "DELETE FROM Products WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted product ID: " + productID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public List<Product> searchByName(String productName, boolean includeInactive) {
        List<Product> products = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                  "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                  "FROM Products WHERE ProductName LIKE ? ORDER BY ProductID ASC";
        } else {
            sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                  "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                  "FROM Products WHERE ProductName LIKE ? AND StockStatus = 'InStock' ORDER BY ProductID ASC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + productName + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching products by name: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    @Override
    public List<Product> getByCategory(int categoryID, boolean includeInactive) {
        List<Product> products = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                  "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                  "FROM Products WHERE CategoryID = ? ORDER BY ProductID ASC";
        } else {
            sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                  "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                  "FROM Products WHERE CategoryID = ? AND StockStatus = 'InStock' ORDER BY ProductID ASC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting products by category: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    @Override
    public List<Product> getPagedProducts(int pageNumber, int pageSize, String sortBy, String sortOrder,
                                          String searchKeyword, int categoryID, boolean includeInactive) {
        List<Product> products = new ArrayList<>();
        
        // Validate và set defaults
        if (pageNumber < 1) pageNumber = 1;
        if (pageSize < 1) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "ProductID";
        if (sortOrder == null || sortOrder.isEmpty()) sortOrder = "ASC";
        
        // Validate sortBy để tránh SQL injection
        String[] allowedColumns = {"ProductID", "ProductName", "Price", "Stock", "CreatedAt"};
        boolean isValid = false;
        for (String col : allowedColumns) {
            if (sortBy.equalsIgnoreCase(col)) {
                sortBy = col;
                isValid = true;
                break;
            }
        }
        if (!isValid) sortBy = "ProductID";
        
        if (!sortOrder.equalsIgnoreCase("DESC")) sortOrder = "ASC";
        
        // Build WHERE clause với prefix p. cho Products table
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        if (!includeInactive) {
            whereClause.append("p.StockStatus = 'InStock'");
        }
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("p.ProductName LIKE ?");
            params.add("%" + searchKeyword.trim() + "%");
        }
        
        if (categoryID > 0) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("p.CategoryID = ?");
            params.add(categoryID);
        }
        
        String where = whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "";
        
        // Build SQL với OFFSET/FETCH (SQL Server pagination) và JOIN Categories để lấy CategoryName
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT p.ProductID, p.CategoryID, p.ProductName, p.Slug, p.Description, p.Price, " +
                     "p.Size, p.Color, p.IsSpecial, p.Stock, p.StockStatus, p.ImageUrl, p.CreatedAt, p.UpdatedAt, " +
                     "c.CategoryName " +
                     "FROM Products p " +
                     "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                     where + " ORDER BY p." + sortBy + " " + sortOrder +
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
                    Product product = mapResultSetToProduct(rs);
                    // Set categoryName từ JOIN
                    String categoryName = rs.getString("CategoryName");
                    if (categoryName != null) {
                        product.setCategoryName(categoryName);
                    }
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting paged products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    @Override
    public int count(String searchKeyword, int categoryID, boolean includeInactive) {
        // Build WHERE clause
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        if (!includeInactive) {
            whereClause.append("StockStatus = 'InStock'");
        }
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("ProductName LIKE ?");
            params.add("%" + searchKeyword.trim() + "%");
        }
        
        if (categoryID > 0) {
            if (whereClause.length() > 0) whereClause.append(" AND ");
            whereClause.append("CategoryID = ?");
            params.add(categoryID);
        }
        
        String where = whereClause.length() > 0 ? "WHERE " + whereClause.toString() : "";
        String sql = "SELECT COUNT(*) FROM Products " + where;
        
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
            System.err.println("Error counting products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet sang Product object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductID(rs.getInt("ProductID"));
        product.setCategoryID(rs.getInt("CategoryID"));
        product.setProductName(rs.getString("ProductName"));
        product.setSlug(rs.getString("Slug"));
        product.setDescription(rs.getString("Description"));
        
        BigDecimal price = rs.getBigDecimal("Price");
        if (price != null) {
            product.setPrice(price);
        }
        
        product.setSize(rs.getString("Size"));
        product.setColor(rs.getString("Color"));
        product.setSpecial(rs.getBoolean("IsSpecial"));
        product.setStock(rs.getInt("Stock"));
        product.setStockStatus(rs.getString("StockStatus"));
        product.setImageUrl(rs.getString("ImageUrl"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            product.setCreatedAt(new Date(createdAt.getTime()));
        }
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            product.setUpdatedAt(new Date(updatedAt.getTime()));
        }
        
        return product;
    }
    
    @Override
    public List<Product> searchForChatbot(String keyword, boolean includeInactive) {
        List<Product> products = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return products;
        }
        
        keyword = keyword.trim().toLowerCase();
        
        // Chuẩn hóa từ khóa: map các từ viết tắt và từ đồng nghĩa
        String normalizedKeyword = normalizeSearchKeyword(keyword);
        
        // Tách từ khóa thành các từ riêng lẻ
        String[] keywords = normalizedKeyword.split("\\s+");
        
        // Tạo danh sách các pattern để search
        List<String> searchPatterns = new ArrayList<>();
        searchPatterns.add(normalizedKeyword); // Từ khóa đầy đủ
        for (String kw : keywords) {
            if (kw.length() > 1) { // Bỏ qua từ quá ngắn
                searchPatterns.add(kw);
            }
        }
        
        // Tìm kiếm với nhiều pattern
        for (String pattern : searchPatterns) {
            String sql;
            
            if (includeInactive) {
                sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                      "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                      "FROM Products " +
                      "WHERE (LOWER(ProductName) LIKE ? OR LOWER(Description) LIKE ?) " +
                      "ORDER BY ProductID ASC";
            } else {
                sql = "SELECT ProductID, CategoryID, ProductName, Slug, Description, Price, " +
                      "Size, Color, IsSpecial, Stock, StockStatus, ImageUrl, CreatedAt, UpdatedAt " +
                      "FROM Products " +
                      "WHERE (LOWER(ProductName) LIKE ? OR LOWER(Description) LIKE ?) AND StockStatus = 'InStock' " +
                      "ORDER BY ProductID ASC";
            }
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                String searchPattern = "%" + pattern + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = mapResultSetToProduct(rs);
                        // Kiểm tra trùng lặp (dựa trên ProductID)
                        boolean exists = false;
                        for (Product p : products) {
                            if (p.getProductID() == product.getProductID()) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            products.add(product);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error searching products for chatbot: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Nếu đã tìm thấy đủ sản phẩm, dừng lại
            if (products.size() >= 20) {
                break;
            }
        }
        
        return products;
    }
    
    /**
     * Chuẩn hóa từ khóa tìm kiếm: map các từ viết tắt và từ đồng nghĩa
     */
    private String normalizeSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return keyword;
        }
        
        keyword = keyword.toLowerCase().trim();
        
        // Map các từ viết tắt và từ đồng nghĩa
        // Điện thoại
        if (keyword.contains("đt") || keyword.contains("dt")) {
            keyword = keyword.replace("đt", "điện thoại").replace("dt", "điện thoại");
        }
        if (keyword.contains("phone") || keyword.contains("smartphone")) {
            keyword = keyword.replace("phone", "điện thoại").replace("smartphone", "điện thoại");
        }
        // Xử lý "mua đt", "mua điện thoại" -> "điện thoại"
        if (keyword.contains("mua đt") || keyword.contains("mua dt")) {
            keyword = keyword.replace("mua đt", "điện thoại").replace("mua dt", "điện thoại");
        }
        if (keyword.contains("mua điện thoại")) {
            keyword = keyword.replace("mua điện thoại", "điện thoại");
        }
        
        // Laptop
        if (keyword.contains("laptop") || keyword.contains("máy tính")) {
            keyword = keyword.replace("máy tính", "laptop");
        }
        
        // Tablet
        if (keyword.contains("tablet") || keyword.contains("máy tính bảng")) {
            keyword = keyword.replace("máy tính bảng", "tablet");
        }
        
        // Tai nghe
        if (keyword.contains("tai nghe") || keyword.contains("headphone") || keyword.contains("earphone")) {
            keyword = keyword.replace("headphone", "tai nghe").replace("earphone", "tai nghe");
        }
        
        // Sạc
        if (keyword.contains("sạc") || keyword.contains("charger")) {
            keyword = keyword.replace("charger", "sạc");
        }
        
        // Ốp lưng
        if (keyword.contains("ốp") || keyword.contains("case")) {
            keyword = keyword.replace("case", "ốp");
        }
        
        return keyword;
    }
}
