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
        productService = new ProductService();
        orderService = new OrderService();
        categoryService = new CategoryService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền truy cập (Manager only)
        jakarta.servlet.http.HttpSession session = request.getSession();
        model.User currentUser = (model.User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra role (Manager)
        String roleName = currentUser.getRoleName();
        if (roleName == null || !roleName.equals("Manager")) {
            // Nếu không phải Manager, redirect về dashboard phù hợp
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        try {
            // Lấy thống kê cho Manager (chỉ lấy active records)
            int totalProducts = productService.countProducts(null, 0, false);
            int totalCategories = categoryService.getAllCategories().size();
            int totalOrders = orderService.countOrders(null, null, 0);
            
            // Đếm các đơn hàng theo trạng thái
            int pendingOrders = orderService.countOrders(null, "Pending", 0);
            int processingOrders = orderService.countOrders(null, "Processing", 0);
            int deliveredOrders = orderService.countOrders(null, "Delivered", 0);
            
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
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/managerDashboard.jsp").forward(request, response);
        }
    }
}

