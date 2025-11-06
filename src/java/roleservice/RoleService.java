package roleservice;

import model.Role;
import roledao.IRoleDAO;
import roledao.RoleDAO;
import java.util.List;

/**
 * Implementation của IRoleService
 * Chứa business logic cho Role
 * Sử dụng RoleDAO (JDBC) để truy cập dữ liệu
 */
public class RoleService implements IRoleService {

    private IRoleDAO roleDAO;

    public RoleService() {
        this.roleDAO = new RoleDAO();
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDAO.getAll();
    }

    @Override
    public Role getRoleById(int roleID) {
        if (roleID <= 0) {
            throw new IllegalArgumentException("Role ID must be greater than 0");
        }
        return roleDAO.getById(roleID);
    }

    @Override
    public Role getRoleByName(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        return roleDAO.getByName(roleName.trim());
    }

    @Override
    public void addRole(Role role) {
        validateRole(role);
        roleDAO.insert(role);
    }

    @Override
    public void updateRole(Role role) {
        validateRole(role);
        if (role.getRoleID() <= 0) {
            throw new IllegalArgumentException("Role ID must be greater than 0");
        }
        boolean updated = roleDAO.update(role);
        if (!updated) {
            throw new RuntimeException("Failed to update role with ID: " + role.getRoleID());
        }
    }

    @Override
    public void deleteRole(int roleID) {
        if (roleID <= 0) {
            throw new IllegalArgumentException("Role ID must be greater than 0");
        }
        boolean deleted = roleDAO.delete(roleID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete role with ID: " + roleID);
        }
    }

    private void validateRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            throw new IllegalArgumentException("Role name is required");
        }
    }
}
