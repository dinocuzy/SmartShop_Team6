package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import orderservice.IOrderService;
import orderservice.OrderService;
import paymentservice.IPaymentService;
import paymentservice.PaymentService;
import notificationservice.INotificationService;
import notificationservice.NotificationService;

import java.io.IOException;

/**
 * Servlet xử lý trang Staff Dashboard
 * URL mapping: /staff/dashboard hoặc /staff
 * Staff có quyền: Hỗ trợ và vận hành (orders, payments, notifications)
 */
@WebServlet({"/staff/dashboard", "/staff"})
public class StaffDashboardServlet extends HttpServlet {
    
    private IOrderService orderService;
    private IPaymentService paymentService;
    private INotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();
        paymentService = new PaymentService();
        notificationService = new NotificationService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền truy cập (Staff only)
        jakarta.servlet.http.HttpSession session = request.getSession();
        model.User currentUser = (model.User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra role (Staff)
        String roleName = currentUser.getRoleName();
        if (roleName == null || !roleName.equals("Staff")) {
            // Nếu không phải Staff, redirect về dashboard phù hợp
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        try {
            // Lấy thống kê cho Staff
            int totalOrders = orderService.countOrders(null, null, 0);
            int totalPayments = paymentService.getAllPayments().size();
            
            // Đếm các đơn hàng theo trạng thái
            int pendingOrders = orderService.countOrders(null, "Pending", 0);
            int processingOrders = orderService.countOrders(null, "Processing", 0);
            int deliveredOrders = orderService.countOrders(null, "Delivered", 0);
            
            // Lấy tổng số notifications (có thể lọc theo unread)
            
            // Set attributes
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("totalPayments", totalPayments);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("processingOrders", processingOrders);
            request.setAttribute("deliveredOrders", deliveredOrders);
            
            // Forward đến staff dashboard JSP
            request.getRequestDispatcher("/views/admin/staffDashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/staffDashboard.jsp").forward(request, response);
        }
    }
}

