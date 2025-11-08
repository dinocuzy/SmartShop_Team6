package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Order;
import model.User;
import model.Address;
import orderservice.IOrderService;
import orderservice.OrderService;
import userservice.IUserService;
import userservice.UserService;
import addressservice.IAddressService;
import addressservice.AddressService;
import orderstatushistoryservice.IOrderStatusHistoryService;
import orderstatushistoryservice.OrderStatusHistoryService;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho Order
 * URL mapping: /admin/orders
 * Actions: list, add, edit, delete, save
 */
@WebServlet("/admin/orders")
public class OrderServlet extends HttpServlet {
    
    private IOrderService orderService;
    private IUserService userService;
    private IAddressService addressService;
    private IOrderStatusHistoryService orderStatusHistoryService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            orderService = new OrderService();
            userService = new UserService();
            addressService = new AddressService();
            orderStatusHistoryService = new OrderStatusHistoryService();
        } catch (Exception e) {
            System.err.println("Error initializing OrderServlet: " + e.getMessage());
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
                    deleteOrder(request, response);
                    break;
                case "list":
                default:
                    listOrders(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listOrders(request, response);
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
                    saveOrder(request, response);
                    break;
                default:
                    listOrders(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String orderID = request.getParameter("orderID");
            if (orderID != null && !orderID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    private void listOrders(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy các tham số từ request và xử lý
        String pageParam = request.getParameter("page");
        String searchKeyword = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String userParam = request.getParameter("userID");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        // Parse và validate các tham số
        int pageNumber = 1;
        int pageSize = 10;
        int userID = 0;
        
        // Parse page number
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                pageNumber = Integer.parseInt(pageParam.trim());
                if (pageNumber < 1) pageNumber = 1;
            } catch (NumberFormatException e) {
                pageNumber = 1;
            }
        }
        
        // Parse user ID
        if (userParam != null && !userParam.trim().isEmpty()) {
            try {
                userID = Integer.parseInt(userParam.trim());
                if (userID < 0) userID = 0;
            } catch (NumberFormatException e) {
                userID = 0;
            }
        }
        
        // Set default sort values
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "OrderID";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "ASC";
        }
        
        // Parse status
        String status = null;
        if (statusParam != null && !statusParam.trim().isEmpty() && !statusParam.trim().equalsIgnoreCase("all")) {
            status = statusParam.trim();
        }
        
        // Trim search keyword
        if (searchKeyword != null) {
            searchKeyword = searchKeyword.trim();
            if (searchKeyword.isEmpty()) {
                searchKeyword = null;
            }
        }
        
        List<Order> orders = orderService.getPagedOrders(
            pageNumber, pageSize, sortBy, sortOrder, searchKeyword, status, userID
        );
        
        int totalOrders = orderService.countOrders(searchKeyword, status, userID);
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);
        
        request.setAttribute("orders", orders);
        request.setAttribute("currentPage", pageNumber);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("status", statusParam != null ? statusParam : "all");
        request.setAttribute("userID", userID);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        // Load danh sách users cho dropdown (chỉ lấy active users)
        List<User> users = userService.getAllUsers(false);
        request.setAttribute("users", users);
        
        request.getRequestDispatcher("/views/admin/orderList.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Load danh sách users cho dropdown (chỉ lấy active users)
        List<User> users = userService.getAllUsers(false);
        request.setAttribute("users", users);
        
        request.setAttribute("action", "add");
        request.setAttribute("order", new Order());
        request.getRequestDispatcher("/views/admin/orderList.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String orderIDParam = request.getParameter("orderID");
        
        if (orderIDParam == null || orderIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Order ID is required");
            listOrders(request, response);
            return;
        }
        
        try {
            int orderID = Integer.parseInt(orderIDParam);
            Order order = orderService.getOrderById(orderID);
            
            if (order == null) {
                request.setAttribute("errorMessage", "Order not found with ID: " + orderID);
                listOrders(request, response);
                return;
            }
            
            // Load danh sách users cho dropdown (chỉ lấy active users)
            List<User> users = userService.getAllUsers(false);
            request.setAttribute("users", users);
            
            // Load addresses của user (nếu có userID)
            List<Address> userAddresses = new java.util.ArrayList<>();
            if (order.getUserID() > 0) {
                try {
                    userAddresses = addressService.getAddressesByUser(order.getUserID());
                    if (userAddresses == null) {
                        userAddresses = new java.util.ArrayList<>();
                    }
                } catch (Exception e) {
                    System.err.println("Error loading addresses for user " + order.getUserID() + ": " + e.getMessage());
                    e.printStackTrace();
                    userAddresses = new java.util.ArrayList<>();
                }
            }
            request.setAttribute("userAddresses", userAddresses);
            
            request.setAttribute("action", "edit");
            request.setAttribute("order", order);
            request.getRequestDispatcher("/views/admin/orderList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Order ID format");
            listOrders(request, response);
        }
    }
    
    private void deleteOrder(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String orderIDParam = request.getParameter("orderID");
        
        if (orderIDParam == null || orderIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Order ID is required");
            listOrders(request, response);
            return;
        }
        
        try {
            int orderID = Integer.parseInt(orderIDParam);
            // Note: Orders thường không nên xóa, chỉ cập nhật status. 
            // Nhưng nếu cần xóa, có thể implement soft delete hoặc chỉ xóa nếu không có OrderItems
            request.setAttribute("errorMessage", "Deleting orders is not allowed. Please update order status instead.");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Order ID format");
        }
        
        listOrders(request, response);
    }
    
    private void saveOrder(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String orderIDParam = request.getParameter("orderID");
        String userIDParam = request.getParameter("userID");
        String billingAddressIDParam = request.getParameter("billingAddressID");
        String shippingAddressIDParam = request.getParameter("shippingAddressID");
        String orderStatus = request.getParameter("orderStatus");
        String orderDateParam = request.getParameter("orderDate");
        String totalAmountParam = request.getParameter("totalAmount");
        String note = request.getParameter("note");
        
        if (userIDParam == null || userIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "User ID is required");
            if (orderIDParam != null && !orderIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        if (orderStatus == null || orderStatus.trim().isEmpty()) {
            orderStatus = "Pending";
        }
        
        int userID;
        try {
            userID = Integer.parseInt(userIDParam);
            if (userID <= 0) {
                throw new NumberFormatException("User ID must be > 0");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid User ID format");
            if (orderIDParam != null && !orderIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        BigDecimal totalAmount;
        try {
            if (totalAmountParam != null && !totalAmountParam.trim().isEmpty()) {
                totalAmount = new BigDecimal(totalAmountParam);
                if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Total amount must be >= 0");
                }
            } else {
                totalAmount = BigDecimal.ZERO;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid total amount format");
            if (orderIDParam != null && !orderIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        // Billing address mặc định là null (địa chỉ shop, không cần lưu trong DB)
        Integer billingAddressID = null;
        if (billingAddressIDParam != null && !billingAddressIDParam.trim().isEmpty()) {
            try {
                int parsedID = Integer.parseInt(billingAddressIDParam.trim());
                if (parsedID > 0) {
                    billingAddressID = parsedID;
                }
            } catch (NumberFormatException e) {
                // Giữ null nếu không parse được
            }
        }
        
        Integer shippingAddressID = null;
        if (shippingAddressIDParam != null && !shippingAddressIDParam.trim().isEmpty()) {
            try {
                shippingAddressID = Integer.parseInt(shippingAddressIDParam);
                if (shippingAddressID <= 0) {
                    shippingAddressID = null;
                }
            } catch (NumberFormatException e) {
                shippingAddressID = null;
            }
        }
        
        Date orderDate;
        if (orderDateParam != null && !orderDateParam.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                orderDate = sdf.parse(orderDateParam);
            } catch (ParseException e) {
                orderDate = new Date();
            }
        } else {
            orderDate = new Date();
        }
        
        Order order = new Order();
        
        if (orderIDParam != null && !orderIDParam.isEmpty()) {
            try {
                int orderID = Integer.parseInt(orderIDParam);
                order.setOrderID(orderID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Order ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        order.setUserID(userID);
        order.setBillingAddressID(billingAddressID);
        order.setShippingAddressID(shippingAddressID);
        order.setOrderStatus(orderStatus);
        order.setOrderDate(orderDate);
        order.setTotalAmount(totalAmount);
        order.setNote(note != null ? note.trim() : null);
        
        try {
            if (order.getOrderID() > 0) {
                // Lấy order cũ để so sánh trạng thái
                Order oldOrder = orderService.getOrderById(order.getOrderID());
                String oldStatus = oldOrder != null ? oldOrder.getOrderStatus() : null;
                String newStatus = order.getOrderStatus();
                
                // Cập nhật order
                orderService.updateOrder(order);
                
                // Ghi log lịch sử thay đổi trạng thái nếu có thay đổi
                if (oldOrder != null && oldStatus != null && !oldStatus.equals(newStatus)) {
                    try {
                        HttpSession session = request.getSession();
                        User currentUser = (User) session.getAttribute("currentUser");
                        Integer changedBy = currentUser != null ? currentUser.getUserID() : null;
                        
                    orderStatusHistoryService.recordStatusChange(
                        order.getOrderID(), 
                        oldStatus, 
                        newStatus, 
                        changedBy
                    );
                    } catch (Exception e) {
                        // Không throw exception để không ảnh hưởng đến việc cập nhật order
                        System.err.println("Error recording order status history: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                request.setAttribute("successMessage", "Order updated successfully");
            } else {
                int orderID = orderService.addOrder(order);
                order.setOrderID(orderID);
                
                // Ghi log lịch sử cho đơn hàng mới (trạng thái đầu tiên)
                try {
                    HttpSession session = request.getSession();
                    User currentUser = (User) session.getAttribute("currentUser");
                    Integer changedBy = currentUser != null ? currentUser.getUserID() : null;
                    
                    orderStatusHistoryService.recordStatusChange(
                        orderID, 
                        "New", 
                        order.getOrderStatus(), 
                        changedBy
                    );
                } catch (Exception e) {
                    // Không throw exception để không ảnh hưởng đến việc tạo order
                    System.err.println("Error recording order status history for new order: " + e.getMessage());
                    e.printStackTrace();
                }
                
                request.setAttribute("successMessage", "Order added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (order.getOrderID() > 0) {
                request.setAttribute("order", order);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/orderList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("order", order);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/orderList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
    }
}

