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
 * Client class để gọi Google Gemini API
 * Wrapper cho GeminiUtil với interface rõ ràng hơn
 */
public class GeminiClient {
    
    private static final String DEFAULT_MODEL = "gemini-2.5-flash";
    
    /**
     * Gọi Gemini API để generate text
     * @param prompt Prompt để gửi cho Gemini
     * @return Response từ Gemini
     */
    public static String generateText(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return null;
        }
        
        String[] errorHolder = new String[1];
        List<Map<String, String>> messages = new java.util.ArrayList<>();
        Map<String, String> message = new java.util.HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        
        return GeminiUtil.chatCompletionWithHistory(messages, null, errorHolder);
    }
    
    /**
     * Gọi Gemini API với conversation history
     * @param messages Danh sách messages
     * @return Response từ Gemini
     */
    public static String chatCompletion(List<Map<String, String>> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        
        String[] errorHolder = new String[1];
        return GeminiUtil.chatCompletionWithHistory(messages, null, errorHolder);
    }
    
    /**
     * Gọi Gemini API với system prompt và messages
     * @param systemPrompt System prompt
     * @param messages Danh sách messages
     * @return Response từ Gemini
     */
    public static String chatCompletion(String systemPrompt, List<Map<String, String>> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        
        String[] errorHolder = new String[1];
        return GeminiUtil.chatCompletionWithHistory(messages, systemPrompt, errorHolder);
    }
    
    /**
     * Kiểm tra API key đã được cấu hình chưa
     * @return true nếu đã cấu hình
     */
    public static boolean isApiKeyConfigured() {
        String apiKey = GeminiUtil.getApiKey();
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("YOUR_GEMINI_API_KEY_HERE");
    }
}

