package userdao;

import model.User;
import java.util.List;

public interface IUserDAO {
    List<User> getAll(boolean includeInactive);
    User getById(int userID);
    User getByEmail(String email);
    List<User> getByRole(int roleID, boolean includeInactive);
    List<User> search(String keyword, boolean includeInactive);
    List<User> getPagedUsers(int pageNumber, int pageSize, String sortBy, String sortOrder,
                             String searchKeyword, int roleID, boolean includeInactive);
    int count(String searchKeyword, int roleID, boolean includeInactive);
    int insert(User user);
    boolean update(User user);
    boolean delete(int userID); // Soft delete
}
