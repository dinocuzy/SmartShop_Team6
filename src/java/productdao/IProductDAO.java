package productdao;

import model.Product;
import java.util.List;

/**
 * Interface định nghĩa các phương thức DAO cho Product
 * Data Access Object Layer
 */
public interface IProductDAO {
    
    /**
     * Lấy tất cả products
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive
     * @return Danh sách Product
     */
    List<Product> getAll(boolean includeInactive);
    
    /**
     * Lấy product theo ID
     * @param productID ID product
     * @return Product hoặc null nếu không tìm thấy
     */
    Product getById(int productID);
    
    /**
     * Thêm product mới
     * @param product Product cần thêm
     * @return ID của product đã được tạo (identity)
     */
    int insert(Product product);
    
    /**
     * Cập nhật product
     * @param product Product với thông tin đã cập nhật
     * @return true nếu cập nhật thành công
     */
    boolean update(Product product);
    
    /**
     * Xóa product (hard delete)
     * @param productID ID product cần xóa
     * @return true nếu xóa thành công
     */
    boolean delete(int productID);
    
    /**
     * Tìm kiếm product theo tên
     * @param productName Tên product (partial match)
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive
     * @return Danh sách Product khớp
     */
    List<Product> searchByName(String productName, boolean includeInactive);
    
    /**
     * Lấy products theo category
     * @param categoryID ID category
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive
     * @return Danh sách Product
     */
    List<Product> getByCategory(int categoryID, boolean includeInactive);
    
    /**
     * Lấy products với phân trang, tìm kiếm, sắp xếp, lọc
     * @param pageNumber Số trang (bắt đầu từ 1)
     * @param pageSize Số lượng mỗi trang
     * @param sortBy Cột sắp xếp
     * @param sortOrder Thứ tự (ASC, DESC)
     * @param searchKeyword Từ khóa tìm kiếm
     * @param categoryID ID category lọc
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive
     * @return Danh sách Product
     */
    List<Product> getPagedProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, 
                                   String searchKeyword, int categoryID, boolean includeInactive);
    
    /**
     * Đếm tổng số products với điều kiện
     * @param searchKeyword Từ khóa tìm kiếm
     * @param categoryID ID category lọc
     * @param includeInactive Nếu true, đếm cả sản phẩm inactive
     * @return Tổng số products
     */
    int count(String searchKeyword, int categoryID, boolean includeInactive);
    
    /**
     * Tìm kiếm product theo ProductName, Description (và Brand nếu có)
     * Sử dụng cho chatbot
     * @param keyword Từ khóa tìm kiếm
     * @param includeInactive Nếu true, lấy cả sản phẩm inactive
     * @return Danh sách Product khớp
     */
    List<Product> searchForChatbot(String keyword, boolean includeInactive);
}
