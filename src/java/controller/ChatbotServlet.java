package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.Product;
import service.ChatbotService;
import service.ChatResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet xử lý chatbot cho SmartShop
 * URL mapping: /api/chatbot
 * Method: POST
 * 
 * Sử dụng ChatbotService.handle() mới (NLU + Answer Service + Agentic AI)
 * Đã được đơn giản hóa theo hướng dẫn
 * 
 * Request body (JSON):
 * {
 *   "message": "Câu hỏi của khách hàng",
 *   "conversationId": "ID cuộc hội thoại (optional)"
 * }
 * 
 * Response (JSON):
 * {
 *   "success": true/false,
 *   "message": "AI response",
 *   "productID": số (nếu có sản phẩm),
 *   "productName": "Tên sản phẩm",
 *   "productPrice": số,
 *   "productStock": số,
 *   "productStockStatus": "InStock/OutOfStock",
 *   "productImageUrl": "URL hình ảnh",
 *   "error": "Error message (if any)"
 * }
 */
@WebServlet("/api/chatbot")
public class ChatbotServlet extends HttpServlet {
    
    private ChatbotService chatbotService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            chatbotService = new ChatbotService();
            // API key đã được load bởi GeminiContextListener từ web.xml khi app start
            // Không cần load lại ở đây
            
            // Kiểm tra API key đã được cấu hình chưa (từ GeminiContextListener)
            if (!util.GeminiClient.isApiKeyConfigured()) {
                System.err.println("Warning: Gemini API key chưa được cấu hình. " +
                                 "Vui lòng kiểm tra context-param 'gemini_api_key' trong web.xml và GeminiContextListener.");
            } else {
                System.out.println("ChatbotServlet initialized - Gemini API key đã sẵn sàng (loaded từ web.xml)");
            }
        } catch (Exception e) {
            System.err.println("Error initializing ChatbotServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            // Kiểm tra API key (sử dụng GeminiClient)
            if (!util.GeminiClient.isApiKeyConfigured()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Gemini API key chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            // Đọc request body
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            
            // Parse JSON
            Gson gson = new Gson();
            JsonObject requestJson = null;
            String message = null;
            
            if (requestBody != null && !requestBody.trim().isEmpty()) {
                try {
                    requestJson = gson.fromJson(requestBody, JsonObject.class);
                    if (requestJson != null && requestJson.has("message")) {
                        message = requestJson.get("message").getAsString();
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing JSON request: " + e.getMessage());
                }
            }
            
            // Fallback: lấy từ parameter
            if (message == null || message.trim().isEmpty()) {
                message = request.getParameter("message");
            }
            
            if (message == null || message.trim().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Thiếu tham số 'message'");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            message = message.trim();
            
            // Lấy user từ session
            HttpSession session = request.getSession(false);
            Integer userId = null;
            User currentUser = null;
            
            if (session != null) {
                try {
                    currentUser = (User) session.getAttribute("currentUser");
                    if (currentUser != null) {
                        userId = currentUser.getUserID();
                    }
                } catch (IllegalStateException e) {
                    // Session đã bị invalidate
                    session = request.getSession(true);
                    currentUser = null;
                    userId = null;
                }
            }
            
            // Gọi ChatbotService.handle() mới
            ChatResponse chatResponse = chatbotService.handle(userId, message);
            
            // Build JSON response tương thích với frontend
            jsonResponse.addProperty("success", true);
            
            if (chatResponse != null && chatResponse.getReply() != null) {
                jsonResponse.addProperty("message", chatResponse.getReply());
            } else {
                jsonResponse.addProperty("message", "Xin lỗi, không thể tạo câu trả lời. Vui lòng thử lại sau.");
            }
            
            // Debug: Log suggestions
            System.out.println("=== ChatbotServlet Debug ===");
            System.out.println("ChatResponse: " + (chatResponse != null ? "not null" : "null"));
            if (chatResponse != null) {
                System.out.println("Suggestions: " + (chatResponse.getSuggestions() != null ? chatResponse.getSuggestions().size() + " items" : "null"));
                if (chatResponse.getSuggestions() != null && !chatResponse.getSuggestions().isEmpty()) {
                    System.out.println("First product: " + chatResponse.getSuggestions().get(0).getProductName());
                }
            }
            
            // Thêm danh sách sản phẩm nếu có (để hiển thị nhiều product cards)
            if (chatResponse != null && chatResponse.getSuggestions() != null && 
                !chatResponse.getSuggestions().isEmpty()) {
                
                System.out.println("Adding products array to response...");
                
                JsonArray productsArray = new JsonArray();
                for (Product product : chatResponse.getSuggestions()) {
                    if (product != null) {
                        JsonObject productObj = new JsonObject();
                        productObj.addProperty("productID", product.getProductID());
                        productObj.addProperty("productName", 
                            product.getProductName() != null ? product.getProductName() : "");
                        productObj.addProperty("productPrice", 
                            product.getPrice() != null ? product.getPrice().doubleValue() : 0);
                        productObj.addProperty("productStock", product.getStock());
                        productObj.addProperty("productStockStatus", 
                            product.getStockStatus() != null ? product.getStockStatus() : "InStock");
                        productObj.addProperty("productImageUrl", 
                            product.getImageUrl() != null ? product.getImageUrl() : "");
                        productObj.addProperty("categoryName", 
                            product.getCategoryName() != null ? product.getCategoryName() : "");
                        productObj.addProperty("isSpecial", product.isSpecial());
                        if (product.getCreatedAt() != null) {
                            productObj.addProperty("createdAt", product.getCreatedAt().getTime());
                        }
                        productsArray.add(productObj);
                    }
                }
                jsonResponse.add("products", productsArray);
                System.out.println("Products array added: " + productsArray.size() + " products");
                
                // Giữ lại product đầu tiên để backward compatibility
                Product firstProduct = chatResponse.getSuggestions().get(0);
                if (firstProduct != null) {
                    jsonResponse.addProperty("productID", firstProduct.getProductID());
                    jsonResponse.addProperty("productName", 
                        firstProduct.getProductName() != null ? firstProduct.getProductName() : "");
                    jsonResponse.addProperty("productPrice", 
                        firstProduct.getPrice() != null ? firstProduct.getPrice().doubleValue() : 0);
                    jsonResponse.addProperty("productStock", firstProduct.getStock());
                    jsonResponse.addProperty("productStockStatus", 
                        firstProduct.getStockStatus() != null ? firstProduct.getStockStatus() : "InStock");
                    jsonResponse.addProperty("productImageUrl", 
                        firstProduct.getImageUrl() != null ? firstProduct.getImageUrl() : "");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in ChatbotServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "Đã xảy ra lỗi khi xử lý yêu cầu";
            }
            jsonResponse.addProperty("error", errorMsg);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // GET method không được hỗ trợ
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("error", "Method GET is not allowed. Please use POST.");
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
