# Hướng dẫn Deploy SmartShop

## Giải quyết lỗi "The module has not been deployed"

### Các bước khắc phục:

1. **Clean và Build Project:**
   - Trong NetBeans: Right-click project → Clean and Build
   - Hoặc: Build → Clean Project, sau đó Build → Build Project

2. **Kiểm tra Tomcat Server:**
   - Đảm bảo Tomcat 10.1 đã được cấu hình trong NetBeans
   - Services → Servers → Tomcat 10.1 → Right-click → Start
   - Kiểm tra port 8080 không bị chiếm

3. **Undeploy và Redeploy:**
   - Services → Servers → Tomcat 10.1 → Applications → Undeploy SmartShop
   - Sau đó Build → Build Project (sẽ tự động deploy)

4. **Kiểm tra Build Folder:**
   - Đảm bảo thư mục `build/web/` được tạo
   - Kiểm tra `build/web/WEB-INF/classes/` có các file .class

5. **Kiểm tra JSP Files:**
   - Đảm bảo tất cả JSP files đã được copy vào `build/web/views/`

6. **Kiểm tra Libraries:**
   - Đảm bảo các file JAR trong `lib/` đã được copy vào `build/web/WEB-INF/lib/`

## Cấu trúc thư mục sau khi build:

```
build/
└── web/
    ├── META-INF/
    ├── views/
    │   ├── admin/
    │   ├── auth/
    │   ├── customer/
    │   └── store/
    ├── WEB-INF/
    │   ├── classes/        (Compiled Java classes)
    │   └── lib/            (JAR dependencies)
    └── WEB-INF/web.xml
```

## URLs sau khi deploy:

- Cửa hàng công khai: `http://localhost:8080/SmartShop/shop`
- Trang chủ: `http://localhost:8080/SmartShop/index`
- Đăng nhập: `http://localhost:8080/SmartShop/login`
- Admin Dashboard: `http://localhost:8080/SmartShop/admin/dashboard`

## Nếu vẫn lỗi:

1. Kiểm tra log của Tomcat trong tab Output hoặc Services → Servers → Tomcat → View Server Log
2. Kiểm tra xem có lỗi compile trong tab Output → Compiler
3. Thử restart NetBeans và Tomcat
4. Kiểm tra file `nbproject/private/private.properties` có đúng đường dẫn Tomcat không

