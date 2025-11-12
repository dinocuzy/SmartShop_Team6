package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.Cart;
import model.CartItem;
import cartservice.ICartService;
import cartservice.CartService;

import java.io.IOException;
import jakarta.servlet.http.Cookie;

/**
 * Servlet xử lý đăng xuất
 * URL mapping: /logout
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    private ICartService cartService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            cartService = new CartService();
        } catch (Exception e) {
            System.err.println("Error initializing LogoutServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // [LƯU CART VÀO DB] Nếu đã đăng nhập, lưu cart vào DB trước khi logout
            User currentUser = (User) session.getAttribute("currentUser");
            Cart cart = (Cart) session.getAttribute("cart");
            
            if (currentUser != null && cart != null && !cart.isEmpty() && cartService != null) {
                try {
                    // Đồng bộ cart từ session vào DB
                    java.util.List<CartItem> cartItems = cart.getItems();
                    cartService.syncCartFromSession(currentUser.getUserID(), cartItems);
                } catch (Exception e) {
                    System.err.println("Error saving cart to DB on logout: " + e.getMessage());
                    e.printStackTrace();
                    // Không throw, vẫn tiếp tục logout
                }
            }
            
            // Xóa tất cả attributes và invalidate session
            session.invalidate();
        }
        
        // Xóa remember me cookies
        Cookie rememberCookie = new Cookie("rememberMe", "");
        rememberCookie.setMaxAge(0);
        rememberCookie.setPath(request.getContextPath() + "/");
        rememberCookie.setHttpOnly(true);
        response.addCookie(rememberCookie);
        
        Cookie emailCookie = new Cookie("rememberEmail", "");
        emailCookie.setMaxAge(0);
        emailCookie.setPath(request.getContextPath() + "/");
        response.addCookie(emailCookie);
        
        // Redirect về trang chủ với thông báo đăng xuất thành công
        response.sendRedirect(request.getContextPath() + "/?logout=success");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}

