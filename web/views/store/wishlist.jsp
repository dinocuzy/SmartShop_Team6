<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách yêu thích - SmartShop</title>
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
        
        .product-card {
            border: none;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s, box-shadow 0.3s;
            height: 100%;
        }
        
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        }
        
        .product-image {
            width: 100%;
            height: 250px;
            object-fit: cover;
            background: #f0f0f0;
        }
        
        .product-price {
            font-size: 1.5rem;
            font-weight: bold;
            color: #dc3545;
        }
        
        .product-old-price {
            font-size: 1rem;
            text-decoration: line-through;
            color: #999;
        }
        
        .wishlist-icon {
            position: absolute;
            top: 10px;
            right: 10px;
            z-index: 10;
            background: white;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            box-shadow: 0 2px 5px rgba(0,0,0,0.2);
            transition: transform 0.3s;
        }
        
        .wishlist-icon:hover {
            transform: scale(1.1);
        }
        
        .wishlist-icon.active {
            color: #dc3545;
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
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/wishlist">
                            <i class="bi bi-heart"></i> Yêu thích
                            <span class="badge bg-light text-dark">${wishlistSize != null ? wishlistSize : 0}</span>
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
        <h2 class="mb-4"><i class="bi bi-heart"></i> Danh sách yêu thích của bạn</h2>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:choose>
            <c:when test="${not empty wishlistProducts && wishlistProducts.size() > 0}">
                <div class="row">
                    <c:forEach var="product" items="${wishlistProducts}">
                        <div class="col-md-3 mb-4">
                            <div class="card product-card">
                                <div class="position-relative">
                                    <c:choose>
                                        <c:when test="${not empty product.imageUrl}">
                                            <img src="${product.imageUrl}" class="product-image" alt="${product.productName}">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="product-image d-flex align-items-center justify-content-center bg-light">
                                                <i class="bi bi-image" style="font-size: 3rem; color: #ccc;"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="wishlist-icon active" onclick="removeFromWishlist(${product.productID})">
                                        <i class="bi bi-heart-fill"></i>
                                    </div>
                                    <c:if test="${product.special}">
                                        <span class="badge bg-warning text-dark badge-special">
                                            <i class="bi bi-star-fill"></i> Đặc biệt
                                        </span>
                                    </c:if>
                                </div>
                                <div class="card-body">
                                    <h5 class="card-title">
                                        <a href="${pageContext.request.contextPath}/product/detail?id=${product.productID}" 
                                           class="text-decoration-none text-dark">
                                            ${product.productName}
                                        </a>
                                    </h5>
                                    <p class="text-muted mb-2">
                                        <small>${product.categoryName != null ? product.categoryName : 'Chưa phân loại'}</small>
                                    </p>
                                    <div class="mb-2">
                                        <span class="product-price">
                                            <fmt:formatNumber value="${product.price}" type="currency" 
                                                currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                        </span>
                                    </div>
                                    <div class="mb-2">
                                        <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                            <span class="badge bg-success">Còn hàng</span>
                                        </c:if>
                                        <c:if test="${product.stockStatus == 'OutOfStock' || product.stock <= 0}">
                                            <span class="badge bg-danger">Hết hàng</span>
                                        </c:if>
                                    </div>
                                    <div class="d-grid gap-2">
                                        <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                            <button class="btn btn-primary btn-sm" 
                                                    onclick="addToCart(${product.productID}, '${pageContext.request.contextPath}/wishlist')">
                                                <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                                            </button>
                                        </c:if>
                                        <a href="${pageContext.request.contextPath}/product/detail?id=${product.productID}" 
                                           class="btn btn-outline-secondary btn-sm">
                                            <i class="bi bi-eye"></i> Xem chi tiết
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="bi bi-heart" style="font-size: 4rem; color: #ccc;"></i>
                    <p class="mt-3">Danh sách yêu thích của bạn đang trống</p>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">
                        <i class="bi bi-shop"></i> Tiếp tục mua sắm
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white text-center py-4 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 SmartShop. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function removeFromWishlist(productID) {
            if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi danh sách yêu thích?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/wishlist';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'remove';
                form.appendChild(actionInput);
                
                const productIDInput = document.createElement('input');
                productIDInput.type = 'hidden';
                productIDInput.name = 'productID';
                productIDInput.value = productID;
                form.appendChild(productIDInput);
                
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        function addToCart(productID, redirectUrl) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/cart';
            
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'add';
            form.appendChild(actionInput);
            
            const productIDInput = document.createElement('input');
            productIDInput.type = 'hidden';
            productIDInput.name = 'productID';
            productIDInput.value = productID;
            form.appendChild(productIDInput);
            
            if (redirectUrl) {
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.name = 'redirect';
                redirectInput.value = redirectUrl;
                form.appendChild(redirectInput);
            }
            
            document.body.appendChild(form);
            form.submit();
        }
    </script>
</body>
</html>

