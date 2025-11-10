package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import util.EmailUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Servlet xử lý form liên hệ và gửi email URL mapping: /contact
 */
@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    private String toEmail;

    @Override
    public void init() throws ServletException {
        super.init();

        // Lấy email nhận từ EmailUtil bằng reflection
        // Không throw exception để tránh context startup failure
        try {
            // Thử lấy CONTACT_EMAIL trước
            try {
                Field contactEmailField = EmailUtil.class.getDeclaredField("CONTACT_EMAIL");
                contactEmailField.setAccessible(true);
                toEmail = (String) contactEmailField.get(null);
            } catch (NoSuchFieldException e) {
                // Nếu không có CONTACT_EMAIL, lấy SMTP_USER
                Field smtpUserField = EmailUtil.class.getDeclaredField("SMTP_USER");
                smtpUserField.setAccessible(true);
                toEmail = (String) smtpUserField.get(null);
            }

            if (toEmail == null || toEmail.isEmpty()) {
                toEmail = "smartshop686868@gmail.com";
            }
        } catch (Exception e) {
            System.err.println("Error getting email from EmailUtil in ContactServlet.init(): " + e.getMessage());
            e.printStackTrace();
            // Set default email để servlet vẫn có thể start
            toEmail = "smartshop686868@gmail.com";
            // KHÔNG throw exception để tránh context startup failure
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hiển thị form liên hệ
        request.getRequestDispatcher("/views/store/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thông tin từ form
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String content = request.getParameter("content");

        // Validation
        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin!");
            // Set form values để giữ lại dữ liệu đã nhập (tránh null)
            request.setAttribute("fullName", fullName != null ? fullName : "");
            request.setAttribute("email", email != null ? email : "");
            request.setAttribute("phone", phone != null ? phone : "");
            request.setAttribute("content", content != null ? content : "");
            request.getRequestDispatcher("/views/store/contact.jsp").forward(request, response);
            return;
        }

        try {
            // Tạo nội dung email
            String subject = "Liên hệ từ SmartShop - " + fullName.trim();
            String emailBody = buildContactEmailBody(fullName.trim(), email.trim(), phone.trim(), content.trim());

            // Gửi email sử dụng EmailUtil (sẽ dùng thông tin từ EmailUtil)
            boolean emailSent = EmailUtil.sendEmail(toEmail, subject, emailBody);

            if (emailSent) {
                // Thành công
                request.setAttribute("successMessage", "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể.");
            } else {
                // Nếu EmailUtil không gửi được, thử gửi bằng phương pháp fallback
                try {
                    sendContactEmailUsingEmailUtilConfig(fullName.trim(), email.trim(), phone.trim(), content.trim());
                    request.setAttribute("successMessage", "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể.");
                } catch (Exception e2) {
                    throw e2;
                }
            }

            request.getRequestDispatcher("/views/store/contact.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi gửi email. Vui lòng thử lại sau hoặc liên hệ trực tiếp qua hotline.");
            // Set form values để giữ lại dữ liệu đã nhập (tránh null)
            request.setAttribute("fullName", fullName != null ? fullName : "");
            request.setAttribute("email", email != null ? email : "");
            request.setAttribute("phone", phone != null ? phone : "");
            request.setAttribute("content", content != null ? content : "");
            request.getRequestDispatcher("/views/store/contact.jsp").forward(request, response);
        }
    }

    /**
     * Tạo nội dung email HTML
     */
    private String buildContactEmailBody(String fullName, String email, String phone, String content) {
        return "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>"
                + "<h2 style='color: #dc3545; margin-bottom: 20px;'>Liên hệ mới từ SmartShop</h2>"
                + "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px;'>"
                + "<p style='margin: 10px 0;'><strong>Họ tên:</strong> " + escapeHtml(fullName) + "</p>"
                + "<p style='margin: 10px 0;'><strong>Email:</strong> " + escapeHtml(email) + "</p>"
                + "<p style='margin: 10px 0;'><strong>Số điện thoại:</strong> " + escapeHtml(phone) + "</p>"
                + "</div>"
                + "<div style='margin-top: 20px;'>"
                + "<h3 style='color: #333; margin-bottom: 10px;'>Nội dung:</h3>"
                + "<div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #dc3545; border-radius: 5px; white-space: pre-wrap;'>"
                + escapeHtml(content)
                + "</div>"
                + "</div>"
                + "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>"
                + "<p style='color: #999; font-size: 12px; text-align: center;'>Email này được gửi tự động từ form liên hệ SmartShop</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    /**
     * Escape HTML để tránh XSS
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Gửi email liên hệ (fallback method sử dụng thông tin từ EmailUtil)
     */
    private void sendContactEmailUsingEmailUtilConfig(String fullName, String email, String phone, String content)
            throws MessagingException {

        try {
            // Lấy thông tin từ EmailUtil bằng reflection
            Field smtpHostField = EmailUtil.class.getDeclaredField("SMTP_HOST");
            smtpHostField.setAccessible(true);
            String smtpHost = (String) smtpHostField.get(null);

            Field smtpPortField = EmailUtil.class.getDeclaredField("SMTP_PORT");
            smtpPortField.setAccessible(true);
            String smtpPort = (String) smtpPortField.get(null);

            Field smtpUserField = EmailUtil.class.getDeclaredField("SMTP_USER");
            smtpUserField.setAccessible(true);
            String smtpUser = (String) smtpUserField.get(null);

            Field smtpPasswordField = EmailUtil.class.getDeclaredField("SMTP_PASSWORD");
            smtpPasswordField.setAccessible(true);
            String smtpPassword = (String) smtpPasswordField.get(null);

            Field fromEmailField = EmailUtil.class.getDeclaredField("FROM_EMAIL");
            fromEmailField.setAccessible(true);
            String fromEmail = (String) fromEmailField.get(null);

            Field fromNameField = EmailUtil.class.getDeclaredField("FROM_NAME");
            fromNameField.setAccessible(true);
            String fromName = (String) fromNameField.get(null);
            
            // Đảm bảo fromEmail không null
            if (fromEmail == null || fromEmail.isEmpty()) {
                fromEmail = smtpUser; // Fallback về SMTP_USER nếu FROM_EMAIL null
            }

            // Cấu hình SMTP properties
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", smtpHost);

            // Tạo session với authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser, smtpPassword);
                }
            });

            // Tạo message
            Message message = new MimeMessage(session);
            // Đảm bảo fromEmail hợp lệ trước khi set
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                fromEmail = smtpUser; // Fallback về SMTP_USER
            }
            // Đảm bảo fromName hợp lệ
            String displayName = (fromName != null && !fromName.trim().isEmpty()) ? fromName.trim() : "SmartShop";
            try {
                // Sử dụng constructor 2 tham số (address, personal) - tự động xử lý encoding
                message.setFrom(new InternetAddress(fromEmail.trim(), displayName));
            } catch (Exception e) {
                // Nếu có lỗi, thử không có display name
                System.err.println("Error setting From address with display name: " + e.getMessage());
                message.setFrom(new InternetAddress(fromEmail.trim()));
            }
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Liên hệ từ SmartShop - " + fullName);

            // Nội dung email HTML
            String emailBody = buildContactEmailBody(fullName, email, phone, content);
            message.setContent(emailBody, "text/html; charset=utf-8");

            // Gửi email
            Transport.send(message);

            System.out.println("Contact email sent successfully to: " + toEmail);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MessagingException("Cannot access EmailUtil configuration: " + e.getMessage(), e);
        }
    }
}
