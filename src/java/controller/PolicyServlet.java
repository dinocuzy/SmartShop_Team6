package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet xử lý các trang chính sách
 * URL mapping: /policy/* (warranty, return, privacy, terms, installment)
 */
@WebServlet("/policy/*")
public class PolicyServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        String policyType = "privacy"; // default
        
        if (pathInfo != null && pathInfo.length() > 1) {
            policyType = pathInfo.substring(1); // Remove leading "/"
        }
        
        // Validate policy type
        String[] validTypes = {"warranty", "return", "privacy", "terms", "installment"};
        boolean isValid = false;
        for (String type : validTypes) {
            if (type.equals(policyType)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            policyType = "privacy";
        }
        
        request.setAttribute("policyType", policyType);
        request.getRequestDispatcher("/views/store/policy.jsp").forward(request, response);
    }
}

