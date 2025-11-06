<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }
        
        .navbar-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .cart-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .cart-item {
            border-bottom: 1px solid #eee;
            padding: 1.5rem 0;
        }
        
        .cart-item:last-child {
            border-bottom: none;
        }
        
        .product-image-cart {
            width: 120px;
            height: 120px;
            object-fit: cover;
            border-radius: 10px;
        }
        
        .quantity-input {
            width: 80px;
            text-align: center;
        }
        
        .summary-card {
            background: #f8f9fa;
            border-radius: 15px;
            padding: 2rem;
            position: sticky;
            top: 20px;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-custom navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/index">
                <i class="bi bi-shop"></i> SmartShop
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/index">
                            <i class="bi bi-house"></i> Trang chủ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/shop">
                            <i class="bi bi-grid"></i> Cửa hàng
                        </a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/cart">
                            <i class="bi bi-cart"></i> Giỏ hàng 
                            <span class="badge bg-light text-dark">${cartSize != null ? cartSize : 0}</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/wishlist">
                            <i class="bi bi-heart"></i> Yêu thích
                        </a>
                    </li>
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                    <i class="bi bi-person-circle"></i> ${sessionScope.currentUser.fullName}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/customer/dashboard">
                                            <i class="bi bi-speedometer2"></i> Trang cá nhân
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/customer/profile">
                                            <i class="bi bi-person"></i> Thông tin cá nhân
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                            <i class="bi bi-box-arrow-right"></i> Đăng xuất
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/login">
                                    <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container my-5">
        <h2 class="mb-4"><i class="bi bi-cart"></i> Giỏ hàng của bạn</h2>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <div class="row">
            <div class="col-lg-8">
                <div class="cart-card">
                    <c:choose>
                        <c:when test="${not empty cartItems && cartItems.size() > 0}">
                            <c:forEach var="item" items="${cartItems}">
                                <div class="cart-item">
                                    <div class="row align-items-center">
                                        <div class="col-md-2">
                                            <c:choose>
                                                <c:when test="${not empty item.imageUrl}">
                                                    <img src="${item.imageUrl}" class="product-image-cart" alt="${item.productName}">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="product-image-cart d-flex align-items-center justify-content-center bg-light">
                                                        <i class="bi bi-image" style="font-size: 2rem; color: #ccc;"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="col-md-4">
                                            <h5>${item.productName}</h5>
                                            <p class="text-muted mb-0">Mã: #${item.productID}</p>
                                            <c:if test="${item.stockStatus == 'InStock' && item.stock > 0}">
                                                <span class="badge bg-success">Còn hàng</span>
                                            </c:if>
                                            <c:if test="${item.stockStatus == 'OutOfStock' || item.stock <= 0}">
                                                <span class="badge bg-danger">Hết hàng</span>
                                            </c:if>
                                        </div>
                                        <div class="col-md-2">
                                            <p class="mb-0">
                                                <strong>
                                                    <fmt:formatNumber value="${item.price}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                            </p>
                                        </div>
                                        <div class="col-md-2">
                                            <form method="post" action="${pageContext.request.contextPath}/cart" class="d-inline">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="productID" value="${item.productID}">
                                                <div class="input-group">
                                                    <button class="btn btn-outline-secondary" type="button" 
                                                            onclick="updateQuantity(${item.productID}, ${item.quantity - 1}, ${item.stock})">-</button>
                                                    <input type="number" name="quantity" value="${item.quantity}" 
                                                           min="1" max="${item.stock}" class="form-control quantity-input" 
                                                           id="quantity-${item.productID}" readonly>
                                                    <button class="btn btn-outline-secondary" type="button" 
                                                            onclick="updateQuantity(${item.productID}, ${item.quantity + 1}, ${item.stock})">+</button>
                                                </div>
                                            </form>
                                        </div>
                                        <div class="col-md-2 text-end">
                                            <p class="mb-2">
                                                <strong>
                                                    <fmt:formatNumber value="${item.subtotal}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                            </p>
                                            <form method="post" action="${pageContext.request.contextPath}/cart" class="d-inline">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="productID" value="${item.productID}">
                                                <button type="submit" class="btn btn-danger btn-sm" 
                                                        onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?')">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            
                            <div class="mt-3">
                                <form method="post" action="${pageContext.request.contextPath}/cart" class="d-inline">
                                    <input type="hidden" name="action" value="clear">
                                    <button type="submit" class="btn btn-outline-danger" 
                                            onclick="return confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')">
                                        <i class="bi bi-trash"></i> Xóa toàn bộ
                                    </button>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-5">
                                <i class="bi bi-cart-x" style="font-size: 4rem; color: #ccc;"></i>
                                <p class="mt-3">Giỏ hàng của bạn đang trống</p>
                                <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">
                                    <i class="bi bi-shop"></i> Tiếp tục mua sắm
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <div class="col-lg-4">
                <div class="summary-card">
                    <h5 class="mb-3">Tóm tắt đơn hàng</h5>
                    <hr>
                    <div class="d-flex justify-content-between mb-3">
                        <span>Tổng số sản phẩm:</span>
                        <strong>${cartSize != null ? cartSize : 0}</strong>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span>Tổng tiền:</span>
                        <strong class="text-danger" style="font-size: 1.5rem;">
                            <fmt:formatNumber value="${cartTotal != null ? cartTotal : 0}" type="currency" 
                                currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                        </strong>
                    </div>
                    <hr>
                    <c:if test="${not empty cartItems && cartItems.size() > 0}">
                        <button class="btn btn-primary w-100 btn-lg" onclick="checkout()">
                            <i class="bi bi-credit-card"></i> Thanh toán
                        </button>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-secondary w-100 mt-2">
                        <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white text-center py-4 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 SmartShop. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function updateQuantity(productID, newQuantity, maxStock) {
            if (newQuantity < 1) {
                newQuantity = 1;
            }
            if (newQuantity > maxStock) {
                newQuantity = maxStock;
                alert('Số lượng không được vượt quá số lượng tồn kho: ' + maxStock);
            }
            
            document.getElementById('quantity-' + productID).value = newQuantity;
            
            // Submit form
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/cart';
            
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'update';
            form.appendChild(actionInput);
            
            const productIDInput = document.createElement('input');
            productIDInput.type = 'hidden';
            productIDInput.name = 'productID';
            productIDInput.value = productID;
            form.appendChild(productIDInput);
            
            const quantityInput = document.createElement('input');
            quantityInput.type = 'hidden';
            quantityInput.name = 'quantity';
            quantityInput.value = newQuantity;
            form.appendChild(quantityInput);
            
            document.body.appendChild(form);
            form.submit();
        }
        
        function checkout() {
            window.location.href = '${pageContext.request.contextPath}/checkout';
        }
    </script>
</body>
</html>

