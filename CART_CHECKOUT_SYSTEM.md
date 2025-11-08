# Hệ thống Cart và Checkout - SmartShop

## 📋 Tổng quan

Hệ thống quản lý giỏ hàng và thanh toán hoàn chỉnh với các tính năng:
- Lưu giỏ hàng vào database khi đã đăng nhập
- Lưu tạm trong session khi chưa đăng nhập
- Đồng bộ cart khi đăng nhập/đăng xuất
- Thanh toán COD và VNPay với flow xử lý riêng

---

## 🗄️ Database Schema

### Bảng CartItems
```sql
CREATE TABLE dbo.CartItems (
  CartItemID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  ProductID INT NOT NULL,
  Quantity INT NOT NULL DEFAULT 1,
  AddedDate DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT FK_CartItems_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE,
  CONSTRAINT FK_CartItems_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE,
  CONSTRAINT UQ_CartItems_UserProduct UNIQUE(UserID, ProductID)
);
```

**Đặc điểm:**
- Mỗi user chỉ có 1 record cho mỗi product (UNIQUE constraint)
- Tự động xóa khi user hoặc product bị xóa (CASCADE)
- Index trên UserID và ProductID để tối ưu query

---

## 📦 Models

### 1. CartItemDB (Database Model)
- `CartItemID`: ID của cart item trong DB
- `UserID`: ID của user sở hữu
- `ProductID`: ID của sản phẩm
- `Quantity`: Số lượng
- `AddedDate`: Ngày thêm vào giỏ
- `Product`: Relationship với Product (JOIN để lấy thông tin)

### 2. CartItem (Session Model)
- Dùng cho session cart
- Chứa thông tin đầy đủ: ProductID, ProductName, Price, ImageUrl, Stock, etc.

### 3. Cart (Session Model)
- Map<ProductID, CartItem>
- Methods: `addItem()`, `updateQuantity()`, `removeItem()`, `clear()`, `getTotal()`

---

## 🔧 DAO Layer

### ICartDAO Interface
```java
- getCartItemsByUser(int userID)
- getCartItemById(int cartItemID)
- getCartItemByUserAndProduct(int userID, int productID)
- insert(CartItemDB cartItem) → returns generated ID
- update(CartItemDB cartItem) → returns boolean
- delete(int cartItemID) → returns boolean
- deleteAllByUser(int userID) → returns count
- deleteByUserAndProduct(int userID, int productID) → returns boolean
- countByUser(int userID) → returns count
```

### CartDAO Implementation
- Sử dụng JDBC với `DBConnection`
- JOIN với Products table để lấy thông tin sản phẩm
- Map ResultSet → CartItemDB với Product object

---

## 🎯 Service Layer

### ICartService Interface
```java
- getCartItemsByUser(int userID)
- addToCart(int userID, int productID, int quantity)
- updateQuantity(int userID, int productID, int quantity)
- removeFromCart(int userID, int productID)
- clearCart(int userID)
- syncCartFromSession(int userID, List<CartItem> sessionCartItems)
```

### CartService Implementation
- **addToCart()**: Nếu sản phẩm đã có → tăng số lượng, chưa có → thêm mới
- **updateQuantity()**: Cập nhật số lượng, nếu <= 0 → xóa
- **syncCartFromSession()**: Đồng bộ cart từ session vào DB, gộp số lượng nếu trùng

---

## 🎮 Controller Layer

### 1. CartServlet (`/cart`)

#### Actions:
- **GET `/cart`**: Hiển thị giỏ hàng
  - Nếu đã đăng nhập: Load từ DB và merge với session cart
  - Nếu chưa đăng nhập: Chỉ hiển thị session cart

- **POST `/cart?action=add`**: Thêm sản phẩm
  - Thêm vào session cart
  - Nếu đã đăng nhập: Lưu vào DB

- **POST `/cart?action=update`**: Cập nhật số lượng
  - Cập nhật session cart
  - Nếu đã đăng nhập: Cập nhật trong DB

- **POST `/cart?action=remove`**: Xóa sản phẩm
  - Xóa khỏi session cart
  - Nếu đã đăng nhập: Xóa khỏi DB

- **POST `/cart?action=clear`**: Xóa toàn bộ
  - Xóa session cart
  - Nếu đã đăng nhập: Xóa khỏi DB

#### Flow:
```
User thêm sản phẩm
    ↓
CartServlet.addToCart()
    ↓
Thêm vào session cart
    ↓
Nếu đã đăng nhập?
    ├─ YES → cartService.addToCart(userID, productID, quantity)
    └─ NO → Chỉ lưu trong session
```

---

### 2. LoginServlet (`/login`)

#### Flow đồng bộ cart:
```
User đăng nhập thành công
    ↓
Kiểm tra session cart
    ├─ Có cart → syncCartFromSession() → Gộp với DB cart
    └─ Không có → Load cart từ DB → Set vào session
    ↓
Cập nhật session cart với cart đã merge
```

#### Logic:
1. Nếu có session cart:
   - Gọi `cartService.syncCartFromSession()` để đồng bộ vào DB
   - Load lại cart từ DB (đã merge)
   - Cập nhật session cart

2. Nếu không có session cart:
   - Load cart từ DB
   - Set vào session

---

### 3. LogoutServlet (`/logout`)

#### Flow:
```
User đăng xuất
    ↓
Nếu đã đăng nhập và có cart?
    ├─ YES → syncCartFromSession() → Lưu vào DB
    └─ NO → Không làm gì
    ↓
Invalidate session
```

---

### 4. CheckoutServlet (`/checkout`)

#### Flow COD:
```
[5A] User chọn COD
    ↓
Tạo Order (Status = "Pending")
    ↓
Tạo OrderItems → Trigger tự động giảm Stock
    ↓
Tạo Payment (Status = "Pending")
    ↓
Xóa cart (session + DB)
    ↓
Redirect đến trang xác nhận
```

#### Flow VNPay:
```
[5B] User chọn VNPay
    ↓
Tạo Order (Status = "Unpaid")
    ↓
KHÔNG tạo OrderItems (chưa giảm stock)
    ↓
Lưu cart items vào session (pendingCartItems)
    ↓
Redirect đến VNPay gateway
```

---

### 5. VNPayCallbackServlet (`/vnpay-callback`)

#### Flow thành công (vnp_ResponseCode = "00"):
```
[6] VNPay trả về thành công
    ↓
[8] Tạo OrderItems từ pendingCartItems
    → Trigger tự động giảm Stock
    ↓
[7] Cập nhật Order.Status = "Paid"
    ↓
[7] Tạo Payment record (Status = "Completed")
    ↓
[9] Xóa cart (session + DB)
    ↓
Hiển thị trang kết quả thành công
```

#### Flow thất bại:
```
VNPay trả về thất bại
    ↓
Cập nhật Order.Status = "Failed"
    ↓
Xóa OrderItems nếu có
    ↓
Xóa session data (pendingCartItems, pendingOrderID)
    ↓
Hiển thị trang kết quả thất bại
```

---

## 📊 Order Status Flow

### Trạng thái đơn hàng:

1. **Pending**: 
   - COD: Đã đặt hàng, chờ xác nhận
   - Có OrderItems, đã giảm stock

2. **Unpaid**: 
   - VNPay: Đã tạo đơn, chờ thanh toán
   - Chưa có OrderItems, chưa giảm stock

3. **Paid**: 
   - VNPay: Thanh toán thành công
   - Đã có OrderItems, đã giảm stock

4. **Failed**: 
   - VNPay: Thanh toán thất bại hoặc hủy
   - Order tạm bị hủy

5. **Shipped**: 
   - Đang giao hàng (admin cập nhật)

6. **Completed**: 
   - Giao hàng thành công (admin cập nhật)

---

## 🔄 Cart Synchronization Flow

### Kịch bản 1: User chưa đăng nhập
```
1. User thêm sản phẩm → Lưu vào session cart
2. User đăng nhập → syncCartFromSession() → Lưu vào DB
3. User đăng xuất → Không cần lưu (chưa đăng nhập)
```

### Kịch bản 2: User đã đăng nhập
```
1. User thêm sản phẩm → Lưu vào session + DB
2. User đăng xuất → syncCartFromSession() → Đảm bảo DB đã sync
3. User đăng nhập lại → Load từ DB → Set vào session
```

### Kịch bản 3: User có cart ở cả 2 nơi
```
1. User có cart trong DB (từ lần trước)
2. User có cart trong session (chưa đăng nhập)
3. User đăng nhập → syncCartFromSession() → Gộp số lượng nếu trùng
```

---

## 🎯 Key Features

### 1. Cart Persistence
- ✅ Lưu vào DB khi đã đăng nhập
- ✅ Lưu tạm trong session khi chưa đăng nhập
- ✅ Đồng bộ tự động khi đăng nhập/đăng xuất

### 2. Cart Merging
- ✅ Gộp số lượng nếu sản phẩm trùng
- ✅ Giữ nguyên số lượng nếu không trùng

### 3. Stock Management
- ✅ COD: Giảm stock ngay khi đặt hàng
- ✅ VNPay: Chỉ giảm stock khi thanh toán thành công

### 4. Order Status
- ✅ COD: Status = "Pending"
- ✅ VNPay: Status = "Unpaid" → "Paid" (thành công) hoặc "Failed" (thất bại)

---

## 📝 Pseudo-code

### CartServlet.addToCart()
```
IF user đã đăng nhập:
    cartService.addToCart(userID, productID, quantity)
    // Service sẽ tự xử lý: nếu có → tăng số lượng, chưa có → thêm mới
END IF

Thêm vào session cart
// Cart.addItem() tự xử lý logic cộng số lượng
```

### LoginServlet.doPost()
```
IF session có cart:
    syncCartFromSession(userID, sessionCartItems)
    // Service sẽ gộp số lượng nếu trùng
END IF

Load cart từ DB
Merge vào session cart
```

### CheckoutServlet.placeOrder()
```
IF payment method = COD:
    Create Order (Status = "Pending")
    Create OrderItems → Trigger giảm stock
    Create Payment
    Clear cart (session + DB)
ELSE IF payment method = VNPay:
    Create Order (Status = "Unpaid")
    Save cart items to session
    Redirect to VNPay
END IF
```

### VNPayCallbackServlet.processCallback()
```
IF vnp_ResponseCode = "00":
    Create OrderItems from pendingCartItems
    // Trigger tự động giảm stock
    Update Order.Status = "Paid"
    Create Payment (Status = "Completed")
    Clear cart (session + DB)
ELSE:
    Update Order.Status = "Failed"
    Delete OrderItems if exists
    Clear session data
END IF
```

---

## 🚀 Testing Scenarios

### Test 1: Cart Persistence
1. User chưa đăng nhập → Thêm sản phẩm → Cart trong session
2. User đăng nhập → Cart được sync vào DB
3. User đăng xuất → Cart vẫn còn trong DB
4. User đăng nhập lại → Cart được load từ DB

### Test 2: Cart Merging
1. User có sản phẩm A (qty=2) trong DB
2. User thêm sản phẩm A (qty=1) khi chưa đăng nhập
3. User đăng nhập → Sản phẩm A có qty=3 (đã gộp)

### Test 3: COD Checkout
1. User chọn COD → Order Status = "Pending"
2. Stock giảm ngay
3. Cart bị xóa

### Test 4: VNPay Checkout
1. User chọn VNPay → Order Status = "Unpaid"
2. Stock chưa giảm
3. Thanh toán thành công → Order Status = "Paid", Stock giảm, Cart xóa
4. Thanh toán thất bại → Order Status = "Failed", Cart vẫn còn

---

## 📁 File Structure

```
src/java/
├── model/
│   ├── Cart.java (Session model)
│   ├── CartItem.java (Session model)
│   └── CartItemDB.java (Database model)
├── cartdao/
│   ├── ICartDAO.java
│   └── CartDAO.java
├── cartservice/
│   ├── ICartService.java
│   └── CartService.java
└── controller/
    ├── CartServlet.java
    ├── CheckoutServlet.java
    ├── LoginServlet.java
    ├── LogoutServlet.java
    └── VNPayCallbackServlet.java
```

---

## ✅ Checklist

- [x] Tạo bảng CartItems trong database
- [x] Tạo CartItemDB model
- [x] Tạo CartDAO và CartService
- [x] Cập nhật CartServlet để lưu vào DB
- [x] Cập nhật LoginServlet để đồng bộ cart
- [x] Cập nhật LogoutServlet để lưu cart
- [x] Cập nhật CheckoutServlet với status "Unpaid" cho VNPay
- [x] Cập nhật VNPayCallbackServlet với status "Paid"
- [x] Xóa cart sau khi thanh toán thành công

---

## 🎉 Kết quả

Hệ thống Cart và Checkout đã hoàn chỉnh với:
- ✅ Persistence: Cart lưu vào DB khi đã đăng nhập
- ✅ Synchronization: Đồng bộ tự động khi login/logout
- ✅ COD Flow: Tạo Order + OrderItems ngay, Status = "Pending"
- ✅ VNPay Flow: Tạo Order tạm (Unpaid) → Thanh toán → Paid
- ✅ Stock Management: Giảm stock đúng thời điểm
- ✅ Cart Cleanup: Xóa cart sau khi thanh toán thành công

