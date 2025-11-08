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
import util.AuthorizationUtil;

import java.io.IOException;
import java.util.List;

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
        try {
            orderService = new OrderService();
            paymentService = new PaymentService();
            notificationService = new NotificationService();
        } catch (Exception e) {
            System.err.println("Error initializing StaffDashboardServlet: " + e.getMessage());
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
        
        // Kiểm tra role (Staff) - sử dụng AuthorizationUtil
        boolean isStaff = false;
        try {
            isStaff = AuthorizationUtil.hasRole(currentUser, "Staff");
        } catch (Exception e) {
            System.err.println("Error checking role in StaffDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            // Fallback: kiểm tra theo roleID (Staff = 3)
            if (currentUser != null) {
                isStaff = (currentUser.getRoleID() == 3);
            }
        }
        
        if (!isStaff) {
            // Nếu không phải Staff, redirect về dashboard phù hợp
            try {
                if (AuthorizationUtil.hasRole(currentUser, "Admin")) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else if (AuthorizationUtil.hasRole(currentUser, "Manager")) {
                    response.sendRedirect(request.getContextPath() + "/manager/dashboard");
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
        if (orderService == null || paymentService == null || notificationService == null) {
            try {
                if (orderService == null) orderService = new OrderService();
                if (paymentService == null) paymentService = new PaymentService();
                if (notificationService == null) notificationService = new NotificationService();
            } catch (Exception e) {
                System.err.println("Error creating services in doGet: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi khởi tạo hệ thống. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/views/admin/staffDashboard.jsp").forward(request, response);
                return;
            }
        }
        
        try {
            // Lấy thống kê cho Staff với error handling
            int totalOrders = 0;
            int totalPayments = 0;
            int pendingOrders = 0;
            int processingOrders = 0;
            int deliveredOrders = 0;
            int cancelledOrders = 0;
            int unpaidOrders = 0;
            int paidOrders = 0;
            
            try {
                // Tổng số đơn hàng
                totalOrders = orderService.countOrders(null, null, 0);
            } catch (Exception e) {
                System.err.println("Error counting total orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                // Tổng số thanh toán
                List<model.Payment> payments = paymentService.getAllPayments();
                totalPayments = payments != null ? payments.size() : 0;
            } catch (Exception e) {
                System.err.println("Error getting payments: " + e.getMessage());
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
            
            try {
                cancelledOrders = orderService.countOrders(null, "Cancelled", 0);
            } catch (Exception e) {
                System.err.println("Error counting cancelled orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                unpaidOrders = orderService.countOrders(null, "Unpaid", 0);
            } catch (Exception e) {
                System.err.println("Error counting unpaid orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                paidOrders = orderService.countOrders(null, "Paid", 0);
            } catch (Exception e) {
                System.err.println("Error counting paid orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Set attributes
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("totalPayments", totalPayments);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("processingOrders", processingOrders);
            request.setAttribute("deliveredOrders", deliveredOrders);
            request.setAttribute("cancelledOrders", cancelledOrders);
            request.setAttribute("unpaidOrders", unpaidOrders);
            request.setAttribute("paidOrders", paidOrders);
            
            // Forward đến staff dashboard JSP
            request.getRequestDispatcher("/views/admin/staffDashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Unexpected error in StaffDashboardServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tải dashboard: " + e.getMessage());
            
            // Đảm bảo response chưa commit trước khi forward
            if (!response.isCommitted()) {
                try {
                    request.getRequestDispatcher("/views/admin/staffDashboard.jsp").forward(request, response);
                } catch (Exception forwardException) {
                    System.err.println("Error forwarding to staff dashboard: " + forwardException.getMessage());
                    forwardException.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Đã xảy ra lỗi. Vui lòng thử lại sau.");
                }
            }
        }
    }
}

