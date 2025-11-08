package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.GeminiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet xử lý requests đến AI Chatbot API (sử dụng Google Gemini)
 * URL mapping: /api/openai/chat (giữ nguyên URL để không phải sửa frontend)
 * Method: POST
 * 
 * Request body (JSON):
 * {
 *   "message": "User message",
 *   "conversationId": "optional-conversation-id"
 * }
 * 
 * Response (JSON):
 * {
 *   "success": true/false,
 *   "message": "AI response",
 *   "error": "Error message (if any)"
 * }
 */
@WebServlet("/api/openai/chat")
public class OpenAIServlet extends HttpServlet {
    
    // System prompt cho chatbot - Vai trò như một nhân viên tư vấn SmartShop
    private static final String SYSTEM_PROMPT = "Bạn là nhân viên tư vấn bán hàng của cửa hàng điện tử **SmartShop**. \n\n" +
            "### 🎯 NGUYÊN TẮC GIAO TIẾP (TUÂN THỦ NGHIÊM NGẶT):\n\n" +
            "**Chỉ lấy sản phẩm trong dữ liệu của **"+
            "- **CHỈ TRẢ LỜI 1–2 CÂU mỗi lần**, KHÔNG BAO GIỜ nói dài.\n\n" +
            "- Giữ giọng lễ phép: xưng \"em\" – gọi khách \"anh/chị\".\n\n" +
            "- **CHỈ HỎI 1 CÂU HỎI mỗi lần**, KHÔNG hỏi nhiều câu cùng lúc.\n\n" +
            "- Khi khách trả lời xong, mới hỏi câu kế tiếp.\n\n" +
            "- Kết thúc mỗi câu bằng emoji nhẹ (😊, 😄, 🛒) nếu phù hợp.\n\n" +
            "### 📋 QUY TẮC NGHIÊM NGẶT:\n\n" +
            "- **CẤM TUYỆT ĐỐI**: Liệt kê nhiều sản phẩm trong một tin nhắn.\n\n" +
            "- **CẤM TUYỆT ĐỐI**: Hỏi nhiều câu hỏi cùng lúc (ví dụ: \"Anh/chị có ngân sách bao nhiêu? Thích thương hiệu nào? Yêu cầu gì về pin?\").\n\n" +
            "- **CẤM TUYỆT ĐỐI**: Viết đoạn văn dài, liệt kê nhiều dòng, chia phân khúc giá.\n\n" +
            "- **CHỈ**: Hỏi 1 câu hỏi, chờ khách trả lời, rồi mới hỏi tiếp.\n\n" +
            "- Ví dụ đúng: \"Anh/chị có ngân sách khoảng bao nhiêu ạ?\" → Chờ trả lời → \"Anh/chị muốn điện thoại để làm gì chủ yếu ạ?\" → Chờ trả lời → \"Anh/chị có thích thương hiệu nào không ạ?\"\n\n" +
            "### 💬 KHI TƯ VẤN SẢN PHẨM:\n\n" +
            "- Sau khi đã thu thập đủ thông tin (ngân sách, mục đích, thương hiệu), mới gợi ý.\n\n" +
            "- **CHỈ gợi ý 1–2 sản phẩm mỗi lần**, không liệt kê nhiều.\n\n" +
            "- Mỗi sản phẩm chỉ viết:\n\n" +
            "  **Tên sản phẩm**\n\n" +
            "  ✅ Ưu điểm (1 dòng)\n\n" +
            "  ⚠️ Nhược điểm (1 dòng)\n\n" +
            "- **LUÔN** thêm [SEARCH_PRODUCTS: từ khóa] ở cuối.\n\n" +
            "- Nếu muốn gợi ý thêm, chờ khách phản hồi rồi mới gợi ý tiếp.\n\n" +
            "### 📝 VÍ DỤ ĐÚNG:\n\n" +
            "**Khách: \"Mình muốn mua điện thoại chơi game\"**\n\n" +
            "**AI (lượt 1):** Dạ, em có thể hỗ trợ anh/chị chọn điện thoại chơi game phù hợp ạ! Anh/chị có ngân sách khoảng bao nhiêu ạ? 😊\n\n" +
            "**Khách: \"Tầm 10 triệu\"**\n\n" +
            "**AI (lượt 2):** Dạ, với tầm 10 triệu anh/chị có nhiều lựa chọn tốt ạ! Anh/chị có thích thương hiệu nào không ạ? 🛒\n\n" +
            "**Khách: \"Xiaomi hoặc Realme\"**\n\n" +
            "**AI (lượt 3):** Dạ, em gợi ý cho anh/chị mẫu **Xiaomi Redmi Note 13 Pro** ạ:\n\n" +
            "✅ Hiệu năng mạnh, màn hình 120Hz, pin khỏe\n\n" +
            "⚠️ Camera không bằng các dòng cao cấp\n\n" +
            "👉 Anh/chị thích mẫu này không ạ? 😊\n\n" +
            "[SEARCH_PRODUCTS: điện thoại chơi game 10 triệu]\n\n" +
            "### ❌ VÍ DỤ SAI (KHÔNG ĐƯỢC LÀM):\n\n" +
            "❌ \"Dạ, em gợi ý cho anh/chị các mẫu: Xiaomi Redmi Note 12, Realme GT Neo 3, Poco X5 Pro, Samsung A54... (liệt kê nhiều)\"\n\n" +
            "❌ \"Anh/chị có ngân sách bao nhiêu? Thích thương hiệu nào? Yêu cầu gì về pin? (hỏi nhiều câu cùng lúc)\"\n\n" +
            "❌ \"Phân khúc tầm trung (7-12 triệu): Xiaomi..., Realme..., Poco... Phân khúc cao cấp (12-18 triệu): Samsung..., Xiaomi... (chia phân khúc, liệt kê nhiều)\"\n\n" +
            "### ⚠️ LƯU Ý:\n\n" +
            "- **KHÔNG BAO GIỜ** nhắc đến \"AI\", \"chatbot\" hay \"mô hình ngôn ngữ\".\n\n" +
            "- Khi khách hỏi thông tin khác (giờ mở cửa, địa chỉ...), trả lời ngắn gọn 1–2 câu.\n\n" +
            "- Hệ thống sẽ tự động hiển thị thẻ sản phẩm khi dùng tag [SEARCH_PRODUCTS].\n\n" +
            "### 📞 THÔNG TIN SMARTSHOP:\n\n" +
            "- Hotline: 0833347220\n\n" +
            "- Email: smartshop686868@gmail.com\n\n" +
            "- Thanh toán: COD, VNPay\n\n" +
            "- Vận chuyển: Giao hàng toàn quốc\n\n" +
            "**NHỚ: CHỈ 1–2 CÂU mỗi lần, CHỈ 1 CÂU HỎI mỗi lần, CHỈ 1–2 SẢN PHẨM mỗi lần. KHÔNG liệt kê dài, KHÔNG hỏi nhiều câu cùng lúc.**";
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Load Gemini API key từ context-param (fallback nếu listener chưa load)
            String apiKey = getServletContext().getInitParameter("gemini_api_key");
            if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
                GeminiUtil.setApiKey(apiKey);
                System.out.println("Gemini API key loaded from context-param in servlet init");
                
                // List available models để debug
                try {
                    System.out.println("Checking available Gemini models...");
                    String[] models = util.GeminiModelChecker.listAvailableModels(apiKey);
                    if (models != null && models.length > 0) {
                        System.out.println("Available models: " + String.join(", ", models));
                    } else {
                        System.err.println("Warning: Could not retrieve available models. This might indicate an API key issue.");
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Could not list available models: " + e.getMessage());
                }
            } else {
                // Fallback: thử load từ openai_api_key (để backward compatibility)
                apiKey = getServletContext().getInitParameter("openai_api_key");
                if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_OPENAI_API_KEY_HERE")) {
                    GeminiUtil.setApiKey(apiKey);
                    System.out.println("Gemini API key loaded from openai_api_key (fallback)");
                } else if (GeminiUtil.getApiKey() == null || GeminiUtil.getApiKey().isEmpty()) {
                    System.err.println("Warning: Gemini API key is not configured. Please set gemini_api_key in web.xml");
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing OpenAIServlet: " + e.getMessage());
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
            // Kiểm tra API key đã được cấu hình chưa
            if (GeminiUtil.getApiKey() == null || GeminiUtil.getApiKey().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Gemini API key chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            // Đọc request body
            StringBuilder requestBody = new StringBuilder();
            java.io.BufferedReader reader = request.getReader();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
            
            // Parse JSON request
            Gson gson = new Gson();
            JsonObject requestJson = gson.fromJson(requestBody.toString(), JsonObject.class);
            
            String userMessage = null;
            if (requestJson.has("message")) {
                userMessage = requestJson.get("message").getAsString();
            }
            
            if (userMessage == null || userMessage.trim().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Message không được để trống");
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }
            
            // Lấy conversation history từ session (nếu có)
            HttpSession session = request.getSession();
            String conversationId = requestJson.has("conversationId") ? 
                    requestJson.get("conversationId").getAsString() : "default";
            
            @SuppressWarnings("unchecked")
            List<Map<String, String>> conversationHistory = 
                    (List<Map<String, String>>) session.getAttribute("openai_conversation_" + conversationId);
            
            if (conversationHistory == null) {
                conversationHistory = new ArrayList<>();
            }
            
            // Thêm user message vào history
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage.trim());
            conversationHistory.add(userMsg);
            
            // Giới hạn conversation history (chỉ giữ 10 messages gần nhất để tránh vượt quá token limit)
            // Gemini có free tier rộng rãi nên có thể giữ nhiều hơn, nhưng vẫn giới hạn để tối ưu
            if (conversationHistory.size() > 10) {
                conversationHistory = conversationHistory.subList(conversationHistory.size() - 10, conversationHistory.size());
            }
            
            // Gọi Gemini API với error message holder
            String[] errorMessageHolder = new String[1];
            String aiResponse = GeminiUtil.chatCompletionWithHistory(conversationHistory, SYSTEM_PROMPT, errorMessageHolder);
            
            if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                // Thêm AI response vào history
                Map<String, String> aiMsg = new HashMap<>();
                aiMsg.put("role", "assistant");
                aiMsg.put("content", aiResponse.trim());
                conversationHistory.add(aiMsg);
                
                // Lưu conversation history vào session
                session.setAttribute("openai_conversation_" + conversationId, conversationHistory);
                
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", aiResponse.trim());
                
            } else {
                // Lấy error message từ holder hoặc dùng message mặc định
                String errorMsg = (errorMessageHolder[0] != null && !errorMessageHolder[0].isEmpty()) 
                    ? errorMessageHolder[0] 
                    : "Không thể nhận được phản hồi từ AI. Vui lòng kiểm tra cấu hình API key hoặc thử lại sau.";
                
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", errorMsg);
                
                // Log chi tiết để debug
                System.err.println("Gemini API call failed. Error: " + errorMsg);
                System.err.println("API Key configured: " + (GeminiUtil.getApiKey() != null && !GeminiUtil.getApiKey().isEmpty() && !GeminiUtil.getApiKey().equals("YOUR_GEMINI_API_KEY_HERE")));
            }
            
        } catch (Exception e) {
            System.err.println("Error in OpenAIServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // GET method: clear conversation history
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            HttpSession session = request.getSession();
            String conversationId = request.getParameter("conversationId");
            if (conversationId == null || conversationId.trim().isEmpty()) {
                conversationId = "default";
            }
            
            // Xóa conversation history
            session.removeAttribute("openai_conversation_" + conversationId);
            
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Conversation history cleared");
            
        } catch (Exception e) {
            System.err.println("Error in OpenAIServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
}

