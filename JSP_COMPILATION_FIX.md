# Hướng dẫn sửa lỗi JSP Compilation

## Lỗi: `ClassNotFoundException: org.apache.jsp.views.customer.customerProfile_jsp`

Lỗi này xảy ra khi JSP file chưa được compile hoặc có lỗi syntax.

## Cách sửa:

### 1. Clean và Rebuild Project

```bash
# Trong NetBeans hoặc IDE:
1. Right-click project → Clean
2. Right-click project → Build
3. Restart Tomcat server
```

Hoặc dùng Ant:
```bash
ant clean dist
```

### 2. Xóa thư mục build cũ

Xóa các thư mục:
- `build/` (nếu có)
- `dist/` (nếu có)
- `web/WEB-INF/classes/` (nếu có)

### 3. Kiểm tra JSP syntax

Đảm bảo file `web/views/customer/customerProfile.jsp`:
- Có đầy đủ thẻ đóng `</html>`, `</body>`
- Không có lỗi syntax trong JSP tags
- Import đúng các taglib

### 4. Restart Tomcat

Sau khi rebuild, restart Tomcat server để JSP được compile lại.

### 5. Kiểm tra Tomcat logs

Xem file log trong `Tomcat/logs/catalina.out` để xem lỗi chi tiết.

## Lưu ý:

- JSP files được compile tự động khi server start hoặc khi request đầu tiên đến
- Nếu vẫn lỗi sau khi rebuild, có thể do JSP có syntax error
- Kiểm tra console/logs để xem lỗi cụ thể

