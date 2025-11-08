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
import cartservice.ICartService;
import cartservice.CartService;
import model.Cart;
import model.CartItem;
import model.Product;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Servlet xử lý đăng nhập và đăng xuất
 * URL mapping: /login (GET: hiển thị form, POST: xử lý đăng nhập)
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private IUserService userService;
    private IRoleService roleService;
    private ICartService cartService;
    
    @Override
    public void init() throws ServletException {
        try {
            super.init();
            userService = new UserService();
            roleService = new RoleService();
            cartService = new CartService();
        } catch (Exception e) {
            System.err.println("Error initializing LoginServlet: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow servlet to start, errors will be handled in doGet/doPost
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra nếu đã đăng nhập, redirect về dashboard
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser != null) {
            // Đã đăng nhập, redirect về dashboard phù hợp theo role
            String roleName = currentUser.getRoleName();
            String redirectPath = request.getContextPath() + "/home"; // Default: trang chủ sản phẩm
            
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
                        redirectPath = request.getContextPath() + "/home";
                        break;
                    default:
                        redirectPath = request.getContextPath() + "/home";
                        break;
                }
            }
            
            response.sendRedirect(redirectPath);
            return;
        }
        
        // Chưa đăng nhập, hiển thị form đăng nhập
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập email");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Tìm user theo email
            User user = userService.getUserByEmail(email.trim());
            
            if (user == null) {
                request.setAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra user có active không
            if (!user.isActive()) {
                request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Xác thực mật khẩu
            // Kiểm tra cả hai trường hợp: DB lưu hash hoặc plain text
            boolean passwordValid = false;
            String dbPassword = user.getPasswordHash();
            String inputPassword = password.trim();
            
            // Debug logging
            System.out.println("=== LOGIN DEBUG ===");
            System.out.println("Email: " + email);
            System.out.println("DB Password: " + (dbPassword != null ? dbPassword : "NULL"));
            System.out.println("DB Password length: " + (dbPassword != null ? dbPassword.length() : 0));
            System.out.println("Input password: " + inputPassword);
            
            if (dbPassword != null && !dbPassword.trim().isEmpty()) {
                String dbPasswordTrimmed = dbPassword.trim();
                
                // 1. Thử so sánh plain text trước (phổ biến nhất)
                if (dbPasswordTrimmed.equals(inputPassword)) {
                    passwordValid = true;
                    System.out.println("Password match: PLAIN TEXT");
                } else {
                    // 2. Hash password từ input và so sánh
                    String hashedPassword = hashPassword(inputPassword);
                    System.out.println("Hashed password: " + hashedPassword);
                    
                    // Thử so sánh với hash (SHA-256 hash = 64 ký tự hex)
                    if (dbPasswordTrimmed.length() == 64 && dbPasswordTrimmed.matches("^[0-9a-fA-F]{64}$")) {
                        // DB lưu hash, so sánh hash với hash
                        if (dbPasswordTrimmed.equalsIgnoreCase(hashedPassword)) {
                            passwordValid = true;
                            System.out.println("Password match: HASH");
                        }
                    } else {
                        // DB có thể lưu hash format khác, thử so sánh case-insensitive
                        if (dbPasswordTrimmed.equalsIgnoreCase(hashedPassword)) {
                            passwordValid = true;
                            System.out.println("Password match: HASH (case-insensitive)");
                        }
                    }
                }
            }
            
            System.out.println("Password valid: " + passwordValid);
            System.out.println("=== END DEBUG ===");
            
            if (!passwordValid) {
                System.err.println("Login FAILED for email: " + email);
                request.setAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Lấy roleName nếu chưa có
            if (user.getRoleName() == null || user.getRoleName().isEmpty()) {
                var role = roleService.getRoleById(user.getRoleID());
                if (role != null) {
                    user.setRoleName(role.getRoleName());
                }
            }
            
            // Đăng nhập thành công - lưu vào session
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            
            // [ĐỒNG BỘ CART] Đồng bộ giỏ hàng từ session vào DB
            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.isEmpty() && cartService != null) {
                try {
                    // Đồng bộ cart từ session vào DB (gộp số lượng nếu trùng)
                    java.util.List<CartItem> sessionCartItems = sessionCart.getItems();
                    cartService.syncCartFromSession(user.getUserID(), sessionCartItems);
                    
                    // Load cart từ DB và cập nhật lại session cart
                    java.util.List<model.CartItemDB> dbCartItems = cartService.getCartItemsByUser(user.getUserID());
                    Cart mergedCart = new Cart();
                    
                    for (model.CartItemDB dbItem : dbCartItems) {
                        Product product = dbItem.getProduct();
                        if (product != null) {
                            CartItem cartItem = new CartItem();
                            cartItem.setProductID(product.getProductID());
                            cartItem.setProductName(product.getProductName());
                            cartItem.setImageUrl(product.getImageUrl());
                            cartItem.setPrice(product.getPrice());
                            cartItem.setQuantity(dbItem.getQuantity());
                            cartItem.setStock(product.getStock());
                            cartItem.setStockStatus(product.getStockStatus());
                            mergedCart.addItem(cartItem);
                        }
                    }
                    
                    // Cập nhật session cart với cart đã merge
                    session.setAttribute("cart", mergedCart);
                } catch (Exception e) {
                    System.err.println("Error syncing cart on login: " + e.getMessage());
                    e.printStackTrace();
                    // Không throw, tiếp tục login
                }
            } else if (cartService != null) {
                // Nếu session cart rỗng, load từ DB
                try {
                    java.util.List<model.CartItemDB> dbCartItems = cartService.getCartItemsByUser(user.getUserID());
                    Cart dbCart = new Cart();
                    
                    for (model.CartItemDB dbItem : dbCartItems) {
                        Product product = dbItem.getProduct();
                        if (product != null) {
                            CartItem cartItem = new CartItem();
                            cartItem.setProductID(product.getProductID());
                            cartItem.setProductName(product.getProductName());
                            cartItem.setImageUrl(product.getImageUrl());
                            cartItem.setPrice(product.getPrice());
                            cartItem.setQuantity(dbItem.getQuantity());
                            cartItem.setStock(product.getStock());
                            cartItem.setStockStatus(product.getStockStatus());
                            dbCart.addItem(cartItem);
                        }
                    }
                    
                    session.setAttribute("cart", dbCart);
                } catch (Exception e) {
                    System.err.println("Error loading cart from DB on login: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Kiểm tra "Remember Me"
            String rememberMe = request.getParameter("rememberMe");
            if (rememberMe != null && rememberMe.equals("on")) {
                // Nếu chọn Remember Me, set session timeout 7 ngày
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 ngày
            } else {
                // Nếu không chọn, session timeout 30 phút
                session.setMaxInactiveInterval(30 * 60); // 30 phút
            }
            
            // Redirect về dashboard phù hợp theo role
            String roleName = user.getRoleName();
            String redirectPath = request.getContextPath() + "/home"; // Default: trang chủ sản phẩm
            
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
                        redirectPath = request.getContextPath() + "/home";
                        break;
                    default:
                        redirectPath = request.getContextPath() + "/home";
                        break;
                }
            }
            
            response.sendRedirect(redirectPath);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
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

