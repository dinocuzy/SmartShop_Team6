# Triển khai các chức năng cho các bảng chưa dùng

## 📊 Tổng quan

Đã xác định và triển khai các chức năng cho các bảng trong database chưa được sử dụng.

---

## ✅ Đã hoàn thành 100%

### 1. CompareList (So sánh sản phẩm) ✅

**Files đã tạo:**
- ✅ `src/java/comparelistdao/ICompareListDAO.java`
- ✅ `src/java/comparelistdao/CompareListDAO.java`
- ✅ `src/java/comparelistitemdao/ICompareListItemDAO.java`
- ✅ `src/java/comparelistitemdao/CompareListItemDAO.java`
- ✅ `src/java/comparelistservice/ICompareListService.java`
- ✅ `src/java/comparelistservice/CompareListService.java`
- ✅ `src/java/controller/CompareServlet.java`
- ✅ `web/views/store/compare.jsp`

**Chức năng:**
- Thêm sản phẩm vào danh sách so sánh (tối đa 4 sản phẩm)
- Xóa sản phẩm khỏi danh sách so sánh
- Xóa toàn bộ danh sách so sánh
- Hiển thị bảng so sánh với các thuộc tính: tên, giá, mô tả, màu sắc, kích thước, tồn kho, trạng thái

**URL:** `/compare`

**Cần làm thêm:**
- Thêm icon "So sánh" vào header
- Thêm nút "So sánh" vào các trang sản phẩm

---

### 2. SupportRequest (Quản lý yêu cầu hỗ trợ) ✅

**Files đã tạo:**
- ✅ `src/java/supportrequestdao/ISupportRequestDAO.java`
- ✅ `src/java/supportrequestdao/SupportRequestDAO.java`
- ✅ `src/java/supportrequestservice/ISupportRequestService.java`
- ✅ `src/java/supportrequestservice/SupportRequestService.java`
- ✅ `src/java/controller/SupportRequestServlet.java`
- ✅ `web/views/store/supportRequestForm.jsp`
- ✅ `web/views/store/supportRequestList.jsp`
- ✅ `web/views/store/supportRequestDetail.jsp`

**Chức năng:**
- Khách hàng gửi yêu cầu hỗ trợ
- Admin/Staff xem và quản lý tất cả yêu cầu hỗ trợ
- Customer xem yêu cầu hỗ trợ của mình
- Cập nhật trạng thái: Open, InProgress, Closed, Resolved
- Phân quyền: Customer chỉ xem/cập nhật của mình, Admin/Staff xem tất cả

**URL:** `/support-request`

---

### 3. ProductImages (Quản lý nhiều ảnh cho sản phẩm) ✅

**Files đã tạo:**
- ✅ `src/java/productimagedao/IProductImageDAO.java`
- ✅ `src/java/productimagedao/ProductImageDAO.java`
- ✅ `src/java/productimageservice/IProductImageService.java`
- ✅ `src/java/productimageservice/ProductImageService.java`

**Chức năng:**
- Lấy tất cả ảnh của một sản phẩm (sắp xếp theo SortOrder)
- Lấy ảnh chính (ảnh đầu tiên)
- Thêm/xóa/sửa ảnh
- Cập nhật thứ tự sắp xếp (SortOrder)

**Cần tích hợp:**
- Servlet để upload/delete ảnh
- Cập nhật form thêm/sửa sản phẩm để quản lý nhiều ảnh
- Hiển thị gallery ảnh trên trang chi tiết sản phẩm

---

### 4. OrderStatusHistory (Lịch sử thay đổi trạng thái đơn hàng) ✅

**Files đã tạo:**
- ✅ `src/java/orderstatushistorydao/IOrderStatusHistoryDAO.java`
- ✅ `src/java/orderstatushistorydao/OrderStatusHistoryDAO.java`
- ✅ `src/java/orderstatushistoryservice/IOrderStatusHistoryService.java`
- ✅ `src/java/orderstatushistoryservice/OrderStatusHistoryService.java`
- ✅ Tích hợp vào `OrderServlet` - tự động ghi log khi admin/staff thay đổi trạng thái
- ✅ Tích hợp vào `CheckoutServlet` - ghi log khi tạo order COD (status: Pending)
- ✅ Tích hợp vào `VNPayCallbackServlet` - ghi log khi tạo order VNPay thành công (status: Paid)

**Chức năng:**
- Tự động ghi log khi thay đổi trạng thái đơn hàng
- Lưu thông tin: OrderID, OldStatus, NewStatus, ChangedAt, ChangedBy
- JOIN với Users để lấy tên người thay đổi

**Cần làm thêm:**
- Hiển thị lịch sử thay đổi trên trang chi tiết đơn hàng (JSP)
- API endpoint để lấy lịch sử thay đổi

---

### 5. ProductViews (Thống kê lượt xem sản phẩm) ✅

**Files đã tạo:**
- ✅ `src/java/productviewdao/IProductViewDAO.java`
- ✅ `src/java/productviewdao/ProductViewDAO.java`
- ✅ Tích hợp vào `ProductDetailServlet` - ghi nhận lượt xem khi user xem sản phẩm (cả API endpoint)

**Chức năng:**
- Tự động ghi nhận lượt xem khi user xem sản phẩm
- Lưu UserID (NULL nếu anonymous)
- Đếm số lượt xem của một sản phẩm
- Lấy danh sách sản phẩm được xem nhiều nhất

**Cần làm thêm:**
- Hiển thị số lượt xem trên trang sản phẩm
- Dashboard: Hiển thị sản phẩm được xem nhiều nhất

---

### 6. SocialShares (Chia sẻ sản phẩm lên mạng xã hội) ✅

**Files đã tạo:**
- ✅ `src/java/socialsharedao/ISocialShareDAO.java`
- ✅ `src/java/socialsharedao/SocialShareDAO.java`
- ✅ `src/java/controller/SocialShareServlet.java` (API endpoint: `/api/social-share`)

**Chức năng:**
- Ghi nhận khi user chia sẻ sản phẩm lên mạng xã hội
- Lưu thông tin: ProductID, UserID, Platform (Facebook, Twitter, Zalo, Instagram)
- Đếm số lượt share theo platform
- Lấy danh sách sản phẩm được share nhiều nhất

**Cần làm thêm:**
- Thêm nút chia sẻ (Facebook, Twitter, Zalo, Instagram) vào trang chi tiết sản phẩm
- Gọi API `/api/social-share` khi user click chia sẻ

---

## ⏳ Chưa triển khai

### 7. UserOAuth & OAuthProvider (Đăng nhập OAuth)

**Lý do chưa triển khai:**
- Chức năng này phức tạp hơn, cần cấu hình OAuth credentials (Google, Facebook)
- Cần setup OAuth apps trên các platform
- Cần xử lý callback và session management

**Khi cần triển khai:**
- Tạo `UserOAuthDAO` và `OAuthProviderDAO`
- Tạo `OAuthServlet` để xử lý OAuth flow
- Tích hợp vào `LoginServlet`
- Cập nhật `login.jsp` để thêm nút đăng nhập OAuth

---

## 📋 Tóm tắt

### Đã hoàn thành:
1. ✅ CompareList - So sánh sản phẩm (DAO, Service, Servlet, JSP)
2. ✅ SupportRequest - Quản lý yêu cầu hỗ trợ (DAO, Service, Servlet, JSP)
3. ✅ ProductImages - Quản lý nhiều ảnh (DAO, Service)
4. ✅ OrderStatusHistory - Lịch sử đơn hàng (DAO, Service, tích hợp vào OrderServlet, CheckoutServlet, VNPayCallbackServlet)
5. ✅ ProductViews - Thống kê lượt xem (DAO, tích hợp vào ProductDetailServlet)
6. ✅ SocialShares - Chia sẻ sản phẩm (DAO, Servlet API)

### Cần tích hợp vào UI:
1. ⏳ Thêm icon "So sánh" vào header
2. ⏳ Thêm nút "So sánh" vào các trang sản phẩm
3. ⏳ Thêm form upload nhiều ảnh vào ProductServlet
4. ⏳ Hiển thị lịch sử thay đổi trạng thái trên trang chi tiết đơn hàng
5. ⏳ Hiển thị số lượt xem trên trang sản phẩm
6. ⏳ Thêm nút chia sẻ vào trang chi tiết sản phẩm

---

## 🔧 Các bước tiếp theo (Tùy chọn)

1. Tích hợp UI: Thêm các nút và icon vào các trang JSP
2. Tạo API endpoint để kiểm tra sản phẩm có trong danh sách so sánh không
3. Tích hợp ProductImages vào form thêm/sửa sản phẩm
4. Hiển thị lịch sử OrderStatusHistory trên trang chi tiết đơn hàng
5. Dashboard: Hiển thị thống kê lượt xem và chia sẻ sản phẩm
6. (Tùy chọn) Tích hợp OAuth login

---

## 📌 Lưu ý

- Tất cả các DAO đều sử dụng JDBC với `DBConnection.getConnection()`
- Service layer có validation logic
- Servlets xử lý authentication và authorization
- JSP pages sử dụng dark theme để đồng bộ với giao diện hiện tại
- OrderStatusHistory được tự động ghi log khi:
  - Tạo order mới (CheckoutServlet, VNPayCallbackServlet)
  - Cập nhật trạng thái order (OrderServlet)
- ProductViews được tự động ghi nhận khi:
  - User xem sản phẩm (ProductDetailServlet)
