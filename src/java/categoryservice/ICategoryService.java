package categoryservice;

import model.Category;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Category
 */
public interface ICategoryService {

    /**
     * Lấy tất cả danh mục
     *
     * @return Danh sách các Category
     */
    List<Category> getAllCategories();

    /**
     * Lấy danh mục theo ID
     *
     * @param categoryID ID danh mục
     * @return Category object hoặc null nếu không tìm thấy
     */
    Category getCategoryById(int categoryID);

    /**
     * Thêm danh mục mới
     *
     * @param category Category object cần thêm
     * @throws IllegalArgumentException nếu validation thất bại
     */
    void addCategory(Category category);

    /**
     * Cập nhật thông tin danh mục
     *
     * @param category Category object cần cập nhật
     * @throws IllegalArgumentException nếu validation thất bại hoặc không tìm thấy category
     */
    void updateCategory(Category category);

    /**
     * Xóa danh mục
     *
     * @param categoryID ID danh mục cần xóa
     * @throws IllegalArgumentException nếu không tìm thấy category hoặc có lỗi xảy ra
     */
    void deleteCategory(int categoryID);
}
