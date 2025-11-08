package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Class cấu hình và utility cho VNPay payment gateway
 */
public class VNPayConfig {
    
    // VNPay Configuration - Cần cấu hình trong web.xml
    // Các giá trị này sẽ được set từ context-param trong web.xml
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = ""; // Sẽ được set trong servlet
    public static String vnp_TmnCode = ""; // Terminal Code từ VNPay - lấy từ web.xml
    public static String vnp_HashSecret = ""; // Secret Key từ VNPay - lấy từ web.xml
    public static String vnp_apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    
    // Version
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String vnp_OrderType = "other";
    
    // Locale
    public static String vnp_Locale = "vn";
    
    // Currency
    public static String vnp_CurrCode = "VND";
    
    /**
     * Tạo random string cho vnp_TxnRef
     */
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Hash data với SHA512 (deprecated - dùng hmacSHA512 thay thế)
     */
    public static String Sha512(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }
    
    /**
     * HMAC SHA512 - Theo chuẩn VNPay
     * @param key Secret key (vnp_HashSecret)
     * @param data Data cần hash
     * @return HMAC SHA512 hash string (hex)
     */
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] hash = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Error in hmacSHA512: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * Tạo query string từ params để hash
     * Lưu ý: KHÔNG bao gồm vnp_SecureHash và vnp_SecureHashType
     * Các field được sort theo alphabet và chỉ lấy field có giá trị
     * Theo code mẫu VNPay: encode giá trị với US_ASCII khi build hash data
     */
    public static String hashAllFields(Map<String, String> fields) {
        // Loại bỏ vnp_SecureHash và vnp_SecureHashType
        Map<String, String> fieldsToHash = new HashMap<>(fields);
        fieldsToHash.remove("vnp_SecureHash");
        fieldsToHash.remove("vnp_SecureHashType");
        
        List<String> fieldNames = new ArrayList<>(fieldsToHash.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fieldsToHash.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                // Encode với US_ASCII theo code mẫu VNPay
                try {
                    sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    sb.append(fieldValue); // Fallback nếu không encode được
                }
                if (itr.hasNext()) {
                    sb.append("&");
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * Tạo secure hash từ query string
     */
    public static String getSecureHash(String queryString) {
        String hashData = queryString;
        if (vnp_HashSecret != null && !vnp_HashSecret.isEmpty()) {
            hashData += "&vnp_SecureHash=" + Sha512(vnp_HashSecret + hashData);
        }
        return hashData;
    }
    
    /**
     * Verify response hash từ VNPay
     * Lưu ý: fields map KHÔNG được chứa vnp_SecureHash và vnp_SecureHashType
     * Sử dụng HMAC-SHA512 theo chuẩn VNPay
     */
    public static boolean verifySecureHash(Map<String, String> fields, String secureHash) {
        if (vnp_HashSecret == null || vnp_HashSecret.isEmpty()) {
            System.err.println("VNPayConfig: vnp_HashSecret is not set!");
            return false;
        }
        
        // Loại bỏ vnp_SecureHash và vnp_SecureHashType nếu có
        Map<String, String> fieldsToHash = new HashMap<>(fields);
        fieldsToHash.remove("vnp_SecureHash");
        fieldsToHash.remove("vnp_SecureHashType");
        
        // Build query string từ các fields (đã loại bỏ SecureHash)
        // Lưu ý: hashAllFields đã encode với US_ASCII
        String queryString = hashAllFields(fieldsToHash);
        
        // Hash: HMAC-SHA512(vnp_HashSecret, queryString)
        String checkSum = hmacSHA512(vnp_HashSecret, queryString);
        
        // So sánh (case-insensitive để an toàn hơn)
        boolean isValid = secureHash != null && checkSum != null && 
                         secureHash.equalsIgnoreCase(checkSum);
        
        if (!isValid) {
            System.err.println("VNPay Hash Verification Failed:");
            System.err.println("  Expected: " + checkSum);
            System.err.println("  Received: " + secureHash);
            System.err.println("  Query String: " + queryString);
        }
        
        return isValid;
    }
    
    /**
     * URL encode string
     */
    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}

