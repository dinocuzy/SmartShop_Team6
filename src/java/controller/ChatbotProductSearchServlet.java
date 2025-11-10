package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.Promotion;
import productdao.ProductDAO;
import productdao.IProductDAO;
import promotionproductdao.IPromotionProductDAO;
import promotionproductdao.PromotionProductDAO;
import promotionservice.IPromotionService;
import promotionservice.PromotionService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet để tìm kiếm sản phẩm cho chatbot
 * URL: /api/chatbot/products/search
 * Method: GET
 * Parameter: q (search query)
 */
@WebServlet("/api/chatbot/products/search")
public class ChatbotProductSearchServlet extends HttpServlet {
    
    private IProductDAO productDAO;
    private IPromotionProductDAO promotionProductDAO;
    private IPromotionService promotionService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            productDAO = new ProductDAO();
            promotionProductDAO = new PromotionProductDAO();
            promotionService = new PromotionService();
        } catch (Exception e) {
            System.err.println("Error initializing ChatbotProductSearchServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            String query = request.getParameter("q");
            
            if (query == null || query.trim().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Query parameter is required");
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            // Tìm kiếm sản phẩm (chỉ lấy sản phẩm đang có hàng, giới hạn 6 sản phẩm)
            List<Product> products = productDAO.searchByName(query.trim(), false);
            
            // Nếu không tìm thấy, thử tìm kiếm rộng hơn (không giới hạn InStock)
            if (products == null || products.isEmpty()) {
                products = productDAO.searchByName(query.trim(), true);
            }
            
            // Giới hạn 6 sản phẩm đầu tiên
            if (products != null && products.size() > 6) {
                products = products.subList(0, 6);
            } else if (products == null) {
                products = new java.util.ArrayList<>();
            }
            
            JsonArray productsArray = new JsonArray();
            
            for (Product product : products) {
                JsonObject productJson = new JsonObject();
                
                // Lấy thông tin cơ bản
                productJson.addProperty("productID", product.getProductID());
                productJson.addProperty("productName", product.getProductName());
                productJson.addProperty("imageUrl", product.getImageUrl() != null ? product.getImageUrl() : "");
                
                // Tính giá với promotion
                BigDecimal currentPrice = product.getPrice();
                BigDecimal originalPrice = currentPrice;
                int discountPercent = 0;
                
                // Lấy promotion nếu có
                int promotionID = promotionProductDAO.getActivePromotionIDByProduct(product.getProductID());
                if (promotionID > 0) {
                    Promotion promotion = promotionService.getPromotionById(promotionID);
                    if (promotion != null && promotion.isValid()) {
                        // Tính ngược lại giá gốc từ giá sau giảm
                        if (promotion.getDiscountPercent() != null && promotion.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal discountPercentValue = promotion.getDiscountPercent();
                            BigDecimal multiplier = new BigDecimal("100")
                                    .subtract(discountPercentValue)
                                    .divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
                            originalPrice = currentPrice.divide(multiplier, 2, java.math.RoundingMode.HALF_UP);
                            discountPercent = discountPercentValue.intValue();
                        } else if (promotion.getDiscountAmount() != null && promotion.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                            originalPrice = currentPrice.add(promotion.getDiscountAmount());
                            BigDecimal discountAmount = promotion.getDiscountAmount();
                            discountPercent = discountAmount.multiply(new BigDecimal("100"))
                                    .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP).intValue();
                        }
                    }
                }
                
                productJson.addProperty("price", currentPrice.toString());
                productJson.addProperty("originalPrice", originalPrice.toString());
                productJson.addProperty("discountPercent", discountPercent);
                productJson.addProperty("hasDiscount", discountPercent > 0);
                
                productsArray.add(productJson);
            }
            
            jsonResponse.addProperty("success", true);
            jsonResponse.add("products", productsArray);
            jsonResponse.addProperty("count", products.size());
            
        } catch (Exception e) {
            System.err.println("Error in ChatbotProductSearchServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Đã xảy ra lỗi khi tìm kiếm sản phẩm");
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}

