package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.CompareListItem;
import model.Product;
import comparelistservice.ICompareListService;
import comparelistservice.CompareListService;
import productservice.IProductService;
import productservice.ProductService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet xử lý danh sách so sánh sản phẩm (CompareList)
 * URL mapping: /compare
 * Actions: add, remove, view, clear
 */
@WebServlet("/compare")
public class CompareServlet extends HttpServlet {
    
    private ICompareListService compareListService;
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            compareListService = new CompareListService();
            productService = new ProductService();
        } catch (Exception e) {
            System.err.println("Error initializing CompareServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        viewCompareList(request, response);
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
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/compare");
            return;
        }
        
        switch (action) {
            case "add":
                addToCompareList(request, response, currentUser);
                break;
            case "remove":
                removeFromCompareList(request, response, currentUser);
                break;
            case "clear":
                clearCompareList(request, response, currentUser);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/compare");
                break;
        }
    }
    
    /**
     * Hiển thị trang so sánh sản phẩm
     */
    private void viewCompareList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Lấy danh sách CompareListItem đang so sánh
            List<CompareListItem> compareItems = compareListService.getUserCompareList(currentUser.getUserID());
            
            // Lấy số lượng sản phẩm trong danh sách so sánh
            int compareCount = compareListService.getCompareListCount(currentUser.getUserID());
            
            // Chuyển đổi CompareListItem thành Product objects
            List<Product> compareProducts = new ArrayList<>();
            for (CompareListItem item : compareItems) {
                Product product = productService.getProductById(item.getProductID());
                if (product != null) {
                    compareProducts.add(product);
                }
            }
            
            request.setAttribute("compareProducts", compareProducts);
            request.setAttribute("compareCount", compareCount);
            
            request.getRequestDispatcher("/views/store/compare.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách so sánh: " + e.getMessage());
            request.getRequestDispatcher("/views/store/compare.jsp").forward(request, response);
        }
    }
    
    /**
     * Thêm sản phẩm vào danh sách so sánh
     */
    private void addToCompareList(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        String productIDParam = request.getParameter("productID");
        String redirectURL = request.getParameter("redirectURL");
        
        if (productIDParam == null || productIDParam.trim().isEmpty()) {
            if (redirectURL != null && !redirectURL.isEmpty()) {
                response.sendRedirect(redirectURL);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam.trim());
            
            boolean success = compareListService.addProduct(currentUser.getUserID(), productID);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã thêm sản phẩm vào danh sách so sánh");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể thêm sản phẩm vào danh sách so sánh");
            }
            
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi thêm sản phẩm vào danh sách so sánh: " + e.getMessage());
        }
        
        if (redirectURL != null && !redirectURL.isEmpty()) {
            response.sendRedirect(redirectURL);
        } else {
            response.sendRedirect(request.getContextPath() + "/compare");
        }
    }
    
    /**
     * Xóa sản phẩm khỏi danh sách so sánh
     */
    private void removeFromCompareList(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        String productIDParam = request.getParameter("productID");
        
        if (productIDParam == null || productIDParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/compare");
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam.trim());
            
            boolean success = compareListService.removeProduct(currentUser.getUserID(), productID);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã xóa sản phẩm khỏi danh sách so sánh");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể xóa sản phẩm khỏi danh sách so sánh");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/compare");
    }
    
    /**
     * Xóa toàn bộ danh sách so sánh
     */
    private void clearCompareList(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            boolean success = compareListService.clearList(currentUser.getUserID());
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã xóa toàn bộ danh sách so sánh");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể xóa danh sách so sánh");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi xóa danh sách so sánh: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/compare");
    }
}

