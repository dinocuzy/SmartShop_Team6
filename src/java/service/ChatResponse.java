package service;

import model.Product;
import java.util.List;

/**
 * Response từ AnswerService
 * Chứa câu trả lời và danh sách sản phẩm gợi ý
 */
public class ChatResponse {
    
    private String reply; // Câu trả lời từ AI
    private List<Product> suggestions; // Danh sách sản phẩm gợi ý
    
    public ChatResponse() {
    }
    
    public ChatResponse(String reply, List<Product> suggestions) {
        this.reply = reply;
        this.suggestions = suggestions;
    }
    
    // Getters and Setters
    public String getReply() {
        return reply;
    }
    
    public void setReply(String reply) {
        this.reply = reply;
    }
    
    public List<Product> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<Product> suggestions) {
        this.suggestions = suggestions;
    }
}

