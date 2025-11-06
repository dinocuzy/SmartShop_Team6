package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartItem;
import model.Product;
import productservice.IProductService;
import productservice.ProductService;

import java.io.IOException;

/**
 * Servlet xử lý giỏ hàng
 * URL mapping: /cart
 * Actions: add, remove, update, view, clear
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        productService = new ProductService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "view";
        }
        
        switch (action) {
            case "view":
                viewCart(request, response);
                break;
            default:
                viewCart(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        
        switch (action) {
            case "add":
                addToCart(request, response);
                break;
            case "update":
                updateCart(request, response);
                break;
            case "remove":
                removeFromCart(request, response);
                break;
            case "clear":
                clearCart(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/cart");
                break;
        }
    }
    
    /**
     * Hiển thị giỏ hàng
     */
    private void viewCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Cart cart = getCart(session);
        
        request.setAttribute("cartItems", cart.getItems());
        request.setAttribute("cartTotal", cart.getTotal());
        request.setAttribute("cartSize", cart.getItemCount());
        request.setAttribute("cartTotalQuantity", cart.getTotalQuantity());
        
        request.getRequestDispatcher("/views/store/cart.jsp").forward(request, response);
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    private void addToCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String productIDParam = request.getParameter("productID");
            String quantityParam = request.getParameter("quantity");
            
            if (productIDParam == null || productIDParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }
            
            int productID = Integer.parseInt(productIDParam.trim());
            int quantity = 1;
            
            if (quantityParam != null && !quantityParam.trim().isEmpty()) {
                try {
                    quantity = Integer.parseInt(quantityParam.trim());
                    if (quantity < 1) quantity = 1;
                } catch (NumberFormatException e) {
                    quantity = 1;
                }
            }
            
            // Lấy thông tin sản phẩm
            Product product = productService.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("errorMessage", "Sản phẩm không tồn tại");
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }
            
            // Kiểm tra stock
            if (product.getStockStatus() == null || !product.getStockStatus().equals("InStock") || product.getStock() <= 0) {
                request.setAttribute("errorMessage", "Sản phẩm hiện không còn hàng");
                response.sendRedirect(request.getContextPath() + "/product/detail?id=" + productID);
                return;
            }
            
            HttpSession session = request.getSession();
            Cart cart = getCart(session);
            
            // Tạo CartItem mới
            CartItem cartItem = new CartItem();
            cartItem.setProductID(product.getProductID());
            cartItem.setProductName(product.getProductName());
            cartItem.setImageUrl(product.getImageUrl());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(quantity);
            cartItem.setStock(product.getStock());
            cartItem.setStockStatus(product.getStockStatus());
            
            // Thêm vào giỏ hàng (Cart sẽ tự xử lý logic cộng số lượng nếu đã có)
            cart.addItem(cartItem);
            
            // Redirect về trang trước hoặc giỏ hàng
            String redirectUrl = request.getParameter("redirect");
            if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/cart");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/shop");
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    private void updateCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String productIDParam = request.getParameter("productID");
            String quantityParam = request.getParameter("quantity");
            
            if (productIDParam == null || quantityParam == null) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            
            int productID = Integer.parseInt(productIDParam.trim());
            int quantity = Integer.parseInt(quantityParam.trim());
            
            if (quantity < 1) {
                quantity = 1;
            }
            
            HttpSession session = request.getSession();
            Cart cart = getCart(session);
            
            cart.updateQuantity(productID, quantity);
            
            response.sendRedirect(request.getContextPath() + "/cart");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String productIDParam = request.getParameter("productID");
            
            if (productIDParam == null || productIDParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            
            int productID = Integer.parseInt(productIDParam.trim());
            
            HttpSession session = request.getSession();
            Cart cart = getCart(session);
            
            cart.removeItem(productID);
            
            response.sendRedirect(request.getContextPath() + "/cart");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    private void clearCart(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Cart cart = getCart(session);
        cart.clear();
        
        response.sendRedirect(request.getContextPath() + "/cart");
    }
    
    /**
     * Lấy giỏ hàng từ session, tạo mới nếu chưa có
     */
    @SuppressWarnings("unchecked")
    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        
        return cart;
    }
    
    /**
     * Lấy số lượng sản phẩm trong giỏ hàng (utility method có thể dùng trong JSP)
     */
    public static int getCartSize(HttpSession session) {
        if (session == null) {
            return 0;
        }
        
        @SuppressWarnings("unchecked")
        Cart cart = (Cart) session.getAttribute("cart");
        
        if (cart == null) {
            return 0;
        }
        
        return cart.getItemCount();
    }
}

