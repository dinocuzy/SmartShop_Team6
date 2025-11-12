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
import model.CompareListItem;
import model.Product;
import addressservice.IAddressService;
import addressservice.AddressService;
import paymentmethodservice.IPaymentMethodService;
import paymentmethodservice.PaymentMethodService;
import comparelistservice.ICompareListService;
import comparelistservice.CompareListService;
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
    private ICompareListService compareListService;
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            addressService = new AddressService();
            paymentMethodService = new PaymentMethodService();
            compareListService = new CompareListService();
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
                
            } else if (pathInfo.equals("/compare/get")) {
                // Lấy danh sách so sánh
                try {
                    if (currentUser != null) {
                        // Lấy từ database
                        List<CompareListItem> items = compareListService.getUserCompareList(currentUser.getUserID());
                        JsonArray productsArray = new JsonArray();
                        for (CompareListItem item : items) {
                            Product product = productService.getProductById(item.getProductID());
                            if (product != null) {
                                JsonObject productObj = new JsonObject();
                                productObj.addProperty("productID", product.getProductID());
                                productObj.addProperty("productName", product.getProductName());
                                productObj.addProperty("price", product.getPrice() != null ? product.getPrice().doubleValue() : 0);
                                productObj.addProperty("imageUrl", product.getImageUrl() != null ? product.getImageUrl() : "");
                                productObj.addProperty("stock", product.getStock());
                                productObj.addProperty("stockStatus", product.getStockStatus() != null ? product.getStockStatus() : "");
                                productsArray.add(productObj);
                            }
                        }
                        jsonResponse.addProperty("success", true);
                        jsonResponse.add("products", productsArray);
                        jsonResponse.addProperty("count", items.size());
                    } else {
                        // Trả về empty list cho user chưa đăng nhập (client sẽ dùng localStorage)
                        jsonResponse.addProperty("success", true);
                        jsonResponse.add("products", new JsonArray());
                        jsonResponse.addProperty("count", 0);
                    }
                } catch (Exception e) {
                    System.err.println("Error getting compare list: " + e.getMessage());
                    e.printStackTrace();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Error getting compare list: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                
            } else if (pathInfo.equals("/compare/check")) {
                // Kiểm tra sản phẩm có trong danh sách so sánh không
                String productIDParam = request.getParameter("productID");
                try {
                    if (productIDParam != null && !productIDParam.trim().isEmpty()) {
                        int productID = Integer.parseInt(productIDParam.trim());
                        boolean inList = false;
                        
                        if (currentUser != null) {
                            inList = compareListService.isProductInList(currentUser.getUserID(), productID);
                        }
                        
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("inList", inList);
                    } else {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "productID parameter is required");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } catch (Exception e) {
                    System.err.println("Error checking compare list: " + e.getMessage());
                    e.printStackTrace();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Error checking compare list: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                
            } else if (pathInfo.equals("/online/count")) {
                // Lấy số người đang online
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
            HttpSession session = request.getSession(false);
            User currentUser = null;
            if (session != null) {
                currentUser = (User) session.getAttribute("currentUser");
            }
            
            if (pathInfo.equals("/compare/add")) {
                // Thêm sản phẩm vào danh sách so sánh
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String requestBody = sb.toString();
                
                JsonObject requestJson = gson.fromJson(requestBody, JsonObject.class);
                String productIDParam = requestJson.has("productID") ? requestJson.get("productID").getAsString() : null;
                
                if (productIDParam == null || productIDParam.trim().isEmpty()) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "productID is required");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    try {
                        int productID = Integer.parseInt(productIDParam.trim());
                        
                        if (currentUser != null) {
                            // Lưu vào database
                            boolean success = compareListService.addProduct(currentUser.getUserID(), productID);
                            if (success) {
                                jsonResponse.addProperty("success", true);
                                jsonResponse.addProperty("message", "Đã thêm sản phẩm vào danh sách so sánh");
                            } else {
                                jsonResponse.addProperty("success", false);
                                jsonResponse.addProperty("error", "Không thể thêm sản phẩm vào danh sách so sánh");
                            }
                        } else {
                            // Trả về success để client lưu vào localStorage
                            jsonResponse.addProperty("success", true);
                            jsonResponse.addProperty("message", "Đã thêm sản phẩm vào danh sách so sánh (localStorage)");
                            jsonResponse.addProperty("useLocalStorage", true);
                        }
                    } catch (NumberFormatException e) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Invalid productID format");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } catch (Exception e) {
                        System.err.println("Error adding to compare list: " + e.getMessage());
                        e.printStackTrace();
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Error adding to compare list: " + e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                }
                
            } else if (pathInfo.equals("/compare/remove")) {
                // Xóa sản phẩm khỏi danh sách so sánh
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String requestBody = sb.toString();
                
                JsonObject requestJson = gson.fromJson(requestBody, JsonObject.class);
                String productIDParam = requestJson.has("productID") ? requestJson.get("productID").getAsString() : null;
                
                if (productIDParam == null || productIDParam.trim().isEmpty()) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "productID is required");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    try {
                        int productID = Integer.parseInt(productIDParam.trim());
                        
                        if (currentUser != null) {
                            // Xóa từ database
                            boolean success = compareListService.removeProduct(currentUser.getUserID(), productID);
                            if (success) {
                                jsonResponse.addProperty("success", true);
                                jsonResponse.addProperty("message", "Đã xóa sản phẩm khỏi danh sách so sánh");
                            } else {
                                jsonResponse.addProperty("success", false);
                                jsonResponse.addProperty("error", "Không thể xóa sản phẩm khỏi danh sách so sánh");
                            }
                        } else {
                            // Trả về success để client xóa từ localStorage
                            jsonResponse.addProperty("success", true);
                            jsonResponse.addProperty("message", "Đã xóa sản phẩm khỏi danh sách so sánh (localStorage)");
                            jsonResponse.addProperty("useLocalStorage", true);
                        }
                    } catch (NumberFormatException e) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Invalid productID format");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } catch (Exception e) {
                        System.err.println("Error removing from compare list: " + e.getMessage());
                        e.printStackTrace();
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Error removing from compare list: " + e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                }
                
            } else if (pathInfo.equals("/compare/sync")) {
                // Đồng bộ localStorage với database khi user đăng nhập
                if (currentUser == null) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "User not logged in");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    BufferedReader reader = request.getReader();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String requestBody = sb.toString();
                    
                    JsonObject requestJson = gson.fromJson(requestBody, JsonObject.class);
                    JsonArray productIDsArray = requestJson.has("productIDs") ? requestJson.getAsJsonArray("productIDs") : null;
                    
                    if (productIDsArray != null) {
                        try {
                            int syncedCount = 0;
                            for (int i = 0; i < productIDsArray.size(); i++) {
                                int productID = productIDsArray.get(i).getAsInt();
                                if (compareListService.addProduct(currentUser.getUserID(), productID)) {
                                    syncedCount++;
                                }
                            }
                            jsonResponse.addProperty("success", true);
                            jsonResponse.addProperty("syncedCount", syncedCount);
                            jsonResponse.addProperty("message", "Đã đồng bộ " + syncedCount + " sản phẩm vào danh sách so sánh");
                        } catch (Exception e) {
                            System.err.println("Error syncing compare list: " + e.getMessage());
                            e.printStackTrace();
                            jsonResponse.addProperty("success", false);
                            jsonResponse.addProperty("error", "Error syncing compare list: " + e.getMessage());
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "productIDs array is required");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                
            } else {
                // Fallback to GET for unknown endpoints
                doGet(request, response);
                return;
            }
            
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

