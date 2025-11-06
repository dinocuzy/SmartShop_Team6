package productservice;

import model.Product;
import productdao.IProductDAO;
import productdao.ProductDAO;
import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation của IProductService
 * Chứa business logic và validation cho Product
 * Sử dụng ProductDAO (JDBC) để truy cập dữ liệu
 */
public class ProductService implements IProductService {
    
    private final IProductDAO productDAO;
    
    /**
     * Constructor khởi tạo ProductDAO
     */
    public ProductService() {
        this.productDAO = new ProductDAO();
    }
    
    /**
     * Validate dữ liệu Product trước khi insert/update
     * @param product Product cần validate
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be greater than or equal to 0");
        }
        
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Stock must be greater than or equal to 0");
        }
        
        if (product.getCategoryID() <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0");
        }
    }
    
    @Override
    public int addProduct(Product product) {
        validateProduct(product);
        // Set default StockStatus nếu null
        if (product.getStockStatus() == null || product.getStockStatus().isEmpty()) {
            product.setStockStatus(product.getStock() > 0 ? "InStock" : "OutOfStock");
        }
        int generatedId = productDAO.insert(product);
        if (generatedId <= 0) {
            throw new RuntimeException("Failed to insert product. Generated ID is invalid: " + generatedId);
        }
        return generatedId;
    }
    
    @Override
    public void updateProduct(Product product) {
        if (product.getProductID() <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        
        // Kiểm tra sản phẩm có tồn tại không
        Product existingProduct = productDAO.getById(product.getProductID());
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product not found with ID: " + product.getProductID());
        }
        
        validateProduct(product);
        boolean updated = productDAO.update(product);
        if (!updated) {
            throw new RuntimeException("Failed to update product with ID: " + product.getProductID());
        }
    }
    
    @Override
    public void deleteProduct(int productID) {
        if (productID <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        
        Product product = productDAO.getById(productID);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productID);
        }
        
        boolean deleted = productDAO.delete(productID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete product with ID: " + productID);
        }
    }
    
    @Override
    public Product getProductById(int productID) {
        if (productID <= 0) {
            return null;
        }
        return productDAO.getById(productID);
    }
    
    @Override
    public List<Product> getAllProducts(boolean includeInactive) {
        return productDAO.getAll(includeInactive);
    }
    
    @Override
    public List<Product> searchProductsByName(String productName, boolean includeInactive) {
        if (productName == null || productName.trim().isEmpty()) {
            return getAllProducts(includeInactive);
        }
        return productDAO.searchByName(productName.trim(), includeInactive);
    }
    
    @Override
    public List<Product> getProductsByCategory(int categoryID, boolean includeInactive) {
        if (categoryID <= 0) {
            return getAllProducts(includeInactive);
        }
        return productDAO.getByCategory(categoryID, includeInactive);
    }
    
    @Override
    public List<Product> getPagedProducts(int pageNumber, int pageSize, String sortBy, String sortOrder,
                                          String searchKeyword, int categoryID, boolean includeInactive) {
        return productDAO.getPagedProducts(pageNumber, pageSize, sortBy, sortOrder,
                                          searchKeyword, categoryID, includeInactive);
    }
    
    @Override
    public int countProducts(String searchKeyword, int categoryID, boolean includeInactive) {
        return productDAO.count(searchKeyword, categoryID, includeInactive);
    }
}
