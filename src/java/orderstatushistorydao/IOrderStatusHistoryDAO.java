package orderstatushistorydao;

import model.OrderStatusHistory;
import java.util.List;

/**
 * Interface định nghĩa các thao tác với bảng OrderStatusHistory
 */
public interface IOrderStatusHistoryDAO {
    
    /**
     * Ghi lại lịch sử thay đổi trạng thái đơn hàng
     * @param orderID ID của đơn hàng
     * @param oldStatus Trạng thái cũ
     * @param newStatus Trạng thái mới
     * @param changedBy ID của user thay đổi (có thể null)
     * @return ID của record vừa tạo, -1 nếu lỗi
     */
    int recordStatusChange(int orderID, String oldStatus, String newStatus, Integer changedBy);
    
    /**
     * Lấy tất cả lịch sử thay đổi trạng thái của một đơn hàng
     * @param orderID ID của đơn hàng
     * @return Danh sách lịch sử thay đổi, sắp xếp theo thời gian tăng dần
     */
    List<OrderStatusHistory> getHistoryByOrderID(int orderID);
    
    /**
     * Lấy record lịch sử theo ID
     * @param historyID ID của record
     * @return OrderStatusHistory object hoặc null nếu không tìm thấy
     */
    OrderStatusHistory getById(int historyID);
    
    /**
     * Xóa lịch sử của một đơn hàng (khi xóa đơn hàng)
     * @param orderID ID của đơn hàng
     * @return Số record đã xóa
     */
    int deleteByOrderID(int orderID);
}

