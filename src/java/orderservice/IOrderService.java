package orderservice;

import model.Order;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Order
 */
public interface IOrderService {

    /**
     * Thêm order mới
     *
     * @param order Order object cần thêm
     * @return OrderID được tạo tự động
     */
    int addOrder(Order order);

    /**
     * Cập nhật order
     *
     * @param order Order object với thông tin đã cập nhật
     */
    void updateOrder(Order order);

    /**
     * Lấy order theo ID
     *
     * @param orderID ID order
     * @return Order object hoặc null nếu không tìm thấy
     */
    Order getOrderById(int orderID);

    /**
     * Lấy tất cả orders
     *
     * @return Danh sách các Order
     */
    List<Order> getAllOrders();

    /**
     * Lấy danh sách orders theo user
     *
     * @param userID ID user
     * @return Danh sách các Order
     */
    List<Order> getOrdersByUser(int userID);

    /**
     * Lấy danh sách orders theo status
     *
     * @param status Trạng thái order
     * @return Danh sách các Order
     */
    List<Order> getOrdersByStatus(String status);

    /**
     * Lấy danh sách orders với phân trang
     *
     * @param pageNumber Số trang
     * @param pageSize Số lượng order mỗi trang
     * @param sortBy Cột sắp xếp
     * @param sortOrder Thứ tự sắp xếp
     * @param searchKeyword Từ khóa tìm kiếm
     * @param status Trạng thái lọc
     * @param userID ID user lọc
     * @return Danh sách các Order
     */
    List<Order> getPagedOrders(int pageNumber, int pageSize, String sortBy, String sortOrder,
                                String searchKeyword, String status, int userID);

    /**
     * Đếm tổng số orders
     *
     * @param searchKeyword Từ khóa tìm kiếm
     * @param status Trạng thái lọc
     * @param userID ID user lọc
     * @return Tổng số orders
     */
    int countOrders(String searchKeyword, String status, int userID);
}
