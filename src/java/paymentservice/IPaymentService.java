package paymentservice;

import model.Payment;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Payment
 */
public interface IPaymentService {

    /**
     * Thêm payment mới
     *
     * @param payment Payment object cần thêm
     */
    void addPayment(Payment payment);

    /**
     * Cập nhật payment
     *
     * @param payment Payment object với thông tin đã cập nhật
     */
    void updatePayment(Payment payment);

    /**
     * Lấy payment theo ID
     *
     * @param paymentID ID payment
     * @return Payment object hoặc null nếu không tìm thấy
     */
    Payment getPaymentById(int paymentID);

    /**
     * Lấy tất cả payments của một order
     *
     * @param orderID ID order
     * @return Danh sách các Payment
     */
    List<Payment> getPaymentsByOrder(int orderID);

    /**
     * Lấy tất cả payments theo status
     *
     * @param status Trạng thái payment
     * @return Danh sách các Payment
     */
    List<Payment> getPaymentsByStatus(String status);

    /**
     * Lấy tất cả payments
     *
     * @return Danh sách các Payment
     */
    List<Payment> getAllPayments();
}
