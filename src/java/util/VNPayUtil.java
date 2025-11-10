package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Utility class để tạo VNPay payment URL
 */
public class VNPayUtil {
    
    /**
     * Tạo payment URL cho VNPay
     * 
     * @param orderID Order ID
     * @param amount Số tiền (VND)
     * @param orderInfo Thông tin đơn hàng
     * @param returnUrl URL callback sau khi thanh toán
     * @param ipAddr IP address của khách hàng
     * @return VNPay payment URL
     */
    public static String createPaymentUrl(int orderID, long amount, String orderInfo, 
                                         String returnUrl, String ipAddr) {
        
        // Tạo transaction reference (vnp_TxnRef)
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8) + orderID;
        
        // Tạo create date (vnp_CreateDate)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        
        // Tạo expire date (vnp_ExpireDate) - 15 phút sau
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        
        // Build params - KHÔNG encode ở đây, để hash đúng
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu amount * 100
        vnp_Params.put("vnp_CurrCode", VNPayConfig.vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        // Lưu ý: orderInfo không được encode ở đây, để hash đúng
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", VNPayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", VNPayConfig.vnp_Locale);
        // returnUrl không được encode ở đây, để hash đúng
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        // Build query string và hash
        // hashAllFields sẽ encode giá trị với US_ASCII khi build hash data
        String queryString = VNPayConfig.hashAllFields(vnp_Params);
        // Sử dụng HMAC-SHA512 theo chuẩn VNPay
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, queryString);
        
        // Build query string cho URL (ENCODE cả field name và field value với US_ASCII)
        // Theo code mẫu VNPay: encode cả field name và field value
        StringBuilder urlBuilder = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Encode cả field name và field value với US_ASCII theo code mẫu VNPay
                try {
                    urlBuilder.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    urlBuilder.append("=");
                    urlBuilder.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    // Fallback nếu không encode được
                    urlBuilder.append(fieldName);
                    urlBuilder.append("=");
                    urlBuilder.append(fieldValue);
                }
                if (itr.hasNext()) {
                    urlBuilder.append("&");
                }
            }
        }
        urlBuilder.append("&vnp_SecureHash=").append(vnp_SecureHash);
        
        // Build final URL
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + urlBuilder.toString();
        
        return paymentUrl;
    }
    
    /**
     * Lấy IP address từ request
     * Sử dụng Object để tránh dependency vào servlet API
     */
    public static String getIpAddress(Object request) {
        if (request == null) {
            return "127.0.0.1";
        }
        
        try {
            // Sử dụng reflection để tránh compile-time dependency
            java.lang.reflect.Method getHeader = request.getClass().getMethod("getHeader", String.class);
            java.lang.reflect.Method getRemoteAddr = request.getClass().getMethod("getRemoteAddr");
            
            String ipAddress = (String) getHeader.invoke(request, "X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = (String) getHeader.invoke(request, "Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = (String) getHeader.invoke(request, "WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = (String) getHeader.invoke(request, "HTTP_CLIENT_IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = (String) getHeader.invoke(request, "HTTP_X_FORWARDED_FOR");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = (String) getRemoteAddr.invoke(request);
            }
            return ipAddress != null ? ipAddress : "127.0.0.1";
        } catch (Exception e) {
            System.err.println("Error getting IP address: " + e.getMessage());
            return "127.0.0.1";
        }
    }
}

