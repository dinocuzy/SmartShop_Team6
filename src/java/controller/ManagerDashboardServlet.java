package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import productservice.IProductService;
import productservice.ProductService;
import orderservice.IOrderService;
import orderservice.OrderService;
import categoryservice.ICategoryService;
import categoryservice.CategoryService;
import util.AuthorizationUtil;

import java.io.IOException;

/**
 * Servlet xử lý trang Manager Dashboard
 * URL mapping: /manager/dashboard hoặc /manager
 * Manager có quyền: Quản lý catalog (products, categories) và orders
 */
@WebServlet({"/manager/dashboard", "/manager"})
public class ManagerDashboardServlet extends HttpServlet {
    
    private IProductService productService;
    private IOrderService orderService;
    private ICategoryService categoryService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            productService = new ProductService();
            orderService = new OrderService();
            categoryService = new CategoryService();
        } catch (Exception e) {
            System.err.println("Error initializing ManagerDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để tránh context startup failure
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền truy cập
        jakarta.servlet.http.HttpSession session = request.getSession();
        model.User currentUser = (model.User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra role (Manager) - sử dụng AuthorizationUtil
        boolean isManager = false;
        try {
            isManager = AuthorizationUtil.hasRole(currentUser, "Manager");
        } catch (Exception e) {
            System.err.println("Error checking role in ManagerDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            // Fallback: kiểm tra theo roleID (Manager = 2)
            if (currentUser != null) {
                isManager = (currentUser.getRoleID() == 2);
            }
        }
        
        if (!isManager) {
            // Nếu không phải Manager, redirect về dashboard phù hợp
            try {
                if (AuthorizationUtil.hasRole(currentUser, "Admin")) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else if (AuthorizationUtil.hasRole(currentUser, "Staff")) {
                    response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                } else {
                    // Customer hoặc role khác - redirect về home (Customer được phép)
                    response.sendRedirect(request.getContextPath() + "/home");
                }
            } catch (Exception e) {
                // Fallback: nếu là Customer thì về home, còn không thì về login
                if (currentUser != null && currentUser.getRoleID() == 4) {
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }
            }
            return;
        }
        
        // Kiểm tra services đã được khởi tạo chưa
        if (productService == null || orderService == null || categoryService == null) {
            try {
                if (productService == null) productService = new ProductService();
                if (orderService == null) orderService = new OrderService();
                if (categoryService == null) categoryService = new CategoryService();
            } catch (Exception e) {
                System.err.println("Error creating services in doGet: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi khởi tạo hệ thống. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/views/admin/managerDashboard.jsp").forward(request, response);
                return;
            }
        }
        
        try {
            // Lấy thống kê cho Manager với error handling
            int totalProducts = 0;
            int totalCategories = 0;
            int totalOrders = 0;
            int pendingOrders = 0;
            int processingOrders = 0;
            int deliveredOrders = 0;
            
            try {
                // Tổng số sản phẩm
                totalProducts = productService.countProducts(null, 0, false);
            } catch (Exception e) {
                System.err.println("Error counting products: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                // Tổng số danh mục
                totalCategories = categoryService.getAllCategories().size();
            } catch (Exception e) {
                System.err.println("Error counting categories: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                // Tổng số đơn hàng
                totalOrders = orderService.countOrders(null, null, 0);
            } catch (Exception e) {
                System.err.println("Error counting total orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                // Đếm các đơn hàng theo trạng thái
                pendingOrders = orderService.countOrders(null, "Pending", 0);
            } catch (Exception e) {
                System.err.println("Error counting pending orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                processingOrders = orderService.countOrders(null, "Processing", 0);
            } catch (Exception e) {
                System.err.println("Error counting processing orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                deliveredOrders = orderService.countOrders(null, "Delivered", 0);
            } catch (Exception e) {
                System.err.println("Error counting delivered orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Set attributes
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalCategories", totalCategories);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("processingOrders", processingOrders);
            request.setAttribute("deliveredOrders", deliveredOrders);
            
            // Forward đến manager dashboard JSP
            request.getRequestDispatcher("/views/admin/managerDashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Unexpected error in ManagerDashboardServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tải dashboard: " + e.getMessage());
            
            // Đảm bảo response chưa commit trước khi forward
            if (!response.isCommitted()) {
                try {
                    request.getRequestDispatcher("/views/admin/managerDashboard.jsp").forward(request, response);
                } catch (Exception forwardException) {
                    System.err.println("Error forwarding to manager dashboard: " + forwardException.getMessage());
                    forwardException.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Đã xảy ra lỗi. Vui lòng thử lại sau.");
                }
            }
        }
    }
}

