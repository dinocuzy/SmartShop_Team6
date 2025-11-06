package roleservice;

import model.Role;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Role
 */
public interface IRoleService {

    /**
     * Lấy tất cả vai trò
     *
     * @return Danh sách các Role
     */
    List<Role> getAllRoles();

    /**
     * Lấy vai trò theo ID
     *
     * @param roleID ID vai trò
     * @return Role object hoặc null nếu không tìm thấy
     */
    Role getRoleById(int roleID);

    /**
     * Lấy vai trò theo tên
     *
     * @param roleName Tên vai trò
     * @return Role object hoặc null nếu không tìm thấy
     */
    Role getRoleByName(String roleName);

    /**
     * Thêm vai trò mới
     *
     * @param role Role object cần thêm
     */
    void addRole(Role role);

    /**
     * Cập nhật vai trò
     *
     * @param role Role object với thông tin đã cập nhật
     */
    void updateRole(Role role);

    /**
     * Xóa vai trò
     *
     * @param roleID ID vai trò cần xóa
     */
    void deleteRole(int roleID);
}
