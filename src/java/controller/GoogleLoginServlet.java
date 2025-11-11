package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Servlet xử lý đăng nhập bằng Google OAuth
 * URL mapping: /auth/google/login
 */
@WebServlet("/auth/google/login")
public class GoogleLoginServlet extends HttpServlet {
    
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String SCOPE = "openid email profile";
    private static final String RESPONSE_TYPE = "code";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy Google OAuth config từ web.xml
        String clientId = getServletContext().getInitParameter("google_client_id");
        String redirectUri = getServletContext().getInitParameter("google_redirect_uri");
        
        if (clientId == null || clientId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Google OAuth chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        if (redirectUri == null || redirectUri.trim().isEmpty()) {
            // Tự động tạo redirect URI từ request
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            redirectUri = scheme + "://" + serverName + (serverPort != 80 && serverPort != 443 ? ":" + serverPort : "") 
                        + contextPath + "/auth/google/callback";
        }
        
        // Tạo state để bảo mật (CSRF protection)
        String state = generateState();
        HttpSession session = request.getSession();
        session.setAttribute("oauth_state", state);
        session.setAttribute("oauth_redirect_after_login", request.getParameter("redirect"));
        
        // Tạo authorization URL
        String authUrl = GOOGLE_AUTH_URL +
                "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&response_type=" + RESPONSE_TYPE +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8) +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8) +
                "&access_type=offline" +
                "&prompt=consent";
        
        // Redirect đến Google
        response.sendRedirect(authUrl);
    }
    
    /**
     * Tạo state token ngẫu nhiên để bảo vệ khỏi CSRF attacks
     */
    private String generateState() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

