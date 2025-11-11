package controller;

import analyticsdao.AnalyticsDAO;
import analyticsdao.IAnalyticsDAO;
import util.GeminiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Servlet xử lý Analytics API
 * URL mapping: /api/analytics
 * Method: GET
 * 
 * Parameters (optional):
 * - startDate: yyyy-MM-dd
 * - endDate: yyyy-MM-dd
 * 
 * Response (JSON):
 * {
 *   "success": true/false,
 *   "totalRevenue": 0,
 *   "totalOrders": 0,
 *   "totalViews": 0,
 *   "avgRevenuePerOrder": 0,
 *   "comment": "AI comment",
 *   "revenueByDate": [...],
 *   "ordersByDate": [...],
 *   "viewsByDate": [...],
 *   "error": "Error message (if any)"
 * }
 */
@WebServlet("/api/analytics")
public class AnalyticsServlet extends HttpServlet {
    
    private IAnalyticsDAO analyticsDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            analyticsDAO = new AnalyticsDAO();
            // Load Gemini API key từ context-param
            String apiKey = getServletContext().getInitParameter("gemini_api_key");
            if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
                GeminiUtil.setApiKey(apiKey);
                System.out.println("Gemini API key loaded in AnalyticsServlet init");
            }
        } catch (Exception e) {
            System.err.println("Error initializing AnalyticsServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            // Parse periodType first
            String periodType = request.getParameter("periodType"); // "day", "month", "year"
            if (periodType == null || periodType.trim().isEmpty()) {
                periodType = "day";
            } else {
                periodType = periodType.trim().toLowerCase();
            }
            
            // Parse date parameters - chỉ parse khi periodType là "day"
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");
            String yearParam = request.getParameter("year");
            String monthParam = request.getParameter("month");
            
            Date startDate = null;
            Date endDate = null;
            Integer year = null;
            Integer month = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            // Chỉ parse startDate/endDate khi periodType là "day"
            if ("day".equals(periodType)) {
                if (startDateParam != null && !startDateParam.trim().isEmpty()) {
                    try {
                        startDate = sdf.parse(startDateParam.trim());
                    } catch (ParseException e) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Invalid startDate format. Use yyyy-MM-dd");
                        out.print(jsonResponse.toString());
                        out.flush();
                        return;
                    }
                }
                
                if (endDateParam != null && !endDateParam.trim().isEmpty()) {
                    try {
                        endDate = sdf.parse(endDateParam.trim());
                    } catch (ParseException e) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Invalid endDate format. Use yyyy-MM-dd");
                        out.print(jsonResponse.toString());
                        out.flush();
                        return;
                    }
                }
            }
            
            if (yearParam != null && !yearParam.trim().isEmpty()) {
                try {
                    year = Integer.parseInt(yearParam.trim());
                } catch (NumberFormatException e) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Invalid year format");
                    out.print(jsonResponse.toString());
                    out.flush();
                    return;
                }
            }
            
            if (monthParam != null && !monthParam.trim().isEmpty()) {
                try {
                    month = Integer.parseInt(monthParam.trim());
                    if (month < 1 || month > 12) {
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("error", "Month must be between 1 and 12");
                        out.print(jsonResponse.toString());
                        out.flush();
                        return;
                    }
                } catch (NumberFormatException e) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("error", "Invalid month format");
                    out.print(jsonResponse.toString());
                    out.flush();
                    return;
                }
            }
            
            // Nếu có month, tính startDate và endDate cho tháng đó
            if (year != null && month != null) {
                try {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.set(year, month - 1, 1, 0, 0, 0);
                    cal.set(java.util.Calendar.MILLISECOND, 0);
                    startDate = cal.getTime();
                    
                    cal.add(java.util.Calendar.MONTH, 1);
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                    cal.set(java.util.Calendar.MINUTE, 59);
                    cal.set(java.util.Calendar.SECOND, 59);
                    endDate = cal.getTime();
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            // Lấy dữ liệu từ database
            BigDecimal totalRevenue = analyticsDAO.getTotalRevenue(startDate, endDate);
            int totalOrders = analyticsDAO.getTotalOrders(startDate, endDate);
            int totalViews = analyticsDAO.getTotalViews(startDate, endDate);
            BigDecimal avgRevenuePerOrder = analyticsDAO.getAvgRevenuePerOrder(startDate, endDate);
            
            // Lấy dữ liệu cho biểu đồ dựa trên periodType
            List<Map<String, Object>> revenueByDate = new ArrayList<>();
            List<Map<String, Object>> ordersByDate = new ArrayList<>();
            List<Map<String, Object>> viewsByDate = new ArrayList<>();
            
            if ("year".equals(periodType)) {
                // Phân tích theo năm
                revenueByDate = analyticsDAO.getRevenueByYear();
                ordersByDate = analyticsDAO.getOrdersByYear();
                viewsByDate = analyticsDAO.getViewsByYear();
            } else if ("month".equals(periodType)) {
                // Phân tích theo tháng
                revenueByDate = analyticsDAO.getRevenueByMonth(year);
                ordersByDate = analyticsDAO.getOrdersByMonth(year);
                viewsByDate = analyticsDAO.getViewsByMonth(year);
            } else {
                // Phân tích theo ngày (mặc định)
                revenueByDate = analyticsDAO.getRevenueByDate(startDate, endDate);
                ordersByDate = analyticsDAO.getOrdersByDate(startDate, endDate);
                viewsByDate = analyticsDAO.getViewsByDate(startDate, endDate);
            }
            
            // Tạo prompt cho Gemini API
            String prompt = createAnalyticsPrompt(totalRevenue, totalOrders, totalViews, avgRevenuePerOrder);
            
            // Gọi Gemini API để sinh nhận xét tự động
            String aiComment = generateAIComment(prompt);
            
            // Format response
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("totalRevenue", totalRevenue.toString());
            jsonResponse.addProperty("totalOrders", totalOrders);
            jsonResponse.addProperty("totalViews", totalViews);
            jsonResponse.addProperty("avgRevenuePerOrder", avgRevenuePerOrder.toString());
            jsonResponse.addProperty("comment", aiComment);
            
            // Thêm dữ liệu cho biểu đồ
            Gson gson = new Gson();
            jsonResponse.add("revenueByDate", gson.toJsonTree(revenueByDate));
            jsonResponse.add("ordersByDate", gson.toJsonTree(ordersByDate));
            jsonResponse.add("viewsByDate", gson.toJsonTree(viewsByDate));
            
        } catch (Exception e) {
            System.err.println("Error in AnalyticsServlet: " + e.getMessage());
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", "Đã xảy ra lỗi khi xử lý dữ liệu: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    /**
     * Tạo prompt cho Gemini API
     */
    private String createAnalyticsPrompt(BigDecimal totalRevenue, int totalOrders, int totalViews, BigDecimal avgRevenuePerOrder) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("Bạn là chuyên gia phân tích dữ liệu kinh doanh. Hãy phân tích và đưa ra nhận xét về tình hình kinh doanh của SmartShop dựa trên các số liệu sau:\n\n");
        prompt.append("📊 SỐ LIỆU THỐNG KÊ:\n");
        prompt.append("- Tổng doanh thu: ").append(formatPrice(totalRevenue)).append("\n");
        prompt.append("- Tổng số đơn hàng: ").append(totalOrders).append(" đơn\n");
        prompt.append("- Tổng lượt xem sản phẩm: ").append(totalViews).append(" lượt\n");
        prompt.append("- Doanh thu trung bình mỗi đơn: ").append(formatPrice(avgRevenuePerOrder)).append("\n\n");
        
        // Tính toán thêm một số chỉ số
        if (totalViews > 0) {
            double conversionRate = (double) totalOrders / totalViews * 100;
            prompt.append("- Tỷ lệ chuyển đổi (đơn hàng/lượt xem): ").append(String.format("%.2f", conversionRate)).append("%\n");
        }
        
        prompt.append("\n");
        prompt.append("Hãy đưa ra nhận xét ngắn gọn (2-3 câu) về:\n");
        prompt.append("1. Tình hình doanh thu và đơn hàng\n");
        prompt.append("2. Hiệu quả chuyển đổi (nếu có dữ liệu)\n");
        prompt.append("3. Đề xuất cải thiện (nếu cần)\n\n");
        prompt.append("Viết bằng tiếng Việt, giọng điệu chuyên nghiệp nhưng dễ hiểu.");
        
        return prompt.toString();
    }
    
    /**
     * Gọi Gemini API để sinh nhận xét tự động
     */
    private String generateAIComment(String prompt) {
        try {
            // Kiểm tra API key
            if (GeminiUtil.getApiKey() == null || GeminiUtil.getApiKey().isEmpty()) {
                return "⚠️ Gemini API key chưa được cấu hình. Không thể tạo nhận xét tự động.";
            }
            
            // Tạo messages cho Gemini
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            // Gọi Gemini API
            String[] errorHolder = new String[1];
            String aiResponse = GeminiUtil.chatCompletionWithHistory(messages, null, errorHolder);
            
            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                String errorMsg = errorHolder[0] != null ? errorHolder[0] : "Không thể kết nối đến Gemini API";
                return "⚠️ " + errorMsg;
            }
            
            return aiResponse.trim();
            
        } catch (Exception e) {
            System.err.println("Error generating AI comment: " + e.getMessage());
            e.printStackTrace();
            return "⚠️ Đã xảy ra lỗi khi tạo nhận xét tự động: " + e.getMessage();
        }
    }
    
    /**
     * Format giá tiền theo định dạng VNĐ
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0 ₫";
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(price) + " ₫";
    }
}

