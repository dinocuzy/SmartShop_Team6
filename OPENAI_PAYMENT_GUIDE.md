# Hướng dẫn thanh toán và sử dụng OpenAI API

## ⚠️ OpenAI API yêu cầu thanh toán

### 1. Tài khoản miễn phí (Free Trial)
- **Khi đăng ký lần đầu**: OpenAI thường cấp **$5-18 tín dụng miễn phí**
- **Sau khi hết tín dụng**: **PHẢI nạp tiền** mới sử dụng tiếp được
- **Không có gói miễn phí vĩnh viễn** cho API

### 2. Chi phí sử dụng

#### GPT-3.5-turbo (Model đang dùng):
- **Input**: ~$0.0015 per 1K tokens (khoảng 0.035 VNĐ per 1K tokens)
- **Output**: ~$0.002 per 1K tokens (khoảng 0.047 VNĐ per 1K tokens)

#### Ví dụ chi phí:
- 1 tin nhắn chatbot (~50 tokens) = **khoảng 0.002 VNĐ**
- 1000 tin nhắn = **khoảng 2 VNĐ**
- 100,000 tin nhắn = **khoảng 200 VNĐ**

### 3. Cách nạp tiền

1. **Truy cập**: https://platform.openai.com/account/billing
2. **Click**: "Add payment method" hoặc "Add funds"
3. **Nhập thông tin thẻ**:
   - Thẻ tín dụng/ghi nợ quốc tế (Visa, Mastercard)
   - Hoặc PayPal (nếu hỗ trợ)
4. **Nạp số tiền tối thiểu**: 
   - Thường là $5-10 USD
   - Tương đương khoảng 120,000 - 240,000 VNĐ
5. **Xác nhận**: Thanh toán và chờ xử lý

### 4. Giám sát chi phí

#### Xem usage:
- **Dashboard**: https://platform.openai.com/usage
- Xem số tokens đã dùng
- Xem chi phí theo ngày/tuần/tháng

#### Set usage limits (Giới hạn sử dụng):
1. Vào: https://platform.openai.com/account/limits
2. Set **hard limit** (giới hạn cứng):
   - Ví dụ: $10/month
   - Khi đạt limit, API sẽ tự động dừng
3. Set **soft limit** (cảnh báo):
   - Ví dụ: $8/month
   - Sẽ gửi email cảnh báo khi đạt soft limit

### 5. Tối ưu chi phí

#### Để giảm chi phí:
1. **Giới hạn max_tokens** (đang set 500, có thể giảm xuống 200-300)
2. **Tối ưu system prompt** (ngắn gọn hơn)
3. **Giới hạn conversation history** (đang giữ 20 messages, có thể giảm xuống 10)
4. **Cache responses** cho các câu hỏi phổ biến
5. **Chỉ bật chatbot** trong giờ làm việc (nếu cần)

#### Code optimization:
```java
// Giảm max_tokens từ 500 xuống 300
requestBody.addProperty("max_tokens", 300);

// Giảm conversation history từ 20 xuống 10
if (conversationHistory.size() > 10) {
    conversationHistory = conversationHistory.subList(conversationHistory.size() - 10, conversationHistory.size());
}
```

### 6. Lựa chọn thay thế (nếu không muốn trả tiền)

#### Option 1: Tắt chatbot tạm thời
- Ẩn chatbot widget
- Hoặc hiển thị message: "Chatbot đang bảo trì"

#### Option 2: Sử dụng AI miễn phí khác
- **Hugging Face** (có một số model miễn phí, nhưng chậm hơn)
- **Google Gemini** (có free tier, nhưng cần Google Cloud account)
- **Ollama** (chạy local, miễn phí nhưng cần server mạnh)

#### Option 3: Chatbot đơn giản (Rule-based)
- Tạo chatbot dựa trên keywords
- Không cần AI, không tốn phí
- Nhưng hạn chế về khả năng

### 7. Ước tính chi phí hàng tháng

#### Giả sử:
- **100 users/ngày** sử dụng chatbot
- **Mỗi user** gửi **5 tin nhắn** (1 conversation)
- **Mỗi tin nhắn** ~**100 tokens** (input + output)

#### Tính toán:
- Tokens/ngày: 100 users × 5 messages × 100 tokens = **50,000 tokens**
- Tokens/tháng: 50,000 × 30 = **1,500,000 tokens**
- Chi phí/tháng: 1,500,000 × $0.002/1000 = **$3 USD** (~**72,000 VNĐ**)

### 8. So sánh với các giải pháp khác

| Giải pháp | Chi phí | Chất lượng | Tốc độ |
|-----------|---------|------------|--------|
| OpenAI GPT-3.5 | ~$3-10/tháng | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Google Gemini | Miễn phí (limited) | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| Hugging Face | Miễn phí | ⭐⭐⭐ | ⭐⭐⭐ |
| Rule-based | Miễn phí | ⭐⭐ | ⭐⭐⭐⭐⭐ |

### 9. Khuyến nghị

#### Nếu ngân sách hạn chế:
1. **Nạp tối thiểu** $5-10 (dùng được vài tháng)
2. **Set usage limit** để tránh vượt quá ngân sách
3. **Monitor usage** thường xuyên
4. **Tối ưu code** để giảm tokens

#### Nếu muốn miễn phí:
1. **Tắt chatbot** và dùng contact form thông thường
2. **Hoặc** dùng rule-based chatbot (đơn giản)
3. **Hoặc** chờ đến khi có AI miễn phí tốt hơn

### 10. Kiểm tra tình trạng hiện tại

1. **Xem usage**: https://platform.openai.com/usage
2. **Xem billing**: https://platform.openai.com/account/billing
3. **Xem credits**: Kiểm tra còn tín dụng không

---

## 📞 Liên hệ hỗ trợ

- **Email**: smartshop686868@gmail.com
- **Hotline**: 0833347220
- **OpenAI Support**: https://help.openai.com/

---

**Kết luận**: Có, cần thanh toán để sử dụng OpenAI API sau khi hết tín dụng miễn phí. Chi phí rất rẻ (khoảng vài USD/tháng) nếu sử dụng hợp lý.

