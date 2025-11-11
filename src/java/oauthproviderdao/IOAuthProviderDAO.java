package oauthproviderdao;

import model.OAuthProvider;
import java.util.List;

/**
 * Interface cho OAuthProviderDAO
 */
public interface IOAuthProviderDAO {
    OAuthProvider getById(int providerID);
    OAuthProvider getByName(String providerName);
    List<OAuthProvider> getAll();
}

