# Hướng dẫn tích hợp VNPay

## 1. Đăng ký tài khoản VNPay

### Môi trường Test (Sandbox):
1. Truy cập: https://sandbox.vnpayment.vn/
2. Đăng ký tài khoản merchant
3. Lấy thông tin:
   - **Terminal Code (vnp_TmnCode)**: Mã terminal được cấp
   - **Secret Key (vnp_HashSecret)**: Mã bảo mật để hash dữ liệu

### Môi trường Production:
1. Truy cập: https://www.vnpay.vn/
2. Đăng ký tài khoản merchant
3. Lấy thông tin tương tự như trên

## 2. Cấu hình trong web.xml

Mở file `web/WEB-INF/web.xml` và cập nhật các giá trị:

```xml
<context-param>
    <param-name>vnp_TmnCode</param-name>
    <param-value>YOUR_TMN_CODE</param-value>
</context-param>

<context-param>
    <param-name>vnp_HashSecret</param-name>
    <param-value>YOUR_HASH_SECRET</param-value>
</context-param>

<context-param>
    <param-name>vnp_PayUrl</param-name>
    <!-- Sandbox -->
    <param-value>https://sandbox.vnpayment.vn/paymentv2/vpcpay.html</param-value>
    <!-- Production -->
    <!-- <param-value>https://www.vnpay.vn/paymentv2/vpcpay.html</param-value> -->
</context-param>
```

**Lưu ý**: Thay `YOUR_TMN_CODE` và `YOUR_HASH_SECRET` bằng giá trị thực tế từ VNPay.

## 3. Thêm Payment Method VNPay vào Database

Chạy SQL sau để thêm phương thức thanh toán VNPay:

```sql
INSERT INTO PaymentMethods (MethodName, Provider, IsActive)
VALUES (N'VNPay', N'VNPay', 1);
```

Hoặc thêm qua Admin Dashboard → Payment Methods.

## 4. Flow thanh toán

### 4.1. Customer chọn VNPay
1. Customer vào `/checkout`
2. Chọn phương thức thanh toán "VNPay"
3. Submit form

### 4.2. CheckoutServlet xử lý
1. Tạo Order và Payment với status "Pending"
2. Kiểm tra nếu là VNPay → tạo payment URL
3. Redirect customer đến VNPay gateway

### 4.3. Customer thanh toán trên VNPay
1. Customer nhập thông tin thẻ/tài khoản
2. Xác nhận thanh toán
3. VNPay redirect về `/vnpay-callback`

### 4.4. VNPayCallbackServlet xử lý
1. Verify secure hash từ VNPay
2. Kiểm tra response code:
   - `00`: Thành công → Cập nhật Payment status = "Completed", Order status = "Confirmed"
   - Khác `00`: Thất bại → Cập nhật Payment status = "Failed"
3. Xóa giỏ hàng (nếu thành công)
4. Redirect đến trang kết quả

## 5. Các file đã tạo

1. **VNPayConfig.java**: Class cấu hình và utility functions
2. **VNPayUtil.java**: Class tạo payment URL và get IP address
3. **VNPayCallbackServlet.java**: Servlet xử lý callback từ VNPay
4. **vnpay_return.jsp**: Trang hiển thị kết quả thanh toán
5. **CheckoutServlet.java**: Đã cập nhật để redirect đến VNPay

## 6. Test với Sandbox

### Test Cards (Sandbox):
- **Thẻ thành công**: 9704198526191432198
- **CVV**: 123
- **Ngày hết hạn**: Bất kỳ ngày trong tương lai
- **OTP**: 123456

### Test Scenarios:
1. Thanh toán thành công (Response Code = 00)
2. Thanh toán thất bại (Response Code khác 00)
3. Hủy thanh toán (Customer click Cancel)

## 7. Lưu ý bảo mật

1. **KHÔNG** commit `vnp_HashSecret` vào Git
2. Sử dụng environment variables hoặc properties file cho production
3. Verify secure hash trong callback để đảm bảo request đến từ VNPay
4. Log tất cả transactions để audit

## 8. Troubleshooting

### Lỗi: "VNPay chưa được cấu hình"
- Kiểm tra `web.xml` đã có context-param chưa
- Kiểm tra giá trị `vnp_TmnCode` và `vnp_HashSecret` đã được set chưa

### Lỗi: "Chữ ký không hợp lệ"
- Kiểm tra `vnp_HashSecret` có đúng không
- Kiểm tra hash algorithm (SHA-512)

### Payment URL không hoạt động
- Kiểm tra `vnp_PayUrl` đúng môi trường (Sandbox/Production)
- Kiểm tra các parameters đã được set đúng chưa

## 9. Chuyển sang Production

1. Đăng ký tài khoản Production tại https://www.vnpay.vn/
2. Cập nhật `vnp_TmnCode` và `vnp_HashSecret` mới
3. Đổi `vnp_PayUrl` sang production URL
4. Test kỹ trước khi deploy
5. Cấu hình IP whitelist trong VNPay merchant portal (nếu có)

