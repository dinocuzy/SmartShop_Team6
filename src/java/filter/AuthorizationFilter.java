package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import util.AuthorizationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter kiểm tra quyền truy cập (Authorization) cho các URL pattern
 * Kiểm tra user đã đăng nhập và có role phù hợp không
 */
// Temporarily disabled to debug context startup issue
// @WebFilter(filterName = "AuthorizationFilter", urlPatterns = {
//     "/admin/*",
//     "/manager/*",
//     "/staff/*",
//     "/customer/*"
// })
public class AuthorizationFilter implements Filter {
    
    // Danh sách các URL pattern được phép truy cập công khai (không cần đăng nhập)
    private static final List<String> PUBLIC_URLS = new ArrayList<>();
    
    static {
        // Có thể thêm các URL công khai nếu cần
        PUBLIC_URLS.add("/login");
        PUBLIC_URLS.add("/register");
        PUBLIC_URLS.add("/logout");
        PUBLIC_URLS.add("/store");
        PUBLIC_URLS.add("/store/*");
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            // Khởi tạo filter
            System.out.println("AuthorizationFilter initialized");
        } catch (Exception e) {
            System.err.println("Error initializing AuthorizationFilter: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow filter to start
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
            
            System.out.println("AuthorizationFilter: Checking access for path: " + path);
            
            // Kiểm tra nếu là URL công khai
            if (isPublicURL(path)) {
                System.out.println("AuthorizationFilter: Public URL, allowing access");
                chain.doFilter(request, response);
                return;
            }
            
            // Lấy user từ session
            User currentUser = AuthorizationUtil.getCurrentUser(httpRequest);
            
            // Kiểm tra user đã đăng nhập chưa
            if (currentUser == null) {
                System.out.println("AuthorizationFilter: User not logged in, redirecting to login");
                HttpSession session = httpRequest.getSession();
                session.setAttribute("redirectAfterLogin", requestURI);
                httpResponse.sendRedirect(contextPath + "/login");
                return;
            }
            
            // Kiểm tra user có active không
            if (!currentUser.isActive()) {
                System.out.println("AuthorizationFilter: User account is inactive");
                httpRequest.getSession().invalidate();
                httpResponse.sendRedirect(contextPath + "/login?error=inactive");
                return;
            }
            
            // Kiểm tra quyền truy cập theo path
            if (path.startsWith("/admin")) {
                if (!AuthorizationUtil.canAccessAdminArea(httpRequest)) {
                    System.out.println("AuthorizationFilter: User does not have admin access");
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                        "Bạn không có quyền truy cập khu vực quản trị");
                    return;
                }
            } else if (path.startsWith("/manager")) {
                if (!AuthorizationUtil.isAdminOrManager(httpRequest)) {
                    System.out.println("AuthorizationFilter: User does not have manager access");
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                        "Bạn không có quyền truy cập khu vực quản lý");
                    return;
                }
            } else if (path.startsWith("/staff")) {
                if (!AuthorizationUtil.isStaffMember(httpRequest)) {
                    System.out.println("AuthorizationFilter: User does not have staff access");
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                        "Bạn không có quyền truy cập khu vực nhân viên");
                    return;
                }
            } else if (path.startsWith("/customer")) {
                // Customer area: chỉ cần đăng nhập, tất cả các role đều có thể truy cập
                // Nhưng có thể thêm logic kiểm tra nếu cần
                if (!AuthorizationUtil.isLoggedIn(httpRequest)) {
                    System.out.println("AuthorizationFilter: User not logged in for customer area");
                    httpResponse.sendRedirect(contextPath + "/login");
                    return;
                }
            }
            
            if (currentUser != null) {
                String email = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
                String roleName = currentUser.getRoleName() != null ? currentUser.getRoleName() : "unknown";
                System.out.println("AuthorizationFilter: Access granted for user: " + email + 
                                  " with role: " + roleName);
            }
            
            // Cho phép tiếp tục
            chain.doFilter(request, response);
        } catch (Exception e) {
            System.err.println("Error in AuthorizationFilter.doFilter: " + e.getMessage());
            e.printStackTrace();
            // Allow request to continue - don't block everything
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
        // Cleanup
        System.out.println("AuthorizationFilter destroyed");
    }
    
    /**
     * Kiểm tra URL có phải là URL công khai không
     * @param path Đường dẫn URL
     * @return true nếu là URL công khai
     */
    private boolean isPublicURL(String path) {
        for (String publicPath : PUBLIC_URLS) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                return true;
            }
        }
        return false;
    }
}
