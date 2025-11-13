package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import listener.OnlineUserListener;
import model.Cart;
import model.User;
import model.Address;
import model.PaymentMethod;
import model.Product;
import addressservice.IAddressService;
import addressservice.AddressService;
import paymentmethodservice.IPaymentMethodService;
import paymentmethodservice.PaymentMethodService;
import productservice.IProductService;
import productservice.ProductService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * API Servlet để xử lý các request từ chatbot và các component khác
 * URL mapping: /api/*
 */
@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {
    
    private IAddressService addressService;
    private IPaymentMethodService paymentMethodService;
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            addressService = new AddressService();
            paymentMethodService = new PaymentMethodService();
            productService = new ProductService();
        } catch (Exception e) {
            System.err.println("Error initializing ApiServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            HttpSession session = request.getSession(false);
            User currentUser = null;
            if (session != null) {
                currentUser = (User) session.getAttribute("currentUser");
            }
            
            if (pathInfo.equals("/cart/check")) {
                // Kiểm tra xem user có sản phẩm trong giỏ hàng không
                boolean hasItems = false;
                boolean isLoggedIn = currentUser != null;
                
                if (isLoggedIn && session != null) {
                    Cart cart = (Cart) session.getAttribute("cart");
                    if (cart != null && !cart.isEmpty()) {
                        hasItems = true;
                    }
                }
                
                jsonResponse.addProperty("hasItems", hasItems);
                jsonResponse.addProperty("isLoggedIn", isLoggedIn);
                jsonResponse.addProperty("success", true);
                
            } else if (pathInfo.equals("/addresses")) {
                // Lấy danh sách địa chỉ của user
                if (currentUser == null) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "User not logged in");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    try {
                        List<Address> addresses = addressService.getAddressesByUser(currentUser.getUserID());
                        jsonResponse.addProperty("success", true);
                        jsonResponse.add("addresses", gson.toJsonTree(addresses));
                    } catch (Exception e) {
                        System.err.println("Error getting addresses: " + e.getMessage());
                        e.printStackTrace();
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Error getting addresses: " + e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                }
                
            } else if (pathInfo.equals("/payment-methods")) {
                // Lấy danh sách phương thức thanh toán
                try {
                    List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods(false);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.add("paymentMethods", gson.toJsonTree(paymentMethods));
                } catch (Exception e) {
                    System.err.println("Error getting payment methods: " + e.getMessage());
                    e.printStackTrace();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Error getting payment methods: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                
            } else if (pathInfo.equals("/online/count")) {
                // Lấy số người đang online từ listener (ServletContext)
                try {
                    ServletContext context = getServletContext();
                    int onlineCount = OnlineUserListener.getOnlineUserCount(context);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("count", onlineCount);
                } catch (Exception e) {
                    System.err.println("Error getting online count: " + e.getMessage());
                    e.printStackTrace();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Error getting online count: " + e.getMessage());
                    jsonResponse.addProperty("count", 0);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("error", "Unknown API endpoint");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            
        } catch (Exception e) {
            System.err.println("Error in ApiServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Internal server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            // No POST endpoints currently implemented
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "No POST endpoints available");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error in ApiServlet POST: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Internal server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}

