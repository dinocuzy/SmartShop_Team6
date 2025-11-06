package orderservice;

import model.Order;
import orderdao.IOrderDAO;
import orderdao.OrderDAO;
import java.util.List;

/**
 * Implementation của IOrderService
 * Chứa business logic và validation cho Order
 * Sử dụng OrderDAO (JDBC) để truy cập dữ liệu
 */
public class OrderService implements IOrderService {

    private final IOrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    @Override
    public int addOrder(Order order) {
        validateOrder(order);
        return orderDAO.insert(order);
    }

    @Override
    public void updateOrder(Order order) {
        validateOrder(order);
        if (order.getOrderID() <= 0) {
            throw new IllegalArgumentException("Order ID must be greater than 0");
        }
        boolean updated = orderDAO.update(order);
        if (!updated) {
            throw new RuntimeException("Failed to update order with ID: " + order.getOrderID());
        }
    }

    @Override
    public Order getOrderById(int orderID) {
        if (orderID <= 0) {
            throw new IllegalArgumentException("Order ID must be greater than 0");
        }
        return orderDAO.getById(orderID);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderDAO.getAll();
    }

    @Override
    public List<Order> getOrdersByUser(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        return orderDAO.getByUser(userID);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return orderDAO.getByStatus(status);
    }

    @Override
    public List<Order> getPagedOrders(int pageNumber, int pageSize, String sortBy, String sortOrder,
                                       String searchKeyword, String status, int userID) {
        return orderDAO.getPagedOrders(pageNumber, pageSize, sortBy, sortOrder, searchKeyword, status, userID);
    }

    @Override
    public int countOrders(String searchKeyword, String status, int userID) {
        return orderDAO.count(searchKeyword, status, userID);
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getUserID() <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total Amount must be greater than or equal to 0");
        }
        if (order.getOrderStatus() == null || order.getOrderStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Order Status is required");
        }
    }
}
