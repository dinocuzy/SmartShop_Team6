package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import categoryservice.ICategoryService;
import categoryservice.CategoryService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho Category
 * URL mapping: /admin/categories
 * Actions: list, add, edit, delete, save
 */
@WebServlet("/admin/categories")
public class CategoryServlet extends HttpServlet {
    
    private ICategoryService categoryService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        categoryService = new CategoryService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteCategory(request, response);
                    break;
                case "list":
                default:
                    listCategories(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listCategories(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "save":
                    saveCategory(request, response);
                    break;
                default:
                    listCategories(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String categoryID = request.getParameter("categoryID");
            if (categoryID != null && !categoryID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    /**
     * Hiển thị danh sách categories
     */
    private void listCategories(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Category> categories = categoryService.getAllCategories();
        request.setAttribute("categories", categories);
        
        request.getRequestDispatcher("/views/admin/categoryList.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form thêm category mới
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("action", "add");
        request.setAttribute("category", new Category());
        request.getRequestDispatcher("/views/admin/categoryList.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa category
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String categoryIDParam = request.getParameter("categoryID");
        
        if (categoryIDParam == null || categoryIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Category ID is required");
            listCategories(request, response);
            return;
        }
        
        try {
            int categoryID = Integer.parseInt(categoryIDParam);
            Category category = categoryService.getCategoryById(categoryID);
            
            if (category == null) {
                request.setAttribute("errorMessage", "Category not found with ID: " + categoryID);
                listCategories(request, response);
                return;
            }
            
            request.setAttribute("action", "edit");
            request.setAttribute("category", category);
            request.getRequestDispatcher("/views/admin/categoryList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Category ID format");
            listCategories(request, response);
        }
    }
    
    /**
     * Xóa category
     */
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String categoryIDParam = request.getParameter("categoryID");
        
        if (categoryIDParam == null || categoryIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Category ID is required");
            listCategories(request, response);
            return;
        }
        
        try {
            int categoryID = Integer.parseInt(categoryIDParam);
            categoryService.deleteCategory(categoryID);
            request.setAttribute("successMessage", "Category deleted successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Category ID format");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        listCategories(request, response);
    }
    
    /**
     * Lưu category (thêm mới hoặc cập nhật)
     */
    private void saveCategory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String categoryIDParam = request.getParameter("categoryID");
        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");
        
        if (categoryName == null || categoryName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Category name is required");
            if (categoryIDParam != null && !categoryIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        Category category = new Category();
        
        if (categoryIDParam != null && !categoryIDParam.isEmpty()) {
            try {
                int categoryID = Integer.parseInt(categoryIDParam);
                category.setCategoryID(categoryID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Category ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        category.setCategoryName(categoryName.trim());
        category.setDescription(description != null ? description.trim() : null);
        category.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : null);
        
        try {
            if (category.getCategoryID() > 0) {
                categoryService.updateCategory(category);
                request.setAttribute("successMessage", "Category updated successfully");
            } else {
                categoryService.addCategory(category);
                request.setAttribute("successMessage", "Category added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (category.getCategoryID() > 0) {
                request.setAttribute("category", category);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/categoryList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("category", category);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/categoryList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/categories?action=list");
    }
}
