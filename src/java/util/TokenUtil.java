package util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class để tạo và quản lý token
 */
public class TokenUtil {
    
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Tạo token ngẫu nhiên
     * @return Token string
     */
    public static String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * Tạo token với prefix
     * @param prefix Prefix cho token (ví dụ: "reset_", "verify_")
     * @return Token string với prefix
     */
    public static String generateToken(String prefix) {
        return prefix + generateToken();
    }
}

