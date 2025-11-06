package paymentservice;

import model.Payment;
import paymentdao.IPaymentDAO;
import paymentdao.PaymentDAO;
import java.util.List;

/**
 * Implementation của IPaymentService
 * Chứa business logic cho Payment
 * Sử dụng PaymentDAO (JDBC) để truy cập dữ liệu
 */
public class PaymentService implements IPaymentService {
    
    private final IPaymentDAO paymentDAO;
    
    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
    }
    
    @Override
    public void addPayment(Payment payment) {
        validatePayment(payment);
        paymentDAO.insert(payment);
    }
    
    @Override
    public void updatePayment(Payment payment) {
        validatePayment(payment);
        if (payment.getPaymentID() <= 0) {
            throw new IllegalArgumentException("Payment ID must be greater than 0");
        }
        boolean updated = paymentDAO.update(payment);
        if (!updated) {
            throw new RuntimeException("Failed to update payment with ID: " + payment.getPaymentID());
        }
    }
    
    @Override
    public Payment getPaymentById(int paymentID) {
        if (paymentID <= 0) {
            throw new IllegalArgumentException("Payment ID must be greater than 0");
        }
        return paymentDAO.getById(paymentID);
    }
    
    @Override
    public List<Payment> getPaymentsByOrder(int orderID) {
        if (orderID <= 0) {
            throw new IllegalArgumentException("Order ID must be greater than 0");
        }
        return paymentDAO.getByOrder(orderID);
    }
    
    @Override
    public List<Payment> getPaymentsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return paymentDAO.getByStatus(status);
    }
    
    @Override
    public List<Payment> getAllPayments() {
        return paymentDAO.getAll();
    }
    
    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        if (payment.getOrderID() <= 0) {
            throw new IllegalArgumentException("Order ID must be greater than 0");
        }
        if (payment.getPaymentMethodID() <= 0) {
            throw new IllegalArgumentException("Payment Method ID must be greater than 0");
        }
        if (payment.getAmount() == null || payment.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (payment.getPaymentStatus() == null || payment.getPaymentStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Payment Status is required");
        }
    }
}
