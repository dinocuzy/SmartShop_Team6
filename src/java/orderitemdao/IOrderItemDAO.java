package orderitemdao;

import model.OrderItem;
import java.util.List;

public interface IOrderItemDAO {
    List<OrderItem> getAll();
    OrderItem getById(int orderItemID);
    List<OrderItem> getByOrder(int orderID);
    List<OrderItem> getByProduct(int productID);
    int insert(OrderItem orderItem);
    boolean update(OrderItem orderItem);
    boolean delete(int orderItemID);
    boolean deleteByOrder(int orderID);
}
