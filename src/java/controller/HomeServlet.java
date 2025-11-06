package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import roleservice.IRoleService;
import roleservice.RoleService;

import java.io.IOException;

/**
 * Servlet điều hướng chính dựa trên role của user
 * URL mapping: /home hoặc /
 * Nếu user đã đăng nhập, sẽ redirect đến dashboard phù hợp với role
 * Nếu chưa đăng nhập, redirect đến trang chủ hoặc đăng nhập
 */
@WebServlet({"/home", "/"})
public class HomeServlet extends HttpServlet {
    
    private IRoleService roleService;
    
    @Override
    public void init() throws ServletException {
        try {
            super.init();
            roleService = new RoleService();
        } catch (Exception e) {
            System.err.println("Error initializing HomeServlet: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow servlet to start, errors will be handled in doGet
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            // Chưa đăng nhập - redirect đến trang chủ cửa hàng
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }
        
        // User đã đăng nhập - điều hướng theo role
        String roleName = currentUser.getRoleName();
        
        if (roleName == null || roleName.isEmpty()) {
            // Nếu không có roleName, lấy từ RoleID
            try {
                var role = roleService.getRoleById(currentUser.getRoleID());
                if (role != null) {
                    roleName = role.getRoleName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Điều hướng theo role
        String redirectPath = null;
        
        if (roleName != null) {
            switch (roleName) {
                case "Admin":
                    redirectPath = request.getContextPath() + "/admin/dashboard";
                    break;
                case "Manager":
                    redirectPath = request.getContextPath() + "/manager/dashboard";
                    break;
                case "Staff":
                    redirectPath = request.getContextPath() + "/staff/dashboard";
                    break;
                case "Customer":
                    // Customer đăng nhập vào trang chủ sản phẩm
                    redirectPath = request.getContextPath() + "/index";
                    break;
                default:
                    // Nếu role không xác định, redirect đến trang chủ sản phẩm
                    redirectPath = request.getContextPath() + "/index";
                    break;
            }
        } else {
            // Nếu không có role, mặc định là customer - vào trang chủ sản phẩm
            redirectPath = request.getContextPath() + "/index";
        }
        
        response.sendRedirect(redirectPath);
    }
}

