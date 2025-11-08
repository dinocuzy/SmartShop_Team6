package util;

import model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility class để kiểm tra quyền truy cập (Authorization)
 * Hỗ trợ role-based access control (RBAC)
 */
public class AuthorizationUtil {
    
    // Role constants - dựa trên RoleID trong database
    public static final int ROLE_ADMIN_ID = 1;
    public static final int ROLE_MANAGER_ID = 2;
    public static final int ROLE_STAFF_ID = 3;
    public static final int ROLE_CUSTOMER_ID = 4;
    
    // Role name constants
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_STAFF = "Staff";
    public static final String ROLE_CUSTOMER = "Customer";
    
    /**
     * Lấy user hiện tại từ session
     * @param request HttpServletRequest
     * @return User object hoặc null nếu chưa đăng nhập
     */
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }
    
    /**
     * Kiểm tra user đã đăng nhập chưa
     * @param request HttpServletRequest
     * @return true nếu đã đăng nhập, false nếu chưa
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }
    
    /**
     * Kiểm tra user có role cụ thể không (theo RoleID)
     * @param request HttpServletRequest
     * @param roleID RoleID cần kiểm tra
     * @return true nếu user có role này
     */
    public static boolean hasRole(HttpServletRequest request, int roleID) {
        User user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        return user.getRoleID() == roleID;
    }
    
    /**
     * Kiểm tra user có role cụ thể không (theo RoleName)
     * @param request HttpServletRequest
     * @param roleName Tên role cần kiểm tra
     * @return true nếu user có role này
     */
    public static boolean hasRole(HttpServletRequest request, String roleName) {
        User user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        if (roleName == null) {
            return false;
        }
        // So sánh với roleID hoặc roleName
        String userRoleName = user.getRoleName();
        if (userRoleName != null && userRoleName.equalsIgnoreCase(roleName)) {
            return true;
        }
        // Fallback: so sánh theo roleID
        return hasRole(request, getRoleIDByName(roleName));
    }
    
    /**
     * Kiểm tra user có role cụ thể không (theo RoleName) - overload với User object
     * @param user User object cần kiểm tra
     * @param roleName Tên role cần kiểm tra
     * @return true nếu user có role này
     */
    public static boolean hasRole(User user, String roleName) {
        if (user == null) {
            return false;
        }
        if (roleName == null) {
            return false;
        }
        // So sánh với roleName
        String userRoleName = user.getRoleName();
        if (userRoleName != null && userRoleName.equalsIgnoreCase(roleName)) {
            return true;
        }
        // Fallback: so sánh theo roleID
        int roleID = getRoleIDByName(roleName);
        if (roleID > 0) {
            return user.getRoleID() == roleID;
        }
        return false;
    }
    
    /**
     * Kiểm tra user có role cụ thể không (theo RoleID) - overload với User object
     * @param user User object cần kiểm tra
     * @param roleID RoleID cần kiểm tra
     * @return true nếu user có role này
     */
    public static boolean hasRole(User user, int roleID) {
        if (user == null) {
            return false;
        }
        return user.getRoleID() == roleID;
    }
    
    /**
     * Kiểm tra user có phải Admin không
     * @param request HttpServletRequest
     * @return true nếu là Admin
     */
    public static boolean isAdmin(HttpServletRequest request) {
        return hasRole(request, ROLE_ADMIN_ID) || hasRole(request, ROLE_ADMIN);
    }
    
    /**
     * Kiểm tra user có phải Manager không
     * @param request HttpServletRequest
     * @return true nếu là Manager
     */
    public static boolean isManager(HttpServletRequest request) {
        return hasRole(request, ROLE_MANAGER_ID) || hasRole(request, ROLE_MANAGER);
    }
    
    /**
     * Kiểm tra user có phải Staff không
     * @param request HttpServletRequest
     * @return true nếu là Staff
     */
    public static boolean isStaff(HttpServletRequest request) {
        return hasRole(request, ROLE_STAFF_ID) || hasRole(request, ROLE_STAFF);
    }
    
    /**
     * Kiểm tra user có phải Customer không
     * @param request HttpServletRequest
     * @return true nếu là Customer
     */
    public static boolean isCustomer(HttpServletRequest request) {
        return hasRole(request, ROLE_CUSTOMER_ID) || hasRole(request, ROLE_CUSTOMER);
    }
    
    /**
     * Kiểm tra user có phải Admin hoặc Manager không
     * @param request HttpServletRequest
     * @return true nếu là Admin hoặc Manager
     */
    public static boolean isAdminOrManager(HttpServletRequest request) {
        return isAdmin(request) || isManager(request);
    }
    
    /**
     * Kiểm tra user có phải Admin, Manager hoặc Staff không (tất cả nhân viên)
     * @param request HttpServletRequest
     * @return true nếu là Admin, Manager hoặc Staff
     */
    public static boolean isStaffMember(HttpServletRequest request) {
        return isAdmin(request) || isManager(request) || isStaff(request);
    }
    
    /**
     * Kiểm tra user có quyền truy cập admin area không
     * @param request HttpServletRequest
     * @return true nếu có quyền (Admin, Manager, Staff)
     */
    public static boolean canAccessAdminArea(HttpServletRequest request) {
        return isStaffMember(request);
    }
    
    /**
     * Chuyển đổi role name thành role ID
     * @param roleName Tên role
     * @return RoleID tương ứng, -1 nếu không tìm thấy
     */
    public static int getRoleIDByName(String roleName) {
        if (roleName == null) {
            return -1;
        }
        switch (roleName.trim()) {
            case ROLE_ADMIN:
                return ROLE_ADMIN_ID;
            case ROLE_MANAGER:
                return ROLE_MANAGER_ID;
            case ROLE_STAFF:
                return ROLE_STAFF_ID;
            case ROLE_CUSTOMER:
                return ROLE_CUSTOMER_ID;
            default:
                return -1;
        }
    }
    
    /**
     * Chuyển đổi role ID thành role name
     * @param roleID RoleID
     * @return Tên role tương ứng, null nếu không tìm thấy
     */
    public static String getRoleNameByID(int roleID) {
        switch (roleID) {
            case ROLE_ADMIN_ID:
                return ROLE_ADMIN;
            case ROLE_MANAGER_ID:
                return ROLE_MANAGER;
            case ROLE_STAFF_ID:
                return ROLE_STAFF;
            case ROLE_CUSTOMER_ID:
                return ROLE_CUSTOMER;
            default:
                return null;
        }
    }
    
    /**
     * Kiểm tra user có quyền truy cập resource dựa trên role được phép
     * @param request HttpServletRequest
     * @param allowedRoles Danh sách role được phép truy cập
     * @return true nếu user có một trong các role được phép
     */
    public static boolean hasAnyRole(HttpServletRequest request, String... allowedRoles) {
        if (allowedRoles == null || allowedRoles.length == 0) {
            return false;
        }
        for (String role : allowedRoles) {
            if (hasRole(request, role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra user có quyền truy cập resource dựa trên role ID được phép
     * @param request HttpServletRequest
     * @param allowedRoleIDs Danh sách role ID được phép truy cập
     * @return true nếu user có một trong các role ID được phép
     */
    public static boolean hasAnyRoleID(HttpServletRequest request, int... allowedRoleIDs) {
        if (allowedRoleIDs == null || allowedRoleIDs.length == 0) {
            return false;
        }
        User user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        int userRoleID = user.getRoleID();
        for (int roleID : allowedRoleIDs) {
            if (userRoleID == roleID) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra user có phải chủ sở hữu resource không (userID khớp)
     * @param request HttpServletRequest
     * @param resourceUserID UserID của resource (ví dụ: order userID, profile userID)
     * @return true nếu user là chủ sở hữu hoặc là Admin
     */
    public static boolean isOwnerOrAdmin(HttpServletRequest request, int resourceUserID) {
        User user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        // Admin có thể truy cập tất cả
        if (isAdmin(request)) {
            return true;
        }
        // Kiểm tra user có phải chủ sở hữu không
        return user.getUserID() == resourceUserID;
    }
}
