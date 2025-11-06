package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import userservice.IUserService;
import userservice.UserService;
import model.User;
import util.TokenUtil;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Servlet xử lý reset password từ token
 * URL mapping: /reset-password
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    
    private IUserService userService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        
        if (token == null || token.isEmpty()) {
            request.setAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn");
            request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
            return;
        }
        
        // Xác thực token
        Map<String, Object> tokenData = ForgotPasswordServlet.validateResetToken(token);
        
        if (tokenData == null) {
            request.setAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn");
            request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
            return;
        }
        
        // Lưu token vào request để sử dụng trong POST
        request.setAttribute("token", token);
        request.setAttribute("email", tokenData.get("email"));
        
        request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        if (token == null || token.isEmpty()) {
            request.setAttribute("errorMessage", "Token không hợp lệ");
            request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu mới");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
            return;
        }
        
        if (password.length() < 6) {
            request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
            return;
        }
        
        try {
            // Xác thực token
            Map<String, Object> tokenData = ForgotPasswordServlet.validateResetToken(token);
            
            if (tokenData == null) {
                request.setAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn");
                request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
                return;
            }
            
            // Lấy user
            int userID = (Integer) tokenData.get("userID");
            User user = userService.getUserById(userID);
            
            if (user == null) {
                request.setAttribute("errorMessage", "Không tìm thấy người dùng");
                request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
                return;
            }
            
            // Hash password mới
            String hashedPassword = hashPassword(password);
            
            // Cập nhật mật khẩu
            user.setPasswordHash(hashedPassword);
            userService.updateUser(user);
            
            // Xóa token sau khi đã sử dụng
            ForgotPasswordServlet.removeToken(token);
            
            // Redirect đến trang đăng nhập với thông báo thành công
            request.getSession().setAttribute("successMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/resetPassword.jsp").forward(request, response);
        }
    }
    
    /**
     * Hash password bằng SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
}

