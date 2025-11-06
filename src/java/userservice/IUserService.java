package userservice;

import model.User;
import model.Address;
import java.util.List;
import java.util.Map;

/**
 * Interface định nghĩa business logic cho User
 */
public interface IUserService {

    /**
     * Thêm user mới
     *
     * @param user User object cần thêm
     * @return ID của user đã được tạo
     */
    int addUser(User user);

    /**
     * Cập nhật user
     *
     * @param user User object với thông tin đã cập nhật
     */
    void updateUser(User user);

    /**
     * Xóa user (soft delete)
     *
     * @param userID ID user cần xóa
     */
    void deleteUser(int userID);

    /**
     * Lấy user theo ID
     *
     * @param userID ID user
     * @return User object hoặc null nếu không tìm thấy
     */
    User getUserById(int userID);

    /**
     * Lấy user theo email
     *
     * @param email Email của user
     * @return User object hoặc null nếu không tìm thấy
     */
    User getUserByEmail(String email);

    /**
     * Lấy tất cả user
     *
     * @param includeInactive Nếu true, lấy cả user inactive (IsActive = 0)
     * @return Danh sách các User
     */
    List<User> getAllUsers(boolean includeInactive);

    /**
     * Lấy danh sách user theo role
     *
     * @param roleID ID role
     * @param includeInactive Nếu true, lấy cả user inactive (IsActive = 0)
     * @return Danh sách các User
     */
    List<User> getUsersByRole(int roleID, boolean includeInactive);

    /**
     * Tìm kiếm user
     *
     * @param keyword Từ khóa tìm kiếm
     * @param includeInactive Nếu true, lấy cả user inactive (IsActive = 0)
     * @return Danh sách các User khớp
     */
    List<User> searchUsers(String keyword, boolean includeInactive);

    /**
     * Lấy danh sách user với phân trang
     *
     * @param pageNumber Số trang
     * @param pageSize Số lượng user mỗi trang
     * @param sortBy Cột sắp xếp
     * @param sortOrder Thứ tự sắp xếp
     * @param searchKeyword Từ khóa tìm kiếm
     * @param roleID ID role lọc
     * @param includeInactive Nếu true, lấy cả user inactive (IsActive = 0)
     * @return Danh sách các User
     */
    List<User> getPagedUsers(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchKeyword, int roleID, boolean includeInactive);

    /**
     * Đếm tổng số user
     *
     * @param searchKeyword Từ khóa tìm kiếm
     * @param roleID ID role lọc
     * @param includeInactive Nếu true, đếm cả user inactive (IsActive = 0)
     * @return Tổng số user
     */
    int countUsers(String searchKeyword, int roleID, boolean includeInactive);
    
    /**
     * Lấy danh sách user với phân trang và JOIN với địa chỉ mặc định
     * Phương thức này tối ưu hơn vì chỉ cần 1 query thay vì N+1 queries
     *
     * @param pageNumber Số trang
     * @param pageSize Số lượng user mỗi trang
     * @param sortBy Cột sắp xếp
     * @param sortOrder Thứ tự sắp xếp
     * @param searchKeyword Từ khóa tìm kiếm
     * @param roleID ID role lọc
     * @param includeInactive Nếu true, lấy cả user inactive (IsActive = 0)
     * @return Map chứa User và địa chỉ mặc định (có thể null nếu user không có địa chỉ)
     */
    Map<User, Address> getPagedUsersWithDefaultAddress(
        int pageNumber, int pageSize, String sortBy, String sortOrder,
        String searchKeyword, int roleID, boolean includeInactive);
}
