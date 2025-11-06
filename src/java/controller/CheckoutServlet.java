package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartItem;
import model.User;
import model.Order;
import model.OrderItem;
import model.Address;
import model.Payment;
import model.PaymentMethod;
import orderservice.IOrderService;
import orderservice.OrderService;
import orderitemservice.IOrderItemService;
import orderitemservice.OrderItemService;
import paymentservice.IPaymentService;
import paymentservice.PaymentService;
import addressservice.IAddressService;
import addressservice.AddressService;
import paymentmethodservice.IPaymentMethodService;
import paymentmethodservice.PaymentMethodService;
import productservice.IProductService;
import productservice.ProductService;
import model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servlet xử lý checkout và đặt hàng
 * URL mapping: /checkout
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    
    private IOrderService orderService;
    private IOrderItemService orderItemService;
    private IPaymentService paymentService;
    private IAddressService addressService;
    private IPaymentMethodService paymentMethodService;
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();
        orderItemService = new OrderItemService();
        paymentService = new PaymentService();
        addressService = new AddressService();
        paymentMethodService = new PaymentMethodService();
        productService = new ProductService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            // Chưa đăng nhập, redirect về trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login?redirect=/checkout");
            return;
        }
        
        Cart cart = (Cart) session.getAttribute("cart");
        
        if (cart == null || cart.isEmpty()) {
            // Giỏ hàng trống, redirect về giỏ hàng
            request.setAttribute("errorMessage", "Giỏ hàng của bạn đang trống");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        try {
            // Lấy danh sách địa chỉ của user
            List<Address> addresses = addressService.getAddressesByUser(currentUser.getUserID());
            
            // Lấy danh sách phương thức thanh toán
            List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods(false);
            
            // Validate sản phẩm trong giỏ hàng (kiểm tra stock)
            List<String> outOfStockProducts = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                Product product = productService.getProductById(item.getProductID());
                if (product == null || product.getStock() < item.getQuantity() || 
                    !"InStock".equals(product.getStockStatus())) {
                    outOfStockProducts.add(item.getProductName());
                }
            }
            
            if (!outOfStockProducts.isEmpty()) {
                request.setAttribute("errorMessage", "Một số sản phẩm không còn đủ hàng: " + String.join(", ", outOfStockProducts));
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            
            // Set attributes
            request.setAttribute("cart", cart);
            request.setAttribute("addresses", addresses);
            request.setAttribute("paymentMethods", paymentMethods);
            request.setAttribute("currentUser", currentUser);
            
            // Forward đến checkout page
            request.getRequestDispatcher("/views/store/checkout.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải trang thanh toán: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Cart cart = (Cart) session.getAttribute("cart");
        
        if (cart == null || cart.isEmpty()) {
            request.setAttribute("errorMessage", "Giỏ hàng của bạn đang trống");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        try {
            String action = request.getParameter("action");
            
            if ("placeOrder".equals(action)) {
                placeOrder(request, response, session, currentUser, cart);
            } else {
                response.sendRedirect(request.getContextPath() + "/checkout");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi đặt hàng: " + e.getMessage());
            request.getRequestDispatcher("/views/store/checkout.jsp").forward(request, response);
        }
    }
    
    /**
     * Xử lý đặt hàng
     */
    private void placeOrder(HttpServletRequest request, HttpServletResponse response, 
                           HttpSession session, User currentUser, Cart cart) 
            throws ServletException, IOException {
        
        try {
            // Parse parameters
            String shippingAddressIDParam = request.getParameter("shippingAddressID");
            String billingAddressIDParam = request.getParameter("billingAddressID");
            String paymentMethodIDParam = request.getParameter("paymentMethodID");
            String note = request.getParameter("note");
            
            int shippingAddressID = 0;
            int billingAddressID = 0;
            int paymentMethodID = 0;
            
            if (shippingAddressIDParam != null && !shippingAddressIDParam.trim().isEmpty()) {
                try {
                    shippingAddressID = Integer.parseInt(shippingAddressIDParam.trim());
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            
            if (billingAddressIDParam != null && !billingAddressIDParam.trim().isEmpty()) {
                try {
                    billingAddressID = Integer.parseInt(billingAddressIDParam.trim());
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            
            if (paymentMethodIDParam == null || paymentMethodIDParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn phương thức thanh toán");
                doGet(request, response);
                return;
            }
            
            try {
                paymentMethodID = Integer.parseInt(paymentMethodIDParam.trim());
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Phương thức thanh toán không hợp lệ");
                doGet(request, response);
                return;
            }
            
            // Validate lại sản phẩm trong giỏ hàng
            for (CartItem item : cart.getItems()) {
                Product product = productService.getProductById(item.getProductID());
                if (product == null || product.getStock() < item.getQuantity() || 
                    !"InStock".equals(product.getStockStatus())) {
                    request.setAttribute("errorMessage", "Sản phẩm " + item.getProductName() + " không còn đủ hàng");
                    doGet(request, response);
                    return;
                }
            }
            
            // Tạo Order
            Order order = new Order();
            order.setUserID(currentUser.getUserID());
            order.setBillingAddressID(billingAddressID > 0 ? billingAddressID : null);
            order.setShippingAddressID(shippingAddressID > 0 ? shippingAddressID : null);
            order.setOrderStatus("Pending");
            order.setTotalAmount(cart.getTotal());
            order.setNote(note != null ? note.trim() : null);
            order.setOrderDate(new Date());
            
            // Insert Order và lấy OrderID
            int orderID = orderService.addOrder(order);
            order.setOrderID(orderID);
            
            // Tạo OrderItems
            // Lưu ý: Database trigger trg_UpdateProductStock_AfterOrder sẽ tự động cập nhật stock
            for (CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderID(orderID);
                orderItem.setProductID(cartItem.getProductID());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getPrice());
                
                orderItemService.addOrderItem(orderItem);
            }
            
            // Tạo Payment
            Payment payment = new Payment();
            payment.setOrderID(orderID);
            payment.setPaymentMethodID(paymentMethodID);
            payment.setAmount(cart.getTotal());
            payment.setPaymentStatus("Pending");
            payment.setPaymentDate(new Date());
            
            paymentService.addPayment(payment);
            
            // Xóa giỏ hàng
            cart.clear();
            session.setAttribute("cart", cart);
            
            // Redirect đến trang xác nhận đơn hàng
            response.sendRedirect(request.getContextPath() + "/customer/orders?orderID=" + orderID + "&success=true");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi đặt hàng: " + e.getMessage());
            doGet(request, response);
        }
    }
}

