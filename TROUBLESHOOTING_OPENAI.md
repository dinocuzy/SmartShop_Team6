# Hướng dẫn xử lý lỗi OpenAI Chatbot

## Lỗi: "Không thể nhận được phản hồi từ AI"

### Nguyên nhân phổ biến:

1. **API Key chưa được cấu hình hoặc không hợp lệ**
   - Kiểm tra file `web/WEB-INF/web.xml`
   - Đảm bảo `<param-value>` chứa API key thực tế (không phải `YOUR_OPENAI_API_KEY_HERE`)
   - API key phải bắt đầu bằng `sk-`
   - **Lưu ý**: Không có khoảng trắng thừa ở đầu/cuối API key

2. **API Key đã hết hạn hoặc bị revoke**
   - Tạo API key mới tại https://platform.openai.com/api-keys
   - Cập nhật vào `web.xml`

3. **Hết quota/tín dụng**
   - Kiểm tra tại https://platform.openai.com/usage
   - Nạp tiền vào tài khoản nếu cần

4. **Lỗi kết nối mạng**
   - Kiểm tra kết nối internet
   - Kiểm tra firewall/proxy có chặn `api.openai.com` không

### Cách kiểm tra:

1. **Kiểm tra logs trong server console:**
   ```
   - Tìm dòng "OpenAI API key loaded successfully from context-param"
   - Tìm các dòng "OpenAI API Error" để xem chi tiết lỗi
   - Tìm dòng "Response Code" để biết HTTP status code
   ```

2. **Kiểm tra API key trong web.xml:**
   ```xml
   <context-param>
       <param-name>openai_api_key</param-name>
       <param-value>sk-proj-xxxxxxxxxxxxx</param-value>
   </context-param>
   ```
   - Đảm bảo không có khoảng trắng thừa
   - Đảm bảo API key bắt đầu bằng `sk-`

3. **Test API key bằng curl (nếu có):**
   ```bash
   curl https://api.openai.com/v1/models \
     -H "Authorization: Bearer YOUR_API_KEY"
   ```

### Các error message cụ thể:

- **"OpenAI API key chưa được cấu hình"**
  → Cấu hình API key trong `web.xml`

- **"API key không hợp lệ hoặc đã hết hạn"** (Response Code: 401)
  → Tạo API key mới và cập nhật

- **"Đã vượt quá giới hạn sử dụng API"** (Response Code: 429)
  → Chờ một lúc hoặc nâng cấp plan

- **"Lỗi server từ OpenAI"** (Response Code: 500+)
  → Lỗi từ phía OpenAI, thử lại sau

- **"Kết nối timeout"**
  → Kiểm tra kết nối mạng, firewall

- **"Không thể kết nối đến OpenAI API"**
  → Kiểm tra internet, DNS

### Cách fix:

1. **Restart server sau khi cấu hình API key:**
   - Dừng server
   - Cập nhật `web.xml`
   - Khởi động lại server

2. **Kiểm tra logs:**
   - Xem console output của server
   - Tìm các dòng bắt đầu bằng "OpenAI API"

3. **Test lại:**
   - Mở chatbot
   - Gửi tin nhắn test: "Xin chào"
   - Xem error message chi tiết (đã được cải thiện)

### Lưu ý:

- API key phải được trim (không có khoảng trắng thừa)
- API key chỉ hiển thị 1 lần khi tạo - lưu cẩn thận
- Không commit API key vào Git public
- Sử dụng biến môi trường trong production (tùy chọn)

