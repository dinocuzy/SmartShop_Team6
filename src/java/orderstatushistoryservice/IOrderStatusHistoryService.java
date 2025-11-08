package orderstatushistoryservice;

import model.OrderStatusHistory;
import java.util.List;

/**
 * Interface Service cho OrderStatusHistory
 */
public interface IOrderStatusHistoryService {
    
    /**
     * Ghi lại lịch sử thay đổi trạng thái đơn hàng
     * @param orderID ID của đơn hàng
     * @param oldStatus Trạng thái cũ
     * @param newStatus Trạng thái mới
     * @param changedBy ID của user thay đổi (có thể null)
     * @return true nếu thành công
     */
    boolean recordStatusChange(int orderID, String oldStatus, String newStatus, Integer changedBy);
    
    /**
     * Lấy tất cả lịch sử thay đổi trạng thái của một đơn hàng
     * @param orderID ID của đơn hàng
     * @return Danh sách lịch sử thay đổi
     */
    List<OrderStatusHistory> getHistoryByOrderID(int orderID);
    
    /**
     * Lấy record lịch sử theo ID
     * @param historyID ID của record
     * @return OrderStatusHistory object hoặc null
     */
    OrderStatusHistory getById(int historyID);
}

