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
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class để gọi OpenAI API
 * Hỗ trợ chat completion API của OpenAI
 */
public class OpenAIUtil {
    
    // OpenAI API endpoint
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    // Default model
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    
    // API Key - sẽ được load từ context-param trong web.xml
    private static String apiKey = "";
    
    /**
     * Khởi tạo API key từ context
     * @param apiKey API key từ web.xml
     */
    public static void setApiKey(String apiKey) {
        OpenAIUtil.apiKey = apiKey != null ? apiKey.trim() : "";
        if (OpenAIUtil.apiKey != null && !OpenAIUtil.apiKey.isEmpty() && !OpenAIUtil.apiKey.equals("YOUR_OPENAI_API_KEY_HERE")) {
            System.out.println("OpenAI API key đã được cấu hình (length: " + OpenAIUtil.apiKey.length() + " chars)");
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
     * Gọi OpenAI Chat API để lấy response
     * @param userMessage Tin nhắn từ user
     * @param systemPrompt System prompt (optional)
     * @return Response từ OpenAI hoặc null nếu có lỗi
     */
    public static String chatCompletion(String userMessage, String systemPrompt) {
        return chatCompletion(userMessage, systemPrompt, DEFAULT_MODEL);
    }
    
    /**
     * Gọi OpenAI Chat API với model cụ thể
     * @param userMessage Tin nhắn từ user
     * @param systemPrompt System prompt (optional)
     * @param model Model name (default: gpt-3.5-turbo)
     * @return Response từ OpenAI hoặc null nếu có lỗi
     */
    public static String chatCompletion(String userMessage, String systemPrompt, String model) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("OpenAI API key is not configured");
            return null;
        }
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            System.err.println("User message is empty");
            return null;
        }
        
        if (model == null || model.trim().isEmpty()) {
            model = DEFAULT_MODEL;
        }
        
        try {
            // Tạo JSON request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);
            
            JsonArray messages = new JsonArray();
            
            // Thêm system prompt nếu có
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content", systemPrompt.trim());
                messages.add(systemMessage);
            }
            
            // Thêm user message
            JsonObject userMessageObj = new JsonObject();
            userMessageObj.addProperty("role", "user");
            userMessageObj.addProperty("content", userMessage.trim());
            messages.add(userMessageObj);
            
            requestBody.add("messages", messages);
            
            // Cấu hình request parameters
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 500);
            
            // Gửi request
            URL url = new URL(OPENAI_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            
            // Gửi request body
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                writer.write(gson.toJson(requestBody));
                writer.flush();
            }
            
            // Đọc response
            int responseCode = conn.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
                JsonArray choices = jsonResponse.getAsJsonArray("choices");
                if (choices != null && choices.size() > 0) {
                    JsonObject firstChoice = choices.get(0).getAsJsonObject();
                    JsonObject message = firstChoice.getAsJsonObject("message");
                    if (message != null) {
                        String content = message.get("content").getAsString();
                        System.out.println("OpenAI API Response: " + content);
                        return content;
                    }
                }
                
                System.err.println("OpenAI API: No content in response");
                return null;
                
            } else {
                // Đọc error response
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                reader.close();
                
                System.err.println("OpenAI API Error - Response Code: " + responseCode);
                System.err.println("Error Response: " + errorResponse.toString());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gọi OpenAI Chat API với context từ conversation history
     * @param messages Danh sách messages (Map với "role" và "content")
     * @param systemPrompt System prompt (optional)
     * @return Response từ OpenAI hoặc null nếu có lỗi
     */
    public static String chatCompletionWithHistory(java.util.List<Map<String, String>> messages, String systemPrompt) {
        return chatCompletionWithHistory(messages, systemPrompt, null);
    }
    
    /**
     * Gọi OpenAI Chat API với context từ conversation history và lưu error message
     * @param messages Danh sách messages (Map với "role" và "content")
     * @param systemPrompt System prompt (optional)
     * @param errorMessageHolder Mảng String để lưu error message (có thể null)
     * @return Response từ OpenAI hoặc null nếu có lỗi
     */
    public static String chatCompletionWithHistory(java.util.List<Map<String, String>> messages, String systemPrompt, String[] errorMessageHolder) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_OPENAI_API_KEY_HERE")) {
            String errorMsg = "OpenAI API key chưa được cấu hình. Vui lòng kiểm tra file web.xml.";
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
            // Tạo JSON request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", DEFAULT_MODEL);
            
            JsonArray messagesArray = new JsonArray();
            
            // Thêm system prompt nếu có
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content", systemPrompt.trim());
                messagesArray.add(systemMessage);
            }
            
            // Thêm conversation history
            for (Map<String, String> message : messages) {
                JsonObject messageObj = new JsonObject();
                String role = message.get("role");
                String content = message.get("content");
                if (role != null && content != null) {
                    messageObj.addProperty("role", role);
                    messageObj.addProperty("content", content);
                    messagesArray.add(messageObj);
                }
            }
            
            requestBody.add("messages", messagesArray);
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 300); // Giảm từ 500 xuống 300 để tiết kiệm chi phí
            
            // Gửi request
            URL url = new URL(OPENAI_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
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
                JsonArray choices = jsonResponse.getAsJsonArray("choices");
                if (choices != null && choices.size() > 0) {
                    JsonObject firstChoice = choices.get(0).getAsJsonObject();
                    JsonObject message = firstChoice.getAsJsonObject("message");
                    if (message != null) {
                        String content = message.get("content").getAsString();
                        System.out.println("OpenAI API Response: " + content);
                        return content;
                    }
                }
                
                String errorMsg = "OpenAI API: No content in response";
                System.err.println(errorMsg);
                if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                    errorMessageHolder[0] = errorMsg;
                }
                return null;
                
            } else {
                // Đọc error response
                BufferedReader reader = null;
                String errorDetail = null;
                try {
                    reader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorDetail = errorResponse.toString();
                    
                    // Parse error message từ JSON response
                    try {
                        Gson gson = new Gson();
                        JsonObject errorJson = gson.fromJson(errorDetail, JsonObject.class);
                        if (errorJson.has("error")) {
                            JsonObject errorObj = errorJson.getAsJsonObject("error");
                            if (errorObj.has("message")) {
                                errorDetail = errorObj.get("message").getAsString();
                            } else if (errorObj.has("type")) {
                                errorDetail = errorObj.get("type").getAsString();
                            }
                        }
                    } catch (Exception parseEx) {
                        // Nếu không parse được JSON, dùng error response gốc
                    }
                    
                    System.err.println("OpenAI API Error - Response Code: " + responseCode);
                    System.err.println("Error Response: " + errorDetail);
                    
                } catch (Exception e) {
                    System.err.println("Error reading error stream: " + e.getMessage());
                    errorDetail = "Lỗi kết nối đến OpenAI API (Response Code: " + responseCode + ")";
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            // Ignore
                        }
                    }
                }
                
                // Set error message vào holder
                String finalErrorMsg = null;
                if (responseCode == 401) {
                    finalErrorMsg = "API key không hợp lệ hoặc đã hết hạn. Vui lòng kiểm tra lại API key trong web.xml hoặc tạo API key mới tại https://platform.openai.com/api-keys";
                } else if (responseCode == 429) {
                    // Kiểm tra nếu là quota exceeded
                    if (errorDetail != null && (errorDetail.toLowerCase().contains("quota") || 
                                                 errorDetail.toLowerCase().contains("exceeded") ||
                                                 errorDetail.toLowerCase().contains("billing"))) {
                        finalErrorMsg = "Tài khoản OpenAI đã hết quota/tín dụng. " +
                                       "Vui lòng kiểm tra và nạp tiền tại https://platform.openai.com/account/billing. " +
                                       "Bạn có thể xem usage tại https://platform.openai.com/usage";
                    } else {
                        finalErrorMsg = "Đã vượt quá giới hạn rate limit. Vui lòng thử lại sau vài phút.";
                    }
                } else if (responseCode == 500 || responseCode >= 500) {
                    finalErrorMsg = "Lỗi server từ OpenAI. Vui lòng thử lại sau.";
                } else if (errorDetail != null && !errorDetail.isEmpty()) {
                    // Kiểm tra nếu errorDetail chứa thông tin về quota
                    if (errorDetail.toLowerCase().contains("quota") || errorDetail.toLowerCase().contains("billing")) {
                        finalErrorMsg = "Tài khoản OpenAI đã hết quota. " +
                                       "Vui lòng kiểm tra và nạp tiền tại https://platform.openai.com/account/billing";
                    } else {
                        finalErrorMsg = "Lỗi: " + errorDetail;
                    }
                } else {
                    finalErrorMsg = "Lỗi kết nối đến OpenAI API (Response Code: " + responseCode + ")";
                }
                
                if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                    errorMessageHolder[0] = finalErrorMsg;
                }
                return null;
            }
            
        } catch (java.net.SocketTimeoutException e) {
            String errorMsg = "Kết nối timeout. Vui lòng thử lại sau.";
            System.err.println("OpenAI API Timeout: " + e.getMessage());
            e.printStackTrace();
            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                errorMessageHolder[0] = errorMsg;
            }
            return null;
        } catch (java.net.UnknownHostException e) {
            String errorMsg = "Không thể kết nối đến OpenAI API. Vui lòng kiểm tra kết nối internet.";
            System.err.println("OpenAI API Connection Error: " + e.getMessage());
            e.printStackTrace();
            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                errorMessageHolder[0] = errorMsg;
            }
            return null;
        } catch (Exception e) {
            String errorMsg = "Lỗi không xác định: " + e.getMessage();
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            e.printStackTrace();
            if (errorMessageHolder != null && errorMessageHolder.length > 0) {
                errorMessageHolder[0] = errorMsg;
            }
            return null;
        }
    }
}

