package supportrequestservice;

import model.SupportRequest;
import java.util.List;

/**
 * Interface Service cho SupportRequest
 */
public interface ISupportRequestService {
    
    /**
     * Tạo SupportRequest mới
     * @param request SupportRequest object
     * @return true nếu thành công
     */
    boolean createRequest(SupportRequest request);
    
    /**
     * Lấy SupportRequest theo ID
     * @param requestID ID của request
     * @return SupportRequest object hoặc null
     */
    SupportRequest getRequestById(int requestID);
    
    /**
     * Lấy tất cả SupportRequest của một user
     * @param userID ID của user
     * @return Danh sách SupportRequest
     */
    List<SupportRequest> getUserRequests(int userID);
    
    /**
     * Lấy tất cả SupportRequest (cho admin/staff)
     * @return Danh sách SupportRequest
     */
    List<SupportRequest> getAllRequests();
    
    /**
     * Lấy SupportRequest theo status
     * @param status Trạng thái
     * @return Danh sách SupportRequest
     */
    List<SupportRequest> getRequestsByStatus(String status);
    
    /**
     * Cập nhật SupportRequest
     * @param request SupportRequest object
     * @return true nếu thành công
     */
    boolean updateRequest(SupportRequest request);
    
    /**
     * Xóa SupportRequest
     * @param requestID ID của request
     * @return true nếu thành công
     */
    boolean deleteRequest(int requestID);
    
    /**
     * Tạo SupportRequest mới từ thông tin user
     * @param userID ID của user
     * @param subject Tiêu đề
     * @param message Nội dung
     * @return ID của request vừa tạo, -1 nếu lỗi
     */
    int createRequest(int userID, String subject, String message);
    
    /**
     * Đếm số lượng request theo status
     * @param status Trạng thái
     * @return Số lượng request
     */
    int countByStatus(String status);
    
    /**
     * Cập nhật trạng thái của request
     * @param requestID ID của request
     * @param status Trạng thái mới
     * @return true nếu thành công
     */
    boolean updateStatus(int requestID, String status);
}

