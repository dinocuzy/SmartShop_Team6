# Hệ thống CRUD Product - SmartShop

Hệ thống quản lý sản phẩm hoàn chỉnh theo mô hình MVC 3 tầng (DAO - Service - Servlet) cho ứng dụng Java Web.

## 📋 Cấu trúc dự án

```
SmartShop/
├── src/java/
│   ├── util/
│   │   └── DBConnection.java          # Quản lý kết nối SQL Server
│   ├── model/
│   │   └── Product.java               # Entity Product (JavaBean)
│   ├── productdao/
│   │   ├── IProductDAO.java          # Interface DAO
│   │   └── ProductDAO.java           # Implementation DAO (JDBC)
│   ├── productservice/
│   │   ├── IProductService.java      # Interface Service
│   │   └── ProductService.java       # Implementation Service (Business Logic)
│   └── controller/
│       └── ProductServlet.java       # Servlet xử lý request/response
└── web/WEB-INF/views/
    └── productList.jsp               # JSP hiển thị (Bootstrap 5, JSTL, EL)
```

## 🚀 Cài đặt và Cấu hình

### 1. Cấu hình Database Connection

Sửa file `src/java/util/DBConnection.java`:

```java
private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=SmartShopDB;encrypt=false;trustServerCertificate=true";
private static final String DB_USER = "sa";
private static final String DB_PASSWORD = "your_password"; // Thay đổi mật khẩu
```

### 2. Tạo Database và Bảng

Chạy script SQL sau trên SQL Server:

```sql
USE SmartShopDB;
GO

CREATE TABLE Product (
    ProductID INT IDENTITY(1,1) PRIMARY KEY,
    ProductName NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX),
    Price DECIMAL(18,2) CHECK (Price >= 0),
    Stock INT CHECK (Stock >= 0),
    CategoryID INT,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME NULL,
    IsActive BIT DEFAULT 1,
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);
GO
```

### 3. Build và Deploy

1. Mở dự án trong NetBeans/IDE của bạn
2. Build project (Clean and Build)
3. Deploy lên Tomcat 10.1

## 📝 Tính năng

### ✅ CRUD đầy đủ
- **Create**: Thêm sản phẩm mới
- **Read**: Xem danh sách sản phẩm
- **Update**: Chỉnh sửa sản phẩm
- **Delete**: Xóa mềm sản phẩm (Soft Delete - set IsActive = 0)

### 🔍 Tìm kiếm và Lọc
- Tìm kiếm theo tên sản phẩm (partial match)
- Lọc theo danh mục (CategoryID)
- Sắp xếp theo: ID, Tên, Giá, Tồn kho, Ngày tạo
- Sắp xếp tăng dần/giảm dần

### 📄 Phân trang
- Hiển thị 10 sản phẩm mỗi trang (có thể tùy chỉnh)
- Navigation với Previous/Next và số trang

### ✅ Validation
- Validation phía Server (Servlet):
  - ProductName: bắt buộc
  - Price: >= 0
  - Stock: >= 0
  - CategoryID: > 0
- Validation phía Client (HTML5):
  - Required fields
  - Min values cho số

### 🎨 UI/UX
- Bootstrap 5 responsive design
- Bootstrap Icons
- Modal form cho thêm/sửa
- Alert messages (success/error)
- Auto-hide alerts sau 5 giây

## 🌐 URL Mapping

- **Danh sách**: `GET /SmartShop/admin/products?action=list`
- **Thêm mới**: `GET /SmartShop/admin/products?action=add`
- **Chỉnh sửa**: `GET /SmartShop/admin/products?action=edit&productID={id}`
- **Xóa**: `GET /SmartShop/admin/products?action=delete&productID={id}`
- **Lưu**: `POST /SmartShop/admin/products?action=save`

## 📌 Các tham số URL

### Query Parameters cho List:
- `page`: Số trang (mặc định: 1)
- `search`: Từ khóa tìm kiếm
- `categoryID`: ID danh mục lọc (0 = tất cả)
- `sortBy`: Cột sắp xếp (ProductID, ProductName, Price, Stock, CreatedAt)
- `sortOrder`: Thứ tự (ASC, DESC)

### Ví dụ:
```
/admin/products?action=list&page=1&search=laptop&categoryID=1&sortBy=Price&sortOrder=ASC
```

## 🔧 Các lớp chính

### 1. Model: Product.java
- JavaBean với đầy đủ getter/setter
- Validation trong setter (Price >= 0, Stock >= 0)

### 2. DAO Layer: ProductDAO.java
- Sử dụng JDBC với PreparedStatement (tránh SQL Injection)
- SQL Server syntax (GETDATE(), OFFSET/FETCH)
- Soft delete (IsActive = 0)
- Log khi insert/update

### 3. Service Layer: ProductService.java
- Business logic và validation
- Kiểm tra dữ liệu trước khi gọi DAO

### 4. Controller: ProductServlet.java
- Xử lý GET/POST requests
- Forward đến JSP
- Set attributes cho JSP

### 5. View: productList.jsp
- Bootstrap 5 UI
- JSTL và EL (không dùng scriptlet)
- Modal form
- Phân trang

## ⚠️ Lưu ý

1. **Database Connection**: Nhớ cập nhật thông tin kết nối trong `DBConnection.java`

2. **Categories**: Hiện tại danh sách categories được hardcode trong JSP. Cần tạo CategoryDAO/Service để load động.

3. **Tomcat 10.1**: Đảm bảo sử dụng Jakarta Servlet API (không phải javax.servlet)

4. **JSTL**: Đảm bảo đã có thư viện JSTL trong `WEB-INF/lib`:
   - jakarta.servlet.jsp.jstl-3.0.1.jar
   - jakarta.servlet.jsp.jstl-api-3.0.1.jar

5. **SQL Server JDBC Driver**: Đảm bảo có `mssql-jdbc-12.10.0.jre11.jar` trong classpath

## 🎯 Mở rộng

Để tạo CRUD tương tự cho các entity khác (User, Order, Category...):
1. Tạo Model class
2. Tạo DAO interface và implementation
3. Tạo Service interface và implementation
4. Tạo Servlet controller
5. Tạo JSP view

Cấu trúc code tương tự như Product, chỉ cần thay đổi:
- Tên class/interface
- Tên bảng database
- Các thuộc tính của entity

## 📞 Support

Nếu có vấn đề, kiểm tra:
1. Console log của Tomcat
2. Database connection string
3. Database đã được tạo chưa
4. JDBC driver đã có trong classpath chưa
