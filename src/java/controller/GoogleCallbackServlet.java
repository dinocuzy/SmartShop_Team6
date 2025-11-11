package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import oauthservice.IOAuthService;
import oauthservice.OAuthService;
import cartservice.ICartService;
import cartservice.CartService;
import model.Cart;
import model.CartItem;
import model.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Servlet xử lý callback từ Google OAuth
 * URL mapping: /auth/google/callback
 */
@WebServlet("/auth/google/callback")
public class GoogleCallbackServlet extends HttpServlet {
    
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    
    private IOAuthService oauthService;
    private ICartService cartService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        oauthService = new OAuthService();
        cartService = new CartService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String error = request.getParameter("error");
        
        // Kiểm tra error
        if (error != null) {
            request.setAttribute("errorMessage", "Đăng nhập Google thất bại: " + error);
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra state (CSRF protection)
        HttpSession session = request.getSession();
        String sessionState = (String) session.getAttribute("oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            request.setAttribute("errorMessage", "Invalid state parameter. Có thể có lỗi bảo mật.");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Xóa state sau khi verify
        session.removeAttribute("oauth_state");
        
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Không nhận được authorization code từ Google.");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Lấy config từ web.xml
            String clientId = getServletContext().getInitParameter("google_client_id");
            String clientSecret = getServletContext().getInitParameter("google_client_secret");
            String redirectUri = getServletContext().getInitParameter("google_redirect_uri");
            
            if (clientId == null || clientSecret == null) {
                request.setAttribute("errorMessage", "Google OAuth chưa được cấu hình đầy đủ.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            if (redirectUri == null || redirectUri.trim().isEmpty()) {
                String scheme = request.getScheme();
                String serverName = request.getServerName();
                int serverPort = request.getServerPort();
                String contextPath = request.getContextPath();
                redirectUri = scheme + "://" + serverName + (serverPort != 80 && serverPort != 443 ? ":" + serverPort : "") 
                            + contextPath + "/auth/google/callback";
            }
            
            // Exchange authorization code cho access token
            String tokenResponse = exchangeCodeForToken(code, clientId, clientSecret, redirectUri);
            // Google token endpoint trả về JSON, không phải URL-encoded
            Map<String, String> tokenData = parseJson(tokenResponse);
            
            String accessToken = tokenData.get("access_token");
            String refreshToken = tokenData.get("refresh_token");
            
            if (accessToken == null) {
                String errorDesc = tokenData.get("error_description");
                String tokenError = tokenData.get("error");
                String errorMsg = "Không thể lấy access token từ Google";
                if (tokenError != null) {
                    errorMsg += ": " + tokenError;
                }
                if (errorDesc != null) {
                    errorMsg += " - " + errorDesc;
                }
                if (tokenError == null && errorDesc == null) {
                    errorMsg += ": Unknown error. Response: " + tokenResponse;
                }
                request.setAttribute("errorMessage", errorMsg);
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Lấy thông tin user từ Google
            String userInfoJson = getUserInfoFromGoogle(accessToken);
            Map<String, String> userInfo = parseJson(userInfoJson);
            
            String email = userInfo.get("email");
            String fullName = userInfo.get("name");
            String googleUserID = userInfo.get("id");
            String picture = userInfo.get("picture");
            
            if (email == null || googleUserID == null) {
                request.setAttribute("errorMessage", "Không thể lấy thông tin user từ Google.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Tạo hoặc lấy user từ database
            User user = oauthService.createOrGetUserFromGoogle(email, fullName, googleUserID, accessToken, refreshToken);
            
            if (user == null) {
                request.setAttribute("errorMessage", "Không thể tạo hoặc lấy thông tin user.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Đăng nhập user
            session.setAttribute("currentUser", user);
            
            // Đồng bộ cart (tương tự LoginServlet)
            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.isEmpty() && cartService != null) {
                try {
                    java.util.List<CartItem> sessionCartItems = sessionCart.getItems();
                    cartService.syncCartFromSession(user.getUserID(), sessionCartItems);
                    
                    java.util.List<model.CartItemDB> dbCartItems = cartService.getCartItemsByUser(user.getUserID());
                    Cart mergedCart = new Cart();
                    
                    for (model.CartItemDB dbItem : dbCartItems) {
                        Product product = dbItem.getProduct();
                        if (product != null) {
                            CartItem cartItem = new CartItem();
                            cartItem.setProductID(product.getProductID());
                            cartItem.setProductName(product.getProductName());
                            cartItem.setImageUrl(product.getImageUrl());
                            cartItem.setPrice(product.getPrice());
                            cartItem.setQuantity(dbItem.getQuantity());
                            cartItem.setStock(product.getStock());
                            cartItem.setStockStatus(product.getStockStatus());
                            mergedCart.addItem(cartItem);
                        }
                    }
                    
                    session.setAttribute("cart", mergedCart);
                } catch (Exception e) {
                    System.err.println("Error syncing cart on Google login: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (cartService != null) {
                try {
                    java.util.List<model.CartItemDB> dbCartItems = cartService.getCartItemsByUser(user.getUserID());
                    Cart dbCart = new Cart();
                    
                    for (model.CartItemDB dbItem : dbCartItems) {
                        Product product = dbItem.getProduct();
                        if (product != null) {
                            CartItem cartItem = new CartItem();
                            cartItem.setProductID(product.getProductID());
                            cartItem.setProductName(product.getProductName());
                            cartItem.setImageUrl(product.getImageUrl());
                            cartItem.setPrice(product.getPrice());
                            cartItem.setQuantity(dbItem.getQuantity());
                            cartItem.setStock(product.getStock());
                            cartItem.setStockStatus(product.getStockStatus());
                            dbCart.addItem(cartItem);
                        }
                    }
                    
                    session.setAttribute("cart", dbCart);
                } catch (Exception e) {
                    System.err.println("Error loading cart from DB on Google login: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Set session timeout
            session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 ngày
            
            // Redirect
            String redirectAfterLogin = (String) session.getAttribute("oauth_redirect_after_login");
            session.removeAttribute("oauth_redirect_after_login");
            
            if (redirectAfterLogin != null && !redirectAfterLogin.trim().isEmpty()) {
                response.sendRedirect(redirectAfterLogin);
            } else {
                // Redirect về dashboard phù hợp theo role
                String roleName = user.getRoleName();
                String redirectPath = request.getContextPath() + "/home";
                
                if (roleName != null) {
                    switch (roleName) {
                        case "Admin":
                            redirectPath = request.getContextPath() + "/admin/dashboard";
                            break;
                        case "Manager":
                            redirectPath = request.getContextPath() + "/manager/dashboard";
                            break;
                        case "Staff":
                            redirectPath = request.getContextPath() + "/staff/dashboard";
                            break;
                        case "Customer":
                        default:
                            redirectPath = request.getContextPath() + "/home";
                            break;
                    }
                }
                
                response.sendRedirect(redirectPath);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi đăng nhập bằng Google: " + e.getMessage());
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
        }
    }
    
    /**
     * Exchange authorization code cho access token
     */
    private String exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) 
            throws IOException {
        URL url = new URL(GOOGLE_TOKEN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        
        String params = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                       "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                       "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                       "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                       "&grant_type=authorization_code";
        
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(params);
            writer.flush();
        }
        
        // Kiểm tra response code trước khi đọc
        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();
        
        // Đọc từ error stream nếu có lỗi, từ input stream nếu thành công
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 
                        ? conn.getInputStream() 
                        : conn.getErrorStream(), 
                    StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        
        // Nếu có lỗi, throw exception với thông tin chi tiết
        if (responseCode < 200 || responseCode >= 300) {
            String errorResponse = response.toString();
            throw new IOException("Google OAuth error (HTTP " + responseCode + "): " + errorResponse);
        }
        
        return response.toString();
    }
    
    /**
     * Lấy thông tin user từ Google API
     */
    private String getUserInfoFromGoogle(String accessToken) throws IOException {
        URL url = new URL(GOOGLE_USERINFO_URL + "?access_token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        
        return response.toString();
    }
    
    /**
     * Parse URL encoded response (token response)
     */
    private Map<String, String> parseUrlEncoded(String urlEncoded) {
        return util.JsonUtil.parseUrlEncoded(urlEncoded);
    }
    
    /**
     * Parse JSON response (user info)
     */
    private Map<String, String> parseJson(String json) {
        return util.JsonUtil.parseSimpleJson(json);
    }
}

