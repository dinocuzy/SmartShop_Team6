<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.productName} - SmartShop</title>
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
        
        .product-detail-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 2rem;
        }
        
        .product-image-large {
            width: 100%;
            max-height: 500px;
            object-fit: cover;
            border-radius: 10px;
        }
        
        .product-price-large {
            font-size: 2.5rem;
            font-weight: bold;
            color: #dc3545;
        }
        
        .product-old-price-large {
            font-size: 1.5rem;
            text-decoration: line-through;
            color: #999;
        }
        
        .product-card {
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        
        .product-card .card-body {
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }
        
        .product-card .card-title {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            line-height: 1.4;
            min-height: 2.8rem;
            max-height: 2.8rem;
        }
        
        .product-card .card-title a {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .product-info-card {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1.5rem;
            margin-top: 1rem;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="${product.productName}" />
    </jsp:include>

    <!-- Main Content -->
    <div class="container my-5">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty product}">
            <div class="product-detail-card">
                <div class="row">
                    <!-- Product Image -->
                    <div class="col-md-6">
                        <c:choose>
                            <c:when test="${not empty product.imageUrl}">
                                <img src="${product.imageUrl}" class="product-image-large" alt="${product.productName}">
                            </c:when>
                            <c:otherwise>
                                <div class="product-image-large d-flex align-items-center justify-content-center bg-light" 
                                     style="height: 500px;">
                                    <i class="bi bi-image" style="font-size: 5rem; color: #ccc;"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${product.special}">
                            <span class="badge bg-warning text-dark mt-3">
                                <i class="bi bi-star-fill"></i> Sản phẩm đặc biệt
                            </span>
                        </c:if>
                    </div>
                    
                    <!-- Product Info -->
                    <div class="col-md-6">
                        <h1 class="mb-3">${product.productName}</h1>
                        
                        <div class="mb-3">
                            <span class="badge bg-info">${product.categoryName != null ? product.categoryName : 'Chưa phân loại'}</span>
                            <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                <span class="badge bg-success">Còn hàng</span>
                            </c:if>
                            <c:if test="${product.stockStatus == 'OutOfStock' || product.stock <= 0}">
                                <span class="badge bg-danger">Hết hàng</span>
                            </c:if>
                        </div>
                        
                        <div class="mb-4">
                            <span class="product-price-large">
                                <fmt:formatNumber value="${product.price}" type="currency" 
                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                            </span>
                        </div>
                        
                        <div class="product-info-card mb-4">
                            <h5><i class="bi bi-info-circle"></i> Thông tin sản phẩm</h5>
                            <hr>
                            <p><strong>Mã sản phẩm:</strong> #${product.productID}</p>
                            <c:if test="${product.stock > 0}">
                                <p><strong>Số lượng còn:</strong> ${product.stock} sản phẩm</p>
                            </c:if>
                            <c:if test="${not empty product.size}">
                                <p><strong>Kích thước:</strong> ${product.size}</p>
                            </c:if>
                            <c:if test="${not empty product.color}">
                                <p><strong>Màu sắc:</strong> ${product.color}</p>
                            </c:if>
                            <c:if test="${not empty product.slug}">
                                <p><strong>Slug:</strong> /${product.slug}</p>
                            </c:if>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                <button class="btn btn-primary btn-lg" onclick="addToCart(${product.productID})">
                                    <i class="bi bi-cart-plus"></i> Thêm vào giỏ hàng
                                </button>
                            </c:if>
                            <button class="btn btn-outline-danger" onclick="toggleWishlist(${product.productID})" id="wishlistBtn">
                                <i class="bi bi-heart" id="wishlistIcon"></i> 
                                <span id="wishlistText">Thêm vào yêu thích</span>
                            </button>
                        </div>
                        <c:if test="${product.stockStatus == 'OutOfStock' || product.stock <= 0}">
                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle"></i> Sản phẩm này hiện không còn hàng
                            </div>
                        </c:if>
                    </div>
                </div>
                
                <!-- Product Description -->
                <div class="row mt-4">
                    <div class="col-12">
                        <div class="product-info-card">
                            <h5><i class="bi bi-file-text"></i> Mô tả sản phẩm</h5>
                            <hr>
                            <div class="product-description">
                                <c:choose>
                                    <c:when test="${not empty product.description}">
                                        ${product.description}
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted">Chưa có mô tả cho sản phẩm này.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Recommended Products Section -->
            <c:if test="${not empty recommendedProducts}">
                <div class="row mt-5">
                    <div class="col-12">
                        <h4 class="mb-4"><i class="bi bi-star-fill text-warning"></i> Sản phẩm gợi ý dựa trên lượt xem</h4>
                        <div class="row g-4">
                            <c:forEach var="recProduct" items="${recommendedProducts}" varStatus="status">
                                <c:if test="${status.index < 6}">
                                    <div class="col-md-4 col-sm-6">
                                        <div class="card product-card h-100 border-0 shadow-sm">
                                            <a href="${pageContext.request.contextPath}/product/detail?id=${recProduct.productID}" class="text-decoration-none">
                                                <div class="position-relative">
                                                    <img src="${pageContext.request.contextPath}${recProduct.imageUrl}" 
                                                         class="card-img-top product-image" 
                                                         alt="${recProduct.productName}"
                                                         onerror="this.src='${pageContext.request.contextPath}/images/default-product.png'">
                                                    <c:if test="${recProduct.isSpecial}">
                                                        <span class="badge bg-danger position-absolute top-0 start-0 m-2">Đặc biệt</span>
                                                    </c:if>
                                                </div>
                                            </a>
                                            <div class="card-body d-flex flex-column">
                                                <h6 class="card-title" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis; line-height: 1.4; min-height: 2.8rem; max-height: 2.8rem;">
                                                    <a href="${pageContext.request.contextPath}/product/detail?id=${recProduct.productID}" 
                                                       class="text-decoration-none text-dark" style="display: block; overflow: hidden; text-overflow: ellipsis;">
                                                        ${recProduct.productName}
                                                    </a>
                                                </h6>
                                                <div class="mt-auto">
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <div>
                                                            <span class="text-danger fw-bold fs-5">
                                                                <fmt:formatNumber value="${recProduct.price}" type="currency" currencyCode="VND" />
                                                            </span>
                                                        </div>
                                                        <c:if test="${recProduct.stock > 0}">
                                                            <span class="badge bg-success">Còn hàng</span>
                                                        </c:if>
                                                        <c:if test="${recProduct.stock <= 0}">
                                                            <span class="badge bg-secondary">Hết hàng</span>
                                                        </c:if>
                                                    </div>
                                                    <div class="mt-2">
                                                        <button class="btn btn-primary btn-sm w-100" 
                                                                onclick="addToCart(${recProduct.productID})"
                                                                ${recProduct.stock <= 0 ? 'disabled' : ''}>
                                                            <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Back to Shop -->
            <div class="text-center mt-4">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary">
                    <i class="bi bi-arrow-left"></i> Quay lại cửa hàng
                </a>
            </div>
        </c:if>
        
        <c:if test="${empty product}">
            <div class="text-center py-5">
                <i class="bi bi-exclamation-triangle" style="font-size: 4rem; color: #ffc107;"></i>
                <p class="mt-3">Không tìm thấy sản phẩm</p>
                <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">
                    <i class="bi bi-arrow-left"></i> Về trang chủ
                </a>
            </div>
        </c:if>
    </div>

    <jsp:include page="/views/common/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Check if product is in wishlist on page load
        window.addEventListener('DOMContentLoaded', function() {
            checkWishlistStatus(${product.productID});
        });
        
        function addToCart(productID) {
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
            
            const redirectInput = document.createElement('input');
            redirectInput.type = 'hidden';
            redirectInput.name = 'redirect';
            redirectInput.value = '${pageContext.request.contextPath}/product/detail?id=' + productID;
            form.appendChild(redirectInput);
            
            document.body.appendChild(form);
            form.submit();
        }
        
        function toggleWishlist(productID) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/wishlist';
            
            // Check current status (simplified - in real app, use AJAX)
            const icon = document.getElementById('wishlistIcon');
            const isActive = icon.classList.contains('bi-heart-fill');
            
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = isActive ? 'remove' : 'add';
            form.appendChild(actionInput);
            
            const productIDInput = document.createElement('input');
            productIDInput.type = 'hidden';
            productIDInput.name = 'productID';
            productIDInput.value = productID;
            form.appendChild(productIDInput);
            
            const redirectInput = document.createElement('input');
            redirectInput.type = 'hidden';
            redirectInput.name = 'redirect';
            redirectInput.value = '${pageContext.request.contextPath}/product/detail?id=' + productID;
            form.appendChild(redirectInput);
            
            document.body.appendChild(form);
            form.submit();
        }
        
        function checkWishlistStatus(productID) {
            // This would ideally be an AJAX call to check status
            // For now, we'll just show the button in default state
        }
    </script>
</body>
</html>

