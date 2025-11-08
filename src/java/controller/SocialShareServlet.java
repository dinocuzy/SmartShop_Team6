package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.SocialShare;
import socialsharedao.ISocialShareDAO;
import socialsharedao.SocialShareDAO;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet xử lý chia sẻ sản phẩm lên mạng xã hội
 * URL mapping: /api/social-share
 * Method: POST
 * Parameters: productID, platform
 */
@WebServlet("/api/social-share")
public class SocialShareServlet extends HttpServlet {
    
    private ISocialShareDAO socialShareDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            socialShareDAO = new SocialShareDAO();
        } catch (Exception e) {
            System.err.println("Error initializing SocialShareServlet: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để tránh context startup failure
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String productIDParam = request.getParameter("productID");
        String platform = request.getParameter("platform");
        
        if (productIDParam == null || productIDParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"error\": \"Product ID is required\"}");
            out.flush();
            return;
        }
        
        if (platform == null || platform.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"error\": \"Platform is required\"}");
            out.flush();
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDParam.trim());
            
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("currentUser");
            
            SocialShare share = new SocialShare();
            share.setProductID(productID);
            share.setUserID(currentUser != null ? currentUser.getUserID() : null);
            share.setPlatform(platform.trim());
            
            int shareID = socialShareDAO.insert(share);
            
            if (shareID > 0) {
                PrintWriter out = response.getWriter();
                out.print("{\"success\": true, \"shareID\": " + shareID + "}");
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter out = response.getWriter();
                out.print("{\"success\": false, \"error\": \"Failed to record share\"}");
                out.flush();
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"error\": \"Invalid Product ID format\"}");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"error\": \"" + escapeJson(e.getMessage()) + "\"}");
            out.flush();
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

