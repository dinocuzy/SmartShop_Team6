package orderdao;

import model.Order;
import java.util.List;

public interface IOrderDAO {
    List<Order> getAll();
    Order getById(int orderID);
    List<Order> getByUser(int userID);
    List<Order> getByStatus(String status);
    List<Order> getPagedOrders(int pageNumber, int pageSize, String sortBy, String sortOrder,
                               String searchKeyword, String status, int userID);
    int count(String searchKeyword, String status, int userID);
    int insert(Order order);
    boolean update(Order order);
    boolean delete(int orderID);
}
