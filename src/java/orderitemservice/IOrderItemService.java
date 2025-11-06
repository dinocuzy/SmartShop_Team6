package orderitemservice;

import model.OrderItem;
import java.util.List;

/**
 * Interface định nghĩa business logic cho OrderItem
 */
public interface IOrderItemService {

    /**
     * Thêm order item mới
     *
     * @param orderItem OrderItem object cần thêm
     */
    void addOrderItem(OrderItem orderItem);

    /**
     * Cập nhật order item
     *
     * @param orderItem OrderItem object với thông tin đã cập nhật
     */
    void updateOrderItem(OrderItem orderItem);

    /**
     * Xóa order item
     *
     * @param orderItemID ID order item cần xóa
     */
    void deleteOrderItem(int orderItemID);

    /**
     * Lấy order item theo ID
     *
     * @param orderItemID ID order item
     * @return OrderItem object hoặc null nếu không tìm thấy
     */
    OrderItem getOrderItemById(int orderItemID);

    /**
     * Lấy tất cả order items của một order
     *
     * @param orderID ID order
     * @return Danh sách các OrderItem
     */
    List<OrderItem> getOrderItemsByOrder(int orderID);

    /**
     * Lấy tất cả order items của một product
     *
     * @param productID ID product
     * @return Danh sách các OrderItem
     */
    List<OrderItem> getOrderItemsByProduct(int productID);

    /**
     * Xóa tất cả order items của một order
     *
     * @param orderID ID order
     */
    void deleteOrderItemsByOrder(int orderID);
}
