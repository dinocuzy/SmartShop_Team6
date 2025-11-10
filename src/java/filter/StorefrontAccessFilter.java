package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import util.AuthorizationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter chặn Admin, Manager, Staff truy cập vào các trang storefront (shop)
 * Chỉ cho phép Customer truy cập storefront
 * Admin/Manager/Staff chỉ được phép truy cập các trang quản lý
 */
public class StorefrontAccessFilter implements Filter {
    
    // Danh sách các URL pattern của storefront cần chặn
    private static final List<String> STOREFRONT_URLS = new ArrayList<>();
    
    // Danh sách các URL được phép cho tất cả (bao gồm Admin/Manager/Staff)
    private static final List<String> ALLOWED_URLS = new ArrayList<>();
    
    static {
        // Các URL storefront cần chặn
        STOREFRONT_URLS.add("/home");
        STOREFRONT_URLS.add("/shop");
        STOREFRONT_URLS.add("/product");
        STOREFRONT_URLS.add("/cart");
        STOREFRONT_URLS.add("/checkout");
        STOREFRONT_URLS.add("/wishlist");
        STOREFRONT_URLS.add("/compare");
        STOREFRONT_URLS.add("/contact");
        STOREFRONT_URLS.add("/news");
        STOREFRONT_URLS.add("/faq");
        STOREFRONT_URLS.add("/stores");
        STOREFRONT_URLS.add("/policy");
        STOREFRONT_URLS.add("/subscribe");
        
        // Các URL được phép cho tất cả (không chặn)
        ALLOWED_URLS.add("/login");
        ALLOWED_URLS.add("/logout");
        ALLOWED_URLS.add("/register");
        ALLOWED_URLS.add("/forgot-password");
        ALLOWED_URLS.add("/reset-password");
        ALLOWED_URLS.add("/admin");
        ALLOWED_URLS.add("/manager");
        ALLOWED_URLS.add("/staff");
        ALLOWED_URLS.add("/customer");
        ALLOWED_URLS.add("/api"); // API endpoints (như /api/social-share)
        ALLOWED_URLS.add("/support-request"); // Support request được phép
        ALLOWED_URLS.add("/vnpay-callback"); // VNPay callback được phép (không phải truy cập storefront)
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            System.out.println("StorefrontAccessFilter initialized - Blocking Admin/Manager/Staff from storefront");
        } catch (Exception e) {
            System.err.println("Error initializing StorefrontAccessFilter: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            String requestURI = httpRequest.getRequestURI();
            String contextPath = httpRequest.getContextPath();
            String path = requestURI.substring(contextPath.length());
            
            // Nếu là root path "/", kiểm tra xem có phải là storefront không
            if ("/".equals(path)) {
                path = "/home"; // Xem như truy cập /home
            }
            
            // Kiểm tra nếu là URL được phép
            if (isAllowedURL(path)) {
                chain.doFilter(request, response);
                return;
            }
            
            // Kiểm tra nếu là URL storefront
            if (!isStorefrontURL(path)) {
                // Không phải storefront URL, cho phép truy cập
                chain.doFilter(request, response);
                return;
            }
            
            // Lấy user từ session
            HttpSession session = httpRequest.getSession(false);
            User currentUser = null;
            
            if (session != null) {
                currentUser = (User) session.getAttribute("currentUser");
            }
            
            // Nếu chưa đăng nhập, cho phép truy cập storefront (public access)
            if (currentUser == null) {
                chain.doFilter(request, response);
                return;
            }
            
            // Kiểm tra role của user
            String roleName = currentUser.getRoleName();
            int roleID = currentUser.getRoleID();
            
            // Chặn Admin, Manager, Staff truy cập storefront
            boolean isBlockedRole = false;
            String redirectPath = null;
            
            if ("Admin".equalsIgnoreCase(roleName) || roleID == 1) {
                isBlockedRole = true;
                redirectPath = contextPath + "/admin/dashboard";
            } else if ("Manager".equalsIgnoreCase(roleName) || roleID == 2) {
                isBlockedRole = true;
                redirectPath = contextPath + "/manager/dashboard";
            } else if ("Staff".equalsIgnoreCase(roleName) || roleID == 3) {
                isBlockedRole = true;
                redirectPath = contextPath + "/staff/dashboard";
            }
            
            if (isBlockedRole) {
                System.out.println("StorefrontAccessFilter: Blocked " + roleName + 
                                 " (ID: " + roleID + ") from accessing storefront: " + path);
                System.out.println("StorefrontAccessFilter: Redirecting to: " + redirectPath);
                
                // Lưu thông báo vào session
                session.setAttribute("blockedMessage", 
                    "Tài khoản " + roleName + " không được phép truy cập cửa hàng. Vui lòng sử dụng trang quản lý.");
                
                httpResponse.sendRedirect(redirectPath);
                return;
            }
            
            // Customer hoặc role khác, cho phép truy cập storefront
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in StorefrontAccessFilter.doFilter: " + e.getMessage());
            e.printStackTrace();
            // Cho phép request tiếp tục nếu có lỗi
            try {
                chain.doFilter(request, response);
            } catch (Exception ex) {
                System.err.println("Error in chain.doFilter: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("StorefrontAccessFilter destroyed");
    }
    
    /**
     * Kiểm tra URL có phải là storefront URL không
     * @param path Đường dẫn URL
     * @return true nếu là storefront URL
     */
    private boolean isStorefrontURL(String path) {
        for (String storefrontPath : STOREFRONT_URLS) {
            if (path.equals(storefrontPath) || path.startsWith(storefrontPath + "/")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra URL có được phép truy cập không (không chặn)
     * @param path Đường dẫn URL
     * @return true nếu được phép
     */
    private boolean isAllowedURL(String path) {
        // Kiểm tra các URL được phép
        for (String allowedPath : ALLOWED_URLS) {
            if (path.equals(allowedPath) || path.startsWith(allowedPath + "/")) {
                return true;
            }
        }
        return false;
    }
}

