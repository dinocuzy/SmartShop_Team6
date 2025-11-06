package paymentmethoddao;

import model.PaymentMethod;
import java.util.List;

public interface IPaymentMethodDAO {
    List<PaymentMethod> getAll(boolean includeInactive);
    PaymentMethod getById(int paymentMethodID);
    int insert(PaymentMethod paymentMethod);
    boolean update(PaymentMethod paymentMethod);
    boolean delete(int paymentMethodID);
}
