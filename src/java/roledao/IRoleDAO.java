package roledao;

import model.Role;
import java.util.List;

public interface IRoleDAO {
    List<Role> getAll();
    Role getById(int roleID);
    Role getByName(String roleName);
    int insert(Role role);
    boolean update(Role role);
    boolean delete(int roleID);
}
