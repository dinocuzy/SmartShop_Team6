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
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .product-card {
            border: none;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.3);
            transition: transform 0.3s, box-shadow 0.3s;
            height: 100%;
            background: #2c2c2c;
            border: 1px solid #444;
        }
        
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(139, 92, 246, 0.3);
        }
        
        .product-image {
            width: 100% !important;
            height: 250px !important;
            min-width: 100% !important;
            min-height: 250px !important;
            max-width: none !important;
            max-height: none !important;
            object-fit: cover !important;
            object-position: center;
            background: #1a1a1a;
            display: block;
        }
        
        .product-price {
            font-size: 1.5rem;
            font-weight: bold;
            color: #dc3545;
        }
        
        .product-old-price {
            font-size: 1rem;
            text-decoration: line-through;
            color: #6a6a6a;
        }
        
        .wishlist-icon {
            position: absolute;
            top: 10px;
            right: 10px;
            z-index: 10;
            background: #2c2c2c;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            box-shadow: 0 2px 5px rgba(0,0,0,0.5);
            transition: transform 0.3s;
            border: 1px solid #444;
        }
        
        .wishlist-icon:hover {
            transform: scale(1.1);
        }
        
        .wishlist-icon.active {
            color: #dc3545;
        }
        
        .card-body {
            color: #fff;
        }
        
        .card-title a {
            color: #fff !important;
        }
        
        .card-title a:hover {
            color: #8b5cf6 !important;
        }
        
        .text-muted {
            color: #b0b0b0 !important;
        }
        
        .btn {
            border-radius: 8px;
        }
        
        .bg-light {
            background-color: #1a1a1a !important;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp">
        <jsp:param name="active" value="wishlist" />
    </jsp:include>
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Danh sách yêu thích" />
    </jsp:include>

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
                                        <a href="javascript:void(0);" 
                                           onclick="openProductModal(${product.productID})"
                                           class="text-decoration-none">
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

    <jsp:include page="/views/common/footer.jsp" />

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
    
    <!-- Product Detail Modal -->
    <jsp:include page="/views/common/productModal.jsp" />
</body>
</html>

