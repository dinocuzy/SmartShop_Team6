package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.Category;
import model.Promotion;
import productservice.IProductService;
import productservice.ProductService;
import categoryservice.ICategoryService;
import categoryservice.CategoryService;
import promotionservice.IPromotionService;
import promotionservice.PromotionService;
import promotionproductdao.IPromotionProductDAO;
import promotionproductdao.PromotionProductDAO;
import util.AuthorizationUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho Product
 * URL mapping: /admin/products
 * Actions: list, add, edit, delete, save
 */
@WebServlet("/admin/products")
public class ProductServlet extends HttpServlet {
    
    private IProductService productService;
    private ICategoryService categoryService;
    private IPromotionService promotionService;
    private IPromotionProductDAO promotionProductDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        productService = new ProductService();
        categoryService = new CategoryService();
        promotionService = new PromotionService();
        promotionProductDAO = new PromotionProductDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền truy cập - chỉ Admin, Manager, Staff mới được truy cập
        if (!AuthorizationUtil.canAccessAdminArea(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Bạn không có quyền truy cập trang quản lý sản phẩm");
            return;
        }
        
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
                    // Chỉ Admin và Manager mới được xóa
                    if (!AuthorizationUtil.isAdminOrManager(request)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                            "Chỉ Admin và Manager mới có quyền xóa sản phẩm");
                        return;
                    }
                    deleteProduct(request, response);
                    break;
                case "list":
                default:
                    listProducts(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listProducts(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền truy cập - chỉ Admin, Manager, Staff mới được truy cập
        if (!AuthorizationUtil.canAccessAdminArea(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Bạn không có quyền truy cập trang quản lý sản phẩm");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "save":
                    saveProduct(request, response);
                    break;
                default:
                    listProducts(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            // Nếu có productID thì forward về edit, không thì về add
            String productID = request.getParameter("productID");
            if (productID != null && !productID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    /**
     * Hiển thị danh sách sản phẩm với phân trang, tìm kiếm, sắp xếp, lọc
     */
    private void listProducts(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy các tham số từ request và xử lý
        String pageParam = request.getParameter("page");
        String searchKeyword = request.getParameter("search");
        String categoryParam = request.getParameter("categoryID");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        String showAllParam = request.getParameter("showAll");
        
        // Parse và validate các tham số
        int pageNumber = 1;
        int pageSize = 10;
        int categoryID = 0;
        boolean includeInactive = "true".equalsIgnoreCase(showAllParam);
        
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
        
        // Set default sort values
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "ProductID";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "ASC";
        }
        
        // Trim search keyword
        if (searchKeyword != null) {
            searchKeyword = searchKeyword.trim();
            if (searchKeyword.isEmpty()) {
                searchKeyword = null;
            }
        }
        
        // Lấy danh sách sản phẩm với phân trang
        List<Product> products = productService.getPagedProducts(
            pageNumber, pageSize, sortBy, sortOrder, searchKeyword, categoryID, includeInactive
        );
        
        // Đếm tổng số sản phẩm
        int totalProducts = productService.countProducts(searchKeyword, categoryID, includeInactive);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        
        // Set attributes để hiển thị trên JSP
        request.setAttribute("products", products);
        request.setAttribute("currentPage", pageNumber);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("categoryID", categoryID);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("showAll", includeInactive);
        
        // Load danh sách categories để hiển thị trong dropdown (cần cho cả list và modal)
        List<Category> categories = categoryService.getAllCategories();
        request.setAttribute("categories", categories);
        
        // Load danh sách active promotions
        List<Promotion> promotions = promotionService.getActivePromotions();
        if (promotions == null) {
            promotions = new java.util.ArrayList<>();
        }
        request.setAttribute("promotions", promotions);
        
        // Load thông tin promotion cho từng sản phẩm (để hiển thị cột "Đã áp dụng giảm giá")
        java.util.Map<Integer, Integer> productPromotionMap = new java.util.HashMap<>();
        for (Product product : products) {
            int promotionID = promotionProductDAO.getActivePromotionIDByProduct(product.getProductID());
            if (promotionID > 0) {
                productPromotionMap.put(product.getProductID(), promotionID);
            }
        }
        request.setAttribute("productPromotionMap", productPromotionMap);
        
        // Forward đến JSP
        request.getRequestDispatcher("/views/admin/productList.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form thêm sản phẩm mới
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Load danh sách categories
        List<Category> categories = categoryService.getAllCategories();
        request.setAttribute("categories", categories);
        
        // Load danh sách active promotions
        List<Promotion> promotions = promotionService.getActivePromotions();
        if (promotions == null) {
            promotions = new java.util.ArrayList<>();
        }
        request.setAttribute("promotions", promotions);
        
        request.setAttribute("action", "add");
        request.setAttribute("product", new Product());
        request.getRequestDispatcher("/views/admin/productList.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa sản phẩm
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String productIDParam = request.getParameter("productID");
        
        if (productIDParam == null || productIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Product ID is required");
            listProducts(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam);
            Product product = productService.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("errorMessage", "Product not found with ID: " + productID);
                listProducts(request, response);
                return;
            }
            
            // Load danh sách categories
            List<Category> categories = categoryService.getAllCategories();
            request.setAttribute("categories", categories);
            
            // Load danh sách active promotions
            List<Promotion> promotions = promotionService.getActivePromotions();
            if (promotions == null) {
                promotions = new java.util.ArrayList<>();
            }
            request.setAttribute("promotions", promotions);
            
            // Load promotion hiện tại của product (nếu có)
            int currentPromotionID = promotionProductDAO.getActivePromotionIDByProduct(productID);
            request.setAttribute("currentPromotionID", currentPromotionID);
            
            // Nếu có promotion, tính giá gốc từ giá hiện tại và promotion
            if (currentPromotionID > 0) {
                try {
                    Promotion promotion = promotionService.getPromotionById(currentPromotionID);
                    if (promotion != null && promotion.isValid()) {
                        BigDecimal currentPrice = product.getPrice(); // Giá sau giảm hiện tại
                        BigDecimal originalPrice = currentPrice;
                        
                        // Tính ngược lại giá gốc từ giá sau giảm
                        if (promotion.getDiscountPercent() != null && promotion.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                            // Giá gốc = giá sau giảm / (1 - discountPercent/100)
                            BigDecimal discountPercent = promotion.getDiscountPercent();
                            BigDecimal multiplier = new BigDecimal("100").subtract(discountPercent).divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
                            originalPrice = currentPrice.divide(multiplier, 2, java.math.RoundingMode.HALF_UP);
                        } else if (promotion.getDiscountAmount() != null && promotion.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                            // Giá gốc = giá sau giảm + discountAmount
                            originalPrice = currentPrice.add(promotion.getDiscountAmount());
                        }
                        
                        // Tạo Product để hiển thị giá gốc (để user có thể chỉnh sửa)
                        Product displayProduct = new Product();
                        displayProduct.setProductID(product.getProductID());
                        displayProduct.setProductName(product.getProductName());
                        displayProduct.setSlug(product.getSlug());
                        displayProduct.setDescription(product.getDescription());
                        displayProduct.setPrice(originalPrice); // Hiển thị giá gốc
                        displayProduct.setSize(product.getSize());
                        displayProduct.setColor(product.getColor());
                        displayProduct.setStock(product.getStock());
                        displayProduct.setStockStatus(product.getStockStatus());
                        displayProduct.setCategoryID(product.getCategoryID());
                        displayProduct.setSpecial(product.isSpecial());
                        displayProduct.setImageUrl(product.getImageUrl());
                        request.setAttribute("product", displayProduct);
                    } else {
                        request.setAttribute("product", product);
                    }
                } catch (Exception e) {
                    // Nếu có lỗi khi tính giá gốc, hiển thị giá hiện tại
                    request.setAttribute("product", product);
                }
            } else {
                request.setAttribute("product", product);
            }
            
            request.setAttribute("action", "edit");
            request.getRequestDispatcher("/views/admin/productList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Product ID format");
            listProducts(request, response);
        }
    }
    
    /**
     * Xóa sản phẩm (soft delete)
     */
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String productIDParam = request.getParameter("productID");
        
        if (productIDParam == null || productIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Product ID is required");
            listProducts(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam);
            productService.deleteProduct(productID);
            request.setAttribute("successMessage", "Product deleted successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Product ID format");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        listProducts(request, response);
    }
    
    /**
     * Lưu sản phẩm (thêm mới hoặc cập nhật)
     */
    private void saveProduct(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy dữ liệu từ form
        String productIDParam = request.getParameter("productID");
        String productName = request.getParameter("productName");
        String slug = request.getParameter("slug");
        String description = request.getParameter("description");
        String priceParam = request.getParameter("price");
        String size = request.getParameter("size");
        String color = request.getParameter("color");
        String stockParam = request.getParameter("stock");
        String stockStatus = request.getParameter("stockStatus");
        String categoryIDParam = request.getParameter("categoryID");
        String isSpecialParam = request.getParameter("isSpecial");
        String imageUrl = request.getParameter("imageUrl");
        String promotionIDParam = request.getParameter("promotionID");
        
        // Validation
        if (productName == null || productName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Product name is required");
            if (productIDParam != null && !productIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        BigDecimal price;
        int stock;
        int categoryID;
        
        try {
            price = new BigDecimal(priceParam);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException("Price must be >= 0");
            }
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("errorMessage", "Invalid price format. Price must be a number >= 0");
            if (productIDParam != null && !productIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        try {
            stock = Integer.parseInt(stockParam);
            if (stock < 0) {
                throw new NumberFormatException("Stock must be >= 0");
            }
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("errorMessage", "Invalid stock format. Stock must be an integer >= 0");
            if (productIDParam != null && !productIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        try {
            categoryID = Integer.parseInt(categoryIDParam);
            if (categoryID <= 0) {
                throw new NumberFormatException("Category ID must be > 0");
            }
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("errorMessage", "Invalid category ID. Category ID must be > 0");
            if (productIDParam != null && !productIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        if (stockStatus == null || stockStatus.trim().isEmpty()) {
            stockStatus = "InStock";
        }
        
        boolean isSpecial = isSpecialParam != null && isSpecialParam.equals("true");
        
        // Tạo Product object
        Product product = new Product();
        
        if (productIDParam != null && !productIDParam.isEmpty()) {
            // Update existing product
            try {
                int productID = Integer.parseInt(productIDParam);
                product.setProductID(productID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Product ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        product.setProductName(productName.trim());
        product.setSlug(slug != null ? slug.trim() : null);
        product.setDescription(description != null ? description.trim() : null);
        product.setSize(size != null ? size.trim() : null);
        product.setColor(color != null ? color.trim() : null);
        product.setStock(stock);
        product.setStockStatus(stockStatus);
        product.setCategoryID(categoryID);
        product.setSpecial(isSpecial);
        product.setImageUrl(imageUrl != null ? imageUrl.trim() : null);
        
        // Xử lý promotion: Tính giá tự động nếu có promotion
        int promotionID = 0;
        if (promotionIDParam != null && !promotionIDParam.trim().isEmpty()) {
            try {
                promotionID = Integer.parseInt(promotionIDParam.trim());
                if (promotionID > 0) {
                    // Lấy thông tin promotion
                    Promotion promotion = promotionService.getPromotionById(promotionID);
                    if (promotion != null && promotion.isValid()) {
                        // Tính giá sau giảm dựa trên promotion
                        // price ở đây là giá gốc (từ form input)
                        BigDecimal originalPrice = price;
                        
                        BigDecimal newPrice = originalPrice;
                        if (promotion.getDiscountPercent() != null && promotion.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                            // Giảm theo phần trăm
                            BigDecimal discountAmount = originalPrice.multiply(promotion.getDiscountPercent())
                                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                            newPrice = originalPrice.subtract(discountAmount);
                            if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                                newPrice = BigDecimal.ZERO;
                            }
                        } else if (promotion.getDiscountAmount() != null && promotion.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                            // Giảm theo số tiền cố định
                            newPrice = originalPrice.subtract(promotion.getDiscountAmount());
                            if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                                newPrice = BigDecimal.ZERO;
                            }
                        }
                        
                        // Set giá sau giảm vào Price (không còn OldPrice nữa)
                        product.setPrice(newPrice);
                    }
                }
            } catch (IllegalArgumentException e) {
                // Promotion ID không hợp lệ hoặc promotion không tồn tại, bỏ qua
                // (NumberFormatException là subclass của IllegalArgumentException, nên sẽ được catch ở đây)
                promotionID = 0;
            }
        } else {
            // Không có promotion, set giá gốc vào Price
            product.setPrice(price);
        }
        
        // Lưu sản phẩm
        int savedProductID = 0;
        try {
            if (product.getProductID() > 0) {
                productService.updateProduct(product);
                savedProductID = product.getProductID();
                request.setAttribute("successMessage", "Product updated successfully");
            } else {
                savedProductID = productService.addProduct(product);
                // ProductID đã được set trong ProductDAO.insert()
                // Đảm bảo product object có ProductID mới nhất
                if (product.getProductID() <= 0) {
                    product.setProductID(savedProductID);
                }
                request.setAttribute("successMessage", "Product added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (product.getProductID() > 0) {
                request.setAttribute("product", product);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/productList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("product", product);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/productList.jsp").forward(request, response);
                return;
            }
        }
        
        // Xử lý promotion: Lưu vào bảng PromotionProducts
        if (savedProductID > 0) {
            // Xóa tất cả promotions cũ của product
            promotionProductDAO.removeAllPromotionsFromProduct(savedProductID);
            
            // Nếu có promotion mới, thêm vào
            if (promotionID > 0) {
                promotionProductDAO.addPromotionToProduct(promotionID, savedProductID);
            }
        }
        
        // Redirect về danh sách sau khi lưu thành công
        response.sendRedirect(request.getContextPath() + "/admin/products?action=list");
    }
}
