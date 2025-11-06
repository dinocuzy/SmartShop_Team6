# Hệ thống phân quyền (Authorization System)

Hệ thống phân quyền được xây dựng dựa trên Role-Based Access Control (RBAC), cho phép kiểm soát quyền truy cập các tài nguyên trong ứng dụng SmartShop.

## 📁 Cấu trúc

### 1. **AuthorizationUtil.java** (`src/java/util/AuthorizationUtil.java`)
Utility class chứa các phương thức kiểm tra quyền truy cập.

**Các phương thức chính:**
- `getCurrentUser(HttpServletRequest)`: Lấy user hiện tại từ session
- `isLoggedIn(HttpServletRequest)`: Kiểm tra user đã đăng nhập chưa
- `hasRole(HttpServletRequest, int/String)`: Kiểm tra user có role cụ thể không
- `isAdmin(HttpServletRequest)`: Kiểm tra user có phải Admin không
- `isManager(HttpServletRequest)`: Kiểm tra user có phải Manager không
- `isStaff(HttpServletRequest)`: Kiểm tra user có phải Staff không
- `isCustomer(HttpServletRequest)`: Kiểm tra user có phải Customer không
- `isAdminOrManager(HttpServletRequest)`: Kiểm tra Admin hoặc Manager
- `isStaffMember(HttpServletRequest)`: Kiểm tra Admin, Manager hoặc Staff
- `canAccessAdminArea(HttpServletRequest)`: Kiểm tra quyền truy cập admin area
- `hasAnyRole(HttpServletRequest, String...)`: Kiểm tra user có một trong các role được phép
- `isOwnerOrAdmin(HttpServletRequest, int)`: Kiểm tra user là chủ sở hữu hoặc Admin

**Ví dụ sử dụng:**
```java
// Kiểm tra user đã đăng nhập chưa
if (!AuthorizationUtil.isLoggedIn(request)) {
    response.sendRedirect("/login");
    return;
}

// Kiểm tra user có phải Admin không
if (AuthorizationUtil.isAdmin(request)) {
    // Logic cho Admin
}

// Kiểm tra user có quyền truy cập admin area
if (!AuthorizationUtil.canAccessAdminArea(request)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return;
}

// Kiểm tra user có một trong các role được phép
if (AuthorizationUtil.hasAnyRole(request, "Admin", "Manager")) {
    // Logic cho Admin hoặc Manager
}
```

### 2. **AuthorizationFilter.java** (`src/java/filter/AuthorizationFilter.java`)
Filter tự động kiểm tra quyền truy cập cho các URL pattern.

**URL Patterns được bảo vệ:**
- `/admin/*`: Chỉ Admin, Manager, Staff
- `/manager/*`: Chỉ Admin, Manager
- `/staff/*`: Chỉ Admin, Manager, Staff
- `/customer/*`: Tất cả user đã đăng nhập

**URL công khai (không cần đăng nhập):**
- `/login`
- `/register`
- `/logout`
- `/store` và `/store/*`

**Chức năng:**
- Tự động chuyển hướng về `/login` nếu user chưa đăng nhập
- Kiểm tra user có active không
- Kiểm tra quyền truy cập theo URL pattern
- Ghi log các hoạt động kiểm tra

### 3. **PathConstants.java** (`src/java/util/PathConstants.java`)
Class chứa các constants cho đường dẫn URL, tránh hardcode.

**Ví dụ sử dụng:**
```java
// Thay vì hardcode
response.sendRedirect("/admin/products");

// Dùng constant
response.sendRedirect(PathConstants.ADMIN_PRODUCTS);
```

## 🔐 Role Hierarchy

Hệ thống có 4 roles với quyền hạn khác nhau:

1. **Admin** (RoleID = 1)
   - Quyền cao nhất
   - Truy cập tất cả các trang admin
   - Quản lý users, roles, products, orders, etc.

2. **Manager** (RoleID = 2)
   - Quyền quản lý
   - Truy cập `/admin/*` và `/manager/*`
   - Quản lý products, orders, categories

3. **Staff** (RoleID = 3)
   - Quyền nhân viên
   - Truy cập `/admin/*` và `/staff/*`
   - Xem và xử lý orders

4. **Customer** (RoleID = 4)
   - Quyền khách hàng
   - Chỉ truy cập `/customer/*` và `/store/*`
   - Xem profile, orders, cart, wishlist

## 🚀 Cách sử dụng

### 1. Sử dụng trong Servlet

```java
@WebServlet("/admin/products")
public class ProductServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền truy cập
        if (!AuthorizationUtil.canAccessAdminArea(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Logic xử lý
        // ...
    }
}
```

### 2. Sử dụng trong JSP (hiển thị menu theo role)

```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty sessionScope.currentUser}">
    <c:choose>
        <c:when test="${sessionScope.currentUser.roleID == 1}">
            <%-- Menu Admin --%>
            <a href="/admin/dashboard">Dashboard</a>
            <a href="/admin/users">Users</a>
        </c:when>
        <c:when test="${sessionScope.currentUser.roleID == 2}">
            <%-- Menu Manager --%>
            <a href="/manager/dashboard">Dashboard</a>
            <a href="/manager/products">Products</a>
        </c:when>
        <c:when test="${sessionScope.currentUser.roleID == 4}">
            <%-- Menu Customer --%>
            <a href="/customer/dashboard">Dashboard</a>
            <a href="/customer/profile">Profile</a>
        </c:when>
    </c:choose>
</c:if>
```

### 3. Kiểm tra quyền sở hữu (Owner check)

```java
// Ví dụ: User chỉ có thể xem order của chính mình (trừ Admin)
int orderUserID = order.getUserID();
if (!AuthorizationUtil.isOwnerOrAdmin(request, orderUserID)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, 
        "Bạn không có quyền xem đơn hàng này");
    return;
}
```

## 📝 Cấu hình Filter

Filter được cấu hình bằng annotation `@WebFilter`, không cần cấu hình trong `web.xml`:

```java
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {
    "/admin/*",
    "/manager/*",
    "/staff/*",
    "/customer/*"
})
public class AuthorizationFilter implements Filter {
    // ...
}
```

Nếu muốn cấu hình trong `web.xml`:

```xml
<filter>
    <filter-name>AuthorizationFilter</filter-name>
    <filter-class>filter.AuthorizationFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>AuthorizationFilter</filter-name>
    <url-pattern>/admin/*</url-pattern>
    <url-pattern>/manager/*</url-pattern>
    <url-pattern>/staff/*</url-pattern>
    <url-pattern>/customer/*</url-pattern>
</filter-mapping>
```

## ⚠️ Lưu ý

1. **Session Management**: User object phải được lưu trong session với key `"currentUser"` sau khi đăng nhập:
   ```java
   session.setAttribute("currentUser", user);
   ```

2. **User Object**: User object phải có các thuộc tính:
   - `userID`: ID của user
   - `roleID`: ID của role (1=Admin, 2=Manager, 3=Staff, 4=Customer)
   - `roleName`: Tên role (optional, được JOIN từ bảng Roles)
   - `isActive`: Trạng thái active

3. **Redirect After Login**: Filter sẽ lưu URL gốc trong session với key `"redirectAfterLogin"` để redirect lại sau khi đăng nhập thành công.

4. **Error Pages**: Có thể cấu hình error page cho 403 trong `web.xml`:
   ```xml
   <error-page>
       <error-code>403</error-code>
       <location>/views/error/403.jsp</location>
   </error-page>
   ```

## 🎯 Best Practices

1. **Defense in Depth**: Kiểm tra quyền ở cả Filter và Servlet để tăng tính bảo mật.

2. **Centralized Authorization Logic**: Sử dụng `AuthorizationUtil` thay vì viết logic kiểm tra quyền ở nhiều nơi.

3. **Constants Usage**: Sử dụng `PathConstants` và role constants để tránh hardcode.

4. **Logging**: Filter đã có logging để theo dõi các hoạt động kiểm tra quyền.

5. **Graceful Error Handling**: Trả về lỗi 403 với thông báo rõ ràng thay vì redirect về trang chủ.

## 📚 Ví dụ đầy đủ

Xem file `ProductServlet.java` để tham khảo cách sử dụng `AuthorizationUtil` trong servlet thực tế.
