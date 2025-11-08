package orderstatushistoryservice;

import model.OrderStatusHistory;
import orderstatushistorydao.IOrderStatusHistoryDAO;
import orderstatushistorydao.OrderStatusHistoryDAO;
import java.util.List;

/**
 * Service implementation cho OrderStatusHistory
 */
public class OrderStatusHistoryService implements IOrderStatusHistoryService {
    
    private IOrderStatusHistoryDAO orderStatusHistoryDAO;
    
    public OrderStatusHistoryService() {
        this.orderStatusHistoryDAO = new OrderStatusHistoryDAO();
    }
    
    @Override
    public boolean recordStatusChange(int orderID, String oldStatus, String newStatus, Integer changedBy) {
        // Validate input
        if (orderID <= 0) {
            System.err.println("Invalid orderID: " + orderID);
            return false;
        }
        
        if (oldStatus == null || oldStatus.trim().isEmpty()) {
            oldStatus = "New";
        }
        
        if (newStatus == null || newStatus.trim().isEmpty()) {
            System.err.println("NewStatus is required");
            return false;
        }
        
        int result = orderStatusHistoryDAO.recordStatusChange(orderID, oldStatus, newStatus, changedBy);
        return result > 0;
    }
    
    @Override
    public List<OrderStatusHistory> getHistoryByOrderID(int orderID) {
        if (orderID <= 0) {
            return new java.util.ArrayList<>();
        }
        
        return orderStatusHistoryDAO.getHistoryByOrderID(orderID);
    }
    
    @Override
    public OrderStatusHistory getById(int historyID) {
        if (historyID <= 0) {
            return null;
        }
        
        return orderStatusHistoryDAO.getById(historyID);
    }
}

