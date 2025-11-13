package service;

import java.util.List;
import java.util.Map;

/**
 * Response từ ChatbotService
 */
public class ChatbotResponse {
    
    private String answer; // Câu trả lời
    private NluResult nluResult; // Kết quả NLU
    private List<Map<String, Object>> data; // Dữ liệu từ database
    private String action; // Action cho Agentic AI
    
    public ChatbotResponse() {
    }
    
    // Getters and Setters
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public NluResult getNluResult() {
        return nluResult;
    }
    
    public void setNluResult(NluResult nluResult) {
        this.nluResult = nluResult;
    }
    
    public List<Map<String, Object>> getData() {
        return data;
    }
    
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
}

