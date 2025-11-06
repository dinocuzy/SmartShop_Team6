package paymentmethodservice;

import model.PaymentMethod;
import java.util.List;

/**
 * Interface định nghĩa business logic cho PaymentMethod
 */
public interface IPaymentMethodService {

    /**
     * Lấy tất cả phương thức thanh toán
     *
     * @param includeInactive Nếu true, lấy cả phương thức thanh toán inactive (IsActive = 0)
     * @return Danh sách các PaymentMethod
     */
    List<PaymentMethod> getAllPaymentMethods(boolean includeInactive);

    /**
     * Lấy phương thức thanh toán theo ID
     *
     * @param paymentMethodID ID phương thức thanh toán
     * @return PaymentMethod object hoặc null nếu không tìm thấy
     */
    PaymentMethod getPaymentMethodById(int paymentMethodID);

    /**
     * Thêm phương thức thanh toán mới
     *
     * @param paymentMethod PaymentMethod object cần thêm
     */
    void addPaymentMethod(PaymentMethod paymentMethod);

    /**
     * Cập nhật phương thức thanh toán
     *
     * @param paymentMethod PaymentMethod object với thông tin đã cập nhật
     */
    void updatePaymentMethod(PaymentMethod paymentMethod);

    /**
     * Xóa phương thức thanh toán (soft delete)
     *
     * @param paymentMethodID ID phương thức thanh toán cần xóa
     */
    void deletePaymentMethod(int paymentMethodID);
}
