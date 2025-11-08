# Hướng dẫn lấy Google Gemini API Key (MIỄN PHÍ)

## ✅ Gemini API - Hoàn toàn MIỄN PHÍ!

Google Gemini API có **free tier rất rộng rãi** và **không yêu cầu thanh toán** như OpenAI.

### Bước 1: Truy cập Google AI Studio

1. Truy cập: **https://aistudio.google.com/app/apikey**
2. Đăng nhập bằng **tài khoản Google** của bạn
   - Có thể dùng Gmail bất kỳ

### Bước 2: Tạo API Key

1. Trên trang AI Studio, bạn sẽ thấy nút **"Create API Key"** hoặc **"Get API Key"**
2. Click vào nút đó
3. Chọn một Google Cloud Project (hoặc tạo mới)
4. **Copy API key** ngay lập tức
   - Format: `AIza...` (bắt đầu bằng `AIza`)
   - ⚠️ **QUAN TRỌNG**: API key chỉ hiển thị 1 lần, hãy copy ngay!

### Bước 3: Cấu hình API Key vào SmartShop

1. Mở file: `web/WEB-INF/web.xml`

2. Tìm dòng:
   ```xml
   <param-value>YOUR_GEMINI_API_KEY_HERE</param-value>
   ```

3. Thay thế bằng API key của bạn:
   ```xml
   <param-value>AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</param-value>
   ```

4. Lưu file

5. **Restart server** để áp dụng thay đổi

### Bước 4: Kiểm tra hoạt động

1. Truy cập bất kỳ trang nào có chatbot
2. Click vào chatbot button (góc dưới bên phải)
3. Gửi tin nhắn test: "Xin chào"
4. Nếu AI trả lời, nghĩa là đã cấu hình thành công!

## Ưu điểm của Gemini so với OpenAI

| Tính năng | OpenAI | Gemini |
|-----------|--------|--------|
| **Chi phí** | ~$3-10/tháng | **MIỄN PHÍ** ✅ |
| **Free Tier** | $5-18 tín dụng (1 lần) | **Free tier rộng rãi** ✅ |
| **Yêu cầu thanh toán** | Có (sau khi hết tín dụng) | **KHÔNG** ✅ |
| **Chất lượng** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Tốc độ** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Hỗ trợ tiếng Việt** | Tốt | Rất tốt ✅ |

## Free Tier Limits (Gemini)

- **60 requests/minute** (rate limit)
- **1,500 requests/day** (free tier)
- **Không giới hạn** số lượng tokens (trong free tier)
- **Không yêu cầu** payment method

## Lưu ý quan trọng

### Bảo mật
- ⚠️ **KHÔNG** commit API key vào Git
- ⚠️ **KHÔNG** chia sẻ API key công khai
- Nên sử dụng biến môi trường trong production (tùy chọn)

### Rate Limits
- Free tier: **60 requests/minute**
- Nếu vượt quá, sẽ nhận lỗi 429
- Đợi vài phút rồi thử lại

### Model sử dụng
- **gemini-1.5-flash**: Model miễn phí, nhanh, chất lượng tốt
- Có thể đổi sang `gemini-pro` nếu cần (cũng free nhưng chậm hơn)

## Troubleshooting

### Lỗi: "API key không hợp lệ" (401/403)
- Kiểm tra API key có đúng không
- Đảm bảo không có khoảng trắng thừa
- Tạo API key mới nếu cần

### Lỗi: "Rate limit exceeded" (429)
- Đã vượt quá 60 requests/minute
- Đợi 1-2 phút rồi thử lại
- Có thể implement rate limiting trong code

### Lỗi: "Safety filter"
- Response bị chặn do safety filters của Google
- Thử lại với câu hỏi khác
- Không thể tắt safety filters trong free tier

## So sánh với OpenAI

### Chi phí
- **OpenAI**: $3-10/tháng (~72,000-240,000 VNĐ)
- **Gemini**: **MIỄN PHÍ** ✅

### Chất lượng
- **OpenAI GPT-3.5**: Rất tốt, trả lời chính xác
- **Gemini 1.5 Flash**: Tốt, trả lời bằng tiếng Việt rất tự nhiên

### Kết luận
**Gemini là lựa chọn tốt hơn** nếu:
- ✅ Muốn tiết kiệm chi phí
- ✅ Không muốn thanh toán
- ✅ Cần free tier ổn định

## Tài liệu tham khảo

- **Google AI Studio**: https://aistudio.google.com/
- **Gemini API Docs**: https://ai.google.dev/docs
- **API Key**: https://aistudio.google.com/app/apikey
- **Pricing**: https://ai.google.dev/pricing (Free tier rất rộng)

---

**Chúc bạn thành công với Gemini API!** 🚀

