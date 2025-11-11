package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class để parse JSON đơn giản (không dùng thư viện bên ngoài)
 * Chỉ hỗ trợ các trường hợp đơn giản như response từ Google OAuth
 */
public class JsonUtil {
    
    /**
     * Parse JSON string đơn giản thành Map
     * Hỗ trợ format: {"key1":"value1","key2":123,"key3":true,"key4":null}
     */
    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        
        if (json == null || json.trim().isEmpty()) {
            return map;
        }
        
        // Remove outer braces và whitespace
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1).trim();
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1).trim();
        }
        
        // Pattern để match "key":"value" (string values)
        Pattern stringPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
        Matcher stringMatcher = stringPattern.matcher(json);
        
        while (stringMatcher.find()) {
            String key = stringMatcher.group(1);
            String value = stringMatcher.group(2);
            map.put(key, value);
        }
        
        // Pattern để match "key":number (numeric values)
        Pattern numberPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
        Matcher numberMatcher = numberPattern.matcher(json);
        
        while (numberMatcher.find()) {
            String key = numberMatcher.group(1);
            String value = numberMatcher.group(2);
            if (!map.containsKey(key)) {
                map.put(key, value);
            }
        }
        
        // Pattern để match "key":true/false (boolean values)
        Pattern booleanPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(true|false)");
        Matcher booleanMatcher = booleanPattern.matcher(json);
        
        while (booleanMatcher.find()) {
            String key = booleanMatcher.group(1);
            String value = booleanMatcher.group(2);
            if (!map.containsKey(key)) {
                map.put(key, value);
            }
        }
        
        // Pattern để match "key":null (null values - bỏ qua)
        
        return map;
    }
    
    /**
     * Extract giá trị từ JSON string bằng key
     */
    public static String getJsonValue(String json, String key) {
        Map<String, String> map = parseSimpleJson(json);
        return map.get(key);
    }
    
    /**
     * Parse JSON response từ Google OAuth token endpoint
     * Format: access_token=xxx&token_type=Bearer&expires_in=3600
     */
    public static Map<String, String> parseUrlEncoded(String urlEncoded) {
        Map<String, String> map = new HashMap<>();
        
        if (urlEncoded == null || urlEncoded.trim().isEmpty()) {
            return map;
        }
        
        String[] pairs = urlEncoded.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                    map.put(key, value);
                } catch (java.io.UnsupportedEncodingException e) {
                    // Fallback
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        }
        
        return map;
    }
}

