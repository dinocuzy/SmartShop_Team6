package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;
import productdao.IProductDAO;
import productdao.ProductDAO;
import util.GeminiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Servlet xử lý chatbot cho SmartShop
 * URL mapping: /api/chatbot
 * Method: POST
 * 
 * Request parameter: message (từ form POST)
 * 
 * Response (JSON):
 * {
 *   "success": true/false,
 *   "message": "AI response",
 *   "error": "Error message (if any)"
 * }
 */
@WebServlet("/api/chatbot")
public class ChatbotServlet extends HttpServlet {
    
    private IProductDAO productDAO;
    
    // System prompt cho chatbot
    private static final String SYSTEM_PROMPT = "Bạn là nhân viên tư vấn bán hàng của cửa hàng điện tử **SmartShop**. \n\n" +
            "### 🎯 NGUYÊN TẮC GIAO TIẾP:\n\n" +
            "- **CHỈ TRẢ LỜI 1–2 CÂU mỗi lần**, KHÔNG BAO GIỜ nói dài.\n\n" +
            "- Giữ giọng lễ phép: xưng \"em\" – gọi khách \"anh/chị\".\n\n" +
            "- **CHỈ HỎI 1 CÂU HỎI mỗi lần**, KHÔNG hỏi nhiều câu cùng lúc.\n\n" +
            "- Khi khách trả lời xong, mới hỏi câu kế tiếp.\n\n" +
            "- Kết thúc mỗi câu bằng emoji nhẹ (😊, 😄, 🛒) nếu phù hợp.\n\n" +
            "### 📋 QUY TẮC NGHIÊM NGẶT:\n\n" +
            "- **CHỈ dựa vào dữ liệu sản phẩm được cung cấp** để trả lời.\n\n" +
            "- **CẤM TUYỆT ĐỐI**: Liệt kê nhiều sản phẩm trong một tin nhắn.\n\n" +
            "- **CẤM TUYỆT ĐỐI**: Hỏi nhiều câu hỏi cùng lúc.\n\n" +
            "- **CHỈ gợi ý 1–2 sản phẩm mỗi lần**, không liệt kê nhiều.\n\n" +
            "- Nếu không có dữ liệu sản phẩm phù hợp, trả lời: \"Xin lỗi, em chưa tìm thấy sản phẩm phù hợp trong hệ thống SmartShop.\"\n\n" +
            "### 💬 KHI TƯ VẤN SẢN PHẨM:\n\n" +
            "- Sau khi đã thu thập đủ thông tin (ngân sách, mục đích, thương hiệu), mới gợi ý.\n\n" +
            "- Mỗi sản phẩm chỉ viết:\n\n" +
            "  **Tên sản phẩm**\n\n" +
            "  ✅ Ưu điểm (1 dòng)\n\n" +
            "  ⚠️ Nhược điểm (1 dòng)\n\n" +
            "- **LUÔN** thêm [SEARCH_PRODUCTS: từ khóa] ở cuối nếu gợi ý sản phẩm.\n\n" +
            "### ⚠️ LƯU Ý:\n\n" +
            "- **KHÔNG BAO GIỜ** nhắc đến \"AI\", \"chatbot\" hay \"mô hình ngôn ngữ\".\n\n" +
            "- Khi khách hỏi thông tin khác (giờ mở cửa, địa chỉ...), trả lời ngắn gọn 1–2 câu.\n\n" +
            "### 📞 THÔNG TIN SMARTSHOP:\n\n" +
            "- Hotline: 0833347220\n\n" +
            "- Email: smartshop686868@gmail.com\n\n" +
            "- Thanh toán: COD, VNPay\n\n" +
            "- Vận chuyển: Giao hàng toàn quốc\n\n" +
            "**NHỚ: CHỈ 1–2 CÂU mỗi lần, CHỈ 1 CÂU HỎI mỗi lần, CHỈ 1–2 SẢN PHẨM mỗi lần. CHỈ dựa vào dữ liệu được cung cấp.**";
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            productDAO = new ProductDAO();
            // Load Gemini API key từ context-param
            String apiKey = getServletContext().getInitParameter("gemini_api_key");
            if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
                GeminiUtil.setApiKey(apiKey);
                System.out.println("Gemini API key loaded in ChatbotServlet init");
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
            // Kiểm tra API key
            if (GeminiUtil.getApiKey() == null || GeminiUtil.getApiKey().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Gemini API key chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            // Lấy message và conversationId từ request
            String message = null;
            String conversationId = null;
            
            // Thử lấy từ parameter trước (form POST)
            message = request.getParameter("message");
            conversationId = request.getParameter("conversationId");
            
            // Nếu không có, thử đọc từ JSON body
            if (message == null || message.trim().isEmpty()) {
                try {
                    StringBuilder requestBody = new StringBuilder();
                    java.io.BufferedReader reader = request.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        requestBody.append(line);
                    }
                    
                    if (requestBody.length() > 0) {
                        Gson gson = new Gson();
                        JsonObject jsonRequest = gson.fromJson(requestBody.toString(), JsonObject.class);
                        if (jsonRequest.has("message")) {
                            message = jsonRequest.get("message").getAsString();
                        }
                        if (jsonRequest.has("conversationId")) {
                            conversationId = jsonRequest.get("conversationId").getAsString();
                        }
                    }
                } catch (Exception e) {
                    // Ignore, sẽ kiểm tra message sau
                }
            }
            
            if (message == null || message.trim().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Message không được để trống");
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            message = message.trim();
            
            // Lấy conversation history từ session (nếu có)
            HttpSession session = request.getSession(true);
            String sessionKey = "chatbot_history_" + (conversationId != null ? conversationId : "default");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> conversationHistory = (List<Map<String, String>>) session.getAttribute(sessionKey);
            if (conversationHistory == null) {
                conversationHistory = new ArrayList<>();
            }
            
            // Tạo search query kết hợp với context
            String searchQuery = buildSearchQuery(message, conversationHistory);
            
            // Bước 1: Truy vấn database để tìm sản phẩm phù hợp
            List<Product> products = productDAO.searchForChatbot(searchQuery, false);
            
            // Nếu không tìm thấy với searchQuery, thử với message gốc
            if (products == null || products.isEmpty()) {
                products = productDAO.searchForChatbot(message, false);
            }
            
            // Nếu vẫn không tìm thấy, thử tìm kiếm rộng hơn (bao gồm cả inactive)
            if (products == null || products.isEmpty()) {
                products = productDAO.searchForChatbot(searchQuery, true);
            }
            if (products == null || products.isEmpty()) {
                products = productDAO.searchForChatbot(message, true);
            }
            
            // Lọc sản phẩm theo giá nếu có thông tin giá trong context
            products = filterProductsByPrice(products, message, conversationHistory);
            
            // Bước 2: Tạo prompt với dữ liệu sản phẩm
            String prompt;
            if (products == null || products.isEmpty()) {
                // Không có sản phẩm phù hợp
                prompt = SYSTEM_PROMPT + "\n\n" +
                        "Câu hỏi khách hàng: " + message + "\n\n" +
                        "Dữ liệu sản phẩm: Không có sản phẩm phù hợp trong hệ thống.\n\n" +
                        "Hãy trả lời: \"Xin lỗi, em chưa tìm thấy sản phẩm phù hợp trong hệ thống SmartShop.\"";
            } else {
                // Có sản phẩm, format dữ liệu
                StringBuilder productData = new StringBuilder();
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                
                // Giới hạn 10 sản phẩm để không quá dài
                int maxProducts = Math.min(products.size(), 10);
                for (int i = 0; i < maxProducts; i++) {
                    Product product = products.get(i);
                    productData.append("Sản phẩm ").append(i + 1).append(":\n");
                    productData.append("- ProductID: ").append(product.getProductID()).append("\n");
                    productData.append("- Tên: ").append(product.getProductName()).append("\n");
                    productData.append("- Giá: ").append(formatPrice(product.getPrice())).append("\n");
                    productData.append("- Số lượng trong kho: ").append(product.getStock()).append("\n");
                    if (product.getDescription() != null && !product.getDescription().trim().isEmpty()) {
                        productData.append("- Mô tả: ").append(product.getDescription().trim()).append("\n");
                    }
                    if (product.getSize() != null && !product.getSize().trim().isEmpty()) {
                        productData.append("- Kích thước: ").append(product.getSize()).append("\n");
                    }
                    if (product.getColor() != null && !product.getColor().trim().isEmpty()) {
                        productData.append("- Màu sắc: ").append(product.getColor()).append("\n");
                    }
                    productData.append("\n");
                }
                
                prompt = SYSTEM_PROMPT + "\n\n" +
                        "Dưới đây là dữ liệu sản phẩm từ database SmartShop:\n\n" +
                        productData.toString() +
                        "Câu hỏi khách hàng: " + message + "\n\n" +
                        "Hãy trả lời tự nhiên, chỉ dựa vào dữ liệu sản phẩm trên. Nếu không có sản phẩm phù hợp, trả lời: \"Xin lỗi, em chưa tìm thấy sản phẩm phù hợp trong hệ thống SmartShop.\"";
            }
            
            // Bước 3: Gọi Gemini API với conversation history
            String[] errorHolder = new String[1];
            
            // Thêm message hiện tại vào history
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            conversationHistory.add(userMessage);
            
            // Giới hạn history để không quá dài (giữ 10 messages gần nhất)
            if (conversationHistory.size() > 10) {
                conversationHistory = conversationHistory.subList(conversationHistory.size() - 10, conversationHistory.size());
            }
            
            // Tạo messages cho Gemini (bao gồm system prompt và history)
            List<Map<String, String>> messagesForGemini = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "user");
            systemMessage.put("content", SYSTEM_PROMPT);
            messagesForGemini.add(systemMessage);
            messagesForGemini.addAll(conversationHistory);
            
            // Thêm prompt với dữ liệu sản phẩm vào message cuối
            Map<String, String> dataMessage = new HashMap<>();
            dataMessage.put("role", "user");
            dataMessage.put("content", prompt);
            messagesForGemini.add(dataMessage);
            
            String aiResponse = GeminiUtil.chatCompletionWithHistory(messagesForGemini, null, errorHolder);
            
            // Lưu bot response vào history
            if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                Map<String, String> botMessage = new HashMap<>();
                botMessage.put("role", "assistant");
                botMessage.put("content", aiResponse.trim());
                conversationHistory.add(botMessage);
            }
            
            // Lưu history vào session
            session.setAttribute(sessionKey, conversationHistory);
            
            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                // Có lỗi từ Gemini API
                String errorMsg = errorHolder[0] != null ? errorHolder[0] : "Không thể kết nối đến Gemini API";
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", errorMsg);
            } else {
                // Thành công
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", aiResponse.trim());
            }
            
        } catch (Exception e) {
            System.err.println("Error in ChatbotServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Đã xảy ra lỗi khi xử lý yêu cầu: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    /**
     * Xây dựng search query kết hợp với conversation history
     */
    private String buildSearchQuery(String currentMessage, List<Map<String, String>> conversationHistory) {
        StringBuilder query = new StringBuilder();
        
        // Thêm các từ khóa từ conversation history (chỉ lấy user messages)
        List<String> keywords = new ArrayList<>();
        for (Map<String, String> msg : conversationHistory) {
            if ("user".equals(msg.get("role"))) {
                String content = msg.get("content");
                if (content != null && !content.trim().isEmpty()) {
                    // Trích xuất từ khóa quan trọng
                    String[] words = content.toLowerCase().split("\\s+");
                    for (String word : words) {
                        // Bỏ qua các từ không quan trọng
                        if (word.length() > 2 && 
                            !word.equals("mua") && !word.equals("cần") && !word.equals("cho") &&
                            !word.equals("với") && !word.equals("của") && !word.equals("và")) {
                            keywords.add(word);
                        }
                    }
                }
            }
        }
        
        // Thêm từ khóa từ message hiện tại
        String[] currentWords = currentMessage.toLowerCase().split("\\s+");
        for (String word : currentWords) {
            if (word.length() > 1) {
                keywords.add(word);
            }
        }
        
        // Chuẩn hóa và kết hợp keywords
        // Xử lý số tiền (20tr, 20000000 -> tìm sản phẩm trong khoảng giá)
        String priceKeyword = extractPriceKeyword(currentMessage);
        if (priceKeyword != null && !priceKeyword.isEmpty()) {
            query.append(priceKeyword).append(" ");
        }
        
        // Kết hợp các keywords quan trọng
        for (String keyword : keywords) {
            if (keyword.length() > 2) {
                query.append(keyword).append(" ");
            }
        }
        
        // Thêm message hiện tại
        query.append(currentMessage);
        
        return query.toString().trim();
    }
    
    /**
     * Trích xuất từ khóa giá từ message (20tr, 20000000, etc.)
     */
    private String extractPriceKeyword(String message) {
        message = message.toLowerCase().trim();
        
        // Tìm pattern "20tr", "10tr", etc.
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*tr");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String number = matcher.group(1);
            try {
                int millions = Integer.parseInt(number);
                // Chuyển thành từ khóa tìm kiếm (có thể tìm trong khoảng giá)
                return millions + " triệu";
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        // Tìm số lớn (có thể là giá)
        pattern = java.util.regex.Pattern.compile("(\\d{6,})");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            String number = matcher.group(1);
            try {
                long price = Long.parseLong(number);
                if (price >= 1000000) { // >= 1 triệu
                    int millions = (int) (price / 1000000);
                    return millions + " triệu";
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        return null;
    }
    
    /**
     * Lọc sản phẩm theo giá nếu có thông tin giá trong context
     */
    private List<Product> filterProductsByPrice(List<Product> products, String currentMessage, List<Map<String, String>> conversationHistory) {
        if (products == null || products.isEmpty()) {
            return products;
        }
        
        // Tìm giá trong message hiện tại và history
        BigDecimal targetPrice = null;
        BigDecimal priceRange = null;
        
        // Tìm trong message hiện tại
        targetPrice = extractPrice(currentMessage);
        
        // Nếu không có, tìm trong history
        if (targetPrice == null) {
            for (Map<String, String> msg : conversationHistory) {
                if ("user".equals(msg.get("role"))) {
                    BigDecimal price = extractPrice(msg.get("content"));
                    if (price != null) {
                        targetPrice = price;
                        break;
                    }
                }
            }
        }
        
        // Nếu có giá, lọc sản phẩm trong khoảng ±30%
        if (targetPrice != null) {
            priceRange = targetPrice.multiply(new BigDecimal("0.3")); // 30% range
            BigDecimal minPrice = targetPrice.subtract(priceRange);
            BigDecimal maxPrice = targetPrice.add(priceRange);
            
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : products) {
                if (product.getPrice() != null) {
                    if (product.getPrice().compareTo(minPrice) >= 0 && product.getPrice().compareTo(maxPrice) <= 0) {
                        filteredProducts.add(product);
                    }
                }
            }
            
            // Nếu có sản phẩm trong khoảng giá, trả về
            if (!filteredProducts.isEmpty()) {
                return filteredProducts;
            }
        }
        
        // Nếu không có giá hoặc không có sản phẩm trong khoảng, trả về tất cả
        return products;
    }
    
    /**
     * Trích xuất giá từ message (20tr, 20000000, 20 triệu, etc.)
     */
    private BigDecimal extractPrice(String message) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }
        
        message = message.toLowerCase().trim();
        
        // Tìm pattern "20tr", "10tr", etc.
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*tr");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String number = matcher.group(1);
            try {
                int millions = Integer.parseInt(number);
                return new BigDecimal(millions).multiply(new BigDecimal("1000000"));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        // Tìm pattern "20 triệu", "10 triệu", etc.
        pattern = java.util.regex.Pattern.compile("(\\d+)\\s*triệu");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            String number = matcher.group(1);
            try {
                int millions = Integer.parseInt(number);
                return new BigDecimal(millions).multiply(new BigDecimal("1000000"));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        // Tìm số lớn (có thể là giá)
        pattern = java.util.regex.Pattern.compile("(\\d{6,})");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            String number = matcher.group(1);
            try {
                long price = Long.parseLong(number);
                if (price >= 1000000) { // >= 1 triệu
                    return new BigDecimal(price);
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        return null;
    }
    
    /**
     * Format giá tiền theo định dạng VNĐ
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0 ₫";
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(price) + " ₫";
    }
}

