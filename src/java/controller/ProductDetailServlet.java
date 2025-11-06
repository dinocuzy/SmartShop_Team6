package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import productservice.IProductService;
import productservice.ProductService;

import java.io.IOException;

/**
 * Servlet xử lý trang chi tiết sản phẩm
 * URL mapping: /product hoặc /product/detail
 */
@WebServlet({"/product", "/product/detail"})
public class ProductDetailServlet extends HttpServlet {
    
    private IProductService productService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        productService = new ProductService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String productIDParam = request.getParameter("id");
        
        if (productIDParam == null || productIDParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Product ID is required");
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam.trim());
            
            Product product = productService.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("errorMessage", "Product not found");
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }
            
            // Kiểm tra sản phẩm có active không
            if (product.getStockStatus() != null && product.getStockStatus().equals("OutOfStock")) {
                request.setAttribute("errorMessage", "Sản phẩm này hiện không còn hàng");
            }
            
            // Set attributes
            request.setAttribute("product", product);
            
            // Forward đến JSP
            request.getRequestDispatcher("/views/store/productDetail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Product ID format");
            response.sendRedirect(request.getContextPath() + "/shop");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/views/store/productDetail.jsp").forward(request, response);
        }
    }
}

