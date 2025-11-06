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
import addressservice.IAddressService;
import addressservice.AddressService;
import model.Address;
import orderservice.IOrderService;
import orderservice.OrderService;
import model.Order;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Servlet xử lý trang thông tin cá nhân của Customer
 * URL mapping: /customer/profile
 */
@WebServlet("/customer/profile")
public class CustomerProfileServlet extends HttpServlet {
    
    private IUserService userService;
    private IAddressService addressService;
    private IOrderService orderService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
        addressService = new AddressService();
        orderService = new OrderService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("=== CustomerProfileServlet.doGet called ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + request.getPathInfo());
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        System.out.println("Current User: " + (currentUser != null ? currentUser.getEmail() : "null"));
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            System.out.println("CustomerProfileServlet: Loading user with ID: " + currentUser.getUserID());
            
            // Lấy thông tin user mới nhất từ database
            User user = userService.getUserById(currentUser.getUserID());
            
            if (user == null) {
                System.err.println("CustomerProfileServlet: User not found in database");
                // User không tồn tại trong database, invalidate session và redirect
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            System.out.println("CustomerProfileServlet: User loaded: " + user.getFullName());
            
            // Lấy danh sách địa chỉ của user
            List<Address> addresses = new java.util.ArrayList<>();
            try {
                addresses = addressService.getAddressesByUser(user.getUserID());
                if (addresses == null) {
                    addresses = new java.util.ArrayList<>();
                }
                System.out.println("CustomerProfileServlet: Loaded " + addresses.size() + " addresses");
            } catch (Exception addrException) {
                System.err.println("CustomerProfileServlet: Error loading addresses: " + addrException.getMessage());
                addrException.printStackTrace();
                // Nếu lỗi khi load addresses, set empty list để không crash
                addresses = new java.util.ArrayList<>();
            }
            
            // Lấy danh sách đơn hàng của user
            List<Order> orders = new java.util.ArrayList<>();
            try {
                orders = orderService.getOrdersByUser(user.getUserID());
                if (orders == null) {
                    orders = new java.util.ArrayList<>();
                }
                System.out.println("CustomerProfileServlet: Loaded " + orders.size() + " orders");
            } catch (Exception orderException) {
                System.err.println("CustomerProfileServlet: Error loading orders: " + orderException.getMessage());
                orderException.printStackTrace();
                // Nếu lỗi khi load orders, set empty list để không crash
                orders = new java.util.ArrayList<>();
            }
            
            // Set attributes - đảm bảo không null
            request.setAttribute("user", user);
            request.setAttribute("addresses", addresses);
            request.setAttribute("orders", orders);
            request.setAttribute("currentUser", currentUser);
            
            System.out.println("CustomerProfileServlet: Forwarding to JSP");
            System.out.println("CustomerProfileServlet: User = " + (user != null ? user.getEmail() : "null"));
            System.out.println("CustomerProfileServlet: Addresses count = " + addresses.size());
            
            // Kiểm tra JSP path
            String jspPath = "/views/customer/customerProfile.jsp";
            System.out.println("CustomerProfileServlet: JSP path = " + jspPath);
            
            // Forward đến JSP
            try {
                request.getRequestDispatcher(jspPath).forward(request, response);
                System.out.println("CustomerProfileServlet: Forward completed successfully");
            } catch (Exception forwardEx) {
                System.err.println("CustomerProfileServlet: Forward exception: " + forwardEx.getMessage());
                forwardEx.printStackTrace();
                throw forwardEx; // Re-throw để được catch bởi outer catch block
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Log lỗi chi tiết
            System.err.println("CustomerProfileServlet: Error in doGet: " + e.getMessage());
            e.printStackTrace();
            
            // Không redirect về dashboard, mà vẫn forward đến JSP với error message
            request.setAttribute("errorMessage", "Lỗi khi tải thông tin: " + e.getMessage());
            request.setAttribute("currentUser", currentUser);
            // Set empty lists để tránh JSP errors
            request.setAttribute("addresses", new java.util.ArrayList<>());
            request.setAttribute("orders", new java.util.ArrayList<>());
            
            try {
                request.getRequestDispatcher("/views/customer/customerProfile.jsp").forward(request, response);
            } catch (Exception forwardException) {
                // Nếu forward thất bại hoàn toàn, redirect về dashboard thay vì profile để tránh loop
                System.err.println("CustomerProfileServlet: Forward failed: " + forwardException.getMessage());
                forwardException.printStackTrace();
                // Redirect về dashboard với error message trong session
                session.setAttribute("errorMessage", "Lỗi khi tải trang thông tin cá nhân: " + forwardException.getMessage());
                response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/profile");
            return;
        }
        
        try {
            switch (action) {
                case "update":
                    updateProfile(request, response, currentUser);
                    break;
                case "changePassword":
                    changePassword(request, response, currentUser);
                    break;
                case "addAddress":
                    addAddress(request, response, currentUser);
                    break;
                case "updateAddress":
                    updateAddress(request, response, currentUser);
                    break;
                case "deleteAddress":
                    deleteAddress(request, response, currentUser);
                    break;
                case "setDefaultAddress":
                    setDefaultAddress(request, response, currentUser);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/customer/profile");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Cập nhật thông tin cá nhân
     */
    private void updateProfile(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            
            if (fullName == null || fullName.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Họ tên không được để trống");
                doGet(request, response);
                return;
            }
            
            // Lấy user từ database
            User user = userService.getUserById(currentUser.getUserID());
            user.setFullName(fullName.trim());
            if (phone != null) {
                user.setPhone(phone.trim());
            }
            
            // Cập nhật
            userService.updateUser(user);
            
            // Cập nhật session
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            
            request.setAttribute("successMessage", "Cập nhật thông tin thành công!");
            doGet(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi cập nhật: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Đổi mật khẩu
     */
    private void changePassword(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            if (currentPassword == null || newPassword == null || confirmPassword == null) {
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin");
                doGet(request, response);
                return;
            }
            
            // Kiểm tra mật khẩu hiện tại
            String hashedCurrentPassword = hashPassword(currentPassword);
            User user = userService.getUserById(currentUser.getUserID());
            
            if (!user.getPasswordHash().equals(hashedCurrentPassword) && 
                !user.getPasswordHash().equals(currentPassword)) {
                request.setAttribute("errorMessage", "Mật khẩu hiện tại không đúng");
                doGet(request, response);
                return;
            }
            
            // Kiểm tra mật khẩu mới
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Mật khẩu mới và xác nhận không khớp");
                doGet(request, response);
                return;
            }
            
            if (newPassword.length() < 6) {
                request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự");
                doGet(request, response);
                return;
            }
            
            // Cập nhật mật khẩu
            user.setPasswordHash(hashPassword(newPassword));
            userService.updateUser(user);
            
            request.setAttribute("successMessage", "Đổi mật khẩu thành công!");
            doGet(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi đổi mật khẩu: " + e.getMessage());
            doGet(request, response);
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
    
    /**
     * Thêm địa chỉ mới
     */
    private void addAddress(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            Address address = parseAddressFromRequest(request, currentUser.getUserID());
            addressService.addAddress(address);
            
            request.setAttribute("successMessage", "Thêm địa chỉ thành công!");
            doGet(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi thêm địa chỉ: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Cập nhật địa chỉ
     */
    private void updateAddress(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            String addressIDParam = request.getParameter("addressID");
            
            if (addressIDParam == null || addressIDParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Mã địa chỉ không hợp lệ");
                doGet(request, response);
                return;
            }
            
            int addressID = Integer.parseInt(addressIDParam.trim());
            
            // Kiểm tra địa chỉ có thuộc về user này không
            Address existingAddress = addressService.getAddressById(addressID);
            if (existingAddress == null || existingAddress.getUserID() != currentUser.getUserID()) {
                request.setAttribute("errorMessage", "Không tìm thấy địa chỉ hoặc bạn không có quyền sửa");
                doGet(request, response);
                return;
            }
            
            Address address = parseAddressFromRequest(request, currentUser.getUserID());
            address.setAddressID(addressID);
            
            addressService.updateAddress(address);
            
            request.setAttribute("successMessage", "Cập nhật địa chỉ thành công!");
            doGet(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi cập nhật địa chỉ: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Xóa địa chỉ
     */
    private void deleteAddress(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            String addressIDParam = request.getParameter("addressID");
            
            if (addressIDParam == null || addressIDParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Mã địa chỉ không hợp lệ");
                doGet(request, response);
                return;
            }
            
            int addressID = Integer.parseInt(addressIDParam.trim());
            
            // Kiểm tra địa chỉ có thuộc về user này không
            Address address = addressService.getAddressById(addressID);
            if (address == null || address.getUserID() != currentUser.getUserID()) {
                request.setAttribute("errorMessage", "Không tìm thấy địa chỉ hoặc bạn không có quyền xóa");
                doGet(request, response);
                return;
            }
            
            addressService.deleteAddress(addressID);
            
            request.setAttribute("successMessage", "Xóa địa chỉ thành công!");
            doGet(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi xóa địa chỉ: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Đặt địa chỉ làm mặc định
     */
    private void setDefaultAddress(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            String addressIDParam = request.getParameter("addressID");
            
            if (addressIDParam == null || addressIDParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Mã địa chỉ không hợp lệ");
                doGet(request, response);
                return;
            }
            
            int addressID = Integer.parseInt(addressIDParam.trim());
            
            // Kiểm tra địa chỉ có thuộc về user này không
            Address address = addressService.getAddressById(addressID);
            if (address == null || address.getUserID() != currentUser.getUserID()) {
                request.setAttribute("errorMessage", "Không tìm thấy địa chỉ");
                doGet(request, response);
                return;
            }
            
            // Bỏ mặc định của tất cả địa chỉ khác
            List<Address> addresses = addressService.getAddressesByUser(currentUser.getUserID());
            for (Address addr : addresses) {
                if (addr.isDefault() && addr.getAddressID() != addressID) {
                    addr.setDefault(false);
                    addressService.updateAddress(addr);
                }
            }
            
            // Đặt địa chỉ này làm mặc định
            address.setDefault(true);
            addressService.updateAddress(address);
            
            request.setAttribute("successMessage", "Đặt địa chỉ mặc định thành công!");
            doGet(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Parse địa chỉ từ request parameters
     */
    private Address parseAddressFromRequest(HttpServletRequest request, int userID) {
        Address address = new Address();
        address.setUserID(userID);
        
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String line1 = request.getParameter("line1");
        String line2 = request.getParameter("line2");
        String city = request.getParameter("city");
        String district = request.getParameter("district");
        String ward = request.getParameter("ward");
        String country = request.getParameter("country");
        String postalCode = request.getParameter("postalCode");
        String isDefaultParam = request.getParameter("isDefault");
        
        if (fullName != null) address.setFullName(fullName.trim());
        if (phone != null) address.setPhone(phone.trim());
        if (line1 != null) address.setLine1(line1.trim());
        if (line2 != null) address.setLine2(line2.trim());
        if (city != null) address.setCity(city.trim());
        if (district != null) address.setDistrict(district.trim());
        if (ward != null) address.setWard(ward.trim());
        if (country != null) address.setCountry(country.trim());
        if (postalCode != null) address.setPostalCode(postalCode.trim());
        
        boolean isDefault = "on".equals(isDefaultParam) || "true".equals(isDefaultParam);
        address.setDefault(isDefault);
        
        return address;
    }
}

