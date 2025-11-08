package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.SupportRequest;
import supportrequestservice.ISupportRequestService;
import supportrequestservice.SupportRequestService;
import util.AuthorizationUtil;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý yêu cầu hỗ trợ (SupportRequest)
 * URL mapping: /support-request
 * Actions: create, view, update, delete, list
 */
@WebServlet("/support-request")
public class SupportRequestServlet extends HttpServlet {
    
    private ISupportRequestService supportRequestService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            supportRequestService = new SupportRequestService();
        } catch (Exception e) {
            System.err.println("Error initializing SupportRequestServlet: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để tránh context startup failure
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "view":
                viewRequest(request, response, currentUser);
                break;
            case "list":
                listRequests(request, response, currentUser);
                break;
            default:
                listRequests(request, response, currentUser);
                break;
        }
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
            response.sendRedirect(request.getContextPath() + "/support-request");
            return;
        }
        
        switch (action) {
            case "create":
                createRequest(request, response, currentUser);
                break;
            case "update":
                updateRequest(request, response, currentUser);
                break;
            case "updateStatus":
                updateStatus(request, response, currentUser);
                break;
            case "delete":
                deleteRequest(request, response, currentUser);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/support-request");
                break;
        }
    }
    
    /**
     * Hiển thị form tạo yêu cầu hỗ trợ
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/views/store/supportRequestForm.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị danh sách yêu cầu hỗ trợ
     */
    private void listRequests(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            List<SupportRequest> requests;
            
            // Kiểm tra quyền một cách an toàn
            boolean isAdmin = false;
            boolean isManager = false;
            boolean isStaff = false;
            
            try {
                isAdmin = AuthorizationUtil.hasRole(currentUser, "Admin");
                isManager = AuthorizationUtil.hasRole(currentUser, "Manager");
                isStaff = AuthorizationUtil.hasRole(currentUser, "Staff");
            } catch (Exception authException) {
                System.err.println("Error checking role in listRequests: " + authException.getMessage());
                authException.printStackTrace();
                // Fallback: kiểm tra theo roleID
                if (currentUser != null) {
                    int roleID = currentUser.getRoleID();
                    isAdmin = (roleID == 1);
                    isManager = (roleID == 2);
                    isStaff = (roleID == 3);
                }
            }
            
            boolean isStaffMember = isAdmin || isManager || isStaff;
            
            // Admin/Manager/Staff xem tất cả, Customer chỉ xem của mình
            if (isStaffMember) {
                requests = supportRequestService.getAllRequests();
            } else {
                requests = supportRequestService.getUserRequests(currentUser.getUserID());
            }
            
            // Lấy thống kê (chỉ cho admin/manager/staff)
            int openCount = 0;
            int inProgressCount = 0;
            int closedCount = 0;
            
            if (isStaffMember) {
                try {
                    openCount = supportRequestService.countByStatus("Open");
                    inProgressCount = supportRequestService.countByStatus("InProgress");
                    closedCount = supportRequestService.countByStatus("Closed");
                } catch (Exception e) {
                    System.err.println("Error getting statistics: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            request.setAttribute("requests", requests);
            request.setAttribute("openCount", openCount);
            request.setAttribute("inProgressCount", inProgressCount);
            request.setAttribute("closedCount", closedCount);
            request.setAttribute("isAdmin", isStaffMember);
            
            request.getRequestDispatcher("/views/store/supportRequestList.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách yêu cầu hỗ trợ: " + e.getMessage());
            request.getRequestDispatcher("/views/store/supportRequestList.jsp").forward(request, response);
        }
    }
    
    /**
     * Xem chi tiết yêu cầu hỗ trợ
     */
    private void viewRequest(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        String requestIDParam = request.getParameter("id");
        
        if (requestIDParam == null || requestIDParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/support-request");
            return;
        }
        
        try {
            int requestID = Integer.parseInt(requestIDParam.trim());
            SupportRequest supportRequest = supportRequestService.getRequestById(requestID);
            
            if (supportRequest == null) {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy yêu cầu hỗ trợ");
                response.sendRedirect(request.getContextPath() + "/support-request");
                return;
            }
            
            // Kiểm tra quyền: Customer chỉ xem được request của mình
            boolean isStaffMember = false;
            try {
                isStaffMember = AuthorizationUtil.hasRole(currentUser, "Admin") || 
                               AuthorizationUtil.hasRole(currentUser, "Manager") || 
                               AuthorizationUtil.hasRole(currentUser, "Staff");
            } catch (Exception authException) {
                System.err.println("Error checking role in viewRequest: " + authException.getMessage());
                authException.printStackTrace();
                // Fallback: kiểm tra theo roleID
                if (currentUser != null) {
                    int roleID = currentUser.getRoleID();
                    isStaffMember = (roleID == 1 || roleID == 2 || roleID == 3);
                }
            }
            
            if (!isStaffMember && supportRequest.getUserID() != currentUser.getUserID()) {
                request.getSession().setAttribute("errorMessage", "Bạn không có quyền xem yêu cầu hỗ trợ này");
                response.sendRedirect(request.getContextPath() + "/support-request");
                return;
            }
            
            request.setAttribute("supportRequest", supportRequest);
            request.setAttribute("isAdmin", isStaffMember);
            
            request.getRequestDispatcher("/views/store/supportRequestDetail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/support-request");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi tải yêu cầu hỗ trợ: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/support-request");
        }
    }
    
    /**
     * Tạo yêu cầu hỗ trợ mới
     */
    private void createRequest(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        
        if (subject == null || subject.trim().isEmpty() || 
            message == null || message.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin");
            request.setAttribute("subject", subject);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/views/store/supportRequestForm.jsp").forward(request, response);
            return;
        }
        
        try {
            int requestID = supportRequestService.createRequest(currentUser.getUserID(), subject, message);
            
            if (requestID > 0) {
                request.getSession().setAttribute("successMessage", "Đã gửi yêu cầu hỗ trợ thành công. Chúng tôi sẽ phản hồi sớm nhất có thể.");
                response.sendRedirect(request.getContextPath() + "/support-request?action=view&id=" + requestID);
            } else {
                request.setAttribute("errorMessage", "Không thể gửi yêu cầu hỗ trợ. Vui lòng thử lại.");
                request.setAttribute("subject", subject);
                request.setAttribute("message", message);
                request.getRequestDispatcher("/views/store/supportRequestForm.jsp").forward(request, response);
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("subject", subject);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/views/store/supportRequestForm.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi gửi yêu cầu hỗ trợ: " + e.getMessage());
            request.setAttribute("subject", subject);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/views/store/supportRequestForm.jsp").forward(request, response);
        }
    }
    
    /**
     * Cập nhật yêu cầu hỗ trợ
     */
    private void updateRequest(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        String requestIDParam = request.getParameter("requestID");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        
        if (requestIDParam == null || requestIDParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/support-request");
            return;
        }
        
        try {
            int requestID = Integer.parseInt(requestIDParam.trim());
            SupportRequest supportRequest = supportRequestService.getRequestById(requestID);
            
            if (supportRequest == null) {
                request.getSession().setAttribute("errorMessage", "Không tìm thấy yêu cầu hỗ trợ");
                response.sendRedirect(request.getContextPath() + "/support-request");
                return;
            }
            
            // Customer chỉ có thể cập nhật request của mình và chỉ khi status là Open
            boolean isStaffMember = false;
            try {
                isStaffMember = AuthorizationUtil.hasRole(currentUser, "Admin") || 
                               AuthorizationUtil.hasRole(currentUser, "Manager") || 
                               AuthorizationUtil.hasRole(currentUser, "Staff");
            } catch (Exception authException) {
                System.err.println("Error checking role in updateRequest: " + authException.getMessage());
                authException.printStackTrace();
                // Fallback: kiểm tra theo roleID
                if (currentUser != null) {
                    int roleID = currentUser.getRoleID();
                    isStaffMember = (roleID == 1 || roleID == 2 || roleID == 3);
                }
            }
            
            if (!isStaffMember && (supportRequest.getUserID() != currentUser.getUserID() || 
                            !supportRequest.getStatus().equals("Open"))) {
                request.getSession().setAttribute("errorMessage", "Bạn không có quyền cập nhật yêu cầu hỗ trợ này");
                response.sendRedirect(request.getContextPath() + "/support-request");
                return;
            }
            
            if (subject != null && !subject.trim().isEmpty()) {
                supportRequest.setSubject(subject.trim());
            }
            if (message != null && !message.trim().isEmpty()) {
                supportRequest.setMessage(message.trim());
            }
            
            boolean success = supportRequestService.updateRequest(supportRequest);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã cập nhật yêu cầu hỗ trợ thành công");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể cập nhật yêu cầu hỗ trợ");
            }
            
            response.sendRedirect(request.getContextPath() + "/support-request?action=view&id=" + requestID);
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/support-request");
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/support-request");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi cập nhật yêu cầu hỗ trợ: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/support-request");
        }
    }
    
    /**
     * Cập nhật trạng thái yêu cầu hỗ trợ (chỉ admin/staff)
     */
    private void updateStatus(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        // Chỉ admin/staff mới được cập nhật status
        boolean isStaffMember = false;
        try {
            isStaffMember = AuthorizationUtil.hasRole(currentUser, "Admin") || 
                           AuthorizationUtil.hasRole(currentUser, "Manager") || 
                           AuthorizationUtil.hasRole(currentUser, "Staff");
        } catch (Exception authException) {
            System.err.println("Error checking role in updateStatus: " + authException.getMessage());
            authException.printStackTrace();
            // Fallback: kiểm tra theo roleID
            if (currentUser != null) {
                int roleID = currentUser.getRoleID();
                isStaffMember = (roleID == 1 || roleID == 2 || roleID == 3);
            }
        }
        
        if (!isStaffMember) {
            request.getSession().setAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác này");
            response.sendRedirect(request.getContextPath() + "/support-request");
            return;
        }
        
        String requestIDParam = request.getParameter("requestID");
        String status = request.getParameter("status");
        
        if (requestIDParam == null || requestIDParam.trim().isEmpty() || 
            status == null || status.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/support-request");
            return;
        }
        
        try {
            int requestID = Integer.parseInt(requestIDParam.trim());
            
            boolean success = supportRequestService.updateStatus(requestID, status.trim());
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã cập nhật trạng thái thành công");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể cập nhật trạng thái");
            }
            
            response.sendRedirect(request.getContextPath() + "/support-request?action=view&id=" + requestID);
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/support-request");
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/support-request");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/support-request");
        }
    }
    
    /**
     * Xóa yêu cầu hỗ trợ (chỉ admin/staff)
     */
    private void deleteRequest(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        // Chỉ admin/staff mới được xóa
        boolean isStaffMember = false;
        try {
            isStaffMember = AuthorizationUtil.hasRole(currentUser, "Admin") || 
                           AuthorizationUtil.hasRole(currentUser, "Manager") || 
                           AuthorizationUtil.hasRole(currentUser, "Staff");
        } catch (Exception authException) {
            System.err.println("Error checking role in deleteRequest: " + authException.getMessage());
            authException.printStackTrace();
            // Fallback: kiểm tra theo roleID
            if (currentUser != null) {
                int roleID = currentUser.getRoleID();
                isStaffMember = (roleID == 1 || roleID == 2 || roleID == 3);
            }
        }
        
        if (!isStaffMember) {
            request.getSession().setAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác này");
            response.sendRedirect(request.getContextPath() + "/support-request");
            return;
        }
        
        String requestIDParam = request.getParameter("id");
        
        if (requestIDParam == null || requestIDParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/support-request");
            return;
        }
        
        try {
            int requestID = Integer.parseInt(requestIDParam.trim());
            
            boolean success = supportRequestService.deleteRequest(requestID);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã xóa yêu cầu hỗ trợ thành công");
            } else {
                request.getSession().setAttribute("errorMessage", "Không thể xóa yêu cầu hỗ trợ");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi khi xóa yêu cầu hỗ trợ: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/support-request");
    }
}

