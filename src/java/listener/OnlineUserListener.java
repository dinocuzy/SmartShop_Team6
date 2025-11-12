package listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.ServletContext;

/**
 * Session Listener để đếm số người đang online
 * Tăng counter khi session được tạo, giảm khi session bị hủy
 */
@WebListener
public class OnlineUserListener implements HttpSessionListener {
    
    private static final String ONLINE_COUNT_ATTRIBUTE = "onlineUserCount";
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        Integer count = (Integer) context.getAttribute(ONLINE_COUNT_ATTRIBUTE);
        
        if (count == null) {
            count = 0;
        }
        
        count++;
        context.setAttribute(ONLINE_COUNT_ATTRIBUTE, count);
        
        System.out.println("Session created. Online users: " + count);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        Integer count = (Integer) context.getAttribute(ONLINE_COUNT_ATTRIBUTE);
        
        if (count == null) {
            count = 0;
        } else if (count > 0) {
            count--;
        }
        
        context.setAttribute(ONLINE_COUNT_ATTRIBUTE, count);
        
        System.out.println("Session destroyed. Online users: " + count);
    }
    
    /**
     * Lấy số người đang online từ ServletContext
     */
    public static int getOnlineUserCount(ServletContext context) {
        Integer count = (Integer) context.getAttribute(ONLINE_COUNT_ATTRIBUTE);
        return count != null ? count : 0;
    }
}

