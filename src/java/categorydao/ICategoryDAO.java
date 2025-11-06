package categorydao;

import model.Category;
import java.util.List;

/**
 * Interface định nghĩa các phương thức DAO cho Category
 * Data Access Object Layer
 */
public interface ICategoryDAO {
    
    /**
     * Lấy tất cả categories
     * @return Danh sách Category
     */
    List<Category> getAll();
    
    /**
     * Lấy category theo ID
     * @param categoryID ID category
     * @return Category hoặc null nếu không tìm thấy
     */
    Category getById(int categoryID);
    
    /**
     * Thêm category mới
     * @param category Category cần thêm
     * @return ID của category đã được tạo (identity)
     */
    int insert(Category category);
    
    /**
     * Cập nhật category
     * @param category Category với thông tin đã cập nhật
     * @return true nếu cập nhật thành công
     */
    boolean update(Category category);
    
    /**
     * Xóa category
     * @param categoryID ID category cần xóa
     * @return true nếu xóa thành công
     */
    boolean delete(int categoryID);
}
