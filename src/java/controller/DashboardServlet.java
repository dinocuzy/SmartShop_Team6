package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import productservice.IProductService;
import productservice.ProductService;
import userservice.IUserService;
import userservice.UserService;
import orderservice.IOrderService;
import orderservice.OrderService;
import categoryservice.ICategoryService;
import categoryservice.CategoryService;
import paymentservice.IPaymentService;
import paymentservice.PaymentService;
import promotionservice.IPromotionService;
import promotionservice.PromotionService;
import notificationservice.INotificationService;
import notificationservice.NotificationService;

import java.io.IOException;

/**
 * Servlet xử lý trang Admin Dashboard
 * URL mapping: /admin/dashboard hoặc /admin
 */
@WebServlet({"/admin/dashboard", "/admin"})
public class DashboardServlet extends HttpServlet {
    
    private IProductService productService;
    private IUserService userService;
    private IOrderService orderService;
    private ICategoryService categoryService;
    private IPaymentService paymentService;
    private IPromotionService promotionService;
    private INotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        try {
            super.init();
            productService = new ProductService();
            userService = new UserService();
            orderService = new OrderService();
            categoryService = new CategoryService();
            paymentService = new PaymentService();
            promotionService = new PromotionService();
            notificationService = new NotificationService();
        } catch (Exception e) {
            System.err.println("Error initializing DashboardServlet: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow servlet to start, errors will be handled in doGet
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền truy cập (Admin only)
        jakarta.servlet.http.HttpSession session = request.getSession();
        model.User currentUser = (model.User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra role (Admin)
        String roleName = currentUser.getRoleName();
        if (roleName == null || !roleName.equals("Admin")) {
            // Nếu không phải Admin, redirect về dashboard phù hợp
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        try {
            // Lấy thống kê tổng quan (chỉ lấy active records)
            int totalProducts = productService.countProducts(null, 0, false);
            int totalUsers = userService.countUsers(null, 0, false);
            int totalOrders = orderService.countOrders(null, null, 0);
            int totalCategories = categoryService.getAllCategories().size();
            
            // Lấy các đơn hàng gần đây (có thể lấy 5 đơn hàng mới nhất)
            // Đếm các đơn hàng theo trạng thái
            int pendingOrders = orderService.countOrders(null, "Pending", 0);
            int processingOrders = orderService.countOrders(null, "Processing", 0);
            int deliveredOrders = orderService.countOrders(null, "Delivered", 0);
            
            // Lấy tổng số payments
            int totalPayments = paymentService.getAllPayments().size();
            
            // Lấy tổng số promotions đang hoạt động
            int activePromotions = promotionService.getActivePromotions().size();
            
            // Lấy tổng số notifications chưa đọc (có thể tính từ tất cả users)
            
            // Set attributes
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("totalCategories", totalCategories);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("processingOrders", processingOrders);
            request.setAttribute("deliveredOrders", deliveredOrders);
            request.setAttribute("totalPayments", totalPayments);
            request.setAttribute("activePromotions", activePromotions);
            
            // Forward đến dashboard JSP
            request.getRequestDispatcher("/views/admin/adminDashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/adminDashboard.jsp").forward(request, response);
        }
    }
}

