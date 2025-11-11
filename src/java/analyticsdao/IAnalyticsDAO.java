package analyticsdao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface định nghĩa các phương thức DAO cho Analytics
 */
public interface IAnalyticsDAO {
    
    /**
     * Lấy tổng doanh thu
     * @param startDate Ngày bắt đầu (null nếu lấy tất cả)
     * @param endDate Ngày kết thúc (null nếu lấy tất cả)
     * @return Tổng doanh thu
     */
    BigDecimal getTotalRevenue(Date startDate, Date endDate);
    
    /**
     * Lấy tổng số đơn hàng
     * @param startDate Ngày bắt đầu (null nếu lấy tất cả)
     * @param endDate Ngày kết thúc (null nếu lấy tất cả)
     * @return Tổng số đơn hàng
     */
    int getTotalOrders(Date startDate, Date endDate);
    
    /**
     * Lấy tổng lượt xem sản phẩm
     * @param startDate Ngày bắt đầu (null nếu lấy tất cả)
     * @param endDate Ngày kết thúc (null nếu lấy tất cả)
     * @return Tổng lượt xem
     */
    int getTotalViews(Date startDate, Date endDate);
    
    /**
     * Lấy doanh thu trung bình mỗi đơn hàng
     * @param startDate Ngày bắt đầu (null nếu lấy tất cả)
     * @param endDate Ngày kết thúc (null nếu lấy tất cả)
     * @return Doanh thu trung bình
     */
    BigDecimal getAvgRevenuePerOrder(Date startDate, Date endDate);
    
    /**
     * Lấy doanh thu theo ngày (cho biểu đồ)
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return List Map với key: date (String), revenue (BigDecimal)
     */
    List<Map<String, Object>> getRevenueByDate(Date startDate, Date endDate);
    
    /**
     * Lấy số đơn hàng theo ngày (cho biểu đồ)
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return List Map với key: date (String), orders (Integer)
     */
    List<Map<String, Object>> getOrdersByDate(Date startDate, Date endDate);
    
    /**
     * Lấy lượt xem theo ngày (cho biểu đồ)
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return List Map với key: date (String), views (Integer)
     */
    List<Map<String, Object>> getViewsByDate(Date startDate, Date endDate);
    
    /**
     * Lấy doanh thu theo tháng (cho biểu đồ)
     * @param year Năm (null nếu lấy tất cả)
     * @return List Map với key: month (String), revenue (BigDecimal)
     */
    List<Map<String, Object>> getRevenueByMonth(Integer year);
    
    /**
     * Lấy số đơn hàng theo tháng (cho biểu đồ)
     * @param year Năm (null nếu lấy tất cả)
     * @return List Map với key: month (String), orders (Integer)
     */
    List<Map<String, Object>> getOrdersByMonth(Integer year);
    
    /**
     * Lấy lượt xem theo tháng (cho biểu đồ)
     * @param year Năm (null nếu lấy tất cả)
     * @return List Map với key: month (String), views (Integer)
     */
    List<Map<String, Object>> getViewsByMonth(Integer year);
    
    /**
     * Lấy doanh thu theo năm (cho biểu đồ)
     * @return List Map với key: year (String), revenue (BigDecimal)
     */
    List<Map<String, Object>> getRevenueByYear();
    
    /**
     * Lấy số đơn hàng theo năm (cho biểu đồ)
     * @return List Map với key: year (String), orders (Integer)
     */
    List<Map<String, Object>> getOrdersByYear();
    
    /**
     * Lấy lượt xem theo năm (cho biểu đồ)
     * @return List Map với key: year (String), views (Integer)
     */
    List<Map<String, Object>> getViewsByYear();
}

