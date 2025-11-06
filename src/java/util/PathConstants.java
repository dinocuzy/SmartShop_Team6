package util;

/**
 * Constants class chứa các đường dẫn URL pattern
 * Dùng để tránh hardcode URL trong code
 */
public class PathConstants {
    
    // Public paths
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String LOGOUT = "/logout";
    public static final String STORE = "/store";
    public static final String HOME = "/";
    
    // Admin paths
    public static final String ADMIN_DASHBOARD = "/admin/dashboard";
    public static final String ADMIN_USERS = "/admin/users";
    public static final String ADMIN_PRODUCTS = "/admin/products";
    public static final String ADMIN_CATEGORIES = "/admin/categories";
    public static final String ADMIN_ORDERS = "/admin/orders";
    public static final String ADMIN_ROLES = "/admin/roles";
    public static final String ADMIN_PAYMENTS = "/admin/payments";
    public static final String ADMIN_PROMOTIONS = "/admin/promotions";
    public static final String ADMIN_NOTIFICATIONS = "/admin/notifications";
    public static final String ADMIN_PAYMENT_METHODS = "/admin/payment-methods";
    
    // Manager paths
    public static final String MANAGER_DASHBOARD = "/manager/dashboard";
    public static final String MANAGER_PRODUCTS = "/manager/products";
    public static final String MANAGER_ORDERS = "/manager/orders";
    public static final String MANAGER_CATEGORIES = "/manager/categories";
    
    // Staff paths
    public static final String STAFF_DASHBOARD = "/staff/dashboard";
    public static final String STAFF_ORDERS = "/staff/orders";
    
    // Customer paths
    public static final String CUSTOMER_DASHBOARD = "/customer/dashboard";
    public static final String CUSTOMER_PROFILE = "/customer/profile";
    public static final String CUSTOMER_ORDERS = "/customer/orders";
    public static final String CUSTOMER_CART = "/customer/cart";
    public static final String CUSTOMER_WISHLIST = "/customer/wishlist";
    
    // Store paths
    public static final String STORE_HOME = "/store/home";
    public static final String STORE_PRODUCTS = "/store/products";
    public static final String STORE_PRODUCT_DETAIL = "/store/product";
    
    // Error paths
    public static final String ERROR_403 = "/error/403";
    public static final String ERROR_404 = "/error/404";
    public static final String ERROR_500 = "/error/500";
    
    private PathConstants() {
        // Prevent instantiation
    }
}
