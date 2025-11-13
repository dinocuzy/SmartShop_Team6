package service;

import util.GeminiClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

/**
 * Service phân tích câu hỏi của khách hàng (NLU - Natural Language Understanding)
 * Sử dụng Gemini AI để phân tích intent và entities
 */
public class NluService {
    
    private static final String NLU_PROMPT_TEMPLATE = 
        "Ban la he thong NLU cho website ban hang SmartShop.\n" +
        "Doc cau hoi cua khach va tra ve CHI JSON theo format:\n" +
        "{\n" +
        "  \"intent\": \"find_product | ask_promotion | order_status | compare | small_talk\",\n" +
        "  \"category\": \"phone | laptop | tablet | accessory | null\",\n" +
        "  \"price_min\": <number or null>,\n" +
        "  \"price_max\": <number or null>,\n" +
        "  \"features\": [\"camera\",\"battery\",\"gaming\"],\n" +
        "  \"brand\": \"Samsung | Apple | Xiaomi | null\"\n" +
        "}\n\n" +
        "Chi tra ve JSON, khong giai thich them.\n\n" +
        "Cau hoi: \"{MESSAGE}\"";
    
    /**
     * Phân tích câu hỏi của khách hàng
     * @param message Câu hỏi của khách hàng
     * @return NluResult chứa intent và entities
     */
    public NluResult analyze(String message) {
        if (message == null || message.trim().isEmpty()) {
            NluResult result = new NluResult();
            result.setIntent(NluResult.INTENT_GENERAL);
            result.setOriginalMessage(message);
            result.setConfidence(0.0);
            return result;
        }
        
        try {
            // Tạo prompt
            String prompt = NLU_PROMPT_TEMPLATE.replace("{MESSAGE}", message);
            
            // Gọi Gemini API
            String response = GeminiClient.generateText(prompt);
            
            if (response == null || response.trim().isEmpty()) {
                // Fallback: Phân tích đơn giản bằng keyword matching
                return analyzeByKeywords(message);
            }
            
            // Parse JSON response
            return parseGeminiResponse(response, message);
            
        } catch (Exception e) {
            System.err.println("Error in NluService.analyze: " + e.getMessage());
            e.printStackTrace();
            // Fallback: Phân tích đơn giản
            return analyzeByKeywords(message);
        }
    }
    
    /**
     * Parse response từ Gemini
     */
    private NluResult parseGeminiResponse(String response, String originalMessage) {
        NluResult result = new NluResult();
        result.setOriginalMessage(originalMessage);
        
        try {
            // Tìm JSON trong response (có thể có text thêm)
            String jsonStr = extractJsonFromResponse(response);
            
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
            
            // Parse intent
            if (json.has("intent")) {
                result.setIntent(json.get("intent").getAsString());
            } else {
                result.setIntent(NluResult.INTENT_GENERAL);
            }
            
            // Parse confidence
            if (json.has("confidence")) {
                result.setConfidence(json.get("confidence").getAsDouble());
            } else {
                result.setConfidence(0.7);
            }
            
            // Parse category
            if (json.has("category") && !json.get("category").isJsonNull()) {
                String category = json.get("category").getAsString();
                if (!category.equals("null") && !category.isEmpty()) {
                    result.setCategory(category);
                }
            }
            
            // Parse price_min
            if (json.has("price_min") && !json.get("price_min").isJsonNull()) {
                result.setPriceMin(json.get("price_min").getAsDouble());
            }
            
            // Parse price_max
            if (json.has("price_max") && !json.get("price_max").isJsonNull()) {
                result.setPriceMax(json.get("price_max").getAsDouble());
            }
            
            // Parse features
            if (json.has("features") && json.get("features").isJsonArray()) {
                JsonArray featuresArray = json.getAsJsonArray("features");
                List<String> features = new ArrayList<>();
                for (int i = 0; i < featuresArray.size(); i++) {
                    features.add(featuresArray.get(i).getAsString());
                }
                result.setFeatures(features);
            }
            
            // Parse brand
            if (json.has("brand") && !json.get("brand").isJsonNull()) {
                String brand = json.get("brand").getAsString();
                if (!brand.equals("null") && !brand.isEmpty()) {
                    result.setBrand(brand);
                }
            }
            
            // Parse entities (backward compatibility)
            if (json.has("entities")) {
                JsonObject entitiesJson = json.getAsJsonObject("entities");
                
                if (entitiesJson.has("keyword")) {
                    result.addEntity("keyword", entitiesJson.get("keyword").getAsString());
                }
                
                if (entitiesJson.has("productID")) {
                    result.addEntity("productID", entitiesJson.get("productID").getAsInt());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            // Fallback
            return analyzeByKeywords(originalMessage);
        }
        
        return result;
    }
    
    /**
     * Extract JSON từ response (có thể có markdown code block)
     */
    private String extractJsonFromResponse(String response) {
        // Loại bỏ markdown code block nếu có
        response = response.trim();
        if (response.startsWith("```json")) {
            response = response.substring(7);
        } else if (response.startsWith("```")) {
            response = response.substring(3);
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.length() - 3);
        }
        response = response.trim();
        
        // Tìm JSON object đầu tiên
        int startIdx = response.indexOf("{");
        int endIdx = response.lastIndexOf("}");
        
        if (startIdx >= 0 && endIdx > startIdx) {
            return response.substring(startIdx, endIdx + 1);
        }
        
        return response;
    }
    
    /**
     * Phân tích đơn giản bằng keyword matching (fallback)
     */
    private NluResult analyzeByKeywords(String message) {
        NluResult result = new NluResult();
        result.setOriginalMessage(message);
        result.setConfidence(0.5);
        
        String lowerMessage = message.toLowerCase();
        
        // Check greeting
        if (lowerMessage.matches(".*(chào|hello|hi|xin chào|chào bạn).*")) {
            result.setIntent(NluResult.INTENT_GREETING);
            return result;
        }
        
        // Check goodbye
        if (lowerMessage.matches(".*(tạm biệt|goodbye|bye|hẹn gặp).*")) {
            result.setIntent(NluResult.INTENT_GOODBYE);
            return result;
        }
        
        // Check product search
        if (lowerMessage.matches(".*(tìm|mua|bán|sản phẩm|hàng|điện thoại|laptop|máy tính).*")) {
            result.setIntent(NluResult.INTENT_PRODUCT_SEARCH);
            // Extract keyword
            String[] keywords = {"điện thoại", "laptop", "máy tính", "tai nghe", "chuột", "bàn phím"};
            for (String keyword : keywords) {
                if (lowerMessage.contains(keyword)) {
                    result.addEntity("keyword", keyword);
                    break;
                }
            }
            return result;
        }
        
        // Check promotion
        if (lowerMessage.matches(".*(khuyến mãi|giảm giá|sale|promotion).*")) {
            result.setIntent(NluResult.INTENT_PROMOTION);
            return result;
        }
        
        // Check order
        if (lowerMessage.matches(".*(đơn hàng|order|trạng thái|giao hàng).*")) {
            result.setIntent(NluResult.INTENT_ORDER_STATUS);
            return result;
        }
        
        // Check cart
        if (lowerMessage.matches(".*(giỏ hàng|cart|thêm vào giỏ).*")) {
            result.setIntent(NluResult.INTENT_CART);
            return result;
        }
        
        // Default
        result.setIntent(NluResult.INTENT_SMALL_TALK);
        return result;
    }
}

