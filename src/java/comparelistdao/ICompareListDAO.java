package comparelistdao;

import model.CompareList;

/**
 * Interface định nghĩa các thao tác với bảng CompareLists
 */
public interface ICompareListDAO {
    
    /**
     * Lấy CompareList của một user (mỗi user chỉ có một CompareList)
     * @param userID ID của user
     * @return CompareList object hoặc null nếu không tìm thấy
     */
    CompareList getByUserID(int userID);
    
    /**
     * Tạo CompareList mới cho user (nếu chưa có)
     * @param userID ID của user
     * @return ID của CompareList vừa tạo, -1 nếu lỗi
     */
    int createForUser(int userID);
    
    /**
     * Xóa CompareList của user
     * @param userID ID của user
     * @return Số record đã xóa
     */
    int deleteByUserID(int userID);
}

