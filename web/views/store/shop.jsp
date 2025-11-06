<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cửa hàng - SmartShop</title>
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
        
        .badge-special {
            position: absolute;
            top: 10px;
            right: 10px;
            z-index: 10;
        }
        
        .category-filter {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }
        
        .search-filter {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/shop?category=0">
                            <i class="bi bi-grid"></i> Tất cả sản phẩm
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
        <h1 class="mb-4"><i class="bi bi-shop"></i> Cửa hàng SmartShop</h1>
        
        <!-- Search and Filter -->
        <div class="search-filter">
            <form method="get" action="${pageContext.request.contextPath}/shop" class="row g-3">
                <div class="col-md-6">
                    <input type="text" class="form-control" name="search" placeholder="Tìm kiếm sản phẩm..." 
                           value="${searchKeyword != null ? searchKeyword : ''}">
                </div>
                <div class="col-md-3">
                    <select class="form-select" name="category">
                        <option value="0" ${categoryID == 0 ? 'selected' : ''}>Tất cả danh mục</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryID}" ${categoryID == cat.categoryID ? 'selected' : ''}>
                                ${cat.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <select class="form-select" name="sortBy">
                        <option value="ProductID" ${sortBy == 'ProductID' ? 'selected' : ''}>Mới nhất</option>
                        <option value="Price" ${sortBy == 'Price' ? 'selected' : ''}>Giá</option>
                        <option value="ProductName" ${sortBy == 'ProductName' ? 'selected' : ''}>Tên</option>
                    </select>
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-search"></i>
                    </button>
                </div>
            </form>
        </div>
        
        <!-- Products Grid -->
        <c:if test="${not empty products}">
            <div class="row mb-4">
                <div class="col-12">
                    <p class="text-muted">
                        Hiển thị ${fn:length(products)} / ${totalProducts} sản phẩm
                    </p>
                </div>
            </div>
            
            <div class="row g-4">
                <c:forEach var="product" items="${products}">
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
                                <h5 class="card-title">
                                    <a href="${pageContext.request.contextPath}/product?id=${product.productID}" 
                                       class="text-decoration-none text-dark">
                                        ${product.productName}
                                    </a>
                                </h5>
                                <p class="card-text text-muted small">
                                    ${product.categoryName != null ? product.categoryName : 'Chưa phân loại'}
                                </p>
                                <div class="mb-2">
                                    <span class="product-price">
                                        <fmt:formatNumber value="${product.price}" type="currency" 
                                            currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                    </span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <c:choose>
                                        <c:when test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                            <span class="badge bg-success">Còn hàng (${product.stock})</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger">Hết hàng</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="d-grid gap-2">
                                    <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                        <button class="btn btn-primary btn-sm" 
                                                onclick="addToCart(${product.productID}, '${pageContext.request.contextPath}/shop')">
                                            <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                                        </button>
                                    </c:if>
                                    <div class="btn-group" role="group">
                                        <a href="${pageContext.request.contextPath}/product/detail?id=${product.productID}" 
                                           class="btn btn-outline-secondary btn-sm">
                                            <i class="bi bi-eye"></i> Xem
                                        </a>
                                        <button class="btn btn-outline-danger btn-sm" 
                                                onclick="toggleWishlist(${product.productID}, '${pageContext.request.contextPath}/shop')"
                                                title="Thêm vào yêu thích">
                                            <i class="bi bi-heart"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation" class="mt-4">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/shop?page=${currentPage - 1}&category=${categoryID}&search=${searchKeyword}&sortBy=${sortBy}">
                                <i class="bi bi-chevron-left"></i>
                            </a>
                        </li>
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/shop?page=${i}&category=${categoryID}&search=${searchKeyword}&sortBy=${sortBy}">
                                        ${i}
                                    </a>
                                </li>
                            </c:if>
                            <c:if test="${i == currentPage - 3 || i == currentPage + 3}">
                                <li class="page-item disabled">
                                    <span class="page-link">...</span>
                                </li>
                            </c:if>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/shop?page=${currentPage + 1}&category=${categoryID}&search=${searchKeyword}&sortBy=${sortBy}">
                                <i class="bi bi-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </c:if>
        
        <c:if test="${empty products}">
            <div class="text-center py-5">
                <i class="bi bi-inbox" style="font-size: 4rem; color: #ccc;"></i>
                <p class="mt-3 text-muted">Không tìm thấy sản phẩm nào</p>
                <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">
                    <i class="bi bi-arrow-left"></i> Về trang chủ
                </a>
            </div>
        </c:if>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white text-center py-4 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 SmartShop. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
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
        
        function toggleWishlist(productID, redirectUrl) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/wishlist';
            
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

