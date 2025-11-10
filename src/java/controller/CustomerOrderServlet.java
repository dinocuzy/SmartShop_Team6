package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.Order;
import model.OrderItem;
import orderservice.IOrderService;
import orderservice.OrderService;
import orderitemservice.IOrderItemService;
import orderitemservice.OrderItemService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý xem đơn hàng của Customer
 * URL mapping: /customer/orders
 */
@WebServlet("/customer/orders")
public class CustomerOrderServlet extends HttpServlet {
    
    private IOrderService orderService;
    private IOrderItemService orderItemService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            orderService = new OrderService();
            orderItemService = new OrderItemService();
        } catch (Exception e) {
            System.err.println("Error initializing CustomerOrderServlet: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để tránh context startup failure
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String orderIDParam = request.getParameter("orderID");
        String successParam = request.getParameter("success");
        
        if (successParam != null && "true".equals(successParam)) {
            request.setAttribute("successMessage", "Đặt hàng thành công! Cảm ơn bạn đã mua sắm tại SmartShop.");
        }
        
        try {
            if (orderIDParam != null && !orderIDParam.trim().isEmpty()) {
                // Xem chi tiết đơn hàng
                viewOrderDetail(request, response, currentUser, orderIDParam);
            } else {
                // Xem danh sách đơn hàng
                listOrders(request, response, currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            listOrders(request, response, currentUser);
        }
    }
    
    /**
     * Hiển thị danh sách đơn hàng
     */
    private void listOrders(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            // Lấy tất cả đơn hàng của user
            List<Order> orders = orderService.getOrdersByUser(currentUser.getUserID());
            
            request.setAttribute("orders", orders);
            request.setAttribute("currentUser", currentUser);
            
            request.getRequestDispatcher("/views/customer/customerOrders.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/views/customer/customerOrders.jsp").forward(request, response);
        }
    }
    
    /**
     * Hiển thị chi tiết đơn hàng
     */
    private void viewOrderDetail(HttpServletRequest request, HttpServletResponse response, 
                                User currentUser, String orderIDParam) 
            throws ServletException, IOException {
        
        try {
            int orderID = Integer.parseInt(orderIDParam.trim());
            
            // Lấy thông tin đơn hàng
            Order order = orderService.getOrderById(orderID);
            
            // Kiểm tra đơn hàng có thuộc về user này không
            if (order == null || order.getUserID() != currentUser.getUserID()) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này");
                listOrders(request, response, currentUser);
                return;
            }
            
            // Lấy danh sách order items
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(orderID);
            
            request.setAttribute("order", order);
            request.setAttribute("orderItems", orderItems);
            request.setAttribute("currentUser", currentUser);
            
            request.getRequestDispatcher("/views/customer/orderDetail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Mã đơn hàng không hợp lệ");
            listOrders(request, response, currentUser);
        }
    }
}

