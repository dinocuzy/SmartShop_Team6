package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import productservice.IProductService;
import productservice.ProductService;
import promotionproductdao.IPromotionProductDAO;
import promotionproductdao.PromotionProductDAO;
import promotionservice.IPromotionService;
import promotionservice.PromotionService;
import model.Promotion;
import model.ProductView;
import productviewdao.IProductViewDAO;
import productviewdao.ProductViewDAO;
import model.User;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Servlet xử lý trang chi tiết sản phẩm
 * URL mapping: /product hoặc /product/detail
 * Endpoint JSON: /product/api?id=xxx
 */
@WebServlet({"/product", "/product/detail", "/product/api"})
public class ProductDetailServlet extends HttpServlet {
    
    private IProductService productService;
    private IPromotionService promotionService;
    private IPromotionProductDAO promotionProductDAO;
    private IProductViewDAO productViewDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            productService = new ProductService();
            promotionService = new PromotionService();
            promotionProductDAO = new PromotionProductDAO();
            productViewDAO = new ProductViewDAO();
        } catch (Exception e) {
            System.err.println("Error initializing ProductDetailServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        // Nếu là API endpoint, trả về JSON
        if ("/product/api".equals(servletPath)) {
            handleApiRequest(request, response);
            return;
        }
        
        // Xử lý request bình thường (forward đến JSP)
        String productIDParam = request.getParameter("id");
        
        if (productIDParam == null || productIDParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Product ID is required");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam.trim());
            
            Product product = productService.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("errorMessage", "Product not found");
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            
            // Ghi nhận lượt xem sản phẩm
            try {
                HttpSession session = request.getSession();
                User currentUser = (User) session.getAttribute("currentUser");
                
                ProductView view = new ProductView();
                view.setProductID(productID);
                view.setUserID(currentUser != null ? currentUser.getUserID() : null);
                productViewDAO.insert(view);
            } catch (Exception e) {
                // Không throw exception để không ảnh hưởng đến việc hiển thị sản phẩm
                System.err.println("Error recording product view: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Kiểm tra sản phẩm có active không
            if (product.getStockStatus() != null && product.getStockStatus().equals("OutOfStock")) {
                request.setAttribute("errorMessage", "Sản phẩm này hiện không còn hàng");
            }
            
            // Lấy sản phẩm gợi ý dựa trên lượt xem
            java.util.List<Product> recommendedProducts = new java.util.ArrayList<>();
            try {
                HttpSession session = request.getSession();
                User currentUser = (User) session.getAttribute("currentUser");
                
                // Lấy sản phẩm cùng category được xem nhiều
                java.util.List<Product> categoryProducts = productViewDAO.getRecommendedProductsByCategory(
                    product.getCategoryID(), productID, 4
                );
                recommendedProducts.addAll(categoryProducts);
                
                // Nếu user đã đăng nhập, lấy gợi ý dựa trên lượt xem của user
                if (currentUser != null && recommendedProducts.size() < 6) {
                    java.util.List<Product> userProducts = productViewDAO.getRecommendedProductsByUserViews(
                        currentUser.getUserID(), 6 - recommendedProducts.size()
                    );
                    // Loại bỏ trùng lặp
                    for (Product p : userProducts) {
                        boolean exists = false;
                        for (Product existing : recommendedProducts) {
                            if (existing.getProductID() == p.getProductID()) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists && recommendedProducts.size() < 6 && p.getProductID() != productID) {
                            recommendedProducts.add(p);
                        }
                    }
                }
                
                // Nếu vẫn chưa đủ, bổ sung bằng sản phẩm được xem nhiều nhất
                if (recommendedProducts.size() < 6) {
                    java.util.List<Product> mostViewed = productViewDAO.getMostViewedProducts(6);
                    for (Product p : mostViewed) {
                        boolean exists = false;
                        for (Product existing : recommendedProducts) {
                            if (existing.getProductID() == p.getProductID() || p.getProductID() == productID) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists && recommendedProducts.size() < 6) {
                            recommendedProducts.add(p);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting recommended products: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Set attributes
            request.setAttribute("product", product);
            request.setAttribute("recommendedProducts", recommendedProducts);
            
            // Forward đến JSP
            request.getRequestDispatcher("/views/store/productDetail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Product ID format");
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/views/store/productDetail.jsp").forward(request, response);
        }
    }
    
    /**
     * Xử lý API request - trả về JSON
     */
    private void handleApiRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String productIDParam = request.getParameter("id");
        
        if (productIDParam == null || productIDParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"Product ID is required\"}");
            out.flush();
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam.trim());
            Product product = productService.getProductById(productID);
            
            if (product == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                PrintWriter out = response.getWriter();
                out.print("{\"error\": \"Product not found\"}");
                out.flush();
                return;
            }
            
            // Ghi nhận lượt xem sản phẩm (cho API endpoint)
            try {
                HttpSession session = request.getSession();
                User currentUser = (User) session.getAttribute("currentUser");
                
                ProductView view = new ProductView();
                view.setProductID(productID);
                view.setUserID(currentUser != null ? currentUser.getUserID() : null);
                productViewDAO.insert(view);
            } catch (Exception e) {
                // Không throw exception để không ảnh hưởng đến API response
                System.err.println("Error recording product view in API: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Lấy promotion nếu có
            int promotionID = promotionProductDAO.getActivePromotionIDByProduct(productID);
            Promotion promotion = null;
            
            // product.getPrice() là giá sau giảm đã được lưu trong database
            BigDecimal currentPrice = product.getPrice(); // Giá sau giảm hiện tại
            BigDecimal originalPrice = currentPrice; // Mặc định: giá gốc = giá sau giảm (nếu không có promotion)
            BigDecimal discountedPrice = currentPrice; // Giá sau giảm để hiển thị
            int discountPercent = 0;
            
            if (promotionID > 0) {
                promotion = promotionService.getPromotionById(promotionID);
                if (promotion != null && promotion.isValid()) {
                    // Tính ngược lại giá gốc từ giá sau giảm và promotion
                    if (promotion.getDiscountPercent() != null && promotion.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                        // Giá gốc = giá sau giảm / (1 - discountPercent/100)
                        BigDecimal discountPercentValue = promotion.getDiscountPercent();
                        BigDecimal multiplier = new BigDecimal("100")
                                .subtract(discountPercentValue)
                                .divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
                        originalPrice = currentPrice.divide(multiplier, 2, java.math.RoundingMode.HALF_UP);
                        discountPercent = discountPercentValue.intValue();
                        // Tính lại giá sau giảm để đảm bảo chính xác
                        BigDecimal discountAmount = originalPrice.multiply(discountPercentValue)
                                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                        discountedPrice = originalPrice.subtract(discountAmount);
                    } else if (promotion.getDiscountAmount() != null && promotion.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                        // Giá gốc = giá sau giảm + discountAmount
                        BigDecimal discountAmount = promotion.getDiscountAmount();
                        originalPrice = currentPrice.add(discountAmount);
                        discountedPrice = currentPrice; // Giá sau giảm đã đúng
                        // Tính phần trăm giảm
                        discountPercent = discountAmount.multiply(new BigDecimal("100"))
                                .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP).intValue();
                    }
                    
                    // Đảm bảo giá không âm
                    if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
                        discountedPrice = BigDecimal.ZERO;
                    }
                    if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
                        originalPrice = BigDecimal.ZERO;
                    }
                }
            }
            
            // Debug logging
            System.out.println("Product API Response for ID " + productID + ":");
            System.out.println("  CategoryName: " + product.getCategoryName());
            System.out.println("  Original Price: " + originalPrice);
            System.out.println("  Discounted Price: " + discountedPrice);
            System.out.println("  Discount Percent: " + discountPercent);
            
            // Build JSON response
            PrintWriter out = response.getWriter();
            out.print("{");
            out.print("\"productID\":" + product.getProductID() + ",");
            out.print("\"productName\":\"" + escapeJson(product.getProductName()) + "\",");
            out.print("\"price\":" + product.getPrice() + ",");
            out.print("\"originalPrice\":" + originalPrice + ",");
            out.print("\"discountedPrice\":" + discountedPrice + ",");
            out.print("\"discountPercent\":" + discountPercent + ",");
            out.print("\"stock\":" + product.getStock() + ",");
            out.print("\"stockStatus\":\"" + (product.getStockStatus() != null ? product.getStockStatus() : "InStock") + "\",");
            out.print("\"imageUrl\":\"" + (product.getImageUrl() != null ? escapeJson(product.getImageUrl()) : "") + "\",");
            out.print("\"categoryName\":\"" + (product.getCategoryName() != null ? escapeJson(product.getCategoryName()) : "") + "\",");
            out.print("\"description\":\"" + (product.getDescription() != null ? escapeJson(product.getDescription()) : "") + "\",");
            out.print("\"color\":\"" + (product.getColor() != null ? escapeJson(product.getColor()) : "") + "\",");
            out.print("\"size\":\"" + (product.getSize() != null ? escapeJson(product.getSize()) : "") + "\",");
            out.print("\"special\":" + product.isSpecial());
            out.print("}");
            out.flush();
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"Invalid Product ID format\"}");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
            out.flush();
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

