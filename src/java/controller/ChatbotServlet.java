package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;
import model.Promotion;
import model.Category;
import model.Order;
import model.OrderItem;
import model.CartItemDB;
import model.User;
import productdao.IProductDAO;
import productdao.ProductDAO;
import productservice.IProductService;
import productservice.ProductService;
import promotionservice.IPromotionService;
import promotionservice.PromotionService;
import categoryservice.ICategoryService;
import categoryservice.CategoryService;
import orderservice.IOrderService;
import orderservice.OrderService;
import orderitemservice.IOrderItemService;
import orderitemservice.OrderItemService;
import cartservice.ICartService;
import cartservice.CartService;
import util.GeminiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    private IProductService productService;
    private IPromotionService promotionService;
    private ICategoryService categoryService;
    private IOrderService orderService;
    private IOrderItemService orderItemService;
    private ICartService cartService;
    
    // System prompt cho chatbot - Được cải thiện để trả lời mọi yêu cầu
    private static final String SYSTEM_PROMPT = "Bạn là nhân viên tư vấn bán hàng thông minh của cửa hàng điện tử **SmartShop**. \n\n" +
            "### 🎯 NGUYÊN TẮC GIAO TIẾP:\n\n" +
            "- **CHỈ TRẢ LỜI 1–2 CÂU mỗi lần**, KHÔNG BAO GIỜ nói dài.\n\n" +
            "- Giữ giọng lễ phép: xưng \"em\" – gọi khách \"anh/chị\".\n\n" +
            "- **CHỈ HỎI 1 CÂU HỎI mỗi lần**, KHÔNG hỏi nhiều câu cùng lúc.\n\n" +
            "- Khi khách trả lời xong, mới hỏi câu kế tiếp.\n\n" +
            "- Kết thúc mỗi câu bằng emoji nhẹ (😊, 😄, 🛒) nếu phù hợp.\n\n" +
            "### 📋 QUY TẮC NGHIÊM NGẶT:\n\n" +
            "- **CHỈ dựa vào dữ liệu được cung cấp từ database** để trả lời (sản phẩm, khuyến mãi, đơn hàng, giỏ hàng, danh mục).\n\n" +
            "- **CẤM TUYỆT ĐỐI**: Liệt kê nhiều sản phẩm trong một tin nhắn.\n\n" +
            "- **CẤM TUYỆT ĐỐI**: Hỏi nhiều câu hỏi cùng lúc.\n\n" +
            "- **CHỈ gợi ý 1–2 sản phẩm mỗi lần**, không liệt kê nhiều.\n\n" +
            "- Nếu không có dữ liệu phù hợp, trả lời: \"Xin lỗi, em chưa tìm thấy thông tin phù hợp trong hệ thống SmartShop.\"\n\n" +
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
            "### 🎯 CÁC LOẠI CÂU HỎI BẠN CÓ THỂ TRẢ LỜI:\n\n" +
            "1. **Tư vấn sản phẩm**: Tìm sản phẩm theo tên, giá, danh mục, tính năng\n" +
            "2. **Khuyến mãi**: Thông tin các chương trình khuyến mãi đang diễn ra\n" +
            "3. **Đơn hàng**: Kiểm tra trạng thái đơn hàng, lịch sử đơn hàng (nếu khách đã đăng nhập)\n" +
            "4. **Giỏ hàng**: Xem giỏ hàng, số lượng sản phẩm (nếu khách đã đăng nhập)\n" +
            "5. **Danh mục**: Liệt kê danh mục sản phẩm, sản phẩm theo danh mục\n" +
            "6. **Thông tin cửa hàng**: Hotline, email, phương thức thanh toán, vận chuyển\n" +
            "7. **Hướng dẫn**: Cách đặt hàng, thanh toán, đổi trả\n\n" +
            "### 📞 THÔNG TIN SMARTSHOP:\n\n" +
            "- Hotline: 0833347220\n\n" +
            "- Email: smartshop686868@gmail.com\n\n" +
            "- Thanh toán: COD (Thanh toán khi nhận hàng), VNPay (Thanh toán online)\n\n" +
            "- Vận chuyển: Giao hàng toàn quốc\n\n" +
            "- Giờ làm việc: 24/7 (hệ thống online)\n\n" +
            "**NHỚ: CHỈ 1–2 CÂU mỗi lần, CHỈ 1 CÂU HỎI mỗi lần, CHỈ 1–2 SẢN PHẨM mỗi lần. CHỈ dựa vào dữ liệu được cung cấp từ database.**";
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            productDAO = new ProductDAO();
            productService = new ProductService();
            promotionService = new PromotionService();
            categoryService = new CategoryService();
            orderService = new OrderService();
            orderItemService = new OrderItemService();
            cartService = new CartService();
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
            // Sử dụng getSession(false) để không tạo session mới nếu không cần
            HttpSession session = request.getSession(false);
            if (session == null) {
                // Tạo session mới nếu chưa có
                session = request.getSession(true);
            }
            
            // Kiểm tra session có còn valid không
            boolean sessionValid = true;
            try {
                session.getAttribute("sessionCheck");
            } catch (IllegalStateException e) {
                // Session đã bị invalidate, tạo session mới
                sessionValid = false;
                System.err.println("Session was invalidated, creating new session");
            }
            
            if (!sessionValid) {
                session = request.getSession(true);
            }
            
            String sessionKey = "chatbot_history_" + (conversationId != null ? conversationId : "default");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> conversationHistory = null;
            
            try {
                conversationHistory = (List<Map<String, String>>) session.getAttribute(sessionKey);
            } catch (IllegalStateException e) {
                System.err.println("Error getting conversation history from session: " + e.getMessage());
                // Tạo session mới
                session = request.getSession(true);
                conversationHistory = null;
            }
            
            if (conversationHistory == null) {
                conversationHistory = new ArrayList<>();
            }
            
            // Lấy user từ session (nếu đã đăng nhập)
            model.User currentUser = null;
            try {
                currentUser = (model.User) session.getAttribute("currentUser");
            } catch (IllegalStateException e) {
                System.err.println("Error getting currentUser from session: " + e.getMessage());
                // Session đã bị invalidate, tạo session mới
                session = request.getSession(true);
                currentUser = null;
            }
            
            // Bước 1: Phân tích intent và query database tối đa
            DatabaseQueryResult dbData = queryDatabaseForChat(message, conversationHistory, currentUser);
            
            // Bước 2: Tạo prompt với tất cả dữ liệu từ database
            String prompt = buildPromptWithDatabaseData(message, dbData, currentUser);
            
            // Bước 3: Gọi Gemini API với conversation history
            String[] errorHolder = new String[1];
            
            // Thêm message hiện tại vào history
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            conversationHistory.add(userMessage);
            
            // Giới hạn history để không quá dài (giữ 10 messages gần nhất)
            // Tạo list mới từ subList để tránh UnsupportedOperationException
            if (conversationHistory.size() > 10) {
                conversationHistory = new ArrayList<>(conversationHistory.subList(conversationHistory.size() - 10, conversationHistory.size()));
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
            
            // Lưu history vào session (kiểm tra session còn valid)
            try {
                // Kiểm tra lại session trước khi setAttribute
                if (session == null) {
                    session = request.getSession(true);
                }
                // Test xem session còn valid không
                try {
                    session.getAttribute("test");
                } catch (IllegalStateException e) {
                    // Session đã bị invalidate, tạo session mới
                    session = request.getSession(true);
                }
                
                session.setAttribute(sessionKey, conversationHistory);
            } catch (IllegalStateException e) {
                System.err.println("Error saving conversation history to session: " + e.getMessage());
                // Không throw exception, chỉ log lỗi
                // Conversation history sẽ bị mất nhưng không ảnh hưởng đến response
            } catch (Exception e) {
                System.err.println("Unexpected error saving conversation history: " + e.getMessage());
                e.printStackTrace();
            }
            
            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                // Có lỗi từ Gemini API
                String errorMsg = errorHolder[0] != null ? errorHolder[0] : "Không thể kết nối đến Gemini API";
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", errorMsg);
            } else {
                // Thành công
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", aiResponse.trim());
                
                // Chỉ trả về productID khi message thực sự liên quan đến tìm kiếm/gợi ý sản phẩm
                // Kiểm tra xem có intent tìm sản phẩm hoặc message chứa từ khóa liên quan đến sản phẩm không
                boolean shouldShowProductButtons = false;
                if (dbData != null && dbData.products != null && !dbData.products.isEmpty()) {
                    String lowerMessage = message.toLowerCase();
                    String intent = dbData.intent != null ? dbData.intent.toLowerCase() : "";
                    
                    // Chỉ hiển thị nút khi:
                    // 1. Intent là product_search (không phải general hoặc các intent khác)
                    // 2. Hoặc message chứa từ khóa tìm kiếm sản phẩm cụ thể
                    // 3. Hoặc conversation history cho thấy đang tư vấn sản phẩm
                    if ("product_search".equals(intent)) {
                        // Intent là product_search, hiển thị nút
                        shouldShowProductButtons = true;
                    } else if (!"general".equals(intent) && !"promotion".equals(intent) && 
                               !"category".equals(intent) && !"order_status".equals(intent) && 
                               !"cart".equals(intent)) {
                        // Nếu intent không phải các loại trên, kiểm tra từ khóa trong message
                        if (lowerMessage.matches(".*\\b(mua|tìm|tìm kiếm|xem|giá|giá bao nhiêu|bán|sản phẩm|điện thoại|laptop|máy tính|tablet|đồng hồ|tai nghe|loa|chuột|bàn phím|màn hình|tivi|tủ lạnh|máy giặt|điều hòa|quạt|nồi cơm|bếp|máy lọc|máy xay|xe máy|xe đạp|ô tô|quần áo|giày dép|túi xách|balo|vali|mỹ phẩm|son|kem|sữa rửa mặt|nước hoa|đồ chơi|sách|vở|bút|thước|combo|bộ|set|gói|gợi ý|khuyến nghị|nên mua|phù hợp)\\b.*")) {
                            shouldShowProductButtons = true;
                        }
                    }
                    
                    // Kiểm tra conversation history nếu chưa quyết định
                    if (!shouldShowProductButtons && conversationHistory != null && conversationHistory.size() > 1) {
                        // Kiểm tra conversation history xem có đang tư vấn sản phẩm không
                        for (Map<String, String> histMsg : conversationHistory) {
                            String histContent = histMsg.get("content");
                            if (histContent != null) {
                                String lowerHist = histContent.toLowerCase();
                                if (lowerHist.matches(".*\\b(mua|tìm|tìm kiếm|xem|giá|sản phẩm|điện thoại|laptop|máy tính|gợi ý|khuyến nghị|nên mua|phù hợp)\\b.*")) {
                                    shouldShowProductButtons = true;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (shouldShowProductButtons) {
                        Product firstProduct = dbData.products.get(0);
                        if (firstProduct != null) {
                            jsonResponse.addProperty("productID", firstProduct.getProductID());
                            jsonResponse.addProperty("productName", firstProduct.getProductName() != null ? firstProduct.getProductName() : "");
                            jsonResponse.addProperty("productPrice", firstProduct.getPrice() != null ? firstProduct.getPrice().doubleValue() : 0);
                            jsonResponse.addProperty("productStock", firstProduct.getStock()); // getStock() trả về int, không cần kiểm tra null
                            jsonResponse.addProperty("productStockStatus", firstProduct.getStockStatus() != null ? firstProduct.getStockStatus() : "");
                            jsonResponse.addProperty("productImageUrl", firstProduct.getImageUrl() != null ? firstProduct.getImageUrl() : "");
                        }
                    }
                }
            }
            
            // Gửi response
            if (!response.isCommitted()) {
                out.print(jsonResponse.toString());
                out.flush();
            } else {
                System.err.println("Warning: Response already committed, cannot send JSON response");
            }
            
        } catch (IllegalStateException e) {
            // Session đã bị invalidate hoặc response đã được commit
            System.err.println("IllegalStateException in ChatbotServlet: " + e.getMessage());
            e.printStackTrace();
            
            // Kiểm tra xem response đã được commit chưa
            if (!response.isCommitted()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Session đã hết hạn. Vui lòng làm mới trang và thử lại.");
                out.print(jsonResponse.toString());
                out.flush();
            } else {
                System.err.println("Response already committed, cannot send error message");
            }
        } catch (Exception e) {
            System.err.println("Error in ChatbotServlet: " + e.getMessage());
            e.printStackTrace();
            
            // Kiểm tra xem response đã được commit chưa
            if (!response.isCommitted()) {
                jsonResponse.addProperty("success", false);
                String errorMsg = e.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Đã xảy ra lỗi khi xử lý yêu cầu";
                }
                jsonResponse.addProperty("error", errorMsg);
                out.print(jsonResponse.toString());
                out.flush();
            } else {
                System.err.println("Response already committed, cannot send error message");
            }
        }
    }
    
    /**
     * Xây dựng search query kết hợp với conversation history và từ khóa thay thế
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
        
        // Tạo từ khóa thay thế (synonyms) cho các từ khóa quan trọng
        Set<String> expandedKeywords = new HashSet<>();
        for (String keyword : keywords) {
            if (keyword.length() > 2) {
                expandedKeywords.add(keyword);
                // Thêm synonyms
                List<String> synonyms = getSynonyms(keyword);
                expandedKeywords.addAll(synonyms);
            }
        }
        
        // Thêm synonyms cho message hiện tại
        List<String> messageSynonyms = generateSynonymsForMessage(currentMessage);
        expandedKeywords.addAll(messageSynonyms);
        
        // Chuẩn hóa và kết hợp keywords
        // Xử lý số tiền (20tr, 20000000 -> tìm sản phẩm trong khoảng giá)
        String priceKeyword = extractPriceKeyword(currentMessage);
        if (priceKeyword != null && !priceKeyword.isEmpty()) {
            query.append(priceKeyword).append(" ");
        }
        
        // Kết hợp các keywords đã mở rộng
        for (String keyword : expandedKeywords) {
            if (keyword != null && keyword.length() > 2) {
                query.append(keyword).append(" ");
            }
        }
        
        // Thêm message hiện tại
        query.append(currentMessage);
        
        return query.toString().trim();
    }
    
    /**
     * Tạo từ khóa thay thế (synonyms) cho một từ khóa
     */
    private List<String> getSynonyms(String keyword) {
        List<String> synonyms = new ArrayList<>();
        keyword = keyword.toLowerCase().trim();
        
        // Dictionary từ khóa thay thế
        Map<String, List<String>> synonymMap = new HashMap<>();
        
        // Điện thoại / Smartphone
        synonymMap.put("điện thoại", java.util.Arrays.asList("smartphone", "phone", "mobile", "đt", "dt", "cellphone", "cell phone"));
        synonymMap.put("smartphone", java.util.Arrays.asList("điện thoại", "phone", "mobile", "đt", "dt", "cellphone"));
        synonymMap.put("phone", java.util.Arrays.asList("điện thoại", "smartphone", "mobile", "đt", "dt"));
        synonymMap.put("mobile", java.util.Arrays.asList("điện thoại", "smartphone", "phone", "đt", "dt"));
        synonymMap.put("đt", java.util.Arrays.asList("điện thoại", "smartphone", "phone", "mobile"));
        synonymMap.put("dt", java.util.Arrays.asList("điện thoại", "smartphone", "phone", "mobile"));
        
        // Laptop / Máy tính
        synonymMap.put("laptop", java.util.Arrays.asList("máy tính xách tay", "máy tính", "notebook", "computer", "pc", "máy tính laptop"));
        synonymMap.put("máy tính", java.util.Arrays.asList("laptop", "máy tính xách tay", "notebook", "computer", "pc"));
        synonymMap.put("máy tính xách tay", java.util.Arrays.asList("laptop", "máy tính", "notebook", "computer"));
        synonymMap.put("notebook", java.util.Arrays.asList("laptop", "máy tính", "máy tính xách tay", "computer"));
        synonymMap.put("computer", java.util.Arrays.asList("laptop", "máy tính", "máy tính xách tay", "notebook", "pc"));
        synonymMap.put("pc", java.util.Arrays.asList("laptop", "máy tính", "computer", "máy tính xách tay"));
        
        // Tai nghe
        synonymMap.put("tai nghe", java.util.Arrays.asList("headphone", "headphones", "earphone", "earphones", "headset", "loa tai"));
        synonymMap.put("headphone", java.util.Arrays.asList("tai nghe", "headphones", "earphone", "headset"));
        synonymMap.put("headphones", java.util.Arrays.asList("tai nghe", "headphone", "earphone", "headset"));
        synonymMap.put("earphone", java.util.Arrays.asList("tai nghe", "headphone", "headphones", "earphones"));
        synonymMap.put("headset", java.util.Arrays.asList("tai nghe", "headphone", "headphones"));
        
        // Loa
        synonymMap.put("loa", java.util.Arrays.asList("speaker", "speakers", "loa bluetooth", "loa không dây", "sound system"));
        synonymMap.put("speaker", java.util.Arrays.asList("loa", "speakers", "loa bluetooth", "loa không dây"));
        synonymMap.put("speakers", java.util.Arrays.asList("loa", "speaker", "loa bluetooth", "loa không dây"));
        
        // Pin sạc dự phòng
        synonymMap.put("pin sạc dự phòng", java.util.Arrays.asList("powerbank", "power bank", "pin dự phòng", "sạc dự phòng", "pin sạc"));
        synonymMap.put("powerbank", java.util.Arrays.asList("pin sạc dự phòng", "power bank", "pin dự phòng", "sạc dự phòng"));
        synonymMap.put("power bank", java.util.Arrays.asList("pin sạc dự phòng", "powerbank", "pin dự phòng", "sạc dự phòng"));
        synonymMap.put("pin dự phòng", java.util.Arrays.asList("pin sạc dự phòng", "powerbank", "power bank", "sạc dự phòng"));
        
        // Sạc
        synonymMap.put("sạc", java.util.Arrays.asList("charger", "cáp sạc", "sạc điện thoại", "adapter", "củ sạc"));
        synonymMap.put("charger", java.util.Arrays.asList("sạc", "cáp sạc", "sạc điện thoại", "adapter", "củ sạc"));
        synonymMap.put("cáp sạc", java.util.Arrays.asList("sạc", "charger", "sạc điện thoại", "cáp"));
        synonymMap.put("adapter", java.util.Arrays.asList("sạc", "charger", "củ sạc", "sạc điện thoại"));
        
        // Ốp lưng
        synonymMap.put("ốp lưng", java.util.Arrays.asList("case", "phone case", "bumper", "vỏ điện thoại", "bao da"));
        synonymMap.put("case", java.util.Arrays.asList("ốp lưng", "phone case", "bumper", "vỏ điện thoại"));
        synonymMap.put("phone case", java.util.Arrays.asList("ốp lưng", "case", "bumper", "vỏ điện thoại"));
        synonymMap.put("vỏ điện thoại", java.util.Arrays.asList("ốp lưng", "case", "phone case", "bumper"));
        
        // Màn hình
        synonymMap.put("màn hình", java.util.Arrays.asList("monitor", "screen", "display", "màn hình máy tính", "màn hình laptop"));
        synonymMap.put("monitor", java.util.Arrays.asList("màn hình", "screen", "display", "màn hình máy tính"));
        synonymMap.put("screen", java.util.Arrays.asList("màn hình", "monitor", "display"));
        synonymMap.put("display", java.util.Arrays.asList("màn hình", "monitor", "screen"));
        
        // Bàn phím
        synonymMap.put("bàn phím", java.util.Arrays.asList("keyboard", "phím", "bàn phím cơ", "bàn phím máy tính"));
        synonymMap.put("keyboard", java.util.Arrays.asList("bàn phím", "phím", "bàn phím cơ"));
        
        // Chuột
        synonymMap.put("chuột", java.util.Arrays.asList("mouse", "chuột máy tính", "chuột không dây", "chuột bluetooth"));
        synonymMap.put("mouse", java.util.Arrays.asList("chuột", "chuột máy tính", "chuột không dây"));
        
        // Webcam
        synonymMap.put("webcam", java.util.Arrays.asList("camera", "camera máy tính", "camera web", "cam"));
        synonymMap.put("camera", java.util.Arrays.asList("webcam", "camera máy tính", "camera web", "cam"));
        
        // USB
        synonymMap.put("usb", java.util.Arrays.asList("usb drive", "usb flash", "flash drive", "ổ usb", "thẻ nhớ usb"));
        synonymMap.put("usb drive", java.util.Arrays.asList("usb", "usb flash", "flash drive", "ổ usb"));
        synonymMap.put("flash drive", java.util.Arrays.asList("usb", "usb drive", "usb flash", "ổ usb"));
        
        // Thẻ nhớ
        synonymMap.put("thẻ nhớ", java.util.Arrays.asList("memory card", "sd card", "microsd", "thẻ sd", "thẻ microsd"));
        synonymMap.put("memory card", java.util.Arrays.asList("thẻ nhớ", "sd card", "microsd", "thẻ sd"));
        synonymMap.put("sd card", java.util.Arrays.asList("thẻ nhớ", "memory card", "microsd", "thẻ sd"));
        synonymMap.put("microsd", java.util.Arrays.asList("thẻ nhớ", "memory card", "sd card", "thẻ microsd"));
        
        // Apple / iPhone
        synonymMap.put("iphone", java.util.Arrays.asList("apple", "iphone apple", "điện thoại apple"));
        synonymMap.put("apple", java.util.Arrays.asList("iphone", "iphone apple", "điện thoại apple"));
        
        // Samsung
        synonymMap.put("samsung", java.util.Arrays.asList("điện thoại samsung", "galaxy", "samsung galaxy"));
        synonymMap.put("galaxy", java.util.Arrays.asList("samsung", "điện thoại samsung", "samsung galaxy"));
        
        // Xiaomi
        synonymMap.put("xiaomi", java.util.Arrays.asList("mi", "redmi", "điện thoại xiaomi", "điện thoại mi"));
        synonymMap.put("mi", java.util.Arrays.asList("xiaomi", "redmi", "điện thoại xiaomi"));
        synonymMap.put("redmi", java.util.Arrays.asList("xiaomi", "mi", "điện thoại xiaomi"));
        
        // Oppo
        synonymMap.put("oppo", java.util.Arrays.asList("điện thoại oppo", "oppo phone"));
        
        // Vivo
        synonymMap.put("vivo", java.util.Arrays.asList("điện thoại vivo", "vivo phone"));
        
        // Realme
        synonymMap.put("realme", java.util.Arrays.asList("điện thoại realme", "realme phone"));
        
        // Giá rẻ
        synonymMap.put("giá rẻ", java.util.Arrays.asList("rẻ", "giá thấp", "giá tốt", "hợp lý", "phải chăng", "budget"));
        synonymMap.put("rẻ", java.util.Arrays.asList("giá rẻ", "giá thấp", "giá tốt", "hợp lý", "phải chăng"));
        synonymMap.put("giá tốt", java.util.Arrays.asList("giá rẻ", "rẻ", "giá thấp", "hợp lý", "phải chăng"));
        synonymMap.put("hợp lý", java.util.Arrays.asList("giá rẻ", "rẻ", "giá tốt", "phải chăng"));
        
        // Đắt
        synonymMap.put("đắt", java.util.Arrays.asList("cao cấp", "premium", "luxury", "giá cao", "đắt tiền"));
        synonymMap.put("cao cấp", java.util.Arrays.asList("đắt", "premium", "luxury", "giá cao"));
        synonymMap.put("premium", java.util.Arrays.asList("đắt", "cao cấp", "luxury", "giá cao"));
        
        // Mới
        synonymMap.put("mới", java.util.Arrays.asList("mới nhất", "latest", "new", "mới ra", "vừa ra"));
        synonymMap.put("mới nhất", java.util.Arrays.asList("mới", "latest", "new", "mới ra"));
        synonymMap.put("latest", java.util.Arrays.asList("mới", "mới nhất", "new", "mới ra"));
        synonymMap.put("new", java.util.Arrays.asList("mới", "mới nhất", "latest", "mới ra"));
        
        // Cũ
        synonymMap.put("cũ", java.util.Arrays.asList("old", "second hand", "đã qua sử dụng", "đồ cũ"));
        synonymMap.put("old", java.util.Arrays.asList("cũ", "second hand", "đã qua sử dụng"));
        synonymMap.put("second hand", java.util.Arrays.asList("cũ", "old", "đã qua sử dụng", "đồ cũ"));
        
        // Khuyến mãi
        synonymMap.put("khuyến mãi", java.util.Arrays.asList("giảm giá", "sale", "promotion", "ưu đãi", "discount", "giảm"));
        synonymMap.put("giảm giá", java.util.Arrays.asList("khuyến mãi", "sale", "promotion", "ưu đãi", "discount", "giảm"));
        synonymMap.put("sale", java.util.Arrays.asList("khuyến mãi", "giảm giá", "promotion", "ưu đãi", "discount"));
        synonymMap.put("promotion", java.util.Arrays.asList("khuyến mãi", "giảm giá", "sale", "ưu đãi", "discount"));
        synonymMap.put("ưu đãi", java.util.Arrays.asList("khuyến mãi", "giảm giá", "sale", "promotion", "discount"));
        synonymMap.put("discount", java.util.Arrays.asList("khuyến mãi", "giảm giá", "sale", "promotion", "ưu đãi"));
        synonymMap.put("giảm", java.util.Arrays.asList("khuyến mãi", "giảm giá", "sale", "promotion", "ưu đãi"));
        
        // Lấy synonyms từ map
        if (synonymMap.containsKey(keyword)) {
            synonyms.addAll(synonymMap.get(keyword));
        }
        
        // Tìm partial match (nếu keyword chứa trong key)
        for (Map.Entry<String, List<String>> entry : synonymMap.entrySet()) {
            if (keyword.contains(entry.getKey()) || entry.getKey().contains(keyword)) {
                synonyms.addAll(entry.getValue());
            }
        }
        
        return synonyms;
    }
    
    /**
     * Generate synonyms cho toàn bộ message sử dụng Gemini API (nếu có)
     */
    private List<String> generateSynonymsForMessage(String message) {
        List<String> synonyms = new ArrayList<>();
        
        if (message == null || message.trim().isEmpty()) {
            return synonyms;
        }
        
        // Tách message thành các từ
        String[] words = message.toLowerCase().split("\\s+");
        
        // Tạo synonyms cho mỗi từ quan trọng
        for (String word : words) {
            if (word.length() > 2) {
                List<String> wordSynonyms = getSynonyms(word);
                synonyms.addAll(wordSynonyms);
            }
        }
        
        // Thử generate synonyms từ Gemini API nếu có thể
        try {
            if (GeminiUtil.getApiKey() != null && !GeminiUtil.getApiKey().isEmpty()) {
                String synonymPrompt = "Cho từ khóa sau, hãy liệt kê 5-10 từ khóa thay thế/đồng nghĩa phổ biến trong tiếng Việt và tiếng Anh (chỉ trả về danh sách từ khóa, mỗi từ một dòng, không giải thích):\n\n" + message;
                
                List<Map<String, String>> messages = new ArrayList<>();
                Map<String, String> userMsg = new HashMap<>();
                userMsg.put("role", "user");
                userMsg.put("content", synonymPrompt);
                messages.add(userMsg);
                
                String[] errorHolder = new String[1];
                String aiResponse = GeminiUtil.chatCompletionWithHistory(messages, null, errorHolder);
                
                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    // Parse response để lấy synonyms
                    String[] lines = aiResponse.split("\n");
                    for (String line : lines) {
                        line = line.trim();
                        // Bỏ qua số thứ tự, dấu gạch đầu dòng, etc.
                        line = line.replaceAll("^[\\d\\-\\.\\*\\+\\s]+", "").trim();
                        if (line.length() > 1 && line.length() < 50) {
                            synonyms.add(line.toLowerCase());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Nếu không thể generate từ Gemini, chỉ dùng dictionary
            System.err.println("Error generating synonyms from Gemini: " + e.getMessage());
        }
        
        return synonyms;
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
    
    /**
     * Class để lưu kết quả query database
     */
    private static class DatabaseQueryResult {
        List<Product> products;
        List<Promotion> promotions;
        List<Category> categories;
        List<Order> orders;
        List<CartItemDB> cartItems;
        String intent; // "product_search", "promotion", "order_status", "cart", "category", "general"
    }
    
    /**
     * Query database dựa trên message và intent
     */
    private DatabaseQueryResult queryDatabaseForChat(String message, List<Map<String, String>> conversationHistory, model.User currentUser) {
        DatabaseQueryResult result = new DatabaseQueryResult();
        result.products = new ArrayList<>();
        result.promotions = new ArrayList<>();
        result.categories = new ArrayList<>();
        result.orders = new ArrayList<>();
        result.cartItems = new ArrayList<>();
        
        String lowerMessage = message.toLowerCase();
        
        try {
            // 1. Query sản phẩm (luôn query vì có thể liên quan)
            String searchQuery = buildSearchQuery(message, conversationHistory);
            List<Product> products = productDAO.searchForChatbot(searchQuery, false);
            if (products == null || products.isEmpty()) {
                products = productDAO.searchForChatbot(message, false);
            }
            if (products == null || products.isEmpty()) {
                products = productDAO.searchForChatbot(searchQuery, true);
            }
            if (products == null || products.isEmpty()) {
                products = productDAO.searchForChatbot(message, true);
            }
            products = filterProductsByPrice(products, message, conversationHistory);
            result.products = products != null ? products : new ArrayList<>();
            
            // 2. Query khuyến mãi (nếu message liên quan đến khuyến mãi, giảm giá, sale)
            if (lowerMessage.contains("khuyến mãi") || lowerMessage.contains("giảm giá") || 
                lowerMessage.contains("sale") || lowerMessage.contains("promotion") ||
                lowerMessage.contains("ưu đãi") || lowerMessage.contains("discount")) {
                result.promotions = promotionService.getActivePromotions();
                if (result.promotions == null) {
                    result.promotions = new ArrayList<>();
                }
                result.intent = "promotion";
            }
            
            // 3. Query danh mục (nếu message liên quan đến danh mục, category)
            if (lowerMessage.contains("danh mục") || lowerMessage.contains("category") ||
                lowerMessage.contains("loại") || lowerMessage.contains("nhóm")) {
                result.categories = categoryService.getAllCategories();
                if (result.categories == null) {
                    result.categories = new ArrayList<>();
                }
                if (result.intent == null) {
                    result.intent = "category";
                }
            }
            
            // 4. Query đơn hàng (chỉ nếu user đã đăng nhập)
            if (currentUser != null && (lowerMessage.contains("đơn hàng") || lowerMessage.contains("order") ||
                lowerMessage.contains("trạng thái") || lowerMessage.contains("status") ||
                lowerMessage.contains("đã đặt") || lowerMessage.contains("lịch sử"))) {
                try {
                    result.orders = orderService.getOrdersByUser(currentUser.getUserID());
                    if (result.orders == null) {
                        result.orders = new ArrayList<>();
                    }
                    result.intent = "order_status";
                } catch (Exception e) {
                    System.err.println("Error querying orders: " + e.getMessage());
                    result.orders = new ArrayList<>();
                }
            }
            
            // 5. Query giỏ hàng (chỉ nếu user đã đăng nhập)
            if (currentUser != null && (lowerMessage.contains("giỏ hàng") || lowerMessage.contains("cart") ||
                lowerMessage.contains("đã thêm") || lowerMessage.contains("sản phẩm trong giỏ"))) {
                try {
                    result.cartItems = cartService.getCartItemsByUser(currentUser.getUserID());
                    if (result.cartItems == null) {
                        result.cartItems = new ArrayList<>();
                    }
                    if (result.intent == null) {
                        result.intent = "cart";
                    }
                } catch (Exception e) {
                    System.err.println("Error querying cart: " + e.getMessage());
                    result.cartItems = new ArrayList<>();
                }
            }
            
            // 6. Chỉ set intent là product_search nếu message thực sự liên quan đến sản phẩm
            // Kiểm tra xem message có chứa từ khóa tìm kiếm sản phẩm không
            if (result.intent == null) {
                // Kiểm tra các từ khóa liên quan đến sản phẩm
                if (lowerMessage.matches(".*\\b(mua|tìm|tìm kiếm|xem|giá|giá bao nhiêu|bán|sản phẩm|điện thoại|laptop|máy tính|tablet|đồng hồ|tai nghe|loa|chuột|bàn phím|màn hình|tivi|tủ lạnh|máy giặt|điều hòa|quạt|nồi cơm|bếp|máy lọc|máy xay|xe máy|xe đạp|ô tô|quần áo|giày dép|túi xách|balo|vali|mỹ phẩm|son|kem|sữa rửa mặt|nước hoa|đồ chơi|sách|vở|bút|thước|combo|bộ|set|gói|gợi ý|khuyến nghị|nên mua|phù hợp)\\b.*")) {
                    result.intent = "product_search";
                } else {
                    // Nếu không có từ khóa sản phẩm, set intent là "general"
                    result.intent = "general";
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error querying database for chat: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Tạo prompt với tất cả dữ liệu từ database
     */
    private String buildPromptWithDatabaseData(String message, DatabaseQueryResult dbData, model.User currentUser) {
        StringBuilder dataSection = new StringBuilder();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        // 1. Thông tin sản phẩm
        if (dbData.products != null && !dbData.products.isEmpty()) {
            dataSection.append("=== DỮ LIỆU SẢN PHẨM ===\n\n");
            int maxProducts = Math.min(dbData.products.size(), 10);
            for (int i = 0; i < maxProducts; i++) {
                Product product = dbData.products.get(i);
                dataSection.append("Sản phẩm ").append(i + 1).append(":\n");
                dataSection.append("- ProductID: ").append(product.getProductID()).append("\n");
                dataSection.append("- Tên: ").append(product.getProductName()).append("\n");
                dataSection.append("- Giá: ").append(formatPrice(product.getPrice())).append("\n");
                dataSection.append("- Số lượng trong kho: ").append(product.getStock()).append("\n");
                if (product.getDescription() != null && !product.getDescription().trim().isEmpty()) {
                    dataSection.append("- Mô tả: ").append(product.getDescription().trim()).append("\n");
                }
                if (product.getSize() != null && !product.getSize().trim().isEmpty()) {
                    dataSection.append("- Kích thước: ").append(product.getSize()).append("\n");
                }
                if (product.getColor() != null && !product.getColor().trim().isEmpty()) {
                    dataSection.append("- Màu sắc: ").append(product.getColor()).append("\n");
                }
                dataSection.append("\n");
            }
            dataSection.append("\n");
        }
        
        // 2. Thông tin khuyến mãi
        if (dbData.promotions != null && !dbData.promotions.isEmpty()) {
            dataSection.append("=== KHUYẾN MÃI ĐANG DIỄN RA ===\n\n");
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
            for (Promotion promotion : dbData.promotions) {
                dataSection.append("- ").append(promotion.getTitle()).append("\n");
                if (promotion.getDescription() != null) {
                    dataSection.append("  Mô tả: ").append(promotion.getDescription()).append("\n");
                }
                if (promotion.getDiscountPercent() != null) {
                    dataSection.append("  Giảm: ").append(promotion.getDiscountPercent()).append("%\n");
                }
                if (promotion.getDiscountAmount() != null) {
                    dataSection.append("  Giảm: ").append(formatPrice(promotion.getDiscountAmount())).append("\n");
                }
                dataSection.append("  Thời gian: ").append(dateFormat.format(promotion.getStartDate()))
                          .append(" - ").append(dateFormat.format(promotion.getEndDate())).append("\n");
                dataSection.append("\n");
            }
            dataSection.append("\n");
        }
        
        // 3. Thông tin danh mục
        if (dbData.categories != null && !dbData.categories.isEmpty()) {
            dataSection.append("=== DANH MỤC SẢN PHẨM ===\n\n");
            for (Category category : dbData.categories) {
                dataSection.append("- ").append(category.getCategoryName());
                if (category.getDescription() != null && !category.getDescription().trim().isEmpty()) {
                    dataSection.append(": ").append(category.getDescription());
                }
                dataSection.append("\n");
            }
            dataSection.append("\n");
        }
        
        // 4. Thông tin đơn hàng (chỉ nếu user đã đăng nhập)
        if (currentUser != null && dbData.orders != null && !dbData.orders.isEmpty()) {
            dataSection.append("=== ĐƠN HÀNG CỦA KHÁCH HÀNG ===\n\n");
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Order order : dbData.orders) {
                dataSection.append("Đơn hàng #").append(order.getOrderID()).append(":\n");
                dataSection.append("- Ngày đặt: ").append(dateFormat.format(order.getOrderDate())).append("\n");
                dataSection.append("- Trạng thái: ").append(order.getOrderStatus()).append("\n");
                dataSection.append("- Tổng tiền: ").append(formatPrice(order.getTotalAmount())).append("\n");
                dataSection.append("\n");
            }
            dataSection.append("\n");
        }
        
        // 5. Thông tin giỏ hàng (chỉ nếu user đã đăng nhập)
        if (currentUser != null && dbData.cartItems != null && !dbData.cartItems.isEmpty()) {
            dataSection.append("=== GIỎ HÀNG CỦA KHÁCH HÀNG ===\n\n");
            int totalItems = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItemDB item : dbData.cartItems) {
                if (item.getProduct() != null) {
                    dataSection.append("- ").append(item.getProduct().getProductName())
                              .append(" x").append(item.getQuantity())
                              .append(" = ").append(formatPrice(item.getProduct().getPrice()
                              .multiply(new BigDecimal(item.getQuantity())))).append("\n");
                    totalItems += item.getQuantity();
                    totalAmount = totalAmount.add(item.getProduct().getPrice()
                            .multiply(new BigDecimal(item.getQuantity())));
                }
            }
            dataSection.append("\nTổng: ").append(totalItems).append(" sản phẩm, ")
                      .append(formatPrice(totalAmount)).append("\n");
            dataSection.append("\n");
        }
        
        // Tạo prompt cuối cùng
        String prompt = SYSTEM_PROMPT + "\n\n";
        
        if (dataSection.length() > 0) {
            prompt += "Dưới đây là dữ liệu từ database SmartShop:\n\n" + dataSection.toString();
        } else {
            prompt += "Không có dữ liệu phù hợp trong database.\n\n";
        }
        
        prompt += "Câu hỏi khách hàng: " + message + "\n\n";
        prompt += "Hãy trả lời tự nhiên, chỉ dựa vào dữ liệu trên. ";
        prompt += "Nếu không có dữ liệu phù hợp, trả lời: \"Xin lỗi, em chưa tìm thấy thông tin phù hợp trong hệ thống SmartShop.\"";
        
        return prompt;
    }
}

