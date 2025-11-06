package orderitemservice;

import model.OrderItem;
import orderitemdao.IOrderItemDAO;
import orderitemdao.OrderItemDAO;
import java.util.List;

/**
 * Implementation của IOrderItemService
 * Chứa business logic cho OrderItem
 * Sử dụng OrderItemDAO (JDBC) để truy cập dữ liệu
 */
public class OrderItemService implements IOrderItemService {
    
    private final IOrderItemDAO orderItemDAO;
    
    public OrderItemService() {
        this.orderItemDAO = new OrderItemDAO();
    }
    
    @Override
    public void addOrderItem(OrderItem orderItem) {
        validateOrderItem(orderItem);
        orderItemDAO.insert(orderItem);
    }
    
    @Override
    public void updateOrderItem(OrderItem orderItem) {
        validateOrderItem(orderItem);
        if (orderItem.getOrderItemID() <= 0) {
            throw new IllegalArgumentException("Order Item ID must be greater than 0");
        }
        boolean updated = orderItemDAO.update(orderItem);
        if (!updated) {
            throw new RuntimeException("Failed to update order item with ID: " + orderItem.getOrderItemID());
        }
    }
    
    @Override
    public void deleteOrderItem(int orderItemID) {
        if (orderItemID <= 0) {
            throw new IllegalArgumentException("Order Item ID must be greater than 0");
        }
        boolean deleted = orderItemDAO.delete(orderItemID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete order item with ID: " + orderItemID);
        }
    }
    
    @Override
    public OrderItem getOrderItemById(int orderItemID) {
        if (orderItemID <= 0) {
            throw new IllegalArgumentException("Order Item ID must be greater than 0");
        }
        return orderItemDAO.getById(orderItemID);
    }
    
    @Override
    public List<OrderItem> getOrderItemsByOrder(int orderID) {
        if (orderID <= 0) {
            throw new IllegalArgumentException("Order ID must be greater than 0");
        }
        return orderItemDAO.getByOrder(orderID);
    }
    
    @Override
    public List<OrderItem> getOrderItemsByProduct(int productID) {
        if (productID <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        return orderItemDAO.getByProduct(productID);
    }
    
    @Override
    public void deleteOrderItemsByOrder(int orderID) {
        if (orderID <= 0) {
            throw new IllegalArgumentException("Order ID must be greater than 0");
        }
        boolean deleted = orderItemDAO.deleteByOrder(orderID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete order items for order ID: " + orderID);
        }
    }
    
    private void validateOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("Order Item cannot be null");
        }
        if (orderItem.getOrderID() <= 0) {
            throw new IllegalArgumentException("Order ID must be greater than 0");
        }
        if (orderItem.getProductID() <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        if (orderItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (orderItem.getUnitPrice() == null || orderItem.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit Price must be greater than or equal to 0");
        }
    }
}
