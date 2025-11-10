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
        try {
            // Cấu hình properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            
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
            Transport.send(message);
            
            System.out.println("Email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("MessagingException sending email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error sending email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
            return false;
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
}

