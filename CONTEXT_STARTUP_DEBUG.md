# HƯỚNG DẪN DEBUG CONTEXT STARTUP FAILURE

## Lỗi: "context failed to start"

### Đã thực hiện:

1. ✅ **Thêm error handling trong servlet init()**
   - DashboardServlet, ShopServlet, HomeServlet, LoginServlet
   - Servlets sẽ không fail nếu init() có lỗi

2. ✅ **Thêm error handling trong filter**
   - AuthorizationFilter đã được wrap trong try-catch
   - Filter đã được tạm thời disable để test

3. ✅ **Kiểm tra dependencies**
   - Tất cả JAR files đã được copy vào WEB-INF/lib
   - Class files đã được compile

### Các bước debug tiếp theo:

1. **Kiểm tra Tomcat logs:**
   - Vào NetBeans → Window → Output → Tab "Tomcat"
   - Tìm dòng lỗi có chứa "Exception", "Error", "Failed"

2. **Test database connection:**
   ```java
   // Tạo file test trong src/java
   public class TestDB {
       public static void main(String[] args) {
           try {
               Connection conn = DBConnection.getConnection();
               System.out.println("Database connected!");
               conn.close();
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }
   ```

3. **Kiểm tra servlet initialization:**
   - Xem log có message "Error initializing" không
   - Kiểm tra từng servlet một

4. **Tạm thời disable filter:**
   - Filter đã được comment @WebFilter annotation
   - Rebuild và test lại

5. **Nếu vẫn lỗi, thử:**
   - Clean và rebuild project
   - Restart Tomcat server
   - Kiểm tra port 8080 có bị conflict không

### Nguyên nhân phổ biến:

1. **Database connection fail:**
   - SQL Server không chạy
   - Database SmartShopDB chưa tồn tại
   - Credentials sai

2. **Missing dependencies:**
   - JDBC driver không có trong WEB-INF/lib
   - Servlet API không đúng version

3. **Compilation errors:**
   - Có lỗi syntax trong code
   - Missing imports

4. **Filter/Servlet init errors:**
   - Exception trong init() method không được catch
   - Static block có lỗi

### Lưu ý:

- Filter đã được tạm thời disable - nếu context start được, thì filter là nguyên nhân
- Sau khi fix, có thể enable lại filter bằng cách uncomment @WebFilter annotation

