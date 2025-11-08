package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.Role;
import model.Address;
import userservice.IUserService;
import userservice.UserService;
import roleservice.IRoleService;
import roleservice.RoleService;
import addressservice.IAddressService;
import addressservice.AddressService;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Servlet xử lý các request CRUD cho User
 * URL mapping: /admin/users
 * Actions: list, add, edit, delete, save
 */
@WebServlet("/admin/users")
public class UserServlet extends HttpServlet {
    
    private IUserService userService;
    private IRoleService roleService;
    private IAddressService addressService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            userService = new UserService();
            roleService = new RoleService();
            addressService = new AddressService();
        } catch (Exception e) {
            System.err.println("Error initializing UserServlet: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để tránh context startup failure
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "list":
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (Exception e) {
            // Catch TẤT CẢ exceptions (bao gồm cả ServletException và IOException)
            // Không re-throw để tránh Tomcat redirect về error page
            e.printStackTrace();
            System.err.println("UserServlet.doGet error: " + e.getMessage());
            
            // Luôn cố gắng forward về userList.jsp với error message
            try {
                request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
                // Load dữ liệu tối thiểu để hiển thị trang list
                request.setAttribute("users", new java.util.ArrayList<>());
                request.setAttribute("roles", new java.util.ArrayList<>());
                request.setAttribute("userAddressesMap", new HashMap<>());
                request.setAttribute("currentPage", 1);
                request.setAttribute("totalPages", 1);
                request.setAttribute("totalUsers", 0);
                request.getRequestDispatcher("/views/admin/userList.jsp").forward(request, response);
            } catch (Exception ex) {
                // Nếu không thể forward, log và in error ra response
                System.err.println("Critical error in UserServlet - cannot forward: " + ex.getMessage());
                ex.printStackTrace();
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1>Lỗi hệ thống</h1>");
                response.getWriter().println("<p>" + e.getMessage() + "</p>");
                response.getWriter().println("<a href='" + request.getContextPath() + "/admin/users'>Quay lại</a>");
                response.getWriter().println("</body></html>");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "save":
                    saveUser(request, response);
                    break;
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String userID = request.getParameter("userID");
            if (userID != null && !userID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    /**
     * Hiển thị danh sách users với phân trang, tìm kiếm, sắp xếp, lọc
     */
    private void listUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy các tham số từ request và xử lý
        String pageParam = request.getParameter("page");
        String searchKeyword = request.getParameter("search");
        String roleParam = request.getParameter("roleID");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        String showAllParam = request.getParameter("showAll");
        
        // Parse và validate các tham số
        int pageNumber = 1;
        int pageSize = 10;
        int roleID = 0;
        boolean includeInactive = "true".equalsIgnoreCase(showAllParam);
        
        // Parse page number
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                pageNumber = Integer.parseInt(pageParam.trim());
                if (pageNumber < 1) pageNumber = 1;
            } catch (NumberFormatException e) {
                pageNumber = 1;
            }
        }
        
        // Parse role ID
        if (roleParam != null && !roleParam.trim().isEmpty()) {
            try {
                roleID = Integer.parseInt(roleParam.trim());
                if (roleID < 0) roleID = 0;
            } catch (NumberFormatException e) {
                roleID = 0;
            }
        }
        
        // Set default sort values
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "UserID";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "ASC";
        }
        
        // Trim search keyword
        if (searchKeyword != null) {
            searchKeyword = searchKeyword.trim();
            if (searchKeyword.isEmpty()) {
                searchKeyword = null;
            }
        }
        
        // Sử dụng JOIN query để lấy users kèm địa chỉ mặc định trong 1 query (tối ưu hơn)
        Map<User, Address> userAddressMap = new HashMap<>();
        List<User> users = new java.util.ArrayList<>();
        
        try {
            userAddressMap = userService.getPagedUsersWithDefaultAddress(
                pageNumber, pageSize, sortBy, sortOrder, searchKeyword, roleID, includeInactive
            );
            if (userAddressMap != null) {
                users = new java.util.ArrayList<>(userAddressMap.keySet());
            }
        } catch (Exception e) {
            System.err.println("Error loading users with addresses: " + e.getMessage());
            e.printStackTrace();
            userAddressMap = new HashMap<>();
            users = new java.util.ArrayList<>();
        }
        
        // Convert Map<User, Address> thành Map<Integer, Address> để tương thích với JSP
        Map<Integer, Address> userAddressesMap = new HashMap<>();
        for (Map.Entry<User, Address> entry : userAddressMap.entrySet()) {
            User user = entry.getKey();
            Address address = entry.getValue();
            if (user != null && user.getUserID() > 0) {
                userAddressesMap.put(user.getUserID(), address);
            }
        }
        
        int totalUsers = 0;
        try {
            totalUsers = userService.countUsers(searchKeyword, roleID, includeInactive);
        } catch (Exception e) {
            System.err.println("Error counting users: " + e.getMessage());
            e.printStackTrace();
            totalUsers = 0;
        }
        
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        if (totalPages < 1) totalPages = 1;
        
        request.setAttribute("users", users);
        request.setAttribute("userAddressesMap", userAddressesMap);
        request.setAttribute("currentPage", pageNumber);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("roleID", roleID);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("showAll", includeInactive);
        
        List<Role> roles;
        try {
            roles = roleService.getAllRoles();
            if (roles == null) {
                roles = new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error loading roles: " + e.getMessage());
            e.printStackTrace();
            roles = new java.util.ArrayList<>();
        }
        request.setAttribute("roles", roles);
        
        request.getRequestDispatcher("/views/admin/userList.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Role> roles;
        try {
            roles = roleService.getAllRoles();
            if (roles == null) {
                roles = new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error loading roles in showAddForm: " + e.getMessage());
            e.printStackTrace();
            roles = new java.util.ArrayList<>();
        }
        request.setAttribute("roles", roles);
        
        request.setAttribute("action", "add");
        request.setAttribute("user", new User());
        request.getRequestDispatcher("/views/admin/userList.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIDParam = request.getParameter("userID");
        
        if (userIDParam == null || userIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "User ID is required");
            listUsers(request, response);
            return;
        }
        
        try {
            int userID = Integer.parseInt(userIDParam);
            
            User user = userService.getUserById(userID);
            if (user == null) {
                request.setAttribute("errorMessage", "User not found");
                listUsers(request, response);
                return;
            }
            
            // Load roles
            List<Role> roles;
            try {
                roles = roleService.getAllRoles();
                if (roles == null) {
                    roles = new java.util.ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error loading roles in showEditForm: " + e.getMessage());
                e.printStackTrace();
                roles = new java.util.ArrayList<>();
            }
            
            // Load addresses của user
            List<Address> addresses = new java.util.ArrayList<>();
            try {
                addresses = addressService.getAddressesByUser(userID);
                if (addresses == null) {
                    addresses = new java.util.ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error loading addresses in showEditForm: " + e.getMessage());
                e.printStackTrace();
                addresses = new java.util.ArrayList<>();
            }
            
            // Load users list để hiển thị trong bảng (giữ lại các filter/pagination hiện tại)
            String pageParam = request.getParameter("page");
            String searchKeyword = request.getParameter("search");
            String roleParam = request.getParameter("roleID");
            String sortBy = request.getParameter("sortBy");
            String sortOrder = request.getParameter("sortOrder");
            String showAllParam = request.getParameter("showAll");
            
            int pageNumber = 1;
            int pageSize = 10;
            int roleID = 0;
            boolean includeInactive = "true".equalsIgnoreCase(showAllParam);
            
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    pageNumber = Integer.parseInt(pageParam.trim());
                    if (pageNumber < 1) pageNumber = 1;
                } catch (NumberFormatException e) {
                    pageNumber = 1;
                }
            }
            
            if (roleParam != null && !roleParam.trim().isEmpty()) {
                try {
                    roleID = Integer.parseInt(roleParam.trim());
                    if (roleID < 0) roleID = 0;
                } catch (NumberFormatException e) {
                    roleID = 0;
                }
            }
            
            if (sortBy == null || sortBy.trim().isEmpty()) {
                sortBy = "UserID";
            }
            if (sortOrder == null || sortOrder.trim().isEmpty()) {
                sortOrder = "ASC";
            }
            
            if (searchKeyword != null) {
                searchKeyword = searchKeyword.trim();
                if (searchKeyword.isEmpty()) {
                    searchKeyword = null;
                }
            }
            
            // Load users list với pagination
            Map<User, Address> userAddressMap = new HashMap<>();
            List<User> users = new java.util.ArrayList<>();
            
            try {
                userAddressMap = userService.getPagedUsersWithDefaultAddress(
                    pageNumber, pageSize, sortBy, sortOrder, searchKeyword, roleID, includeInactive
                );
                if (userAddressMap != null) {
                    users = new java.util.ArrayList<>(userAddressMap.keySet());
                }
            } catch (Exception e) {
                System.err.println("Error loading users in showEditForm: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Count total users
            int totalUsers = 0;
            try {
                totalUsers = userService.countUsers(searchKeyword, roleID, includeInactive);
            } catch (Exception e) {
                System.err.println("Error counting users in showEditForm: " + e.getMessage());
                e.printStackTrace();
            }
            
            int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
            
            // Set all attributes
            request.setAttribute("users", users);
            request.setAttribute("userAddressesMap", userAddressMap);
            request.setAttribute("roles", roles);
            request.setAttribute("user", user);
            request.setAttribute("addresses", addresses);
            request.setAttribute("action", "edit");
            request.setAttribute("currentPage", pageNumber);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("roleID", roleID);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);
            request.setAttribute("showAll", includeInactive);
            
            request.getRequestDispatcher("/views/admin/userList.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid User ID");
            listUsers(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading user: " + e.getMessage());
            listUsers(request, response);
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIDParam = request.getParameter("userID");
        
        if (userIDParam == null || userIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "User ID is required");
            listUsers(request, response);
            return;
        }
        
        try {
            int userID = Integer.parseInt(userIDParam);
            userService.deleteUser(userID);
            request.setAttribute("successMessage", "User deleted successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid User ID format");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        listUsers(request, response);
    }
    
    private void saveUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIDParam = request.getParameter("userID");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String passwordHash = request.getParameter("passwordHash");
        String phone = request.getParameter("phone");
        String roleIDParam = request.getParameter("roleID");
        String isActiveParam = request.getParameter("isActive");
        
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Full name is required");
            if (userIDParam != null && !userIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email is required");
            if (userIDParam != null && !userIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        int roleID;
        try {
            roleID = Integer.parseInt(roleIDParam);
            if (roleID <= 0) {
                throw new NumberFormatException("Role ID must be > 0");
            }
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("errorMessage", "Invalid role ID. Role ID must be > 0");
            if (userIDParam != null && !userIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        boolean isActive = isActiveParam != null && isActiveParam.equals("true");
        
        User user = new User();
        
        if (userIDParam != null && !userIDParam.isEmpty()) {
            try {
                int userID = Integer.parseInt(userIDParam);
                user.setUserID(userID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid User ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        // Chỉ cập nhật password nếu có giá trị mới
        if (passwordHash != null && !passwordHash.trim().isEmpty()) {
            // Hash password trước khi lưu vào DB
            String hashedPassword = hashPassword(passwordHash.trim());
            user.setPasswordHash(hashedPassword);
        } else if (userIDParam == null || userIDParam.isEmpty()) {
            // Nếu là thêm mới mà không có password thì báo lỗi
            request.setAttribute("errorMessage", "Password is required for new users");
            showAddForm(request, response);
            return;
        }
        user.setPhone(phone != null ? phone.trim() : null);
        user.setRoleID(roleID);
        user.setActive(isActive);
        
        int savedUserID = 0;
        try {
            if (user.getUserID() > 0) {
                userService.updateUser(user);
                savedUserID = user.getUserID();
                request.setAttribute("successMessage", "User updated successfully");
            } else {
                savedUserID = userService.addUser(user);
                user.setUserID(savedUserID);
                request.setAttribute("successMessage", "User added successfully");
            }
            
            // Xử lý address nếu có (chỉ khi user đã được lưu thành công)
            if (savedUserID > 0) {
                String addressIDParam = request.getParameter("addressID");
                String addressFullName = request.getParameter("addressFullName");
                String addressPhone = request.getParameter("addressPhone");
                String addressLine1 = request.getParameter("addressLine1");
                String addressLine2 = request.getParameter("addressLine2");
                String addressCity = request.getParameter("addressCity");
                String addressDistrict = request.getParameter("addressDistrict");
                String addressWard = request.getParameter("addressWard");
                String addressCountry = request.getParameter("addressCountry");
                String addressPostalCode = request.getParameter("addressPostalCode");
                String addressIsDefault = request.getParameter("addressIsDefault");
                
                // Nếu có thông tin address thì lưu
                if (addressLine1 != null && !addressLine1.trim().isEmpty()) {
                    Address address = new Address();
                    
                    if (addressIDParam != null && !addressIDParam.trim().isEmpty()) {
                        try {
                            int addressID = Integer.parseInt(addressIDParam);
                            address.setAddressID(addressID);
                        } catch (NumberFormatException e) {
                            // Ignore invalid address ID
                        }
                    }
                    
                    address.setUserID(savedUserID);
                    address.setFullName(addressFullName != null ? addressFullName.trim() : user.getFullName());
                    address.setPhone(addressPhone != null ? addressPhone.trim() : user.getPhone());
                    address.setLine1(addressLine1.trim());
                    address.setLine2(addressLine2 != null ? addressLine2.trim() : null);
                    address.setCity(addressCity != null ? addressCity.trim() : null);
                    address.setDistrict(addressDistrict != null ? addressDistrict.trim() : null);
                    address.setWard(addressWard != null ? addressWard.trim() : null);
                    address.setCountry(addressCountry != null ? addressCountry.trim() : "Vietnam");
                    address.setPostalCode(addressPostalCode != null ? addressPostalCode.trim() : null);
                    address.setDefault("true".equalsIgnoreCase(addressIsDefault));
                    
                    try {
                        if (address.getAddressID() > 0) {
                            addressService.updateAddress(address);
                        } else {
                            addressService.addAddress(address);
                        }
                    } catch (Exception e) {
                        System.err.println("Error saving address: " + e.getMessage());
                        e.printStackTrace();
                        // Không throw exception, chỉ log lỗi
                    }
                }
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (user.getUserID() > 0) {
                request.setAttribute("user", user);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/userList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("user", user);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/userList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
    }
    
    /**
     * Hash password bằng SHA-256 (giống như LoginServlet)
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

