package oauthservice;

import model.User;
import model.UserOAuth;
import model.OAuthProvider;
import oauthproviderdao.IOAuthProviderDAO;
import oauthproviderdao.OAuthProviderDAO;
import useroauthdao.IUserOAuthDAO;
import useroauthdao.UserOAuthDAO;
import userservice.IUserService;
import userservice.UserService;
import roleservice.IRoleService;
import roleservice.RoleService;

/**
 * Service xử lý business logic cho OAuth
 */
public class OAuthService implements IOAuthService {
    
    private IOAuthProviderDAO oauthProviderDAO;
    private IUserOAuthDAO userOAuthDAO;
    private IUserService userService;
    private IRoleService roleService;
    
    public OAuthService() {
        this.oauthProviderDAO = new OAuthProviderDAO();
        this.userOAuthDAO = new UserOAuthDAO();
        this.userService = new UserService();
        this.roleService = new RoleService();
    }
    
    @Override
    public UserOAuth getOAuthByProviderUserID(String providerName, String providerUserID) {
        OAuthProvider provider = oauthProviderDAO.getByName(providerName);
        if (provider == null) {
            return null;
        }
        
        return userOAuthDAO.getByProviderUserID(provider.getProviderID(), providerUserID);
    }
    
    @Override
    public UserOAuth getOAuthByUserIDAndProvider(int userID, String providerName) {
        OAuthProvider provider = oauthProviderDAO.getByName(providerName);
        if (provider == null) {
            return null;
        }
        
        return userOAuthDAO.getByUserIDAndProvider(userID, provider.getProviderID());
    }
    
    @Override
    public boolean linkOAuthToUser(int userID, String providerName, String providerUserID, String accessToken, String refreshToken) {
        OAuthProvider provider = oauthProviderDAO.getByName(providerName);
        if (provider == null) {
            return false;
        }
        
        // Kiểm tra xem đã link chưa
        UserOAuth existing = userOAuthDAO.getByUserIDAndProvider(userID, provider.getProviderID());
        if (existing != null) {
            // Update token
            existing.setAccessToken(accessToken);
            existing.setRefreshToken(refreshToken);
            return userOAuthDAO.update(existing);
        } else {
            // Tạo mới
            UserOAuth userOAuth = new UserOAuth();
            userOAuth.setUserID(userID);
            userOAuth.setProviderID(provider.getProviderID());
            userOAuth.setProviderUserID(providerUserID);
            userOAuth.setAccessToken(accessToken);
            userOAuth.setRefreshToken(refreshToken);
            
            int result = userOAuthDAO.insert(userOAuth);
            return result > 0;
        }
    }
    
    @Override
    public boolean updateOAuthToken(int userOAuthID, String accessToken, String refreshToken) {
        // This method is not currently used, but can be implemented if needed
        // For now, we update tokens through linkOAuthToUser method
        return false;
    }
    
    @Override
    public User createOrGetUserFromGoogle(String email, String fullName, String googleUserID, String accessToken, String refreshToken) {
        // Kiểm tra xem đã có OAuth link chưa
        UserOAuth existingOAuth = getOAuthByProviderUserID("Google", googleUserID);
        
        if (existingOAuth != null) {
            // User đã tồn tại, lấy thông tin user
            User user = userService.getUserById(existingOAuth.getUserID());
            if (user != null) {
                // Update token nếu có thay đổi
                if (accessToken != null && refreshToken != null) {
                    existingOAuth.setAccessToken(accessToken);
                    existingOAuth.setRefreshToken(refreshToken);
                    userOAuthDAO.update(existingOAuth);
                }
                return user;
            }
        }
        
        // Kiểm tra xem email đã tồn tại chưa (user đã đăng ký bằng email/password)
        User existingUser = userService.getUserByEmail(email);
        
        if (existingUser != null) {
            // User đã tồn tại, link OAuth với user này
            linkOAuthToUser(existingUser.getUserID(), "Google", googleUserID, accessToken, refreshToken);
            return existingUser;
        }
        
        // Tạo user mới
        var customerRole = roleService.getRoleByName("Customer");
        int customerRoleID = 4; // Default Customer RoleID
        if (customerRole != null) {
            customerRoleID = customerRole.getRoleID();
        }
        
        User newUser = new User();
        newUser.setFullName(fullName != null ? fullName.trim() : "Google User");
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPasswordHash("OAUTH_GOOGLE"); // Đánh dấu là OAuth user
        newUser.setPhone(null);
        newUser.setRoleID(customerRoleID);
        newUser.setActive(true);
        
        int userID = userService.addUser(newUser);
        if (userID > 0) {
            newUser.setUserID(userID);
            if (customerRole != null) {
                newUser.setRoleName(customerRole.getRoleName());
            } else {
                newUser.setRoleName("Customer");
            }
            
            // Link OAuth
            linkOAuthToUser(userID, "Google", googleUserID, accessToken, refreshToken);
            
            return newUser;
        }
        
        return null;
    }
}

