package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Notification;
import model.User;
import notificationservice.INotificationService;
import notificationservice.NotificationService;
import userservice.IUserService;
import userservice.UserService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho Notification
 * URL mapping: /admin/notifications
 * Actions: list, add, edit, delete, save, markRead, markAllRead
 */
@WebServlet("/admin/notifications")
public class NotificationServlet extends HttpServlet {
    
    private INotificationService notificationService;
    private IUserService userService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            notificationService = new NotificationService();
            userService = new UserService();
        } catch (Exception e) {
            System.err.println("Error initializing NotificationServlet: " + e.getMessage());
            e.printStackTrace();
            // Không throw exception để tránh context startup failure
        }
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
                    deleteNotification(request, response);
                    break;
                case "markRead":
                    markAsRead(request, response);
                    break;
                case "markAllRead":
                    markAllAsRead(request, response);
                    break;
                case "list":
                default:
                    listNotifications(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listNotifications(request, response);
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
                    saveNotification(request, response);
                    break;
                default:
                    listNotifications(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String notificationID = request.getParameter("notificationID");
            if (notificationID != null && !notificationID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    private void listNotifications(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIDParam = request.getParameter("userID");
        String unreadOnlyParam = request.getParameter("unreadOnly");
        
        List<Notification> notifications;
        
        if (userIDParam != null && !userIDParam.isEmpty()) {
            try {
                int userID = Integer.parseInt(userIDParam);
                if (unreadOnlyParam != null && unreadOnlyParam.equals("true")) {
                    notifications = notificationService.getUnreadNotificationsByUser(userID);
                } else {
                    notifications = notificationService.getNotificationsByUser(userID);
                }
            } catch (NumberFormatException e) {
                notifications = notificationService.getAllNotifications();
            }
        } else {
            notifications = notificationService.getAllNotifications();
        }
        
        // Load danh sách users cho dropdown (chỉ lấy active users)
        List<User> users = userService.getAllUsers(false);
        request.setAttribute("users", users);
        
        request.setAttribute("notifications", notifications);
        request.getRequestDispatcher("/views/admin/notificationList.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Load danh sách users cho dropdown (chỉ lấy active users)
        List<User> users = userService.getAllUsers(false);
        request.setAttribute("users", users);
        
        request.setAttribute("action", "add");
        request.setAttribute("notification", new Notification());
        request.getRequestDispatcher("/views/admin/notificationList.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String notificationIDParam = request.getParameter("notificationID");
        
        if (notificationIDParam == null || notificationIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Notification ID is required");
            listNotifications(request, response);
            return;
        }
        
        try {
            int notificationID = Integer.parseInt(notificationIDParam);
            Notification notification = notificationService.getNotificationById(notificationID);
            
            if (notification == null) {
                request.setAttribute("errorMessage", "Notification not found with ID: " + notificationID);
                listNotifications(request, response);
                return;
            }
            
            // Load danh sách users cho dropdown (chỉ lấy active users)
            List<User> users = userService.getAllUsers(false);
            request.setAttribute("users", users);
            
            request.setAttribute("action", "edit");
            request.setAttribute("notification", notification);
            request.getRequestDispatcher("/views/admin/notificationList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Notification ID format");
            listNotifications(request, response);
        }
    }
    
    private void deleteNotification(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String notificationIDParam = request.getParameter("notificationID");
        
        if (notificationIDParam == null || notificationIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Notification ID is required");
            listNotifications(request, response);
            return;
        }
        
        try {
            int notificationID = Integer.parseInt(notificationIDParam);
            notificationService.deleteNotification(notificationID);
            request.setAttribute("successMessage", "Notification deleted successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Notification ID format");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        listNotifications(request, response);
    }
    
    private void markAsRead(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String notificationIDParam = request.getParameter("notificationID");
        
        if (notificationIDParam != null && !notificationIDParam.isEmpty()) {
            try {
                int notificationID = Integer.parseInt(notificationIDParam);
                notificationService.markAsRead(notificationID);
                request.setAttribute("successMessage", "Notification marked as read");
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Notification ID format");
            }
        }
        
        listNotifications(request, response);
    }
    
    private void markAllAsRead(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIDParam = request.getParameter("userID");
        
        if (userIDParam != null && !userIDParam.isEmpty()) {
            try {
                int userID = Integer.parseInt(userIDParam);
                notificationService.markAllAsRead(userID);
                request.setAttribute("successMessage", "All notifications marked as read");
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid User ID format");
            }
        }
        
        listNotifications(request, response);
    }
    
    private void saveNotification(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String notificationIDParam = request.getParameter("notificationID");
        String userIDParam = request.getParameter("userID");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String isReadParam = request.getParameter("isRead");
        
        if (userIDParam == null || userIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "User ID is required");
            if (notificationIDParam != null && !notificationIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Title is required");
            if (notificationIDParam != null && !notificationIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        if (content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Content is required");
            if (notificationIDParam != null && !notificationIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        int userID;
        try {
            userID = Integer.parseInt(userIDParam);
            if (userID <= 0) {
                throw new NumberFormatException("User ID must be > 0");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid User ID format");
            if (notificationIDParam != null && !notificationIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        boolean isRead = isReadParam != null && isReadParam.equals("true");
        
        Notification notification = new Notification();
        
        if (notificationIDParam != null && !notificationIDParam.isEmpty()) {
            try {
                int notificationID = Integer.parseInt(notificationIDParam);
                notification.setNotificationID(notificationID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Notification ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        notification.setUserID(userID);
        notification.setTitle(title.trim());
        notification.setContent(content.trim());
        notification.setRead(isRead);
        
        try {
            if (notification.getNotificationID() > 0) {
                notificationService.updateNotification(notification);
                request.setAttribute("successMessage", "Notification updated successfully");
            } else {
                notificationService.addNotification(notification);
                request.setAttribute("successMessage", "Notification added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (notification.getNotificationID() > 0) {
                request.setAttribute("notification", notification);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/notificationList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("notification", notification);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/notificationList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/notifications?action=list");
    }
    
}

