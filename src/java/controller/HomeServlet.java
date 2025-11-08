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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

/**
 * Servlet xử lý giao diện cửa hàng công khai
 * URL mapping: /shop, /home, /
 * Actions: home (trang chủ), list (danh sách sản phẩm)
 * Gộp cả HomeServlet và ShopServlet thành một servlet duy nhất
 */
@WebServlet({"/shop", "/home", "/"})
public class HomeServlet extends HttpServlet {
    
    private IProductService productService;
    private ICategoryService categoryService;
    private IPromotionService promotionService;
    private IPromotionProductDAO promotionProductDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            super.init();
            productService = new ProductService();
            categoryService = new CategoryService();
            promotionService = new PromotionService();
            promotionProductDAO = new PromotionProductDAO();
        } catch (Exception e) {
            System.err.println("Error initializing HomeServlet: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow servlet to start, errors will be handled in doGet
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        // Nếu là /home hoặc /, hiển thị trang chủ (gộp home + shop)
        if ("/home".equals(servletPath) || "/".equals(servletPath)) {
            showHomePage(request, response);
        } else {
            // Nếu là /shop, hiển thị trang xem toàn bộ sản phẩm
            showProductList(request, response);
        }
    }
    
    /**
     * Hiển thị trang chủ cửa hàng
     */
    private void showHomePage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Parse parameters cho pagination và filter (nếu có)
            String pageParam = request.getParameter("page");
            String categoryParam = request.getParameter("category");
            String searchParam = request.getParameter("search");
            String sortByParam = request.getParameter("sortBy");
            String sortOrderParam = request.getParameter("sortOrder");
            
            int pageNumber = 1;
            int pageSize = 12;
            int categoryID = 0;
            String searchKeyword = null;
            String sortBy = "ProductID";
            String sortOrder = "DESC";
            
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    pageNumber = Integer.parseInt(pageParam.trim());
                    if (pageNumber < 1) pageNumber = 1;
                } catch (NumberFormatException e) {
                    pageNumber = 1;
                }
            }
            
            if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                try {
                    categoryID = Integer.parseInt(categoryParam.trim());
                    if (categoryID < 0) categoryID = 0;
                } catch (NumberFormatException e) {
                    categoryID = 0;
                }
            }
            
            if (searchParam != null && !searchParam.trim().isEmpty()) {
                searchKeyword = searchParam.trim();
            }
            
            if (sortByParam != null && !sortByParam.trim().isEmpty()) {
                sortBy = sortByParam.trim();
            }
            if (sortOrderParam != null && !sortOrderParam.trim().isEmpty()) {
                sortOrder = sortOrderParam.toUpperCase();
                if (!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
                    sortOrder = "DESC";
                }
            }
            
            // Lấy sản phẩm nổi bật (3 sản phẩm cho promotion cards bên phải)
            List<Product> featuredProducts = productService.getPagedProducts(1, 3, "ProductID", "DESC", null, 0, false);
            
            // Lấy sản phẩm bán chạy (12 sản phẩm cho best-selling section)
            List<Product> bestSellingProducts = productService.getPagedProducts(1, 12, "ProductID", "DESC", null, 0, false);
            
            // Lấy sản phẩm mới nhất (cho các section khác)
            List<Product> newProducts = productService.getPagedProducts(1, 12, "ProductID", "DESC", null, 0, false);
            
            // Lấy danh sách sản phẩm cho grid (có thể filter theo category/search)
            List<Product> products = productService.getPagedProducts(
                pageNumber, pageSize, sortBy, sortOrder, searchKeyword, categoryID, false
            );
            
            // Đếm tổng số sản phẩm
            int totalProducts = productService.countProducts(searchKeyword, categoryID, false);
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
            
            // Lấy danh sách categories
            List<Category> categories = categoryService.getAllCategories();
            
            // Lấy promotion có EndDate xa nhất trong số các promotion đang active để hiển thị countdown
            Promotion latestPromotion = null;
            try {
                List<Promotion> activePromotions = promotionService.getActivePromotions();
                if (activePromotions != null && !activePromotions.isEmpty()) {
                    // Tìm promotion có EndDate xa nhất
                    java.util.Date now = new java.util.Date();
                    for (Promotion promo : activePromotions) {
                        if (promo.isValid() && promo.getEndDate() != null) {
                            if (latestPromotion == null || promo.getEndDate().after(latestPromotion.getEndDate())) {
                                latestPromotion = promo;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting latest promotion for countdown: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Set attributes
            request.setAttribute("featuredProducts", featuredProducts);
            request.setAttribute("bestSellingProducts", bestSellingProducts);
            request.setAttribute("newProducts", newProducts);
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
            request.setAttribute("latestPromotion", latestPromotion); // Thêm promotion cho countdown
            
            // Forward đến JSP (home.jsp - trang chủ gộp home + shop)
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
            String minPriceParam = request.getParameter("minPrice");
            String maxPriceParam = request.getParameter("maxPrice");
            
            int pageNumber = 1;
            int pageSize = 12; // 12 sản phẩm mỗi trang
            int categoryID = 0;
            String searchKeyword = null;
            String sortBy = "ProductID";
            String sortOrder = "DESC";
            java.math.BigDecimal minPrice = null;
            java.math.BigDecimal maxPrice = null;
            
            // Parse page number
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    pageNumber = Integer.parseInt(pageParam.trim());
                    if (pageNumber < 1) pageNumber = 1;
                } catch (NumberFormatException e) {
                    pageNumber = 1;
                }
            }
            
            // Parse category ID (có thể có nhiều category được chọn từ checkbox)
            String[] categoriesParam = request.getParameterValues("category");
            if (categoriesParam != null && categoriesParam.length > 0) {
                // Lấy category đầu tiên nếu có nhiều
                try {
                    categoryID = Integer.parseInt(categoriesParam[0].trim());
                    if (categoryID < 0) categoryID = 0;
                } catch (NumberFormatException e) {
                    categoryID = 0;
                }
            } else if (categoryParam != null && !categoryParam.trim().isEmpty()) {
                // Fallback: parse từ single parameter
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
                String sortByValue = sortByParam.trim();
                // Validate sortBy - chỉ chấp nhận các giá trị hợp lệ
                if (sortByValue.equals("ProductName") || sortByValue.equals("Price") || sortByValue.equals("ProductID")) {
                    sortBy = sortByValue;
                }
                // Nếu không hợp lệ, giữ giá trị mặc định "ProductID"
            }
            if (sortOrderParam != null && !sortOrderParam.trim().isEmpty()) {
                sortOrder = sortOrderParam.trim().toUpperCase();
                if (!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
                    sortOrder = "DESC";
                }
            }
            
            // Parse price range
            if (minPriceParam != null && !minPriceParam.trim().isEmpty()) {
                try {
                    minPrice = new java.math.BigDecimal(minPriceParam.trim());
                } catch (NumberFormatException e) {
                    minPrice = null;
                }
            }
            if (maxPriceParam != null && !maxPriceParam.trim().isEmpty()) {
                try {
                    maxPrice = new java.math.BigDecimal(maxPriceParam.trim());
                } catch (NumberFormatException e) {
                    maxPrice = null;
                }
            }
            
            // Lấy tất cả sản phẩm để filter (tạm thời, sẽ tối ưu sau)
            List<Product> allProducts = productService.getAllProducts(false);
            
            // Apply filters
            List<Product> filteredProducts = new java.util.ArrayList<>();
            for (Product product : allProducts) {
                // Filter by category
                if (categoryID > 0 && product.getCategoryID() != categoryID) {
                    continue;
                }
                
                // Filter by search keyword
                if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                    if (product.getProductName() == null || 
                        !product.getProductName().toLowerCase().contains(searchKeyword.toLowerCase())) {
                        continue;
                    }
                }
                
                // Filter by price range
                if (minPrice != null && product.getPrice() != null) {
                    if (product.getPrice().compareTo(minPrice) < 0) {
                        continue;
                    }
                }
                if (maxPrice != null && product.getPrice() != null) {
                    if (product.getPrice().compareTo(maxPrice) > 0) {
                        continue;
                    }
                }
                
                filteredProducts.add(product);
            }
            
            // Sort products
            Comparator<Product> comparator = null;
            
            if ("ProductName".equals(sortBy)) {
                comparator = new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        String name1 = p1.getProductName() != null ? p1.getProductName() : "";
                        String name2 = p2.getProductName() != null ? p2.getProductName() : "";
                        return name1.compareToIgnoreCase(name2);
                    }
                };
            } else if ("Price".equals(sortBy)) {
                comparator = new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        java.math.BigDecimal price1 = p1.getPrice() != null ? p1.getPrice() : java.math.BigDecimal.ZERO;
                        java.math.BigDecimal price2 = p2.getPrice() != null ? p2.getPrice() : java.math.BigDecimal.ZERO;
                        return price1.compareTo(price2);
                    }
                };
            } else {
                // Default: ProductID
                comparator = new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Integer.compare(p1.getProductID(), p2.getProductID());
                    }
                };
            }
            
            // Apply sort order (DESC or ASC)
            if ("DESC".equals(sortOrder)) {
                comparator = comparator.reversed();
            }
            
            filteredProducts.sort(comparator);
            
            // Pagination
            int totalProducts = filteredProducts.size();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
            int startIndex = (pageNumber - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalProducts);
            List<Product> products = filteredProducts.subList(startIndex, endIndex);
            
            // Lấy danh sách categories
            List<Category> categories = categoryService.getAllCategories();
            
            // Load promotions for products
            List<Promotion> promotions = promotionService.getActivePromotions();
            if (promotions == null) {
                promotions = new java.util.ArrayList<>();
            }
            
            // Load promotion mapping for products
            Map<Integer, Integer> productPromotionMap = new HashMap<>();
            for (Product product : products) {
                int promotionID = promotionProductDAO.getActivePromotionIDByProduct(product.getProductID());
                if (promotionID > 0) {
                    productPromotionMap.put(product.getProductID(), promotionID);
                }
            }
            
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
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("productPromotionMap", productPromotionMap);
            request.setAttribute("promotions", promotions);
            
            // Forward đến JSP
            request.getRequestDispatcher("/views/store/productList.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/views/store/productList.jsp").forward(request, response);
        }
    }
}

