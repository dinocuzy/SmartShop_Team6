# HƯỚNG DẪN SỬA LỖI DEPLOYMENT "context failed to start"

## Đã sửa:

1. ✅ **ContactServlet.init()** - Đảm bảo không throw exception
   - Tất cả lỗi đều được catch và log
   - Set default email nếu có lỗi
   - KHÔNG throw exception để tránh context startup failure

## Các bước kiểm tra:

### 1. Xem Tomcat Logs để tìm lỗi cụ thể:
- Vào NetBeans → Window → Output → Tab "Tomcat"
- Hoặc xem file log tại: `C:\Users\ASUS\AppData\Local\Temp\`
- Tìm dòng có chứa "Exception", "Error", "Failed", "SEVERE"

### 2. Clean và Rebuild Project:
```
1. Right-click project → Clean and Build
2. Hoặc chạy: ant clean dist
```

### 3. Kiểm tra Database Connection:
- Đảm bảo SQL Server đang chạy
- Kiểm tra database SmartShopDB đã tồn tại
- Kiểm tra credentials trong `DBConnection.java`

### 4. Kiểm tra Dependencies:
- Xem `build/web/WEB-INF/lib` có đầy đủ JARs:
  - mssql-jdbc-12.10.0.jre11.jar
  - jakarta.mail-2.0.1.jar
  - jakarta.servlet.jsp.jstl-3.0.1.jar
  - jakarta.servlet.jsp.jstl-api-3.0.1.jar
  - gson-2.10.1.jar

### 5. Restart Tomcat Server:
- Stop server
- Clean project
- Build project
- Start server lại

## Nguyên nhân phổ biến:

1. **Servlet init() throw exception** - Đã sửa ContactServlet ✅
2. **Filter init() throw exception** - AuthorizationFilter đã có try-catch ✅
3. **Database connection fail** - Cần kiểm tra SQL Server
4. **Missing JAR files** - Cần rebuild project
5. **Compilation errors** - Cần clean và rebuild

## Nếu vẫn lỗi:

Vui lòng cung cấp:
1. Full error message từ Tomcat logs
2. Stack trace nếu có
3. Thông tin về SQL Server (đang chạy hay không)

