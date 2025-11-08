package supportrequestdao;

import model.SupportRequest;
import java.util.List;

/**
 * Interface định nghĩa các thao tác với bảng SupportRequests
 */
public interface ISupportRequestDAO {
    
    /**
     * Thêm SupportRequest mới
     * @param request SupportRequest object
     * @return ID của record vừa tạo, -1 nếu lỗi
     */
    int insert(SupportRequest request);
    
    /**
     * Lấy SupportRequest theo ID
     * @param requestID ID của request
     * @return SupportRequest object hoặc null
     */
    SupportRequest getById(int requestID);
    
    /**
     * Lấy tất cả SupportRequest của một user
     * @param userID ID của user
     * @return Danh sách SupportRequest
     */
    List<SupportRequest> getByUserID(int userID);
    
    /**
     * Lấy tất cả SupportRequest (với thông tin user từ JOIN)
     * @return Danh sách SupportRequest
     */
    List<SupportRequest> getAll();
    
    /**
     * Lấy SupportRequest theo status
     * @param status Trạng thái (Open, Closed, InProgress, etc.)
     * @return Danh sách SupportRequest
     */
    List<SupportRequest> getByStatus(String status);
    
    /**
     * Cập nhật SupportRequest
     * @param request SupportRequest object
     * @return true nếu thành công
     */
    boolean update(SupportRequest request);
    
    /**
     * Xóa SupportRequest
     * @param requestID ID của request
     * @return true nếu thành công
     */
    boolean delete(int requestID);
}

