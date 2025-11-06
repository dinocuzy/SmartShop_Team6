package categoryservice;

import categorydao.ICategoryDAO;
import categorydao.CategoryDAO;
import model.Category;
import java.util.List;

/**
 * Implementation của ICategoryService
 * Chứa business logic cho Category
 * Sử dụng CategoryDAO (JDBC) để truy cập dữ liệu
 */
public class CategoryService implements ICategoryService {

    private ICategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.getAll();
    }

    @Override
    public Category getCategoryById(int categoryID) {
        if (categoryID <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0");
        }
        return categoryDAO.getById(categoryID);
    }

    @Override
    public void addCategory(Category category) {
        // Validate category
        validateCategory(category);
        
        // Insert category
        int generatedId = categoryDAO.insert(category);
        if (generatedId > 0) {
            category.setCategoryID(generatedId);
        }
    }

    @Override
    public void updateCategory(Category category) {
        // Validate category
        validateCategory(category);
        
        // Check if category exists
        if (category.getCategoryID() <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0");
        }
        
        Category existingCategory = categoryDAO.getById(category.getCategoryID());
        if (existingCategory == null) {
            throw new IllegalArgumentException("Category not found with ID: " + category.getCategoryID());
        }
        
        // Update category
        boolean updated = categoryDAO.update(category);
        if (!updated) {
            throw new RuntimeException("Failed to update category with ID: " + category.getCategoryID());
        }
    }

    @Override
    public void deleteCategory(int categoryID) {
        if (categoryID <= 0) {
            throw new IllegalArgumentException("Category ID must be greater than 0");
        }
        
        // Check if category exists
        Category category = categoryDAO.getById(categoryID);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryID);
        }
        
        // Delete category
        boolean deleted = categoryDAO.delete(categoryID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete category with ID: " + categoryID);
        }
    }

    /**
     * Validate category data
     *
     * @param category Category object cần validate
     * @throws IllegalArgumentException nếu validation thất bại
     */
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        
        if (category.getCategoryName().trim().length() > 255) {
            throw new IllegalArgumentException("Category name must not exceed 255 characters");
        }
        
        if (category.getDescription() != null && category.getDescription().trim().length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters");
        }
        
        if (category.getImageUrl() != null && category.getImageUrl().trim().length() > 500) {
            throw new IllegalArgumentException("Image URL must not exceed 500 characters");
        }
    }
}
