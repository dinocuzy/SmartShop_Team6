package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Utility class để gọi Google Gemini API
 * Hỗ trợ chat completion API của Google Gemini (Free tier)
 */
public class GeminiUtil {
    
    // Gemini API base URLs - Thử cả v1beta và v1
    private static final String GEMINI_API_BASE_URL_V1BETA = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String GEMINI_API_BASE_URL_V1 = "https://generativelanguage.googleapis.com/v1/models/";
    
    // Default model - Thử các model mới nhất từ danh sách có sẵn
    // Dựa trên danh sách model thực tế từ API:
    // - gemini-2.5-flash: Model flash mới nhất, free tier
    // - gemini-2.0-flash: Model flash ổn định
    // - gemini-flash-latest: Alias cho model flash mới nhất
    // - gemini-pro-latest: Alias cho model pro mới nhất
    // - gemini-2.5-flash-lite: Model flash lite (nhẹ hơn)
    private static final String[] MODELS_TO_TRY = {
        "gemini-2.5-flash",          // Model flash mới nhất (recommended)
        "gemini-2.0-flash",          // Model flash ổn định
        "gemini-flash-latest",       // Alias cho model flash mới nhất
        "gemini-2.5-flash-lite",     // Model flash lite
        "gemini-2.5-pro",            // Model pro mới nhất (nếu flash không có)
        "gemini-pro-latest"          // Alias cho model pro mới nhất
    };
    
    private static final String[] BASE_URLS_TO_TRY = {
        GEMINI_API_BASE_URL_V1BETA,  // Thử v1beta trước (thường có nhiều model hơn)
        GEMINI_API_BASE_URL_V1       // Fallback sang v1
    };
    
    // API Key - sẽ được load từ context-param trong web.xml
    private static String apiKey = "";
    
    /**
     * Khởi tạo API key từ context
     * @param apiKey API key từ web.xml
     */
    public static void setApiKey(String apiKey) {
        GeminiUtil.apiKey = apiKey != null ? apiKey.trim() : "";
        if (GeminiUtil.apiKey != null && !GeminiUtil.apiKey.isEmpty() && !GeminiUtil.apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            System.out.println("Gemini API key đã được cấu hình (length: " + GeminiUtil.apiKey.length() + " chars)");
        }
    }
    
    /**
     * Lấy API key
     * @return API key
     */
    public static String getApiKey() {
        return apiKey;
    }
    
    /**
     * Gọi Gemini API với conversation history và lưu error message
     * @param messages Danh sách messages (Map với "role" và "content")
     * @param systemPrompt System prompt (optional)
     * @param errorMessageHolder Mảng String để lưu error message (có thể null)
     * @return Response từ Gemini hoặc null nếu có lỗi
     */
    public static String chatCompletionWithHistory(List<Map<String, String>> messages, String systemPrompt, String[] errorMessageHolder) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            String errorMsg = "Gemini API key chưa được cấu hình. Vui lòng kiểm tra file web.xml.";
            System.err.println(errorMsg);
            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                errorMessageHolder[0] = errorMsg;
            }
            return null;
        }
        
        if (messages == null || messages.isEmpty()) {
            String errorMsg = "Messages list is empty";
            System.err.println(errorMsg);
            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                errorMessageHolder[0] = errorMsg;
            }
            return null;
        }
        
        try {
            // Tạo JSON request body cho Gemini API
            JsonObject requestBody = new JsonObject();
            JsonArray contents = new JsonArray();
            
            // Gemini sử dụng "parts" với "text" thay vì "messages" với "role" và "content"
            // Cần convert từ format OpenAI sang format Gemini
            
            // Gemini không hỗ trợ system prompt riêng như OpenAI
            // Thêm system prompt vào đầu conversation (chỉ một lần nếu conversation mới)
            boolean hasSystemPrompt = false;
            
            // Kiểm tra xem conversation history đã có system prompt chưa
            for (Map<String, String> msg : messages) {
                String msgContent = msg.get("content");
                if (msgContent != null && (msgContent.contains("Bạn là một nhân viên chăm sóc khách hàng") || 
                                           msgContent.contains("SmartShop") || 
                                           msgContent.contains("VAI TRÒ CỦA BẠN"))) {
                    hasSystemPrompt = true;
                    break;
                }
            }
            
            // Thêm system prompt như user message đầu tiên (nếu chưa có)
            if (!hasSystemPrompt && systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                JsonObject systemContent = new JsonObject();
                JsonArray parts = new JsonArray();
                JsonObject textPart = new JsonObject();
                textPart.addProperty("text", systemPrompt.trim());
                parts.add(textPart);
                systemContent.add("parts", parts);
                systemContent.addProperty("role", "user");
                contents.add(systemContent);
            }
            
            // Thêm conversation history
            for (Map<String, String> message : messages) {
                JsonObject content = new JsonObject();
                JsonArray parts = new JsonArray();
                JsonObject textPart = new JsonObject();
                
                String role = message.get("role");
                String contentText = message.get("content");
                
                if (role != null && contentText != null) {
                    // Gemini sử dụng "user" và "model" thay vì "user" và "assistant"
                    String geminiRole = "user".equals(role) ? "user" : "model";
                    
                    textPart.addProperty("text", contentText.trim());
                    parts.add(textPart);
                    content.add("parts", parts);
                    content.addProperty("role", geminiRole);
                    contents.add(content);
                }
            }
            
            requestBody.add("contents", contents);
            
            // Cấu hình generation config
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.7);
            generationConfig.addProperty("maxOutputTokens", 500); // Gemini free tier rộng rãi nên có thể để cao hơn
            requestBody.add("generationConfig", generationConfig);
            
            // Thử các combination của API version và model
            Exception lastException = null;
            int lastResponseCode = -1;
            String lastErrorDetail = null;
            
            for (String baseUrl : BASE_URLS_TO_TRY) {
                for (String model : MODELS_TO_TRY) {
                    try {
                        // Tạo URL với API key và model
                        String urlString = baseUrl + model + ":generateContent?key=" + apiKey;
                        System.out.println("Trying Gemini API: " + urlString.replace(apiKey, "***"));
                        
                        URL url = new URL(urlString);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(30000); // 30 seconds
                        conn.setReadTimeout(60000); // 60 seconds
                        
                        // Gửi request body
                        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
                            Gson gson = new Gson();
                            writer.write(gson.toJson(requestBody));
                            writer.flush();
                        }
                        
                        // Đọc response
                        int responseCode = conn.getResponseCode();
                        
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            // Thành công! Đọc response
                            BufferedReader reader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();
                            
                            // Parse JSON response
                            Gson gson = new Gson();
                            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                            
                            // Lấy content từ response
                            if (jsonResponse.has("candidates")) {
                                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                                if (candidates != null && candidates.size() > 0) {
                                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                                    if (firstCandidate.has("content")) {
                                        JsonObject content = firstCandidate.getAsJsonObject("content");
                                        if (content.has("parts")) {
                                            JsonArray parts = content.getAsJsonArray("parts");
                                            if (parts != null && parts.size() > 0) {
                                                JsonObject firstPart = parts.get(0).getAsJsonObject();
                                                if (firstPart.has("text")) {
                                                    String text = firstPart.get("text").getAsString();
                                                    System.out.println("✓ Gemini API Success with model: " + model + ", API: " + (baseUrl.contains("v1beta") ? "v1beta" : "v1"));
                                                    conn.disconnect();
                                                    return text;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Kiểm tra finishReason
                            if (jsonResponse.has("candidates")) {
                                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                                if (candidates != null && candidates.size() > 0) {
                                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                                    if (firstCandidate.has("finishReason")) {
                                        String finishReason = firstCandidate.get("finishReason").getAsString();
                                        if ("SAFETY".equals(finishReason)) {
                                            String errorMsg = "Response bị chặn do safety filters. Vui lòng thử lại với câu hỏi khác.";
                                            System.err.println("Gemini API Safety Filter: " + finishReason);
                                            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                                                errorMessageHolder[0] = errorMsg;
                                            }
                                            conn.disconnect();
                                            return null;
                                        }
                                    }
                                }
                            }
                            conn.disconnect();
                        } else {
                            // Lưu error để log
                            lastResponseCode = responseCode;
                            try {
                                BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                                StringBuilder errorResponse = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    errorResponse.append(line);
                                }
                                lastErrorDetail = errorResponse.toString();
                                reader.close();
                                
                                // Parse error message
                                try {
                                    Gson gson = new Gson();
                                    JsonObject errorJson = gson.fromJson(lastErrorDetail, JsonObject.class);
                                    if (errorJson.has("error")) {
                                        JsonObject errorObj = errorJson.getAsJsonObject("error");
                                        if (errorObj.has("message")) {
                                            lastErrorDetail = errorObj.get("message").getAsString();
                                        }
                                    }
                                } catch (Exception parseEx) {
                                    // Ignore
                                }
                                
                                System.err.println("✗ Failed with model: " + model + ", API: " + (baseUrl.contains("v1beta") ? "v1beta" : "v1") + " - " + lastErrorDetail);
                            } catch (Exception e) {
                                // Ignore
                            }
                            conn.disconnect();
                            continue; // Thử model/API khác
                        }
                        
                    } catch (Exception e) {
                        lastException = e;
                        System.err.println("✗ Exception with model: " + model + ", API: " + (baseUrl.contains("v1beta") ? "v1beta" : "v1") + " - " + e.getMessage());
                        continue; // Thử model/API khác
                    }
                }
            }
            
            // Nếu tất cả đều thất bại, trả về error với thông tin chi tiết
            String finalErrorMsg;
            if (lastResponseCode == -1) {
                // Không có response code nào (có thể do exception)
                finalErrorMsg = "Không thể kết nối đến Gemini API. Đã thử tất cả các model và API version có sẵn. " +
                               "Vui lòng kiểm tra API key và kết nối internet. " +
                               (lastException != null ? "Chi tiết: " + lastException.getMessage() : "");
            } else if (lastResponseCode == 400) {
                finalErrorMsg = "Request không hợp lệ. " + (lastErrorDetail != null ? lastErrorDetail : "") +
                               " Đã thử tất cả các model: " + String.join(", ", MODELS_TO_TRY);
            } else if (lastResponseCode == 401 || lastResponseCode == 403) {
                finalErrorMsg = "API key không hợp lệ hoặc không có quyền. Vui lòng kiểm tra lại API key trong web.xml hoặc tạo API key mới tại https://aistudio.google.com/app/apikey";
            } else if (lastResponseCode == 404) {
                finalErrorMsg = "Không tìm thấy model phù hợp. Đã thử các model: " + String.join(", ", MODELS_TO_TRY) + 
                               ". " + (lastErrorDetail != null ? lastErrorDetail : "") +
                               " Vui lòng kiểm tra danh sách model có sẵn tại https://aistudio.google.com/";
            } else if (lastResponseCode == 429) {
                finalErrorMsg = "Đã vượt quá giới hạn rate limit. Vui lòng thử lại sau vài phút.";
            } else if (lastResponseCode == 500 || lastResponseCode >= 500) {
                finalErrorMsg = "Lỗi server từ Gemini API. Vui lòng thử lại sau.";
            } else if (lastErrorDetail != null && !lastErrorDetail.isEmpty()) {
                finalErrorMsg = "Lỗi từ Gemini API: " + lastErrorDetail + 
                               " (Response Code: " + lastResponseCode + ")";
            } else {
                finalErrorMsg = "Không thể kết nối đến Gemini API. Đã thử tất cả các model và API version. " +
                               "(Response Code: " + lastResponseCode + ")";
            }
            
            System.err.println("✗ All Gemini API attempts failed. Final error: " + finalErrorMsg);
            
            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                errorMessageHolder[0] = finalErrorMsg;
            }
            return null;
        } catch (Exception e) {
            // Catch any unexpected exceptions
            String errorMsg = "Lỗi không xác định khi gọi Gemini API: " + e.getMessage();
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                errorMessageHolder[0] = errorMsg;
            }
            return null;
        }
    }
}

