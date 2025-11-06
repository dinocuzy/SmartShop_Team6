package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Payment;
import model.Order;
import model.PaymentMethod;
import paymentservice.IPaymentService;
import paymentservice.PaymentService;
import orderservice.IOrderService;
import orderservice.OrderService;
import paymentmethodservice.IPaymentMethodService;
import paymentmethodservice.PaymentMethodService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho Payment
 * URL mapping: /admin/payments
 * Actions: list, add, edit, delete, save
 * 
 * NOTE: Payments được quản lý thông qua Orders (JOIN với Payments table để hiển thị payment status)
 * Servlet này được disable để gộp quản lý vào Orders
 */
// @WebServlet("/admin/payments") // Disabled - Payments được quản lý qua Orders
public class PaymentServlet extends HttpServlet {
    
    private IPaymentService paymentService;
    private IOrderService orderService;
    private IPaymentMethodService paymentMethodService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        paymentService = new PaymentService();
        orderService = new OrderService();
        paymentMethodService = new PaymentMethodService();
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
                    deletePayment(request, response);
                    break;
                case "list":
                default:
                    listPayments(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listPayments(request, response);
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
                    savePayment(request, response);
                    break;
                default:
                    listPayments(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String paymentID = request.getParameter("paymentID");
            if (paymentID != null && !paymentID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    private void listPayments(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Load danh sách payments
        List<Payment> payments = paymentService.getAllPayments();
        request.setAttribute("payments", payments);
        
        // Load danh sách orders và payment methods cho modal (chỉ lấy active cho dropdown)
        List<Order> orders = orderService.getAllOrders();
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods(false);
        request.setAttribute("orders", orders);
        request.setAttribute("paymentMethods", paymentMethods);
        
        request.getRequestDispatcher("/views/admin/paymentList.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Load danh sách orders và payment methods (chỉ lấy active cho dropdown)
        List<Order> orders = orderService.getAllOrders();
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods(false);
        request.setAttribute("orders", orders);
        request.setAttribute("paymentMethods", paymentMethods);
        
        request.setAttribute("action", "add");
        request.setAttribute("payment", new Payment());
        request.getRequestDispatcher("/views/admin/paymentList.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String paymentIDParam = request.getParameter("paymentID");
        
        if (paymentIDParam == null || paymentIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Payment ID is required");
            listPayments(request, response);
            return;
        }
        
        try {
            int paymentID = Integer.parseInt(paymentIDParam);
            Payment payment = paymentService.getPaymentById(paymentID);
            
            if (payment == null) {
                request.setAttribute("errorMessage", "Payment not found with ID: " + paymentID);
                listPayments(request, response);
                return;
            }
            
            // Load danh sách orders và payment methods (chỉ lấy active cho dropdown)
            List<Order> orders = orderService.getAllOrders();
            List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods(false);
            request.setAttribute("orders", orders);
            request.setAttribute("paymentMethods", paymentMethods);
            
            request.setAttribute("action", "edit");
            request.setAttribute("payment", payment);
            request.getRequestDispatcher("/views/admin/paymentList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Payment ID format");
            listPayments(request, response);
        }
    }
    
    private void deletePayment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String paymentIDParam = request.getParameter("paymentID");
        
        if (paymentIDParam == null || paymentIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Payment ID is required");
            listPayments(request, response);
            return;
        }
        
        try {
            int paymentID = Integer.parseInt(paymentIDParam);
            // Payment không có delete method trong Service, chỉ có thể update
            request.setAttribute("errorMessage", "Payment deletion is not allowed. Please update payment status instead.");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Payment ID format");
        }
        
        listPayments(request, response);
    }
    
    private void savePayment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String paymentIDParam = request.getParameter("paymentID");
        String orderIDParam = request.getParameter("orderID");
        String paymentMethodIDParam = request.getParameter("paymentMethodID");
        String amountParam = request.getParameter("amount");
        String paymentStatus = request.getParameter("paymentStatus");
        String paymentDateParam = request.getParameter("paymentDate");
        String transactionCode = request.getParameter("transactionCode");
        
        if (orderIDParam == null || orderIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Order ID is required");
            if (paymentIDParam != null && !paymentIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        if (paymentMethodIDParam == null || paymentMethodIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Payment Method ID is required");
            if (paymentIDParam != null && !paymentIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            paymentStatus = "Pending";
        }
        
        int orderID;
        int paymentMethodID;
        
        try {
            orderID = Integer.parseInt(orderIDParam);
            if (orderID <= 0) {
                throw new NumberFormatException("Order ID must be > 0");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Order ID format");
            if (paymentIDParam != null && !paymentIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        try {
            paymentMethodID = Integer.parseInt(paymentMethodIDParam);
            if (paymentMethodID <= 0) {
                throw new NumberFormatException("Payment Method ID must be > 0");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Payment Method ID format");
            if (paymentIDParam != null && !paymentIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        BigDecimal amount;
        try {
            if (amountParam != null && !amountParam.trim().isEmpty()) {
                amount = new BigDecimal(amountParam);
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Amount must be >= 0");
                }
            } else {
                amount = BigDecimal.ZERO;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid amount format");
            if (paymentIDParam != null && !paymentIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        Date paymentDate;
        if (paymentDateParam != null && !paymentDateParam.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                paymentDate = sdf.parse(paymentDateParam);
            } catch (ParseException e) {
                paymentDate = new Date();
            }
        } else {
            paymentDate = new Date();
        }
        
        Payment payment = new Payment();
        
        if (paymentIDParam != null && !paymentIDParam.isEmpty()) {
            try {
                int paymentID = Integer.parseInt(paymentIDParam);
                payment.setPaymentID(paymentID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Payment ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        payment.setOrderID(orderID);
        payment.setPaymentMethodID(paymentMethodID);
        payment.setAmount(amount);
        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentDate(paymentDate);
        payment.setTransactionCode(transactionCode != null ? transactionCode.trim() : null);
        
        try {
            if (payment.getPaymentID() > 0) {
                paymentService.updatePayment(payment);
                request.setAttribute("successMessage", "Payment updated successfully");
            } else {
                paymentService.addPayment(payment);
                request.setAttribute("successMessage", "Payment added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (payment.getPaymentID() > 0) {
                request.setAttribute("payment", payment);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/paymentList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("payment", payment);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/paymentList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/payments?action=list");
    }
}

