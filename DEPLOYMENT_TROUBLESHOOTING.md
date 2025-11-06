# HƯỚNG DẪN XỬ LÝ LỖI DEPLOYMENT

## Lỗi: "context failed to start"

### Nguyên nhân có thể:

1. **Database Connection Fail**
   - Kiểm tra SQL Server đang chạy
   - Kiểm tra thông tin kết nối trong `DBConnection.java`:
     - URL: `jdbc:sqlserver://localhost:1433;databaseName=SmartShopDB`
     - User: `sa`
     - Password: `11012004`
   - Kiểm tra database `SmartShopDB` đã tồn tại chưa

2. **Missing Dependencies**
   - Kiểm tra `build/web/WEB-INF/lib` có đầy đủ JARs:
     - `mssql-jdbc-12.10.0.jre11.jar`
     - `gson-2.10.1.jar`
     - `jakarta.mail-2.0.1.jar`
     - `jakarta.servlet.jsp.jstl-3.0.1.jar`
     - `jakarta.servlet.jsp.jstl-api-3.0.1.jar`

3. **Compilation Errors**
   - Kiểm tra log trong Tomcat console
   - Xem file `build/web/WEB-INF/classes` có đầy đủ .class files

4. **JSP Files Missing**
   - Đã kiểm tra: 27 JSP files tồn tại ✅

### Cách kiểm tra:

1. **Xem Tomcat Logs:**
   - Vào `C:\Users\ASUS\AppData\Local\Temp\` tìm file log của Tomcat
   - Hoặc xem trong NetBeans Output window

2. **Test Database Connection:**
   ```java
   // Test trong main method
   try {
       Connection conn = DBConnection.getConnection();
       System.out.println("Database connected!");
       conn.close();
   } catch (Exception e) {
       e.printStackTrace();
   }
   ```

3. **Kiểm tra Servlet Init:**
   - Đã thêm try-catch trong init() methods
   - Servlets sẽ không fail nếu init() có lỗi

### Giải pháp:

1. **Nếu Database không kết nối được:**
   - Start SQL Server
   - Kiểm tra port 1433
   - Tạo database SmartShopDB nếu chưa có
   - Cập nhật credentials trong `DBConnection.java`

2. **Nếu thiếu libraries:**
   - Rebuild project
   - Kiểm tra `build/web/WEB-INF/lib` có đầy đủ JARs

3. **Nếu có lỗi compile:**
   - Clean and Build project
   - Kiểm tra lỗi trong Problems window

### Lưu ý:

- Servlets sử dụng `@WebServlet` annotation, không cần web.xml servlet mapping
- Database connection chỉ được tạo khi có request, không phải khi servlet init
- Nếu database chưa sẵn sàng, servlet vẫn start được nhưng sẽ lỗi khi có request

