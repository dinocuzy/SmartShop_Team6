package paymentmethodservice;

import model.PaymentMethod;
import paymentmethoddao.IPaymentMethodDAO;
import paymentmethoddao.PaymentMethodDAO;
import java.util.List;

/**
 * Implementation của IPaymentMethodService
 * Chứa business logic cho PaymentMethod
 * Sử dụng PaymentMethodDAO (JDBC) để truy cập dữ liệu
 */
public class PaymentMethodService implements IPaymentMethodService {

    private IPaymentMethodDAO paymentMethodDAO;

    public PaymentMethodService() {
        this.paymentMethodDAO = new PaymentMethodDAO();
    }

    @Override
    public List<PaymentMethod> getAllPaymentMethods(boolean includeInactive) {
        return paymentMethodDAO.getAll(includeInactive);
    }

    @Override
    public PaymentMethod getPaymentMethodById(int paymentMethodID) {
        if (paymentMethodID <= 0) {
            throw new IllegalArgumentException("Payment Method ID must be greater than 0");
        }
        return paymentMethodDAO.getById(paymentMethodID);
    }

    @Override
    public void addPaymentMethod(PaymentMethod paymentMethod) {
        validatePaymentMethod(paymentMethod);
        paymentMethodDAO.insert(paymentMethod);
    }

    @Override
    public void updatePaymentMethod(PaymentMethod paymentMethod) {
        validatePaymentMethod(paymentMethod);
        if (paymentMethod.getPaymentMethodID() <= 0) {
            throw new IllegalArgumentException("Payment Method ID must be greater than 0");
        }
        boolean updated = paymentMethodDAO.update(paymentMethod);
        if (!updated) {
            throw new RuntimeException("Failed to update payment method with ID: " + paymentMethod.getPaymentMethodID());
        }
    }

    @Override
    public void deletePaymentMethod(int paymentMethodID) {
        if (paymentMethodID <= 0) {
            throw new IllegalArgumentException("Payment Method ID must be greater than 0");
        }
        boolean deleted = paymentMethodDAO.delete(paymentMethodID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete payment method with ID: " + paymentMethodID);
        }
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment Method cannot be null");
        }
        if (paymentMethod.getMethodName() == null || paymentMethod.getMethodName().trim().isEmpty()) {
            throw new IllegalArgumentException("Method name is required");
        }
    }
}
