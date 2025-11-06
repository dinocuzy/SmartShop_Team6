package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Promotion;
import promotionservice.IPromotionService;
import promotionservice.PromotionService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho Promotion
 * URL mapping: /admin/promotions
 * Actions: list, add, edit, delete, save
 */
@WebServlet("/admin/promotions")
public class PromotionServlet extends HttpServlet {
    
    private IPromotionService promotionService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        promotionService = new PromotionService();
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
                    deletePromotion(request, response);
                    break;
                case "list":
                default:
                    listPromotions(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listPromotions(request, response);
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
                    savePromotion(request, response);
                    break;
                default:
                    listPromotions(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String promotionID = request.getParameter("promotionID");
            if (promotionID != null && !promotionID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    private void listPromotions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy tham số showAll từ request
        String showAllParam = request.getParameter("showAll");
        boolean includeInactive = "true".equalsIgnoreCase(showAllParam);
        
        // Lấy danh sách promotions
        List<Promotion> promotions = promotionService.getAllPromotions(includeInactive);
        
        // Set attributes
        request.setAttribute("promotions", promotions);
        request.setAttribute("showAll", includeInactive);
        
        request.getRequestDispatcher("/views/admin/promotionList.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("action", "add");
        request.setAttribute("promotion", new Promotion());
        request.getRequestDispatcher("/views/admin/promotionList.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String promotionIDParam = request.getParameter("promotionID");
        
        if (promotionIDParam == null || promotionIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Promotion ID is required");
            listPromotions(request, response);
            return;
        }
        
        try {
            int promotionID = Integer.parseInt(promotionIDParam);
            Promotion promotion = promotionService.getPromotionById(promotionID);
            
            if (promotion == null) {
                request.setAttribute("errorMessage", "Promotion not found with ID: " + promotionID);
                listPromotions(request, response);
                return;
            }
            
            request.setAttribute("action", "edit");
            request.setAttribute("promotion", promotion);
            request.getRequestDispatcher("/views/admin/promotionList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Promotion ID format");
            listPromotions(request, response);
        }
    }
    
    private void deletePromotion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String promotionIDParam = request.getParameter("promotionID");
        
        if (promotionIDParam == null || promotionIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Promotion ID is required");
            listPromotions(request, response);
            return;
        }
        
        try {
            int promotionID = Integer.parseInt(promotionIDParam);
            promotionService.deletePromotion(promotionID);
            request.setAttribute("successMessage", "Promotion deleted successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Promotion ID format");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        listPromotions(request, response);
    }
    
    private void savePromotion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String promotionIDParam = request.getParameter("promotionID");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String discountPercentParam = request.getParameter("discountPercent");
        String discountAmountParam = request.getParameter("discountAmount");
        String startDateParam = request.getParameter("startDate");
        String endDateParam = request.getParameter("endDate");
        String isActiveParam = request.getParameter("isActive");
        
        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Title is required");
            if (promotionIDParam != null && !promotionIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        Date startDate;
        Date endDate;
        
        try {
            if (startDateParam != null && !startDateParam.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                startDate = sdf.parse(startDateParam);
            } else {
                request.setAttribute("errorMessage", "Start date is required");
                if (promotionIDParam != null && !promotionIDParam.isEmpty()) {
                    showEditForm(request, response);
                } else {
                    showAddForm(request, response);
                }
                return;
            }
        } catch (ParseException e) {
            request.setAttribute("errorMessage", "Invalid start date format");
            if (promotionIDParam != null && !promotionIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        try {
            if (endDateParam != null && !endDateParam.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                endDate = sdf.parse(endDateParam);
            } else {
                request.setAttribute("errorMessage", "End date is required");
                if (promotionIDParam != null && !promotionIDParam.isEmpty()) {
                    showEditForm(request, response);
                } else {
                    showAddForm(request, response);
                }
                return;
            }
        } catch (ParseException e) {
            request.setAttribute("errorMessage", "Invalid end date format");
            if (promotionIDParam != null && !promotionIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        BigDecimal discountPercent = null;
        if (discountPercentParam != null && !discountPercentParam.trim().isEmpty()) {
            try {
                discountPercent = new BigDecimal(discountPercentParam);
                if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                    discountPercent = null;
                }
            } catch (NumberFormatException e) {
                discountPercent = null;
            }
        }
        
        BigDecimal discountAmount = null;
        if (discountAmountParam != null && !discountAmountParam.trim().isEmpty()) {
            try {
                discountAmount = new BigDecimal(discountAmountParam);
                if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
                    discountAmount = null;
                }
            } catch (NumberFormatException e) {
                discountAmount = null;
            }
        }
        
        boolean isActive = isActiveParam != null && isActiveParam.equals("true");
        
        Promotion promotion = new Promotion();
        
        if (promotionIDParam != null && !promotionIDParam.isEmpty()) {
            try {
                int promotionID = Integer.parseInt(promotionIDParam);
                promotion.setPromotionID(promotionID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Promotion ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        promotion.setTitle(title.trim());
        promotion.setDescription(description != null ? description.trim() : null);
        promotion.setDiscountPercent(discountPercent);
        promotion.setDiscountAmount(discountAmount);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setActive(isActive);
        
        try {
            if (promotion.getPromotionID() > 0) {
                promotionService.updatePromotion(promotion);
                request.setAttribute("successMessage", "Promotion updated successfully");
            } else {
                promotionService.addPromotion(promotion);
                request.setAttribute("successMessage", "Promotion added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (promotion.getPromotionID() > 0) {
                request.setAttribute("promotion", promotion);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/promotionList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("promotion", promotion);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/promotionList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/promotions?action=list");
    }
}

