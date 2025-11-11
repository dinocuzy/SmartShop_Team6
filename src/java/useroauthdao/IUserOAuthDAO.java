package useroauthdao;

import model.UserOAuth;
import java.util.List;

/**
 * Interface cho UserOAuthDAO
 */
public interface IUserOAuthDAO {
    UserOAuth getByProviderUserID(int providerID, String providerUserID);
    UserOAuth getByUserIDAndProvider(int userID, int providerID);
    List<UserOAuth> getByUserID(int userID);
    int insert(UserOAuth userOAuth);
    boolean update(UserOAuth userOAuth);
    boolean delete(int userOAuthID);
}

