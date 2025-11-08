package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;
import productservice.IProductService;
import productservice.ProductService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet xử lý danh sách yêu thích (Wishlist)
 * URL mapping: /wishlist
 * Actions: add, remove, view
 */
@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {
    
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        productService = new ProductService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        viewWishlist(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/wishlist");
            return;
        }
        
        switch (action) {
            case "add":
                addToWishlist(request, response);
                break;
            case "remove":
                removeFromWishlist(request, response);
                break;
            case "toggle":
                toggleWishlist(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/wishlist");
                break;
        }
    }
    
    /**
     * Hiển thị danh sách yêu thích
     */
    private void viewWishlist(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Set<Integer> wishlist = getWishlist(session);
        
        // Lấy thông tin các sản phẩm trong wishlist
        List<Product> products = new ArrayList<>();
        
        for (Integer productID : wishlist) {
            try {
                Product product = productService.getProductById(productID);
                if (product != null) {
                    products.add(product);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        request.setAttribute("wishlistProducts", products);
        request.setAttribute("wishlistSize", products.size());
        
        request.getRequestDispatcher("/views/store/wishlist.jsp").forward(request, response);
    }
    
    /**
     * Thêm sản phẩm vào danh sách yêu thích
     */
    private void addToWishlist(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String productIDParam = request.getParameter("productID");
            
            if (productIDParam == null || productIDParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            
            int productID = Integer.parseInt(productIDParam.trim());
            
            // Kiểm tra sản phẩm có tồn tại không
            Product product = productService.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("errorMessage", "Sản phẩm không tồn tại");
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            
            HttpSession session = request.getSession();
            Set<Integer> wishlist = getWishlist(session);
            
            wishlist.add(productID);
            
            // Redirect về trang trước
            String redirectUrl = request.getParameter("redirect");
            if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/wishlist");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
    
    /**
     * Xóa sản phẩm khỏi danh sách yêu thích
     */
    private void removeFromWishlist(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String productIDParam = request.getParameter("productID");
            
            if (productIDParam == null || productIDParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/wishlist");
                return;
            }
            
            int productID = Integer.parseInt(productIDParam.trim());
            
            HttpSession session = request.getSession();
            Set<Integer> wishlist = getWishlist(session);
            
            wishlist.remove(productID);
            
            // Redirect về trang trước
            String redirectUrl = request.getParameter("redirect");
            if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/wishlist");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/wishlist");
        }
    }
    
    /**
     * Toggle sản phẩm trong danh sách yêu thích (thêm nếu chưa có, xóa nếu đã có)
     */
    private void toggleWishlist(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String productIDParam = request.getParameter("productID");
            
            if (productIDParam == null || productIDParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            
            int productID = Integer.parseInt(productIDParam.trim());
            
            // Kiểm tra sản phẩm có tồn tại không
            Product product = productService.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("errorMessage", "Sản phẩm không tồn tại");
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            
            HttpSession session = request.getSession();
            Set<Integer> wishlist = getWishlist(session);
            
            // Toggle: nếu đã có thì xóa, chưa có thì thêm
            if (wishlist.contains(productID)) {
                wishlist.remove(productID);
            } else {
                wishlist.add(productID);
            }
            
            // Redirect về trang trước
            String redirectUrl = request.getParameter("redirect");
            if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
    
    /**
     * Lấy danh sách yêu thích từ session, tạo mới nếu chưa có
     */
    @SuppressWarnings("unchecked")
    private Set<Integer> getWishlist(HttpSession session) {
        Set<Integer> wishlist = (Set<Integer>) session.getAttribute("wishlist");
        
        if (wishlist == null) {
            wishlist = new HashSet<>();
            session.setAttribute("wishlist", wishlist);
        }
        
        return wishlist;
    }
    
    /**
     * Kiểm tra sản phẩm có trong wishlist không (utility method)
     */
    public static boolean isInWishlist(HttpSession session, int productID) {
        if (session == null) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        Set<Integer> wishlist = (Set<Integer>) session.getAttribute("wishlist");
        
        if (wishlist == null) {
            return false;
        }
        
        return wishlist.contains(productID);
    }
    
    /**
     * Lấy số lượng sản phẩm trong wishlist (utility method)
     */
    public static int getWishlistSize(HttpSession session) {
        if (session == null) {
            return 0;
        }
        
        @SuppressWarnings("unchecked")
        Set<Integer> wishlist = (Set<Integer>) session.getAttribute("wishlist");
        
        if (wishlist == null) {
            return 0;
        }
        
        return wishlist.size();
    }
}

