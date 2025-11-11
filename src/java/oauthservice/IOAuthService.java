package oauthservice;

import model.User;
import model.UserOAuth;

/**
 * Interface cho OAuthService
 */
public interface IOAuthService {
    UserOAuth getOAuthByProviderUserID(String providerName, String providerUserID);
    UserOAuth getOAuthByUserIDAndProvider(int userID, String providerName);
    boolean linkOAuthToUser(int userID, String providerName, String providerUserID, String accessToken, String refreshToken);
    boolean updateOAuthToken(int userOAuthID, String accessToken, String refreshToken);
    User createOrGetUserFromGoogle(String email, String fullName, String googleUserID, String accessToken, String refreshToken);
}

