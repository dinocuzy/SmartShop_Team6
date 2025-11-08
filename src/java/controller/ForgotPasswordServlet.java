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
        try {
            userService = new UserService();
        } catch (Exception e) {
            System.err.println("Error initializing ForgotPasswordServlet: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để tránh context startup failure
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Hiển thị form quên mật khẩu
        forwardToView(request, response, "/views/auth/forgotPassword.jsp");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String viewPath = "/views/auth/forgotPassword.jsp";
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập email");
            forwardToView(request, response, viewPath);
            return;
        }
        
        email = email.trim();
        
        // Validate email format cơ bản
        if (!email.contains("@") || email.length() < 5) {
            request.setAttribute("errorMessage", "Vui lòng nhập email hợp lệ");
            forwardToView(request, response, viewPath);
            return;
        }
        
        // Kiểm tra userService đã được khởi tạo chưa
        if (userService == null) {
            System.err.println("UserService is null. Initializing...");
            try {
                userService = new UserService();
            } catch (Exception e) {
                System.err.println("Error creating UserService: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
                forwardToView(request, response, viewPath);
                return;
            }
        }
        
        try {
            // Tìm user theo email - sử dụng try-catch để xử lý mọi exception
            User user = null;
            
            try {
                // Gọi getUserByEmail - method này có thể throw IllegalArgumentException nếu email null/empty
                // Nhưng chúng ta đã validate và trim rồi, nên sẽ không throw exception này
                user = userService.getUserByEmail(email);
            } catch (IllegalArgumentException iae) {
                // Email null hoặc empty (không nên xảy ra vì đã validate)
                System.err.println("Invalid email format (should not happen): " + email + " - " + iae.getMessage());
                request.setAttribute("errorMessage", "Email không hợp lệ");
                forwardToView(request, response, viewPath);
                return;
            } catch (Exception userException) {
                // Lỗi database hoặc lỗi khác
                System.err.println("Error getting user by email from database: " + userException.getMessage());
                userException.printStackTrace();
                // Không hiển thị lỗi chi tiết để tránh email enumeration attack
                // Nhưng cần log để debug
                request.setAttribute("successMessage", "Nếu email tồn tại, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu.");
                forwardToView(request, response, viewPath);
                return;
            }
            
            // Kiểm tra user có tồn tại không
            if (user == null) {
                // User không tồn tại - không hiển thị lỗi chi tiết để tránh email enumeration attack
                request.setAttribute("successMessage", "Nếu email tồn tại, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu.");
                forwardToView(request, response, viewPath);
                return;
            }
            
            // Kiểm tra user có active không
            if (!user.isActive()) {
                request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa");
                forwardToView(request, response, viewPath);
                return;
            }
            
            // Tạo reset token sử dụng TokenUtil
            String resetToken = null;
            try {
                resetToken = TokenUtil.generateToken("reset_");
            } catch (Exception tokenException) {
                System.err.println("Error generating token: " + tokenException.getMessage());
                tokenException.printStackTrace();
                request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tạo token. Vui lòng thử lại sau.");
                forwardToView(request, response, viewPath);
                return;
            }
            
            if (resetToken == null || resetToken.isEmpty()) {
                System.err.println("Generated token is null or empty");
                request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tạo token. Vui lòng thử lại sau.");
                forwardToView(request, response, viewPath);
                return;
            }
            
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
            
            // Gửi email sử dụng EmailUtil
            boolean emailSent = false;
            try {
                emailSent = EmailUtil.sendPasswordResetEmail(user.getEmail(), resetToken, resetLink);
                if (emailSent) {
                    System.out.println("Password reset email sent successfully to: " + user.getEmail());
                } else {
                    System.err.println("Failed to send password reset email to: " + user.getEmail());
                }
            } catch (Exception emailException) {
                System.err.println("Error sending password reset email: " + emailException.getMessage());
                emailException.printStackTrace();
                // Không throw exception, tiếp tục xử lý và hiển thị token cho user
                emailSent = false;
            }
            
            // Tạo success message đơn giản, không có HTML phức tạp
            String successMsg;
            if (emailSent) {
                successMsg = "Chúng tôi đã gửi email hướng dẫn đặt lại mật khẩu đến: " + email + 
                             ". Vui lòng kiểm tra hộp thư (bao gồm thư mục spam).";
            } else {
                // Nếu không gửi được email, vẫn hiển thị token cho mục đích test (chỉ trong development)
                successMsg = "Email đã được xử lý. Token reset: " + resetToken + 
                             ". Link reset: " + resetLink;
            }
            
            request.setAttribute("successMessage", successMsg);
            
            // Đảm bảo response chưa commit trước khi forward
            if (!response.isCommitted()) {
                forwardToView(request, response, viewPath);
            } else {
                System.err.println("Response already committed, cannot forward to view");
                // Redirect thay vì forward nếu response đã commit
                response.sendRedirect(request.getContextPath() + "/forgot-password?success=true");
            }
            
        } catch (Throwable e) {
            // Catch Throwable thay vì Exception để bắt mọi loại error
            System.err.println("Unexpected error in ForgotPasswordServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            
            // Log stack trace đầy đủ
            System.err.println("Stack trace:");
            for (StackTraceElement element : e.getStackTrace()) {
                System.err.println("  at " + element.toString());
            }
            
            // Kiểm tra response đã commit chưa
            if (!response.isCommitted()) {
                try {
                    request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage() + ". Vui lòng thử lại sau.");
                    forwardToView(request, response, viewPath);
                } catch (Exception forwardException) {
                    System.err.println("Error in forwardToView during error handling: " + forwardException.getMessage());
                    forwardException.printStackTrace();
                    try {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Đã xảy ra lỗi. Vui lòng thử lại sau.");
                    } catch (IOException ioException) {
                        System.err.println("Error sending error response: " + ioException.getMessage());
                        ioException.printStackTrace();
                    }
                }
            } else {
                System.err.println("Cannot handle error, response already committed");
            }
        }
    }
    
    /**
     * Helper method để forward đến view với error handling
     */
    private void forwardToView(HttpServletRequest request, HttpServletResponse response, String viewPath) 
            throws ServletException, IOException {
        
        // Kiểm tra xem response đã được commit chưa
        if (response.isCommitted()) {
            System.err.println("Response already committed, cannot forward to: " + viewPath);
            return;
        }
        
        try {
            request.getRequestDispatcher(viewPath).forward(request, response);
        } catch (ServletException | IOException e) {
            System.err.println("Error forwarding to " + viewPath + ": " + e.getMessage());
            e.printStackTrace();
            
            // Nếu response chưa commit, thử gửi error response
            if (!response.isCommitted()) {
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Đã xảy ra lỗi khi tải trang. Vui lòng thử lại sau.");
                } catch (IOException ioException) {
                    System.err.println("Error sending error response: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            } else {
                System.err.println("Cannot send error response, response already committed");
            }
        } catch (Exception e) {
            System.err.println("Unexpected error in forwardToView: " + e.getMessage());
            e.printStackTrace();
            if (!response.isCommitted()) {
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Đã xảy ra lỗi khi tải trang. Vui lòng thử lại sau.");
                } catch (IOException ioException) {
                    System.err.println("Error sending error response: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
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

