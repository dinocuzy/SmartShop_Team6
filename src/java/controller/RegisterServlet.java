package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import userservice.IUserService;
import userservice.UserService;
import roleservice.IRoleService;
import roleservice.RoleService;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Servlet xử lý đăng ký tài khoản
 * URL mapping: /register (GET: hiển thị form, POST: xử lý đăng ký)
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private IUserService userService;
    private IRoleService roleService;
    
    @Override
    public void init() throws ServletException {
        try {
            super.init();
            userService = new UserService();
            roleService = new RoleService();
        } catch (Exception e) {
            System.err.println("Error initializing RegisterServlet: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow servlet to start, errors will be handled in doGet/doPost
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra nếu đã đăng nhập, redirect về trang chủ
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        // Chưa đăng nhập, hiển thị form đăng ký
        request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        
        // Validate input
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập họ tên");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập email");
            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        if (password.length() < 6) {
            request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            return;
        }
        
        try {
            // Kiểm tra email đã tồn tại chưa
            User existingUser = userService.getUserByEmail(email.trim());
            if (existingUser != null) {
                request.setAttribute("errorMessage", "Email này đã được sử dụng. Vui lòng chọn email khác");
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", email);
                request.setAttribute("phone", phone);
                request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
                return;
            }
            
            // Lấy RoleID cho Customer (RoleID = 4)
            var customerRole = roleService.getRoleByName("Customer");
            int customerRoleID = 4; // Default Customer RoleID
            if (customerRole != null) {
                customerRoleID = customerRole.getRoleID();
            }
            
            // Tạo user mới
            User newUser = new User();
            newUser.setFullName(fullName.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPasswordHash(hashPassword(password.trim()));
            newUser.setPhone(phone != null ? phone.trim() : null);
            newUser.setRoleID(customerRoleID);
            newUser.setActive(true);
            
            // Thêm user vào database
            int userID = userService.addUser(newUser);
            
            if (userID > 0) {
                // Lấy roleName cho user
                if (customerRole != null) {
                    newUser.setRoleName(customerRole.getRoleName());
                } else {
                    newUser.setRoleName("Customer");
                }
                newUser.setUserID(userID);
                
                // Đăng nhập tự động sau khi đăng ký
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", newUser);
                
                // Redirect về trang chủ với thông báo thành công
                response.sendRedirect(request.getContextPath() + "/home?register=success");
            } else {
                request.setAttribute("errorMessage", "Đăng ký thất bại. Vui lòng thử lại");
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", email);
                request.setAttribute("phone", phone);
                request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/auth/register.jsp").forward(request, response);
        }
    }
    
    /**
     * Hash password bằng SHA-256
     * Lưu ý: Trong production nên dùng BCrypt hoặc Argon2
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
            return password; // Fallback: return plain password
        }
    }
}

