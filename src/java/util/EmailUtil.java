package util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * Utility class để gửi email
 * Sử dụng Jakarta Mail API
 */
public class EmailUtil {
    
    // Cấu hình SMTP (có thể lưu trong file config hoặc environment variables)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "binh222120@gmail.com"; // Thay bằng email của bạn
    private static final String SMTP_PASSWORD = "utnv   defw hqjt wqnd"; // Thay bằng app password của bạn
    private static final String FROM_EMAIL = "binh222120@gmail.com"; // Email gửi
    private static final String FROM_NAME = "SmartShop";
    private static final String CONTACT_EMAIL = "binh222120@gmail.com"; // Email nhận liên hệ
    
    /**
     * Gửi email
     * @param toEmail Email người nhận
     * @param subject Tiêu đề email
     * @param body Nội dung email (HTML)
     * @return true nếu gửi thành công, false nếu có lỗi
     */
    public static boolean sendEmail(String toEmail, String subject, String body) {
        System.out.println("=== EmailUtil.sendEmail called ===");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("SMTP Host: " + SMTP_HOST);
        System.out.println("SMTP Port: " + SMTP_PORT);
        System.out.println("SMTP User: " + SMTP_USER);
        System.out.println("From Email: " + FROM_EMAIL);
        
        try {
            // Cấu hình properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            System.out.println("SMTP properties configured");
            
            // Tạo Authenticator
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
                }
            };
            
            // Tạo session
            Session session = Session.getInstance(props, authenticator);
            
            // Tạo message
            Message message = new MimeMessage(session);
            
            // Set From address với error handling
            try {
                InternetAddress fromAddress;
                if (FROM_NAME != null && !FROM_NAME.trim().isEmpty()) {
                    // Tạo InternetAddress với display name
                    fromAddress = new InternetAddress(FROM_EMAIL, FROM_NAME);
                } else {
                    fromAddress = new InternetAddress(FROM_EMAIL);
                }
                message.setFrom(fromAddress);
            } catch (Exception fromException) {
                System.err.println("Error setting From address: " + fromException.getMessage());
                fromException.printStackTrace();
                // Fallback: set From without display name
                try {
                    message.setFrom(new InternetAddress(FROM_EMAIL));
                } catch (Exception fallbackException) {
                    System.err.println("Error setting From address (fallback): " + fallbackException.getMessage());
                    fallbackException.printStackTrace();
                    return false;
                }
            }
            
            // Set recipient
            try {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            } catch (Exception recipientException) {
                System.err.println("Error setting recipient: " + recipientException.getMessage());
                recipientException.printStackTrace();
                return false;
            }
            
            // Set subject và content
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            
            // Gửi email
            System.out.println("Attempting to send email via Transport.send()...");
            Transport.send(message);
            
            System.out.println("✓ Email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("✗ MessagingException sending email to " + toEmail + ": " + e.getMessage());
            System.err.println("Exception class: " + e.getClass().getName());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Unexpected error sending email to " + toEmail + ": " + e.getMessage());
            System.err.println("Exception class: " + e.getClass().getName());
            e.printStackTrace();
            return false;
        } finally {
            System.out.println("=== EmailUtil.sendEmail completed ===");
        }
    }
    
    /**
     * Gửi email reset password
     * @param toEmail Email người nhận
     * @param resetToken Token để reset password
     * @param resetLink Link để reset password
     * @return true nếu gửi thành công
     */
    public static boolean sendPasswordResetEmail(String toEmail, String resetToken, String resetLink) {
        String subject = "Đặt lại mật khẩu - SmartShop";
        String body = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #667eea;'>Đặt lại mật khẩu</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản SmartShop.</p>" +
                "<p>Vui lòng click vào link bên dưới để đặt lại mật khẩu:</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + resetLink + "' style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); " +
                "color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>" +
                "Đặt lại mật khẩu</a>" +
                "</div>" +
                "<p>Hoặc copy link sau vào trình duyệt:</p>" +
                "<p style='background: #f5f5f5; padding: 10px; word-break: break-all;'>" + resetLink + "</p>" +
                "<p><strong>Token:</strong> " + resetToken + "</p>" +
                "<p style='color: #999; font-size: 12px;'>Link này sẽ hết hạn sau 24 giờ.</p>" +
                "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='color: #999; font-size: 12px;'>SmartShop - Hệ thống quản lý cửa hàng</p>" +
                "</div>" +
                "</body>" +
                "</html>";
        
        return sendEmail(toEmail, subject, body);
    }
    
    /**
     * Gửi email thông tin đơn hàng cho khách hàng
     * @param toEmail Email khách hàng
     * @param order Đơn hàng
     * @param orderItems Danh sách sản phẩm trong đơn hàng
     * @param customerName Tên khách hàng
     * @param shippingAddress Địa chỉ giao hàng (có thể null)
     * @param paymentMethod Phương thức thanh toán (có thể null)
     * @return true nếu gửi thành công
     */
    public static boolean sendOrderConfirmationEmail(
            String toEmail, 
            model.Order order, 
            java.util.List<model.OrderItem> orderItems,
            String customerName,
            model.Address shippingAddress,
            String paymentMethod) {
        
        System.out.println("=== EmailUtil.sendOrderConfirmationEmail called ===");
        System.out.println("To Email: " + toEmail);
        System.out.println("Order ID: " + (order != null ? order.getOrderID() : "null"));
        System.out.println("Order Items: " + (orderItems != null ? orderItems.size() : "null"));
        System.out.println("Customer Name: " + customerName);
        System.out.println("Shipping Address: " + (shippingAddress != null ? "provided" : "null"));
        System.out.println("Payment Method: " + paymentMethod);
        
        if (order == null) {
            System.err.println("✗ Error: order is null");
            return false;
        }
        
        if (orderItems == null || orderItems.isEmpty()) {
            System.err.println("✗ Warning: orderItems is null or empty");
        }
        
        String subject = "Xác nhận đơn hàng #" + order.getOrderID() + " - SmartShop";
        System.out.println("Email subject: " + subject);
        
        // Format ngày đơn hàng
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        String orderDateStr = dateFormat.format(order.getOrderDate());
        
        // Format số tiền
        java.text.DecimalFormat currencyFormat = new java.text.DecimalFormat("#,###");
        String totalAmountStr = currencyFormat.format(order.getTotalAmount()) + " VNĐ";
        
        // Tạo bảng sản phẩm
        StringBuilder productsTable = new StringBuilder();
        productsTable.append("<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>");
        productsTable.append("<thead>");
        productsTable.append("<tr style='background-color: #f8f9fa;'>");
        productsTable.append("<th style='padding: 12px; text-align: left; border: 1px solid #dee2e6;'>Sản phẩm</th>");
        productsTable.append("<th style='padding: 12px; text-align: center; border: 1px solid #dee2e6;'>Số lượng</th>");
        productsTable.append("<th style='padding: 12px; text-align: right; border: 1px solid #dee2e6;'>Đơn giá</th>");
        productsTable.append("<th style='padding: 12px; text-align: right; border: 1px solid #dee2e6;'>Thành tiền</th>");
        productsTable.append("</tr>");
        productsTable.append("</thead>");
        productsTable.append("<tbody>");
        
        for (model.OrderItem item : orderItems) {
            String productName = item.getProductName() != null ? item.getProductName() : "Sản phẩm #" + item.getProductID();
            String quantity = String.valueOf(item.getQuantity());
            String unitPrice = currencyFormat.format(item.getUnitPrice()) + " VNĐ";
            String itemTotal = currencyFormat.format(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity()))) + " VNĐ";
            
            productsTable.append("<tr>");
            productsTable.append("<td style='padding: 12px; border: 1px solid #dee2e6;'>").append(productName).append("</td>");
            productsTable.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6;'>").append(quantity).append("</td>");
            productsTable.append("<td style='padding: 12px; text-align: right; border: 1px solid #dee2e6;'>").append(unitPrice).append("</td>");
            productsTable.append("<td style='padding: 12px; text-align: right; border: 1px solid #dee2e6;'>").append(itemTotal).append("</td>");
            productsTable.append("</tr>");
        }
        
        productsTable.append("</tbody>");
        productsTable.append("<tfoot>");
        productsTable.append("<tr style='background-color: #f8f9fa; font-weight: bold;'>");
        productsTable.append("<td colspan='3' style='padding: 12px; text-align: right; border: 1px solid #dee2e6;'>TỔNG CỘNG:</td>");
        productsTable.append("<td style='padding: 12px; text-align: right; border: 1px solid #dee2e6;'>").append(totalAmountStr).append("</td>");
        productsTable.append("</tr>");
        productsTable.append("</tfoot>");
        productsTable.append("</table>");
        
        // Thông tin địa chỉ giao hàng
        String shippingInfo = "";
        if (shippingAddress != null) {
            shippingInfo = "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>";
            shippingInfo += "<h4 style='margin-top: 0; color: #667eea;'>Địa chỉ giao hàng:</h4>";
            if (shippingAddress.getLine1() != null) {
                shippingInfo += "<p style='margin: 5px 0;'><strong>Địa chỉ:</strong> " + shippingAddress.getLine1();
                if (shippingAddress.getLine2() != null && !shippingAddress.getLine2().trim().isEmpty()) {
                    shippingInfo += ", " + shippingAddress.getLine2();
                }
                shippingInfo += "</p>";
            }
            if (shippingAddress.getCity() != null) {
                shippingInfo += "<p style='margin: 5px 0;'><strong>Thành phố:</strong> " + shippingAddress.getCity() + "</p>";
            }
            if (shippingAddress.getPhone() != null) {
                shippingInfo += "<p style='margin: 5px 0;'><strong>Điện thoại:</strong> " + shippingAddress.getPhone() + "</p>";
            }
            shippingInfo += "</div>";
        }
        
        // Trạng thái đơn hàng
        String orderStatusText = "";
        String statusColor = "#667eea";
        if ("Pending".equals(order.getOrderStatus())) {
            orderStatusText = "Chờ xác nhận";
            statusColor = "#ffc107";
        } else if ("Paid".equals(order.getOrderStatus())) {
            orderStatusText = "Đã thanh toán";
            statusColor = "#28a745";
        } else if ("Processing".equals(order.getOrderStatus())) {
            orderStatusText = "Đang xử lý";
            statusColor = "#17a2b8";
        } else if ("Shipped".equals(order.getOrderStatus())) {
            orderStatusText = "Đang giao hàng";
            statusColor = "#007bff";
        } else if ("Delivered".equals(order.getOrderStatus())) {
            orderStatusText = "Đã giao hàng";
            statusColor = "#28a745";
        } else if ("Cancelled".equals(order.getOrderStatus())) {
            orderStatusText = "Đã hủy";
            statusColor = "#dc3545";
        } else {
            orderStatusText = order.getOrderStatus();
        }
        
        String body = "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px 10px 0 0; text-align: center;'>" +
                "<h1 style='margin: 0;'>Cảm ơn bạn đã đặt hàng!</h1>" +
                "</div>" +
                "<div style='background-color: white; padding: 30px; border: 1px solid #dee2e6; border-top: none; border-radius: 0 0 10px 10px;'>" +
                "<p>Xin chào <strong>" + (customerName != null ? customerName : "Khách hàng") + "</strong>,</p>" +
                "<p>Cảm ơn bạn đã đặt hàng tại <strong>SmartShop</strong>. Chúng tôi đã nhận được đơn hàng của bạn và đang xử lý.</p>" +
                
                "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<h3 style='margin-top: 0; color: #667eea;'>Thông tin đơn hàng</h3>" +
                "<p style='margin: 5px 0;'><strong>Mã đơn hàng:</strong> #" + order.getOrderID() + "</p>" +
                "<p style='margin: 5px 0;'><strong>Ngày đặt hàng:</strong> " + orderDateStr + "</p>" +
                "<p style='margin: 5px 0;'><strong>Trạng thái:</strong> <span style='color: " + statusColor + "; font-weight: bold;'>" + orderStatusText + "</span></p>" +
                (paymentMethod != null ? "<p style='margin: 5px 0;'><strong>Phương thức thanh toán:</strong> " + paymentMethod + "</p>" : "") +
                "</div>" +
                
                shippingInfo +
                
                "<h3 style='color: #667eea; margin-top: 30px;'>Chi tiết đơn hàng</h3>" +
                productsTable.toString() +
                
                (order.getNote() != null && !order.getNote().trim().isEmpty() ? 
                "<div style='background-color: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #ffc107;'>" +
                "<p style='margin: 0;'><strong>Ghi chú:</strong> " + order.getNote() + "</p>" +
                "</div>" : "") +
                
                "<div style='background-color: #e7f3ff; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #007bff;'>" +
                "<p style='margin: 0;'><strong>Lưu ý:</strong> Bạn có thể theo dõi trạng thái đơn hàng trong tài khoản của mình. Chúng tôi sẽ thông báo cho bạn khi đơn hàng được cập nhật.</p>" +
                "</div>" +
                
                "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                "<p style='color: #999; font-size: 12px; text-align: center;'>SmartShop - Hệ thống quản lý cửa hàng<br>" +
                "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        
        System.out.println("Email body length: " + body.length() + " characters");
        System.out.println("Calling sendEmail()...");
        
        boolean result = sendEmail(toEmail, subject, body);
        
        System.out.println("=== EmailUtil.sendOrderConfirmationEmail completed: " + (result ? "SUCCESS" : "FAILED") + " ===");
        
        return result;
    }
}

