package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.ChatbotService;
import service.ChatbotResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Servlet xử lý chat với AI
 * URL mapping: /api/chat
 * Method: POST
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
 *   "answer": "Câu trả lời từ AI",
 *   "intent": "Intent được phát hiện",
 *   "data": [...], // Dữ liệu sản phẩm/đơn hàng/etc
 *   "action": "Action cho Agentic AI",
 *   "error": "Error message (nếu có)"
 * }
 */
@WebServlet("/api/chat")
public class ChatServlet extends HttpServlet {
    
    private ChatbotService chatbotService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        chatbotService = new ChatbotService();
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
            JsonObject requestJson = gson.fromJson(requestBody, JsonObject.class);
            
            String message = null;
            if (requestJson.has("message")) {
                message = requestJson.get("message").getAsString();
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
            
            // Lấy user từ session
            HttpSession session = request.getSession(false);
            User currentUser = null;
            if (session != null) {
                try {
                    currentUser = (User) session.getAttribute("currentUser");
                } catch (IllegalStateException e) {
                    // Session đã bị invalidate
                    session = request.getSession(true);
                    currentUser = null;
                }
            }
            
            // Xử lý message
            ChatbotResponse chatbotResponse = chatbotService.processMessage(message, currentUser);
            
            // Build JSON response
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("answer", chatbotResponse.getAnswer());
            
            if (chatbotResponse.getNluResult() != null) {
                jsonResponse.addProperty("intent", chatbotResponse.getNluResult().getIntent());
                jsonResponse.addProperty("confidence", chatbotResponse.getNluResult().getConfidence());
            }
            
            // Add data
            if (chatbotResponse.getData() != null && !chatbotResponse.getData().isEmpty()) {
                JsonArray dataArray = new JsonArray();
                for (Map<String, Object> item : chatbotResponse.getData()) {
                    JsonObject itemJson = new JsonObject();
                    for (Map.Entry<String, Object> entry : item.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof String) {
                            itemJson.addProperty(entry.getKey(), (String) value);
                        } else if (value instanceof Number) {
                            if (value instanceof Integer) {
                                itemJson.addProperty(entry.getKey(), (Integer) value);
                            } else if (value instanceof Double) {
                                itemJson.addProperty(entry.getKey(), (Double) value);
                            } else {
                                itemJson.addProperty(entry.getKey(), value.toString());
                            }
                        } else if (value instanceof Boolean) {
                            itemJson.addProperty(entry.getKey(), (Boolean) value);
                        } else {
                            itemJson.addProperty(entry.getKey(), value != null ? value.toString() : "");
                        }
                    }
                    dataArray.add(itemJson);
                }
                jsonResponse.add("data", dataArray);
            } else {
                jsonResponse.add("data", new JsonArray());
            }
            
            // Add action (Agentic AI)
            if (chatbotResponse.getAction() != null) {
                jsonResponse.addProperty("action", chatbotResponse.getAction());
                
                // Nếu action là show_products và có data, thêm productID đầu tiên
                if ("show_products".equals(chatbotResponse.getAction()) && 
                    chatbotResponse.getData() != null && 
                    !chatbotResponse.getData().isEmpty()) {
                    
                    Map<String, Object> firstProduct = chatbotResponse.getData().get(0);
                    if (firstProduct.containsKey("productID")) {
                        jsonResponse.addProperty("productID", 
                            Integer.parseInt(firstProduct.get("productID").toString()));
                        jsonResponse.addProperty("productName", 
                            firstProduct.get("productName") != null ? 
                            firstProduct.get("productName").toString() : "");
                        jsonResponse.addProperty("productPrice", 
                            firstProduct.get("price") != null ? 
                            Double.parseDouble(firstProduct.get("price").toString()) : 0);
                        jsonResponse.addProperty("productImageUrl", 
                            firstProduct.get("imageUrl") != null ? 
                            firstProduct.get("imageUrl").toString() : "");
                        jsonResponse.addProperty("productStock", 
                            firstProduct.get("stock") != null ? 
                            Integer.parseInt(firstProduct.get("stock").toString()) : 0);
                        jsonResponse.addProperty("productStockStatus", 
                            firstProduct.get("stockStatus") != null ? 
                            firstProduct.get("stockStatus").toString() : "InStock");
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in ChatServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Đã xảy ra lỗi khi xử lý yêu cầu: " + e.getMessage());
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

