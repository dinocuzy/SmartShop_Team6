package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import orderservice.IOrderService;
import orderservice.OrderService;
import productservice.IProductService;
import productservice.ProductService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý trang Customer Dashboard
 * URL mapping: /customer/dashboard
 * Customer có quyền: Xem sản phẩm, đơn hàng của mình, thông tin cá nhân
 */
@WebServlet("/customer/dashboard")
public class CustomerDashboardServlet extends HttpServlet {
    
    private IOrderService orderService;
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();
        productService = new ProductService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            // Nếu chưa đăng nhập, redirect về trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Lấy đơn hàng của user hiện tại
            List<model.Order> userOrders = orderService.getOrdersByUser(currentUser.getUserID());
            
            // Đếm đơn hàng theo trạng thái của user
            int totalOrders = orderService.countOrders(String.valueOf(currentUser.getUserID()), null, 0);
            int pendingOrders = orderService.countOrders(String.valueOf(currentUser.getUserID()), "Pending", 0);
            int processingOrders = orderService.countOrders(String.valueOf(currentUser.getUserID()), "Processing", 0);
            int deliveredOrders = orderService.countOrders(String.valueOf(currentUser.getUserID()), "Delivered", 0);
            
            // Lấy sản phẩm mới nhất (có thể lấy 6 sản phẩm)
            // getPagedProducts(pageNumber, pageSize, sortBy, sortOrder, searchKeyword, categoryID, includeInactive)
            List<model.Product> recentProducts = productService.getPagedProducts(1, 6, "ProductID", "DESC", null, 0, false);
            
            // Set attributes
            request.setAttribute("userOrders", userOrders);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("processingOrders", processingOrders);
            request.setAttribute("deliveredOrders", deliveredOrders);
            request.setAttribute("recentProducts", recentProducts);
            request.setAttribute("currentUser", currentUser);
            
            // Forward đến customer dashboard JSP
            request.getRequestDispatcher("/views/customer/customerDashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/customer/customerDashboard.jsp").forward(request, response);
        }
    }
}

