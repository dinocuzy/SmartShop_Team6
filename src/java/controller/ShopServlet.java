package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.Category;
import productservice.IProductService;
import productservice.ProductService;
import categoryservice.ICategoryService;
import categoryservice.CategoryService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý giao diện cửa hàng công khai
 * URL mapping: /shop, /index
 * Actions: home (trang chủ), list (danh sách sản phẩm)
 */
@WebServlet({"/shop", "/index"})
public class ShopServlet extends HttpServlet {
    
    private IProductService productService;
    private ICategoryService categoryService;
    
    @Override
    public void init() throws ServletException {
        try {
            super.init();
            productService = new ProductService();
            categoryService = new CategoryService();
        } catch (Exception e) {
            System.err.println("Error initializing ShopServlet: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow servlet to start, errors will be handled in doGet
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        // Nếu là /index, hiển thị trang chủ
        if ("/index".equals(servletPath)) {
            showHomePage(request, response);
        } else {
            // Nếu là /shop, hiển thị danh sách sản phẩm
            showProductList(request, response);
        }
    }
    
    /**
     * Hiển thị trang chủ cửa hàng
     */
    private void showHomePage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Lấy sản phẩm nổi bật (8 sản phẩm mới nhất)
            List<Product> featuredProducts = productService.getPagedProducts(1, 8, "ProductID", "DESC", null, 0, false);
            
            // Lấy sản phẩm mới nhất (8 sản phẩm đầu tiên, sắp xếp theo ProductID DESC)
            List<Product> newProducts = productService.getPagedProducts(1, 8, "ProductID", "DESC", null, 0, false);
            
            // Lấy danh sách categories
            List<Category> categories = categoryService.getAllCategories();
            
            // Set attributes
            request.setAttribute("featuredProducts", featuredProducts);
            request.setAttribute("newProducts", newProducts);
            request.setAttribute("categories", categories);
            
            // Forward đến JSP
            request.getRequestDispatcher("/views/store/home.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/views/store/home.jsp").forward(request, response);
        }
    }
    
    /**
     * Hiển thị danh sách sản phẩm với search, filter, pagination
     */
    private void showProductList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Parse parameters
            String pageParam = request.getParameter("page");
            String categoryParam = request.getParameter("category");
            String searchParam = request.getParameter("search");
            String sortByParam = request.getParameter("sortBy");
            String sortOrderParam = request.getParameter("sortOrder");
            
            int pageNumber = 1;
            int pageSize = 12; // 12 sản phẩm mỗi trang
            int categoryID = 0;
            String searchKeyword = null;
            String sortBy = "ProductID";
            String sortOrder = "DESC";
            
            // Parse page number
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    pageNumber = Integer.parseInt(pageParam.trim());
                    if (pageNumber < 1) pageNumber = 1;
                } catch (NumberFormatException e) {
                    pageNumber = 1;
                }
            }
            
            // Parse category ID
            if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                try {
                    categoryID = Integer.parseInt(categoryParam.trim());
                    if (categoryID < 0) categoryID = 0;
                } catch (NumberFormatException e) {
                    categoryID = 0;
                }
            }
            
            // Parse search keyword
            if (searchParam != null && !searchParam.trim().isEmpty()) {
                searchKeyword = searchParam.trim();
            }
            
            // Parse sort options
            if (sortByParam != null && !sortByParam.trim().isEmpty()) {
                sortBy = sortByParam.trim();
            }
            if (sortOrderParam != null && !sortOrderParam.trim().isEmpty()) {
                sortOrder = sortOrderParam.trim().toUpperCase();
                if (!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
                    sortOrder = "DESC";
                }
            }
            
            // Lấy danh sách sản phẩm (chỉ lấy active)
            List<Product> products = productService.getPagedProducts(
                pageNumber, pageSize, sortBy, sortOrder, searchKeyword, categoryID, false
            );
            
            // Đếm tổng số sản phẩm
            int totalProducts = productService.countProducts(searchKeyword, categoryID, false);
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
            
            // Lấy danh sách categories
            List<Category> categories = categoryService.getAllCategories();
            
            // Set attributes
            request.setAttribute("products", products);
            request.setAttribute("categories", categories);
            request.setAttribute("currentPage", pageNumber);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("categoryID", categoryID);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);
            
            // Forward đến JSP
            request.getRequestDispatcher("/views/store/shop.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/views/store/shop.jsp").forward(request, response);
        }
    }
}

