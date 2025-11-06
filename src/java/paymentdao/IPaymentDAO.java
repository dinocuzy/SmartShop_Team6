package paymentdao;

import model.Payment;
import java.util.List;

public interface IPaymentDAO {
    List<Payment> getAll();
    Payment getById(int paymentID);
    List<Payment> getByOrder(int orderID);
    List<Payment> getByStatus(String status);
    int insert(Payment payment);
    boolean update(Payment payment);
    boolean delete(int paymentID);
}
