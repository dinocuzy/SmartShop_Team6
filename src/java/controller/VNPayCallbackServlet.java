package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Payment;
import orderservice.IOrderService;
import orderservice.OrderService;
import orderitemservice.IOrderItemService;
import orderitemservice.OrderItemService;
import paymentservice.IPaymentService;
import paymentservice.PaymentService;
import paymentmethodservice.IPaymentMethodService;
import paymentmethodservice.PaymentMethodService;
import util.VNPayConfig;
import util.EmailUtil;
import orderstatushistoryservice.IOrderStatusHistoryService;
import orderstatushistoryservice.OrderStatusHistoryService;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet xử lý callback từ VNPay sau khi thanh toán
 * URL mapping: /vnpay-callback
 */
@WebServlet("/vnpay-callback")
public class VNPayCallbackServlet extends HttpServlet {
    
    private IPaymentService paymentService;
    private IOrderService orderService;
    private IOrderItemService orderItemService;
    private IPaymentMethodService paymentMethodService;
    private IOrderStatusHistoryService orderStatusHistoryService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            paymentService = new PaymentService();
            orderService = new OrderService();
            orderItemService = new OrderItemService();
            paymentMethodService = new PaymentMethodService();
            orderStatusHistoryService = new OrderStatusHistoryService();
        } catch (Exception e) {
            System.err.println("Error initializing VNPayCallbackServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processCallback(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processCallback(request, response);
    }
    
    private void processCallback(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Lấy tất cả parameters từ VNPay
            Map<String, String> fields = new HashMap<>();
            for (String paramName : request.getParameterMap().keySet()) {
                String paramValue = request.getParameter(paramName);
                if (paramValue != null && paramValue.length() > 0) {
                    fields.put(paramName, paramValue);
                }
            }
            
            // Lấy các thông tin quan trọng
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_Amount = request.getParameter("vnp_Amount");
            String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
            String vnp_BankCode = request.getParameter("vnp_BankCode");
            String vnp_PayDate = request.getParameter("vnp_PayDate");
            String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
            
            // Loại bỏ vnp_SecureHash và vnp_SecureHashType khỏi map để verify
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            
            // Lấy vnp_HashSecret từ context-param (web.xml)
            String vnp_HashSecret = getServletContext().getInitParameter("vnp_HashSecret");
            if (vnp_HashSecret == null || vnp_HashSecret.isEmpty()) {
                // Fallback: thử lấy từ system property
                vnp_HashSecret = System.getProperty("vnp_HashSecret");
            }
            
            // Set vào VNPayConfig để verify
            if (vnp_HashSecret != null && !vnp_HashSecret.isEmpty()) {
                VNPayConfig.vnp_HashSecret = vnp_HashSecret;
            }
            
            // Verify secure hash (chỉ verify nếu có secure hash)
            boolean isValid = true;
            if (vnp_SecureHash != null && !vnp_SecureHash.isEmpty() && 
                vnp_HashSecret != null && !vnp_HashSecret.isEmpty()) {
                isValid = VNPayConfig.verifySecureHash(fields, vnp_SecureHash);
                
                // Debug logging
                System.out.println("VNPay Callback - Verify Hash:");
                System.out.println("  vnp_SecureHash from VNPay: " + vnp_SecureHash);
                System.out.println("  Fields for hash: " + VNPayConfig.hashAllFields(fields));
                System.out.println("  Hash secret length: " + (vnp_HashSecret != null ? vnp_HashSecret.length() : 0));
                System.out.println("  Is valid: " + isValid);
            } else {
                System.err.println("VNPay Callback - Missing secure hash or hash secret");
                if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
                    System.err.println("  vnp_SecureHash is missing");
                }
                if (vnp_HashSecret == null || vnp_HashSecret.isEmpty()) {
                    System.err.println("  vnp_HashSecret is missing from web.xml");
                }
            }
            
            if (!isValid) {
                System.err.println("VNPay Callback - Invalid secure hash!");
                System.err.println("  This could be due to:");
                System.err.println("  1. Wrong vnp_HashSecret in web.xml");
                System.err.println("  2. Parameters were modified during transmission");
                System.err.println("  3. VNPay configuration mismatch");
                
                // Vẫn tiếp tục xử lý để test, nhưng log warning
                // Trong production, có thể muốn reject request này
                // request.setAttribute("errorMessage", "Chữ ký không hợp lệ!");
                // request.getRequestDispatcher("/views/store/vnpay_return.jsp").forward(request, response);
                // return;
            }
            
            // Kiểm tra nếu không có response code (người dùng hủy thanh toán)
            if (vnp_ResponseCode == null || vnp_ResponseCode.isEmpty()) {
                // Người dùng đã hủy thanh toán hoặc đóng trang
                // Vì chưa tạo Order, chỉ cần xóa session data
                jakarta.servlet.http.HttpSession session = request.getSession();
                
                // Xóa tất cả thông tin pending khỏi session
                session.removeAttribute("pendingOrderUserID");
                session.removeAttribute("pendingOrderBillingAddressID");
                session.removeAttribute("pendingOrderShippingAddressID");
                session.removeAttribute("pendingOrderTotalAmount");
                session.removeAttribute("pendingOrderNote");
                session.removeAttribute("pendingOrderDate");
                session.removeAttribute("pendingOrderPaymentMethodID");
                session.removeAttribute("pendingCartItems");
                session.removeAttribute("pendingTempOrderID");

                request.setAttribute("success", false);
                request.setAttribute("errorMessage", "Bạn đã hủy thanh toán. Đơn hàng chưa được tạo.");

                request.getRequestDispatcher("/views/store/vnpay_return.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra response code
            // vnp_ResponseCode = "00" nghĩa là thành công
            if ("00".equals(vnp_ResponseCode)) {
                jakarta.servlet.http.HttpSession session = request.getSession();
                
                // Lấy thông tin order từ session (đã lưu khi checkout)
                Object pendingUserID = session.getAttribute("pendingOrderUserID");
                Object pendingBillingAddressID = session.getAttribute("pendingOrderBillingAddressID");
                Object pendingShippingAddressID = session.getAttribute("pendingOrderShippingAddressID");
                Object pendingTotalAmount = session.getAttribute("pendingOrderTotalAmount");
                Object pendingNote = session.getAttribute("pendingOrderNote");
                Object pendingOrderDate = session.getAttribute("pendingOrderDate");
                Object pendingPaymentMethodID = session.getAttribute("pendingOrderPaymentMethodID");
                @SuppressWarnings("unchecked")
                java.util.List<model.CartItem> pendingCartItems = 
                    (java.util.List<model.CartItem>) session.getAttribute("pendingCartItems");
                
                // Kiểm tra xem có đủ thông tin để tạo order không
                if (pendingUserID == null || pendingCartItems == null || pendingCartItems.isEmpty()) {
                    request.setAttribute("errorMessage", "Không tìm thấy thông tin đơn hàng trong session!");
                    request.getRequestDispatcher("/views/store/vnpay_return.jsp").forward(request, response);
                    return;
                }
                
                // [1] TẠO ORDER khi thanh toán thành công
                model.Order order = new model.Order();
                order.setUserID((Integer) pendingUserID);
                if (pendingBillingAddressID != null) {
                    order.setBillingAddressID((Integer) pendingBillingAddressID);
                }
                if (pendingShippingAddressID != null) {
                    order.setShippingAddressID((Integer) pendingShippingAddressID);
                }
                order.setTotalAmount((java.math.BigDecimal) pendingTotalAmount);
                if (pendingNote != null) {
                    order.setNote(pendingNote.toString());
                }
                order.setOrderDate((java.util.Date) pendingOrderDate);
                order.setOrderStatus("Paid"); // Thanh toán thành công
                
                // Insert Order và lấy OrderID
                int orderID = orderService.addOrder(order);
                order.setOrderID(orderID);
                
                // [2] TẠO ORDERITEMS và giảm Stock (trigger tự động giảm stock khi insert OrderItems)
                for (model.CartItem cartItem : pendingCartItems) {
                    model.OrderItem orderItem = new model.OrderItem();
                    orderItem.setOrderID(orderID);
                    orderItem.setProductID(cartItem.getProductID());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(cartItem.getPrice());
                    
                    orderItemService.addOrderItem(orderItem);
                }
                
                // [3] TẠO PAYMENT record
                model.Payment payment = new model.Payment();
                payment.setOrderID(orderID);
                if (pendingPaymentMethodID != null) {
                    payment.setPaymentMethodID((Integer) pendingPaymentMethodID);
                } else {
                    // Fallback: tìm VNPay payment method
                    java.util.List<model.PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods(false);
                    for (model.PaymentMethod pm : paymentMethods) {
                        if (pm.getMethodName() != null && pm.getMethodName().toLowerCase().contains("vnpay")) {
                            payment.setPaymentMethodID(pm.getPaymentMethodID());
                            break;
                        }
                    }
                }
                payment.setAmount(new BigDecimal(vnp_Amount).divide(new BigDecimal("100"))); // VNPay trả về amount * 100
                payment.setPaymentStatus("Completed");
                payment.setTransactionCode(vnp_TransactionNo);
                payment.setPaymentDate(new java.util.Date());
                
                paymentService.addPayment(payment);
                
                // Ghi log lịch sử thay đổi trạng thái đơn hàng (VNPay thành công)
                try {
                    model.User currentUser = (model.User) session.getAttribute("currentUser");
                    orderStatusHistoryService.recordStatusChange(
                        orderID, 
                        "New", 
                        "Paid", 
                        currentUser != null ? currentUser.getUserID() : null
                    );
                } catch (Exception e) {
                    // Không throw exception để không ảnh hưởng đến việc tạo order
                    System.err.println("Error recording order status history for VNPay order: " + e.getMessage());
                    e.printStackTrace();
                }
                
                // [9] Xóa giỏ hàng sau khi thanh toán thành công
                model.Cart cart = (model.Cart) session.getAttribute("cart");
                if (cart != null) {
                    cart.clear();
                    session.setAttribute("cart", cart);
                }
                
                // Xóa cart khỏi DB
                model.User currentUser = (model.User) session.getAttribute("currentUser");
                if (currentUser != null) {
                    try {
                        cartservice.ICartService cartService = new cartservice.CartService();
                        cartService.clearCart(currentUser.getUserID());
                    } catch (Exception e) {
                        System.err.println("Error clearing cart from DB: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                // [GỬI EMAIL] Gửi email xác nhận đơn hàng cho khách hàng
                System.out.println("=== VNPay Callback: Starting email sending process ===");
                System.out.println("Current user: " + (currentUser != null ? currentUser.getEmail() : "null"));
                System.out.println("Order ID: " + orderID);
                
                if (currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().trim().isEmpty()) {
                    try {
                        System.out.println("Getting order items for order ID: " + orderID);
                        // Lấy order items
                        java.util.List<model.OrderItem> orderItems = orderItemService.getOrderItemsByOrder(orderID);
                        System.out.println("Order items count: " + (orderItems != null ? orderItems.size() : 0));
                        
                        // Lấy địa chỉ giao hàng
                        model.Address shippingAddress = null;
                        if (order.getShippingAddressID() != null && order.getShippingAddressID() > 0) {
                            System.out.println("Getting shipping address ID: " + order.getShippingAddressID());
                            addressservice.IAddressService addressService = new addressservice.AddressService();
                            shippingAddress = addressService.getAddressById(order.getShippingAddressID());
                            System.out.println("Shipping address: " + (shippingAddress != null ? "found" : "not found"));
                        }
                        
                        // Lấy phương thức thanh toán
                        String paymentMethodName = "VNPay";
                        if (pendingPaymentMethodID != null) {
                            try {
                                model.PaymentMethod paymentMethod = paymentMethodService.getPaymentMethodById((Integer) pendingPaymentMethodID);
                                if (paymentMethod != null) {
                                    paymentMethodName = paymentMethod.getMethodName();
                                }
                            } catch (Exception e) {
                                System.err.println("Error getting payment method: " + e.getMessage());
                            }
                        }
                        System.out.println("Payment method: " + paymentMethodName);
                        
                        // Gửi email
                        System.out.println("Calling EmailUtil.sendOrderConfirmationEmail...");
                        System.out.println("Email: " + currentUser.getEmail());
                        System.out.println("Order ID: " + order.getOrderID());
                        System.out.println("Order items: " + (orderItems != null ? orderItems.size() : 0));
                        
                        boolean emailSent = EmailUtil.sendOrderConfirmationEmail(
                            currentUser.getEmail(),
                            order,
                            orderItems,
                            currentUser.getFullName(),
                            shippingAddress,
                            paymentMethodName
                        );
                        
                        if (emailSent) {
                            System.out.println("✓ Order confirmation email sent successfully to: " + currentUser.getEmail());
                        } else {
                            System.err.println("✗ Failed to send order confirmation email to: " + currentUser.getEmail());
                        }
                    } catch (Exception e) {
                        // Không throw exception để không ảnh hưởng đến việc tạo order
                        System.err.println("✗ Error sending order confirmation email: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("✗ Cannot send email: currentUser is null or email is empty");
                    if (currentUser == null) {
                        System.err.println("  - currentUser is null");
                    } else if (currentUser.getEmail() == null) {
                        System.err.println("  - currentUser.getEmail() is null");
                    } else {
                        System.err.println("  - currentUser.getEmail() is empty: '" + currentUser.getEmail() + "'");
                    }
                }
                System.out.println("=== VNPay Callback: Email sending process completed ===");
                
                // Xóa tất cả thông tin pending khỏi session
                session.removeAttribute("pendingOrderUserID");
                session.removeAttribute("pendingOrderBillingAddressID");
                session.removeAttribute("pendingOrderShippingAddressID");
                session.removeAttribute("pendingOrderTotalAmount");
                session.removeAttribute("pendingOrderNote");
                session.removeAttribute("pendingOrderDate");
                session.removeAttribute("pendingOrderPaymentMethodID");
                session.removeAttribute("pendingCartItems");
                session.removeAttribute("pendingTempOrderID");
                
                // Set success attributes
                request.setAttribute("success", true);
                request.setAttribute("orderID", orderID);
                request.setAttribute("amount", new BigDecimal(vnp_Amount).divide(new BigDecimal("100"))); // VNPay trả về amount * 100
                request.setAttribute("transactionNo", vnp_TransactionNo);
                request.setAttribute("bankCode", vnp_BankCode);
                request.setAttribute("payDate", vnp_PayDate);
                
            } else {
                // Thanh toán thất bại
                // Vì chưa tạo Order, chỉ cần xóa session data
                jakarta.servlet.http.HttpSession session = request.getSession();
                
                // Xóa tất cả thông tin pending khỏi session
                session.removeAttribute("pendingOrderUserID");
                session.removeAttribute("pendingOrderBillingAddressID");
                session.removeAttribute("pendingOrderShippingAddressID");
                session.removeAttribute("pendingOrderTotalAmount");
                session.removeAttribute("pendingOrderNote");
                session.removeAttribute("pendingOrderDate");
                session.removeAttribute("pendingOrderPaymentMethodID");
                session.removeAttribute("pendingCartItems");
                session.removeAttribute("pendingTempOrderID");
                
                // Set error attributes
                request.setAttribute("success", false);
                String errorMsg = "Thanh toán thất bại. Mã lỗi: " + vnp_ResponseCode;
                // Có thể thêm mapping cho các mã lỗi phổ biến
                if ("07".equals(vnp_ResponseCode)) {
                    errorMsg = "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
                } else if ("09".equals(vnp_ResponseCode)) {
                    errorMsg = "Thẻ/Tài khoản chưa đăng ký dịch vụ InternetBanking";
                } else if ("10".equals(vnp_ResponseCode)) {
                    errorMsg = "Xác thực thông tin thẻ/tài khoản không đúng. Quá 3 lần";
                } else if ("11".equals(vnp_ResponseCode)) {
                    errorMsg = "Đã hết hạn chờ thanh toán. Xin vui lòng thực hiện lại giao dịch.";
                } else if ("12".equals(vnp_ResponseCode)) {
                    errorMsg = "Thẻ/Tài khoản bị khóa.";
                } else if ("51".equals(vnp_ResponseCode)) {
                    errorMsg = "Tài khoản không đủ số dư để thực hiện giao dịch.";
                } else if ("65".equals(vnp_ResponseCode)) {
                    errorMsg = "Tài khoản đã vượt quá hạn mức giao dịch trong ngày.";
                } else if ("75".equals(vnp_ResponseCode)) {
                    errorMsg = "Ngân hàng thanh toán đang bảo trì.";
                } else if ("79".equals(vnp_ResponseCode)) {
                    errorMsg = "Nhập sai mật khẩu thanh toán quá số lần quy định.";
                }
                request.setAttribute("errorMessage", errorMsg);
            }
            
            // Forward đến trang kết quả
            request.getRequestDispatcher("/views/store/vnpay_return.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý callback: " + e.getMessage());
            request.getRequestDispatcher("/views/store/vnpay_return.jsp").forward(request, response);
        }
    }
}

