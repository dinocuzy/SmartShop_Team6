package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import userservice.IUserService;
import userservice.UserService;
import model.User;
import util.EmailUtil;
import util.TokenUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servlet xử lý quên mật khẩu
 * URL mapping: /forgot-password
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    
    private IUserService userService;
    
    // Lưu trữ token reset password (trong production nên lưu vào database)
    // Format: token -> {email, expiryTime}
    private static final Map<String, Map<String, Object>> resetTokens = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRY = 24 * 60 * 60 * 1000; // 24 giờ
    
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Hiển thị form quên mật khẩu
        request.getRequestDispatcher("/views/auth/forgotPassword.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập email");
            request.getRequestDispatcher("/views/auth/forgotPassword.jsp").forward(request, response);
            return;
        }
        
        try {
            // Tìm user theo email
            User user = userService.getUserByEmail(email.trim());
            
            if (user == null) {
                // Không hiển thị lỗi chi tiết để tránh email enumeration attack
                request.setAttribute("successMessage", "Nếu email tồn tại, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu.");
                request.getRequestDispatcher("/views/auth/forgotPassword.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra user có active không
            if (!user.isActive()) {
                request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa");
                request.getRequestDispatcher("/views/auth/forgotPassword.jsp").forward(request, response);
                return;
            }
            
            // Tạo reset token
            String resetToken = TokenUtil.generateToken("reset_");
            long expiryTime = System.currentTimeMillis() + TOKEN_EXPIRY;
            
            // Lưu token vào memory (trong production nên lưu vào database)
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("email", user.getEmail());
            tokenData.put("userID", user.getUserID());
            tokenData.put("expiryTime", expiryTime);
            resetTokens.put(resetToken, tokenData);
            
            // Tạo reset link
            String resetLink = request.getScheme() + "://" + request.getServerName() + 
                              (request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "") +
                              request.getContextPath() + "/reset-password?token=" + resetToken;
            
            // Gửi email
            boolean emailSent = EmailUtil.sendPasswordResetEmail(user.getEmail(), resetToken, resetLink);
            
            if (emailSent) {
                request.setAttribute("successMessage", 
                    "Chúng tôi đã gửi email hướng dẫn đặt lại mật khẩu đến: " + email + 
                    "<br><small class='text-muted'>Vui lòng kiểm tra hộp thư (bao gồm thư mục spam).</small>");
            } else {
                // Nếu không gửi được email, vẫn hiển thị token cho mục đích test
                request.setAttribute("successMessage", 
                    "Email đã được xử lý. <br><small class='text-muted'>" +
                    "Lưu ý: Chức năng gửi email chưa được cấu hình đúng. " +
                    "Token reset: <code>" + resetToken + "</code><br>" +
                    "Link reset: <a href='" + resetLink + "'>" + resetLink + "</a></small>");
            }
            
            request.getRequestDispatcher("/views/auth/forgotPassword.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("/views/auth/forgotPassword.jsp").forward(request, response);
        }
    }
    
    /**
     * Xác thực reset token
     * @param token Token cần kiểm tra
     * @return Map chứa thông tin token (email, userID) hoặc null nếu token không hợp lệ
     */
    public static Map<String, Object> validateResetToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        Map<String, Object> tokenData = resetTokens.get(token);
        
        if (tokenData == null) {
            return null;
        }
        
        // Kiểm tra token đã hết hạn chưa
        long expiryTime = (Long) tokenData.get("expiryTime");
        if (System.currentTimeMillis() > expiryTime) {
            resetTokens.remove(token); // Xóa token đã hết hạn
            return null;
        }
        
        return tokenData;
    }
    
    /**
     * Xóa token sau khi đã sử dụng
     * @param token Token cần xóa
     */
    public static void removeToken(String token) {
        resetTokens.remove(token);
    }
}

