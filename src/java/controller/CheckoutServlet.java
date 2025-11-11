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
import orderstatushistoryservice.IOrderStatusHistoryService;
import orderstatushistoryservice.OrderStatusHistoryService;
import addressservice.IAddressService;
import addressservice.AddressService;
import paymentmethodservice.IPaymentMethodService;
import paymentmethodservice.PaymentMethodService;
import productservice.IProductService;
import productservice.ProductService;
import model.Product;
import util.VNPayUtil;
import util.VNPayConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servlet xử lý checkout và đặt hàng URL mapping: /checkout
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private IOrderService orderService;
    private IOrderItemService orderItemService;
    private IPaymentService paymentService;
    private IAddressService addressService;
    private IPaymentMethodService paymentMethodService;
    private IProductService productService;
    private IOrderStatusHistoryService orderStatusHistoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            orderService = new OrderService();
            orderItemService = new OrderItemService();
            paymentService = new PaymentService();
            addressService = new AddressService();
            paymentMethodService = new PaymentMethodService();
            productService = new ProductService();
            orderStatusHistoryService = new OrderStatusHistoryService();
        } catch (Exception e) {
            System.err.println("Error initializing CheckoutServlet: " + e.getMessage());
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
                if (product == null) {
                    outOfStockProducts.add(item.getProductName() + " (không tồn tại)");
                } else if (item.getQuantity() > product.getStock()) {
                    outOfStockProducts.add(item.getProductName() + " (yêu cầu: " + item.getQuantity() + ", có: " + product.getStock() + ")");
                } else if (!"InStock".equals(product.getStockStatus())) {
                    outOfStockProducts.add(item.getProductName() + " (hết hàng)");
                }
            }

            if (!outOfStockProducts.isEmpty()) {
                request.setAttribute("errorMessage", "Một số sản phẩm không đủ hàng: " + String.join(", ", outOfStockProducts));
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
                if (product == null) {
                    request.setAttribute("errorMessage", "Sản phẩm " + item.getProductName() + " không tồn tại");
                    doGet(request, response);
                    return;
                }
                // Kiểm tra số lượng phải <= stock
                if (item.getQuantity() > product.getStock()) {
                    request.setAttribute("errorMessage", "Sản phẩm " + item.getProductName() + " không đủ hàng. Số lượng trong kho: " + product.getStock() + ", số lượng yêu cầu: " + item.getQuantity());
                    doGet(request, response);
                    return;
                }
                // Kiểm tra trạng thái stock
                if (!"InStock".equals(product.getStockStatus())) {
                    request.setAttribute("errorMessage", "Sản phẩm " + item.getProductName() + " không còn hàng");
                    doGet(request, response);
                    return;
                }
            }

            // Kiểm tra phương thức thanh toán
            PaymentMethod paymentMethod = paymentMethodService.getPaymentMethodById(paymentMethodID);
            boolean isVNPay = paymentMethod != null && paymentMethod.getMethodName() != null
                    && paymentMethod.getMethodName().toLowerCase().contains("vnpay");

            if (isVNPay) {
                // [5B] VNPay: KHÔNG tạo Order ngay, chỉ lưu thông tin vào session
                // Order sẽ được tạo khi thanh toán thành công

                // Lưu thông tin order vào session để tạo sau khi thanh toán thành công
                session.setAttribute("pendingOrderUserID", currentUser.getUserID());
                session.setAttribute("pendingOrderBillingAddressID", billingAddressID > 0 ? billingAddressID : null);
                session.setAttribute("pendingOrderShippingAddressID", shippingAddressID > 0 ? shippingAddressID : null);
                session.setAttribute("pendingOrderTotalAmount", cart.getTotal());
                session.setAttribute("pendingOrderNote", note != null ? note.trim() : null);
                session.setAttribute("pendingOrderDate", new Date());
                session.setAttribute("pendingOrderPaymentMethodID", paymentMethodID);
                session.setAttribute("pendingCartItems", new ArrayList<>(cart.getItems())); // Copy cart items

                // Tạo temporary orderID để gửi cho VNPay (dùng timestamp để unique)
                // OrderID thực sẽ được tạo khi thanh toán thành công
                int tempOrderID = (int) System.currentTimeMillis() % 100000000; // 8 chữ số
                session.setAttribute("pendingTempOrderID", tempOrderID);

            } else {
                // [5A] COD: Tạo Order và các bảng liên quan ngay
                Order order = new Order();
                order.setUserID(currentUser.getUserID());
                order.setBillingAddressID(billingAddressID > 0 ? billingAddressID : null);
                order.setShippingAddressID(shippingAddressID > 0 ? shippingAddressID : null);
                order.setTotalAmount(cart.getTotal());
                order.setNote(note != null ? note.trim() : null);
                order.setOrderDate(new Date());
                order.setOrderStatus("Pending"); // COD: chờ xác nhận

                // Insert Order và lấy OrderID
                int orderID = orderService.addOrder(order);
                order.setOrderID(orderID);

                // Tạo OrderItems - Database trigger trg_UpdateProductStock_AfterOrder sẽ tự động giảm stock
                for (CartItem cartItem : cart.getItems()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderID(orderID);
                    orderItem.setProductID(cartItem.getProductID());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(cartItem.getPrice());

                    orderItemService.addOrderItem(orderItem);
                }

                // Tạo Payment cho COD
                Payment payment = new Payment();
                payment.setOrderID(orderID);
                payment.setPaymentMethodID(paymentMethodID);
                payment.setAmount(cart.getTotal());
                payment.setPaymentStatus("Pending");
                payment.setPaymentDate(new Date());
                payment.setTransactionCode("COD" + System.currentTimeMillis());

                paymentService.addPayment(payment);

                // Ghi log lịch sử thay đổi trạng thái đơn hàng (COD)
                try {
                    orderStatusHistoryService.recordStatusChange(
                            orderID,
                            "New",
                            "Pending",
                            currentUser != null ? currentUser.getUserID() : null
                    );
                } catch (Exception e) {
                    // Không throw exception để không ảnh hưởng đến việc tạo order
                    System.err.println("Error recording order status history for COD order: " + e.getMessage());
                    e.printStackTrace();
                }

                // [XÓA CART] Xóa giỏ hàng sau khi đặt hàng COD thành công
                cart.clear();
                session.setAttribute("cart", cart);

                // Xóa cart khỏi DB nếu đã đăng nhập
                if (currentUser != null) {
                    try {
                        cartservice.ICartService cartService = new cartservice.CartService();
                        cartService.clearCart(currentUser.getUserID());
                    } catch (Exception e) {
                        System.err.println("Error clearing cart from DB: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Redirect đến trang xác nhận đơn hàng
                response.sendRedirect(request.getContextPath() + "/customer/orders?orderID=" + orderID + "&success=true");
                return;
            }

            // Xử lý VNPay (chỉ chạy nếu isVNPay = true)
            // Lấy VNPay config từ context parameters (web.xml)
            String vnp_TmnCode = getServletContext().getInitParameter("vnp_TmnCode");
            String vnp_HashSecret = getServletContext().getInitParameter("vnp_HashSecret");
            String vnp_PayUrl = getServletContext().getInitParameter("vnp_PayUrl");

            // Fallback: Nếu không có trong context-param, thử lấy từ system properties
            if ((vnp_TmnCode == null || vnp_TmnCode.isEmpty())
                    && System.getProperty("vnp_TmnCode") != null) {
                vnp_TmnCode = System.getProperty("vnp_TmnCode");
            }
            if ((vnp_HashSecret == null || vnp_HashSecret.isEmpty())
                    && System.getProperty("vnp_HashSecret") != null) {
                vnp_HashSecret = System.getProperty("vnp_HashSecret");
            }

            // Build return URL
            String vnp_ReturnUrl = request.getRequestURL().toString()
                    .replace(request.getRequestURI(), "")
                    + request.getContextPath() + "/vnpay-callback";

            // Nếu có custom PayUrl thì dùng, không thì dùng default
            if (vnp_PayUrl != null && !vnp_PayUrl.isEmpty()) {
                VNPayConfig.vnp_PayUrl = vnp_PayUrl;
            }

            if (vnp_TmnCode != null && !vnp_TmnCode.isEmpty()
                    && vnp_HashSecret != null && !vnp_HashSecret.isEmpty()) {

                // Set VNPay config
                VNPayConfig.vnp_TmnCode = vnp_TmnCode;
                VNPayConfig.vnp_HashSecret = vnp_HashSecret;
                VNPayConfig.vnp_ReturnUrl = vnp_ReturnUrl;

                // Tạo payment URL với temporary orderID
                int tempOrderID = (Integer) session.getAttribute("pendingTempOrderID");
                String orderInfo = "Thanh toan don hang #" + tempOrderID;
                String ipAddr = VNPayUtil.getIpAddress(request);
                long amount = cart.getTotal().longValue();

                String vnpayUrl = VNPayUtil.createPaymentUrl(tempOrderID, amount, orderInfo, vnp_ReturnUrl, ipAddr);

                // Redirect đến VNPay
                response.sendRedirect(vnpayUrl);
                return;
            } else {
                // Nếu chưa cấu hình VNPay, xóa session và thông báo lỗi
                session.removeAttribute("pendingOrderUserID");
                session.removeAttribute("pendingOrderBillingAddressID");
                session.removeAttribute("pendingOrderShippingAddressID");
                session.removeAttribute("pendingOrderTotalAmount");
                session.removeAttribute("pendingOrderNote");
                session.removeAttribute("pendingOrderDate");
                session.removeAttribute("pendingOrderPaymentMethodID");
                session.removeAttribute("pendingCartItems");
                session.removeAttribute("pendingTempOrderID");
                request.setAttribute("errorMessage", "VNPay chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
                doGet(request, response);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi đặt hàng: " + e.getMessage());
            doGet(request, response);
        }
    }
}
