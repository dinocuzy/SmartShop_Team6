<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartShop - Trang chủ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .navbar-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 5rem 0;
            margin-bottom: 3rem;
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
            height: 200px;
            object-fit: cover;
            background: #f0f0f0;
        }
        
        .product-price {
            font-size: 1.25rem;
            font-weight: bold;
            color: #dc3545;
        }
        
        .product-old-price {
            font-size: 0.9rem;
            text-decoration: line-through;
            color: #999;
        }
        
        .badge-special {
            position: absolute;
            top: 10px;
            right: 10px;
            z-index: 10;
        }
        
        .section-title {
            font-size: 2rem;
            font-weight: 600;
            margin-bottom: 2rem;
            color: #333;
        }
        
        .category-card {
            border: none;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s;
            text-decoration: none;
            color: inherit;
        }
        
        .category-card:hover {
            transform: translateY(-5px);
            color: inherit;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-custom navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/shop">
                <i class="bi bi-shop"></i> SmartShop
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/shop">
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

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container text-center">
            <h1 class="display-4 mb-4">Chào mừng đến với SmartShop</h1>
            <p class="lead mb-4">Cửa hàng trực tuyến thông minh - Nơi mua sắm tốt nhất</p>
            <a href="${pageContext.request.contextPath}/shop" class="btn btn-light btn-lg">
                <i class="bi bi-shop"></i> Khám phá ngay
            </a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="container">
        <!-- Categories -->
        <c:if test="${not empty categories}">
            <section class="mb-5">
                <h2 class="section-title"><i class="bi bi-tags"></i> Danh mục sản phẩm</h2>
                <div class="row g-4">
                    <c:forEach var="category" items="${categories}">
                        <div class="col-md-3 col-sm-6">
                            <a href="${pageContext.request.contextPath}/shop?category=${category.categoryID}" 
                               class="category-card card text-center">
                                <div class="card-body">
                                    <i class="bi bi-tag" style="font-size: 3rem; color: #667eea;"></i>
                                    <h5 class="mt-3">${category.categoryName}</h5>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </c:if>
        
        <!-- Featured Products -->
        <c:if test="${not empty featuredProducts}">
            <section class="mb-5">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="section-title mb-0"><i class="bi bi-star-fill"></i> Sản phẩm nổi bật</h2>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-primary">
                        Xem tất cả <i class="bi bi-arrow-right"></i>
                    </a>
                </div>
                <div class="row g-4">
                    <c:forEach var="product" items="${featuredProducts}">
                        <div class="col-md-3 col-sm-6">
                            <div class="card product-card">
                                <c:if test="${product.special}">
                                    <span class="badge bg-warning text-dark badge-special">
                                        <i class="bi bi-star-fill"></i> Đặc biệt
                                    </span>
                                </c:if>
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
                                <div class="card-body">
                                    <h6 class="card-title">
                                        <a href="${pageContext.request.contextPath}/product?id=${product.productID}" 
                                           class="text-decoration-none text-dark">
                                            ${product.productName}
                                        </a>
                                    </h6>
                                    <div class="mb-2">
                                        <span class="product-price">
                                            <fmt:formatNumber value="${product.price}" type="currency" 
                                                currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                        </span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <c:choose>
                                            <c:when test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                                <span class="badge bg-success">Còn hàng</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Hết hàng</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <a href="${pageContext.request.contextPath}/product?id=${product.productID}" 
                                           class="btn btn-sm btn-primary">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </c:if>
        
        <!-- New Products -->
        <c:if test="${not empty newProducts}">
            <section class="mb-5">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="section-title mb-0"><i class="bi bi-clock-history"></i> Sản phẩm mới</h2>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-primary">
                        Xem tất cả <i class="bi bi-arrow-right"></i>
                    </a>
                </div>
                <div class="row g-4">
                    <c:forEach var="product" items="${newProducts}">
                        <div class="col-md-3 col-sm-6">
                            <div class="card product-card">
                                <c:if test="${product.special}">
                                    <span class="badge bg-warning text-dark badge-special">
                                        <i class="bi bi-star-fill"></i> Đặc biệt
                                    </span>
                                </c:if>
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
                                <div class="card-body">
                                    <h6 class="card-title">
                                        <a href="${pageContext.request.contextPath}/product?id=${product.productID}" 
                                           class="text-decoration-none text-dark">
                                            ${product.productName}
                                        </a>
                                    </h6>
                                    <div class="mb-2">
                                        <span class="product-price">
                                            <fmt:formatNumber value="${product.price}" type="currency" 
                                                currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                        </span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <c:choose>
                                            <c:when test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                                <span class="badge bg-success">Còn hàng</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Hết hàng</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <a href="${pageContext.request.contextPath}/product?id=${product.productID}" 
                                           class="btn btn-sm btn-primary">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </c:if>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white py-5 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <h5><i class="bi bi-shop"></i> SmartShop</h5>
                    <p>Cửa hàng trực tuyến thông minh - Nơi mua sắm tốt nhất</p>
                </div>
                <div class="col-md-4">
                    <h5>Liên kết nhanh</h5>
                    <ul class="list-unstyled">
                        <li><a href="${pageContext.request.contextPath}/shop" class="text-white-50 text-decoration-none">Cửa hàng</a></li>
                        <li><a href="${pageContext.request.contextPath}/login" class="text-white-50 text-decoration-none">Đăng nhập</a></li>
                    </ul>
                </div>
                <div class="col-md-4">
                    <h5>Liên hệ</h5>
                    <p class="text-white-50">
                        <i class="bi bi-envelope"></i> binh222120@gmail.com<br>
                        <i class="bi bi-telephone"></i> 0833347220
                    </p>
                </div>
            </div>
            <hr class="bg-white-50">
            <div class="text-center">
                <p class="mb-0">&copy; 2024 SmartShop. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

