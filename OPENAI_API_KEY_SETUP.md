# Hướng dẫn lấy OpenAI API Key

## Bước 1: Đăng ký tài khoản OpenAI

1. Truy cập: https://platform.openai.com/
2. Click vào **"Sign up"** (Đăng ký) hoặc **"Log in"** (Đăng nhập nếu đã có tài khoản)
3. Điền thông tin:
   - Email
   - Mật khẩu
   - Xác nhận email (kiểm tra hộp thư)

## Bước 2: Thêm phương thức thanh toán (Bắt buộc)

1. Sau khi đăng nhập, vào **Settings** → **Billing**
2. Click **"Add payment method"**
3. Nhập thông tin thẻ tín dụng/ghi nợ
4. Xác nhận thanh toán

> **Lưu ý**: OpenAI yêu cầu thêm phương thức thanh toán trước khi có thể sử dụng API. Bạn sẽ được cấp một khoản tín dụng miễn phí khi đăng ký lần đầu (thường là $5-18).

## Bước 3: Tạo API Key

1. Truy cập: https://platform.openai.com/api-keys
   - Hoặc: Dashboard → **API keys** (ở menu bên trái)

2. Click **"+ Create new secret key"**

3. Đặt tên cho API key (ví dụ: "SmartShop Production" hoặc "SmartShop Development")

4. Click **"Create secret key"**

5. **SAO CHÉP API KEY NGAY LẬP TỨC** 
   - ⚠️ **QUAN TRỌNG**: Bạn chỉ có thể xem API key này một lần duy nhất
   - Nếu bạn không sao chép ngay, bạn sẽ phải tạo key mới
   - Format: `sk-...` (bắt đầu bằng `sk-`)

## Bước 4: Cấu hình API Key vào SmartShop

1. Mở file: `web/WEB-INF/web.xml`

2. Tìm dòng:
   ```xml
   <param-value>YOUR_OPENAI_API_KEY_HERE</param-value>
   ```

3. Thay thế bằng API key của bạn:
   ```xml
   <param-value>sk-proj-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</param-value>
   ```

4. Lưu file

5. Restart server để áp dụng thay đổi

## Bước 5: Kiểm tra hoạt động

1. Truy cập trang Contact: `http://localhost:8080/SmartShop/contact`
2. Click vào chatbot button (góc dưới bên phải)
3. Gửi một tin nhắn test, ví dụ: "Xin chào"
4. Nếu AI trả lời, nghĩa là đã cấu hình thành công!

## Lưu ý quan trọng

### Bảo mật
- ⚠️ **KHÔNG** commit API key vào Git
- ⚠️ **KHÔNG** chia sẻ API key công khai
- Nên sử dụng biến môi trường hoặc file cấu hình riêng trong production

### Chi phí
- **Pricing**: https://openai.com/api/pricing/
- **GPT-3.5-turbo**: ~$0.0015 per 1K input tokens, ~$0.002 per 1K output tokens
- **Giới hạn**: Có thể set usage limits trong Dashboard → Settings → Limits
- **Monitoring**: Xem usage tại https://platform.openai.com/usage

### Quản lý API Keys
- Xem danh sách keys: https://platform.openai.com/api-keys
- Có thể tạo nhiều keys cho nhiều môi trường (dev, staging, production)
- Có thể revoke (vô hiệu hóa) keys nếu bị lộ

### Troubleshooting

**Lỗi: "API key is not configured"**
- Kiểm tra đã cấu hình trong `web.xml` chưa
- Kiểm tra API key có đúng format không (bắt đầu bằng `sk-`)
- Restart server sau khi thay đổi `web.xml`

**Lỗi: "Insufficient quota"**
- Kiểm tra còn tín dụng trong tài khoản không
- Vào Dashboard → Billing để kiểm tra

**Lỗi: "Invalid API key"**
- Kiểm tra đã sao chép đúng API key chưa
- Tạo API key mới nếu cần

## Tài liệu tham khảo

- **OpenAI Platform**: https://platform.openai.com/
- **API Documentation**: https://platform.openai.com/docs
- **Pricing**: https://openai.com/api/pricing/
- **Support**: https://help.openai.com/

## Cấu hình nâng cao (Tùy chọn)

### Sử dụng biến môi trường (Production)

Thay vì lưu API key trong `web.xml`, có thể sử dụng biến môi trường:

1. Set biến môi trường trên server:
   ```bash
   export OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxx
   ```

2. Sửa `OpenAIContextListener.java` để đọc từ biến môi trường:
   ```java
   String apiKey = System.getenv("OPENAI_API_KEY");
   if (apiKey == null || apiKey.isEmpty()) {
       apiKey = sce.getServletContext().getInitParameter("openai_api_key");
   }
   ```

### Rate Limiting

Để tránh vượt quá giới hạn, có thể thêm rate limiting:

1. Tạo filter để giới hạn số request mỗi phút
2. Sử dụng Redis hoặc in-memory cache để track requests
3. Return error 429 nếu vượt quá limit

### Model Selection

Có thể thay đổi model trong `OpenAIUtil.java`:
- `gpt-3.5-turbo` (rẻ, nhanh) - **Khuyến nghị**
- `gpt-4` (đắt, chính xác hơn)
- `gpt-4-turbo` (cân bằng giữa giá và chất lượng)

---

**Chúc bạn thành công!** 🚀

