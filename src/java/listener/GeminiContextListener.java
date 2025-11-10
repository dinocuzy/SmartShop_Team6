package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.GeminiUtil;

/**
 * Context Listener để khởi tạo Gemini API key khi application start
 */
@WebListener
public class GeminiContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Load Gemini API key từ context-param trong web.xml
            String apiKey = sce.getServletContext().getInitParameter("gemini_api_key");
            
            if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
                GeminiUtil.setApiKey(apiKey);
                System.out.println("Gemini API key đã được khởi tạo từ GeminiContextListener");
            } else {
                System.err.println("Warning: Gemini API key không được cấu hình trong web.xml. " +
                                 "Chatbot có thể không hoạt động. Vui lòng cập nhật context-param 'gemini_api_key' trong web.xml");
            }
        } catch (Exception e) {
            System.err.println("Error initializing Gemini API key in GeminiContextListener: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để không chặn application startup
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup nếu cần
        System.out.println("GeminiContextListener: Application context destroyed");
    }
}

