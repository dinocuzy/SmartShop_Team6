package productservice;

import model.Product;
import java.util.List;

/**
 * Interface định nghĩa các phương thức business logic cho Product
 * Service Layer - Business Logic Layer
 */
public interface IProductService {
    
    /**
     * Thêm sản phẩm mới (có validation)
     * @param product Product object cần thêm
     * @return ID của product đã được tạo
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    int addProduct(Product product);
    
    /**
     * Cập nhật sản phẩm (có validation)
     * @param product Product object với thông tin đã cập nhật
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    void updateProduct(Product product);
    
    /**
     * Xóa mềm sản phẩm
     * @param productID ID sản phẩm cần xóa
     */
    void deleteProduct(int productID);
    
    /**
     * Lấy sản phẩm theo ID
     * @param productID ID sản phẩm
     * @return Product object hoặc null nếu không tìm thấy
     */
    Product getProductById(int productID);
    
    /**
     * Lấy tất cả sản phẩm
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive (OutOfStock)
     * @return Danh sách các Product
     */
    List<Product> getAllProducts(boolean includeInactive);
    
    /**
     * Tìm kiếm sản phẩm theo tên
     * @param productName Tên sản phẩm
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive (OutOfStock)
     * @return Danh sách các Product khớp
     */
    List<Product> searchProductsByName(String productName, boolean includeInactive);
    
    /**
     * Lấy danh sách sản phẩm theo danh mục
     * @param categoryID ID danh mục
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive (OutOfStock)
     * @return Danh sách các Product thuộc danh mục
     */
    List<Product> getProductsByCategory(int categoryID, boolean includeInactive);
    
    /**
     * Lấy danh sách sản phẩm với phân trang, tìm kiếm, sắp xếp, lọc
     * @param pageNumber Số trang (bắt đầu từ 1)
     * @param pageSize Số lượng sản phẩm mỗi trang
     * @param sortBy Cột sắp xếp
     * @param sortOrder Thứ tự sắp xếp (ASC, DESC)
     * @param searchKeyword Từ khóa tìm kiếm
     * @param categoryID ID danh mục lọc
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive (OutOfStock)
     * @return Danh sách các Product
     */
    List<Product> getPagedProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchKeyword, int categoryID, boolean includeInactive);
    
    /**
     * Đếm tổng số sản phẩm với điều kiện
     * @param searchKeyword Từ khóa tìm kiếm
     * @param categoryID ID danh mục lọc
     * @param includeInactive Nếu true, đếm cả sản phẩm inactive (OutOfStock)
     * @return Tổng số sản phẩm
     */
    int countProducts(String searchKeyword, int categoryID, boolean includeInactive);
}
