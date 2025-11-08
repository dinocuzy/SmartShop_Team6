package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Servlet xử lý đăng ký nhận ưu đãi (Newsletter Subscription)
 * URL mapping: /subscribe (POST)
 */
@WebServlet("/subscribe")
public class SubscribeServlet extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        try {
            super.init();
        } catch (Exception e) {
            System.err.println("Error initializing SubscribeServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String referer = request.getHeader("Referer");
        
        if (email == null || email.trim().isEmpty()) {
            if (referer != null) {
                response.sendRedirect(referer + "?subscribe=error&message=" + 
                    java.net.URLEncoder.encode("Vui lòng nhập email", "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/home?subscribe=error");
            }
            return;
        }
        
        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            if (referer != null) {
                response.sendRedirect(referer + "?subscribe=error&message=" + 
                    java.net.URLEncoder.encode("Email không hợp lệ", "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/home?subscribe=error");
            }
            return;
        }
        
        try {
            // Kiểm tra xem email đã đăng ký chưa
            String checkSql = "SELECT COUNT(*) FROM NewsletterSubscriptions WHERE Email = ?";
            boolean alreadySubscribed = false;
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, email.trim().toLowerCase());
                try (var rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        alreadySubscribed = true;
                    }
                }
            }
            
            if (alreadySubscribed) {
                if (referer != null) {
                    response.sendRedirect(referer + "?subscribe=info&message=" + 
                        java.net.URLEncoder.encode("Email này đã được đăng ký", "UTF-8"));
                } else {
                    response.sendRedirect(request.getContextPath() + "/home?subscribe=info");
                }
                return;
            }
            
            // Thêm email vào database
            String insertSql = "INSERT INTO NewsletterSubscriptions (Email, SubscribedAt, IsActive) VALUES (?, GETDATE(), 1)";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, email.trim().toLowerCase());
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    if (referer != null) {
                        response.sendRedirect(referer + "?subscribe=success&message=" + 
                            java.net.URLEncoder.encode("Đăng ký thành công! Cảm ơn bạn đã đăng ký nhận ưu đãi", "UTF-8"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/home?subscribe=success");
                    }
                } else {
                    if (referer != null) {
                        response.sendRedirect(referer + "?subscribe=error&message=" + 
                            java.net.URLEncoder.encode("Đăng ký thất bại. Vui lòng thử lại", "UTF-8"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/home?subscribe=error");
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error subscribing email: " + e.getMessage());
            e.printStackTrace();
            if (referer != null) {
                response.sendRedirect(referer + "?subscribe=error&message=" + 
                    java.net.URLEncoder.encode("Đã xảy ra lỗi. Vui lòng thử lại sau", "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/home?subscribe=error");
            }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect to home if accessed via GET
        response.sendRedirect(request.getContextPath() + "/home");
    }
}

