package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PaymentMethod;
import paymentmethodservice.IPaymentMethodService;
import paymentmethodservice.PaymentMethodService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho PaymentMethod
 * URL mapping: /admin/payment-methods
 * Actions: list, add, edit, delete, save
 */
@WebServlet("/admin/payment-methods")
public class PaymentMethodServlet extends HttpServlet {
    
    private IPaymentMethodService paymentMethodService;
    
    @Override
    public void init() throws ServletException {
        super.init();
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
                    deletePaymentMethod(request, response);
                    break;
                case "list":
                default:
                    listPaymentMethods(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listPaymentMethods(request, response);
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
                    savePaymentMethod(request, response);
                    break;
                default:
                    listPaymentMethods(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String paymentMethodID = request.getParameter("paymentMethodID");
            if (paymentMethodID != null && !paymentMethodID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    private void listPaymentMethods(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy tham số showAll từ request
        String showAllParam = request.getParameter("showAll");
        boolean includeInactive = "true".equalsIgnoreCase(showAllParam);
        
        // Lấy danh sách payment methods
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods(includeInactive);
        
        // Set attributes
        request.setAttribute("paymentMethods", paymentMethods);
        request.setAttribute("showAll", includeInactive);
        
        request.getRequestDispatcher("/views/admin/paymentMethodList.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("action", "add");
        request.setAttribute("paymentMethod", new PaymentMethod());
        request.getRequestDispatcher("/views/admin/paymentMethodList.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String paymentMethodIDParam = request.getParameter("paymentMethodID");
        
        if (paymentMethodIDParam == null || paymentMethodIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Payment Method ID is required");
            listPaymentMethods(request, response);
            return;
        }
        
        try {
            int paymentMethodID = Integer.parseInt(paymentMethodIDParam);
            PaymentMethod paymentMethod = paymentMethodService.getPaymentMethodById(paymentMethodID);
            
            if (paymentMethod == null) {
                request.setAttribute("errorMessage", "Payment Method not found with ID: " + paymentMethodID);
                listPaymentMethods(request, response);
                return;
            }
            
            request.setAttribute("action", "edit");
            request.setAttribute("paymentMethod", paymentMethod);
            request.getRequestDispatcher("/views/admin/paymentMethodList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Payment Method ID format");
            listPaymentMethods(request, response);
        }
    }
    
    private void deletePaymentMethod(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String paymentMethodIDParam = request.getParameter("paymentMethodID");
        
        if (paymentMethodIDParam == null || paymentMethodIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Payment Method ID is required");
            listPaymentMethods(request, response);
            return;
        }
        
        try {
            int paymentMethodID = Integer.parseInt(paymentMethodIDParam);
            paymentMethodService.deletePaymentMethod(paymentMethodID);
            request.setAttribute("successMessage", "Payment Method deleted successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Payment Method ID format");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        listPaymentMethods(request, response);
    }
    
    private void savePaymentMethod(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String paymentMethodIDParam = request.getParameter("paymentMethodID");
        String methodName = request.getParameter("methodName");
        String provider = request.getParameter("provider");
        String isActiveParam = request.getParameter("isActive");
        
        if (methodName == null || methodName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Method name is required");
            if (paymentMethodIDParam != null && !paymentMethodIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        PaymentMethod paymentMethod = new PaymentMethod();
        
        if (paymentMethodIDParam != null && !paymentMethodIDParam.isEmpty()) {
            try {
                int paymentMethodID = Integer.parseInt(paymentMethodIDParam);
                paymentMethod.setPaymentMethodID(paymentMethodID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Payment Method ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        paymentMethod.setMethodName(methodName.trim());
        paymentMethod.setProvider(provider != null ? provider.trim() : null);
        paymentMethod.setActive(isActiveParam != null && isActiveParam.equals("true"));
        
        try {
            if (paymentMethod.getPaymentMethodID() > 0) {
                paymentMethodService.updatePaymentMethod(paymentMethod);
                request.setAttribute("successMessage", "Payment Method updated successfully");
            } else {
                paymentMethodService.addPaymentMethod(paymentMethod);
                request.setAttribute("successMessage", "Payment Method added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (paymentMethod.getPaymentMethodID() > 0) {
                request.setAttribute("paymentMethod", paymentMethod);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/paymentMethodList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("paymentMethod", paymentMethod);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/paymentMethodList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/payment-methods?action=list");
    }
}

