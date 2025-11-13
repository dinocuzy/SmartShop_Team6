package service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Kết quả phân tích NLU (Natural Language Understanding)
 * Chứa intent, entities, và các thông tin khác từ câu hỏi của user
 */
public class NluResult {
    
    // Intent types (theo hướng dẫn)
    public static final String INTENT_FIND_PRODUCT = "find_product";
    public static final String INTENT_ASK_PROMOTION = "ask_promotion";
    public static final String INTENT_ORDER_STATUS = "order_status";
    public static final String INTENT_COMPARE = "compare";
    public static final String INTENT_SMALL_TALK = "small_talk";
    
    // Intent types (cũ - giữ để tương thích)
    public static final String INTENT_PRODUCT_SEARCH = "product_search";
    public static final String INTENT_PRODUCT_DETAIL = "product_detail";
    public static final String INTENT_PROMOTION = "promotion";
    public static final String INTENT_CART = "cart";
    public static final String INTENT_CATEGORY = "category";
    public static final String INTENT_GREETING = "greeting";
    public static final String INTENT_GOODBYE = "goodbye";
    public static final String INTENT_HELP = "help";
    public static final String INTENT_GENERAL = "general";
    
    private String intent; // Intent chính
    private Map<String, Object> entities; // Entities (keywords, price range, category, etc.)
    private String originalMessage; // Câu hỏi gốc
    private double confidence; // Độ tin cậy (0.0 - 1.0)
    
    // Các field theo hướng dẫn
    private String category; // Category: phone, laptop, tablet, accessory, null
    private Double priceMin; // Giá tối thiểu
    private Double priceMax; // Giá tối đa
    private List<String> features; // Danh sách tính năng: ["camera", "battery", "gaming"]
    private String brand; // Thương hiệu: Samsung, Apple, Xiaomi, null
    
    public NluResult() {
        this.entities = new HashMap<>();
        this.confidence = 0.5;
        this.features = new ArrayList<>();
    }
    
    public NluResult(String intent, String originalMessage) {
        this();
        this.intent = intent;
        this.originalMessage = originalMessage;
    }
    
    // Getters and Setters
    public String getIntent() {
        return intent;
    }
    
    public void setIntent(String intent) {
        this.intent = intent;
    }
    
    public Map<String, Object> getEntities() {
        return entities;
    }
    
    public void setEntities(Map<String, Object> entities) {
        this.entities = entities;
    }
    
    public void addEntity(String key, Object value) {
        this.entities.put(key, value);
    }
    
    public Object getEntity(String key) {
        return this.entities.get(key);
    }
    
    public String getOriginalMessage() {
        return originalMessage;
    }
    
    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    /**
     * Kiểm tra xem có entity nào không
     */
    public boolean hasEntities() {
        return entities != null && !entities.isEmpty();
    }
    
    /**
     * Lấy keyword từ entities
     */
    public String getKeyword() {
        Object keyword = entities.get("keyword");
        return keyword != null ? keyword.toString() : null;
    }
    
    /**
     * Lấy category từ entities
     */
    public String getCategory() {
        Object category = entities.get("category");
        return category != null ? category.toString() : null;
    }
    
    /**
     * Lấy price range từ entities
     */
    public Map<String, Double> getPriceRange() {
        Object priceRange = entities.get("priceRange");
        if (priceRange instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Double> range = (Map<String, Double>) priceRange;
            return range;
        }
        return null;
    }
    
    // Getters and Setters cho các field mới
    
    public void setCategory(String category) {
        this.category = category;
        if (category != null) {
            this.addEntity("category", category);
        }
    }
    
    public Double getPriceMin() {
        return priceMin;
    }
    
    public void setPriceMin(Double priceMin) {
        this.priceMin = priceMin;
        if (priceMin != null) {
            this.addEntity("priceMin", priceMin);
        }
    }
    
    public Double getPriceMax() {
        return priceMax;
    }
    
    public void setPriceMax(Double priceMax) {
        this.priceMax = priceMax;
        if (priceMax != null) {
            this.addEntity("priceMax", priceMax);
        }
    }
    
    public List<String> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<String> features) {
        this.features = features != null ? features : new ArrayList<>();
        if (features != null && !features.isEmpty()) {
            this.addEntity("features", features);
        }
    }
    
    public void addFeature(String feature) {
        if (this.features == null) {
            this.features = new ArrayList<>();
        }
        if (feature != null && !feature.trim().isEmpty()) {
            this.features.add(feature);
        }
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
        if (brand != null) {
            this.addEntity("brand", brand);
        }
    }
}

