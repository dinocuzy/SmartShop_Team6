<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tất cả sản phẩm - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- Define functions IMMEDIATELY in head to ensure they're available when onclick handlers are evaluated -->
    <script>
        // Define openProductModal function early as placeholder
        // Will be fully implemented when productModal.jsp is loaded at end of body
        window.openProductModal = function(productID) {
            console.log('openProductModal placeholder called with productID:', productID);
            // Check if full implementation is available (will be set by productModal.jsp)
            if (window.openProductModalFull && typeof window.openProductModalFull === 'function') {
                console.log('Delegating to full implementation');
                return window.openProductModalFull(productID);
            }
            // Fallback: load product data
            console.warn('Full implementation not ready yet, loading product data...');
            fetch('${pageContext.request.contextPath}/product/api?id=' + productID)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP error! status: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        alert('Lỗi: ' + data.error);
                        return;
                    }
                    console.log('Product loaded, trying to show modal...');
                    // Wait a bit for modal element to be available
                    setTimeout(function() {
                        const modalElement = document.getElementById('productDetailModal');
                        if (modalElement) {
                            // Try to populate modal fields if they exist
                            try {
                                const nameEl = document.getElementById('modalProductName');
                                const codeEl = document.getElementById('modalProductCode');
                                const imageEl = document.getElementById('modalProductImage');
                                const priceEl = document.getElementById('modalPriceCurrent');
                                const descEl = document.getElementById('modalDescription');
                                
                                if (nameEl) nameEl.textContent = data.productName || 'Sản phẩm';
                                if (codeEl) codeEl.textContent = '#' + productID;
                                if (imageEl) imageEl.src = data.imageUrl || '';
                                if (priceEl) priceEl.textContent = new Intl.NumberFormat('vi-VN', {
                                    style: 'currency',
                                    currency: 'VND'
                                }).format(data.price || 0);
                                if (descEl) descEl.textContent = data.description || 'Không có mô tả';
                                
                                // Show modal using Bootstrap
                                if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                                    const modal = new bootstrap.Modal(modalElement);
                                    modal.show();
                                } else {
                                    // Manual fallback
                                    modalElement.style.display = 'block';
                                    modalElement.classList.add('show');
                                    document.body.classList.add('modal-open');
                                }
                            } catch (e) {
                                console.error('Error populating modal:', e);
                                alert('Sản phẩm: ' + (data.productName || 'N/A') + '\nGiá: ' + 
                                      new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(data.price || 0) + 
                                      '\nVui lòng refresh trang để xem đầy đủ thông tin.');
                            }
                        } else {
                            alert('Sản phẩm: ' + (data.productName || 'N/A') + '\nGiá: ' + 
                                  new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(data.price || 0));
                        }
                    }, 100);
                })
                .catch(error => {
                    console.error('Error loading product:', error);
                    alert('Không thể tải thông tin sản phẩm. Vui lòng thử lại.');
                });
        };
        
        // Define addToCart function early
        window.addToCart = function(productID, redirectUrl) {
            console.log('addToCart called with productID:', productID, 'redirectUrl:', redirectUrl);
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
        };
        
        // Define toggleWishlist function early
        window.toggleWishlist = function(productID, redirectUrl) {
            console.log('toggleWishlist called with productID:', productID, 'redirectUrl:', redirectUrl);
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
        };
        
        // Define toggleChatbot placeholder
        window.toggleChatbot = window.toggleChatbot || function() {
            console.log('toggleChatbot placeholder called');
            const chatWindow = document.getElementById('chatbotWindow');
            if (chatWindow) {
                chatWindow.classList.toggle('active');
                if (typeof window.toggleChatbotFull === 'function') {
                    window.toggleChatbotFull();
                }
            } else {
                console.warn('chatbotWindow not found');
            }
        };
    </script>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #ffffff;
            min-height: 100vh;
        }
        
        .main-container {
            background-color: #1a1a1a;
            padding: 2rem 0;
        }
        
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding: 0 1rem;
        }
        
        .page-title {
            font-size: 2rem;
            font-weight: bold;
            color: #ffffff;
            margin: 0;
        }
        
        .sort-buttons {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
        }
        
        .sort-btn {
            background-color: #2d2d2d;
            border: 1px solid #444;
            color: #ffffff;
            padding: 0.5rem 1rem;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            font-size: 0.9rem;
        }
        
        .sort-btn:hover {
            background-color: #3d3d3d;
            border-color: #666;
            color: #ffffff;
        }
        
        .sort-btn.active {
            background-color: #dc3545;
            border-color: #dc3545;
        }
        
        .content-wrapper {
            display: flex;
            gap: 2rem;
            padding: 0 1rem;
        }
        
        .products-grid {
            flex: 1;
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 1.5rem;
        }
        
        .product-card {
            background-color: #2d2d2d;
            border-radius: 10px;
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            border: 1px solid #444;
            min-width: 0;
            width: 100%;
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(220, 53, 69, 0.3);
        }
        
        .product-image-wrapper {
            position: relative;
            width: 100%;
            height: 280px;
            min-height: 280px;
            overflow: hidden;
            background-color: #1a1a1a;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .product-image {
            width: 100% !important;
            height: 100% !important;
            min-width: 100% !important;
            min-height: 100% !important;
            max-width: none !important;
            max-height: none !important;
            object-fit: cover !important;
            object-position: center;
            display: block;
            flex-shrink: 0;
        }
        
        .product-badge {
            position: absolute;
            top: 10px;
            right: 10px;
            background-color: #dc3545;
            color: #ffffff;
            padding: 0.25rem 0.5rem;
            border-radius: 5px;
            font-size: 0.85rem;
            font-weight: bold;
        }
        
        .product-promotion-banner {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            background-color: rgba(220, 53, 69, 0.9);
            color: #ffffff;
            padding: 0.5rem;
            text-align: center;
            font-size: 0.8rem;
            font-weight: bold;
            border-bottom: 2px solid #dc3545;
        }
        
        .product-info {
            padding: 1rem;
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }
        
        .product-name {
            font-size: 1rem;
            font-weight: 600;
            color: #ffffff;
            margin-bottom: 0.5rem;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            line-height: 1.4;
            min-height: 2.8rem;
            max-height: 2.8rem;
        }
        
        .product-name a {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .product-price-wrapper {
            margin-bottom: 0.75rem;
        }
        
        .product-price {
            font-size: 1.5rem;
            font-weight: bold;
            color: #dc3545;
        }
        
        .product-old-price {
            font-size: 1rem;
            text-decoration: line-through;
            color: #888;
            margin-left: 0.5rem;
        }
        
        .product-actions {
            display: flex;
            gap: 0.5rem;
            margin-top: 1rem;
        }
        
        .action-btn {
            flex: 1;
            background-color: #dc3545;
            border: none;
            color: #ffffff;
            padding: 0.5rem;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.25rem;
        }
        
        .action-btn:hover {
            background-color: #c82333;
        }
        
        .action-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background-color: #3d3d3d;
            border: 1px solid #555;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s;
            color: #ffffff;
        }
        
        .action-icon:hover {
            background-color: #4d4d4d;
            border-color: #dc3545;
        }
        
        .filter-sidebar {
            width: 280px;
            background-color: #2d2d2d;
            border-radius: 10px;
            padding: 1.5rem;
            height: fit-content;
            border: 1px solid #444;
        }
        
        .filter-section {
            margin-bottom: 2rem;
        }
        
        .filter-section:last-child {
            margin-bottom: 0;
        }
        
        .filter-title {
            font-size: 1.1rem;
            font-weight: bold;
            color: #ffffff;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #444;
        }
        
        .filter-option {
            display: flex;
            align-items: center;
            margin-bottom: 0.75rem;
        }
        
        .filter-option input[type="checkbox"],
        .filter-option input[type="radio"] {
            margin-right: 0.5rem;
            width: 18px;
            height: 18px;
            cursor: pointer;
        }
        
        .filter-option label {
            color: #cccccc;
            cursor: pointer;
            flex: 1;
        }
        
        .filter-option label:hover {
            color: #ffffff;
        }
        
        .view-more-link {
            color: #dc3545;
            text-decoration: none;
            font-size: 0.9rem;
            margin-top: 0.5rem;
            display: inline-block;
        }
        
        .view-more-link:hover {
            text-decoration: underline;
        }
        
        .apply-filter-btn {
            width: 100%;
            background-color: #dc3545;
            border: none;
            color: #ffffff;
            padding: 0.75rem;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
            margin-top: 1rem;
            transition: all 0.3s;
        }
        
        .apply-filter-btn:hover {
            background-color: #c82333;
        }
        
        .pagination-wrapper {
            margin-top: 2rem;
            display: flex;
            justify-content: center;
        }
        
        .pagination {
            --bs-pagination-color: #ffffff;
            --bs-pagination-bg: #2d2d2d;
            --bs-pagination-border-color: #444;
            --bs-pagination-hover-color: #ffffff;
            --bs-pagination-hover-bg: #3d3d3d;
            --bs-pagination-hover-border-color: #666;
            --bs-pagination-active-color: #ffffff;
            --bs-pagination-active-bg: #dc3545;
            --bs-pagination-active-border-color: #dc3545;
        }
        
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
            color: #888;
        }
        
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp">
        <jsp:param name="active" value="shop" />
    </jsp:include>
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Tất cả sản phẩm" />
    </jsp:include>

    <div class="main-container">
        <div class="container-fluid">
            <!-- Page Header with Sort Options -->
            <div class="page-header">
                <h1 class="page-title">Tất cả sản phẩm</h1>
                <div class="sort-buttons">
                    <c:url var="sortUrl1" value="/shop">
                        <c:param name="sortBy" value="ProductName"/>
                        <c:param name="sortOrder" value="ASC"/>
                        <c:if test="${categoryID > 0}">
                            <c:param name="category" value="${categoryID}"/>
                        </c:if>
                        <c:if test="${not empty searchKeyword}">
                            <c:param name="search" value="${searchKeyword}"/>
                        </c:if>
                        <c:if test="${minPrice != null}">
                            <c:param name="minPrice" value="${minPrice}"/>
                        </c:if>
                        <c:if test="${maxPrice != null}">
                            <c:param name="maxPrice" value="${maxPrice}"/>
                        </c:if>
                    </c:url>
                    <a href="${sortUrl1}" 
                       class="sort-btn ${sortBy eq 'ProductName' && sortOrder eq 'ASC' ? 'active' : ''}">
                        Tên A -> Z
                    </a>
                    
                    <c:url var="sortUrl2" value="/shop">
                        <c:param name="sortBy" value="ProductName"/>
                        <c:param name="sortOrder" value="DESC"/>
                        <c:if test="${categoryID > 0}">
                            <c:param name="category" value="${categoryID}"/>
                        </c:if>
                        <c:if test="${not empty searchKeyword}">
                            <c:param name="search" value="${searchKeyword}"/>
                        </c:if>
                        <c:if test="${minPrice != null}">
                            <c:param name="minPrice" value="${minPrice}"/>
                        </c:if>
                        <c:if test="${maxPrice != null}">
                            <c:param name="maxPrice" value="${maxPrice}"/>
                        </c:if>
                    </c:url>
                    <a href="${sortUrl2}" 
                       class="sort-btn ${sortBy eq 'ProductName' && sortOrder eq 'DESC' ? 'active' : ''}">
                        Tên Z -> A
                    </a>
                    
                    <c:url var="sortUrl3" value="/shop">
                        <c:param name="sortBy" value="Price"/>
                        <c:param name="sortOrder" value="ASC"/>
                        <c:if test="${categoryID > 0}">
                            <c:param name="category" value="${categoryID}"/>
                        </c:if>
                        <c:if test="${not empty searchKeyword}">
                            <c:param name="search" value="${searchKeyword}"/>
                        </c:if>
                        <c:if test="${minPrice != null}">
                            <c:param name="minPrice" value="${minPrice}"/>
                        </c:if>
                        <c:if test="${maxPrice != null}">
                            <c:param name="maxPrice" value="${maxPrice}"/>
                        </c:if>
                    </c:url>
                    <a href="${sortUrl3}" 
                       class="sort-btn ${sortBy eq 'Price' && sortOrder eq 'ASC' ? 'active' : ''}">
                        Giá tăng dần
                    </a>
                    
                    <c:url var="sortUrl4" value="/shop">
                        <c:param name="sortBy" value="Price"/>
                        <c:param name="sortOrder" value="DESC"/>
                        <c:if test="${categoryID > 0}">
                            <c:param name="category" value="${categoryID}"/>
                        </c:if>
                        <c:if test="${not empty searchKeyword}">
                            <c:param name="search" value="${searchKeyword}"/>
                        </c:if>
                        <c:if test="${minPrice != null}">
                            <c:param name="minPrice" value="${minPrice}"/>
                        </c:if>
                        <c:if test="${maxPrice != null}">
                            <c:param name="maxPrice" value="${maxPrice}"/>
                        </c:if>
                    </c:url>
                    <a href="${sortUrl4}" 
                       class="sort-btn ${sortBy eq 'Price' && sortOrder eq 'DESC' ? 'active' : ''}">
                        Giá giảm dần
                    </a>
                    
                    <c:url var="sortUrl5" value="/shop">
                        <c:param name="sortBy" value="ProductID"/>
                        <c:param name="sortOrder" value="DESC"/>
                        <c:if test="${categoryID > 0}">
                            <c:param name="category" value="${categoryID}"/>
                        </c:if>
                        <c:if test="${not empty searchKeyword}">
                            <c:param name="search" value="${searchKeyword}"/>
                        </c:if>
                        <c:if test="${minPrice != null}">
                            <c:param name="minPrice" value="${minPrice}"/>
                        </c:if>
                        <c:if test="${maxPrice != null}">
                            <c:param name="maxPrice" value="${maxPrice}"/>
                        </c:if>
                    </c:url>
                    <a href="${sortUrl5}" 
                       class="sort-btn ${(sortBy eq 'ProductID' or empty sortBy) && (sortOrder eq 'DESC' or empty sortOrder) ? 'active' : ''}">
                        Mới nhất
                    </a>
                </div>
            </div>

            <!-- Content Wrapper -->
            <div class="content-wrapper">
                <!-- Products Grid -->
                <div class="products-grid">
                    <c:choose>
                        <c:when test="${not empty products}">
                            <c:forEach var="product" items="${products}">
                                <div class="product-card">
                                    <div class="product-image-wrapper">
                                        <c:if test="${product.special}">
                                            <div class="product-promotion-banner">
                                                Khai trương cửa hàng - Giảm giá lên đến 30%
                                            </div>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${not empty product.imageUrl}">
                                                <img src="${product.imageUrl}" class="product-image" alt="${product.productName}">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="product-image d-flex align-items-center justify-content-center">
                                                    <i class="bi bi-image" style="font-size: 3rem; color: #666;"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:set var="promotionID" value="${productPromotionMap[product.productID]}" />
                                        <c:if test="${not empty promotionID}">
                                            <c:forEach var="promo" items="${promotions}">
                                                <c:if test="${promo.promotionID == promotionID}">
                                                    <c:set var="discountPercent" value="0" />
                                                    <c:if test="${promo.discountPercent != null && promo.discountPercent > 0}">
                                                        <c:set var="discountPercent" value="${promo.discountPercent.intValue()}" />
                                                    </c:if>
                                                    <c:if test="${discountPercent > 0}">
                                                        <div class="product-badge">-${discountPercent}%</div>
                                                    </c:if>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                    </div>
                                    <div class="product-info">
                                        <h3 class="product-name">
                                            <a href="javascript:void(0);" 
                                               onclick="openProductModal(${product.productID})"
                                               class="text-decoration-none text-white">
                                                ${product.productName}
                                            </a>
                                        </h3>
                                        <p class="text-muted small mb-2" style="font-size: 0.85rem; color: #888 !important;">
                                            ${product.categoryName != null ? product.categoryName : 'Chưa phân loại'}
                                        </p>
                                        <div class="product-price-wrapper">
                                            <c:set var="originalPrice" value="${product.price}" />
                                            <c:set var="discountedPrice" value="${product.price}" />
                                            <c:if test="${not empty promotionID}">
                                                <c:forEach var="promo" items="${promotions}">
                                                    <c:if test="${promo.promotionID == promotionID}">
                                                        <c:if test="${promo.discountPercent != null && promo.discountPercent > 0}">
                                                            <c:set var="originalPrice" value="${product.price / (1 - promo.discountPercent / 100)}" />
                                                            <c:set var="discountedPrice" value="${product.price}" />
                                                        </c:if>
                                                        <c:if test="${promo.discountAmount != null && promo.discountAmount > 0}">
                                                            <c:set var="originalPrice" value="${product.price + promo.discountAmount}" />
                                                            <c:set var="discountedPrice" value="${product.price}" />
                                                        </c:if>
                                                    </c:if>
                                                </c:forEach>
                                            </c:if>
                                            <span class="product-price">
                                                <fmt:formatNumber value="${discountedPrice}" type="currency" 
                                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                            </span>
                                            <c:if test="${originalPrice > discountedPrice}">
                                                <span class="product-old-price">
                                                    <fmt:formatNumber value="${originalPrice}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </span>
                                            </c:if>
                                        </div>
                                        <div class="product-actions">
                                            <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                                <button class="action-btn" 
                                                        onclick="addToCart(${product.productID}, '${pageContext.request.contextPath}/shop')">
                                                    <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                                                </button>
                                            </c:if>
                                            <div class="action-icon" 
                                                 onclick="openProductModal(${product.productID})"
                                                 title="Xem chi tiết">
                                                <i class="bi bi-eye"></i>
                                            </div>
                                            <div class="action-icon" 
                                                 onclick="toggleWishlist(${product.productID}, '${pageContext.request.contextPath}/shop')"
                                                 title="Thêm vào yêu thích">
                                                <i class="bi bi-heart"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" style="grid-column: 1 / -1;">
                                <i class="bi bi-inbox"></i>
                                <p>Không tìm thấy sản phẩm nào</p>
                                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-3">
                                    <i class="bi bi-arrow-left"></i> Về trang chủ
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Filter Sidebar -->
                <div class="filter-sidebar">
                    <form method="get" action="${pageContext.request.contextPath}/shop" id="filterForm">
                        <input type="hidden" name="page" value="1">
                        <input type="hidden" name="sortBy" value="${sortBy}">
                        <input type="hidden" name="sortOrder" value="${sortOrder}">
                        <c:if test="${not empty searchKeyword}">
                            <input type="hidden" name="search" value="${searchKeyword}">
                        </c:if>
                        
                        <!-- Category Filter -->
                        <div class="filter-section">
                            <h4 class="filter-title">Loại sản phẩm</h4>
                            <div class="filter-option">
                                <input type="radio" 
                                       id="category_0" 
                                       name="category" 
                                       value="0"
                                       ${categoryID == 0 ? 'checked' : ''}>
                                <label for="category_0">Tất cả</label>
                            </div>
                            <c:forEach var="cat" items="${categories}">
                                <div class="filter-option">
                                    <input type="radio" 
                                           id="category_${cat.categoryID}" 
                                           name="category" 
                                           value="${cat.categoryID}"
                                           ${categoryID == cat.categoryID ? 'checked' : ''}>
                                    <label for="category_${cat.categoryID}">${cat.categoryName}</label>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Price Filter -->
                        <div class="filter-section">
                            <h4 class="filter-title">Giá</h4>
                            <div class="filter-option">
                                <input type="radio" 
                                       id="price_0" 
                                       name="priceRange" 
                                       value="0-1000000"
                                       ${minPrice == null && maxPrice == null ? 'checked' : ''}>
                                <label for="price_0">Tất cả</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" 
                                       id="price_1" 
                                       name="priceRange" 
                                       value="0-1000000"
                                       ${minPrice != null && minPrice == 0 && maxPrice != null && maxPrice == 1000000 ? 'checked' : ''}>
                                <label for="price_1">Giá dưới 1.000.000₫</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" 
                                       id="price_2" 
                                       name="priceRange" 
                                       value="1000000-5000000"
                                       ${minPrice != null && minPrice == 1000000 && maxPrice != null && maxPrice == 5000000 ? 'checked' : ''}>
                                <label for="price_2">1.000.000₫ - 5.000.000₫</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" 
                                       id="price_3" 
                                       name="priceRange" 
                                       value="5000000-10000000"
                                       ${minPrice != null && minPrice == 5000000 && maxPrice != null && maxPrice == 10000000 ? 'checked' : ''}>
                                <label for="price_3">5.000.000₫ - 10.000.000₫</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" 
                                       id="price_4" 
                                       name="priceRange" 
                                       value="10000000-999999999"
                                       ${minPrice != null && minPrice == 10000000 ? 'checked' : ''}>
                                <label for="price_4">Trên 10.000.000₫</label>
                            </div>
                        </div>
                        
                        <button type="submit" class="apply-filter-btn">
                            <i class="bi bi-funnel"></i> Áp dụng bộ lọc
                        </button>
                    </form>
                </div>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination-wrapper">
                    <nav aria-label="Page navigation">
                        <ul class="pagination">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <c:url var="prevPageUrl" value="/shop">
                                    <c:param name="page" value="${currentPage - 1}"/>
                                    <c:if test="${categoryID > 0}">
                                        <c:param name="category" value="${categoryID}"/>
                                    </c:if>
                                    <c:if test="${not empty searchKeyword}">
                                        <c:param name="search" value="${searchKeyword}"/>
                                    </c:if>
                                    <c:param name="sortBy" value="${sortBy}"/>
                                    <c:param name="sortOrder" value="${sortOrder}"/>
                                    <c:if test="${minPrice != null}">
                                        <c:param name="minPrice" value="${minPrice}"/>
                                    </c:if>
                                    <c:if test="${maxPrice != null}">
                                        <c:param name="maxPrice" value="${maxPrice}"/>
                                    </c:if>
                                </c:url>
                                <a class="page-link" href="${prevPageUrl}">
                                    <i class="bi bi-chevron-left"></i>
                                </a>
                            </li>
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                    <c:url var="pageUrl" value="/shop">
                                        <c:param name="page" value="${i}"/>
                                        <c:if test="${categoryID > 0}">
                                            <c:param name="category" value="${categoryID}"/>
                                        </c:if>
                                        <c:if test="${not empty searchKeyword}">
                                            <c:param name="search" value="${searchKeyword}"/>
                                        </c:if>
                                        <c:param name="sortBy" value="${sortBy}"/>
                                        <c:param name="sortOrder" value="${sortOrder}"/>
                                        <c:if test="${minPrice != null}">
                                            <c:param name="minPrice" value="${minPrice}"/>
                                        </c:if>
                                        <c:if test="${maxPrice != null}">
                                            <c:param name="maxPrice" value="${maxPrice}"/>
                                        </c:if>
                                    </c:url>
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="${pageUrl}">
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
                                <c:url var="nextPageUrl" value="/shop">
                                    <c:param name="page" value="${currentPage + 1}"/>
                                    <c:if test="${categoryID > 0}">
                                        <c:param name="category" value="${categoryID}"/>
                                    </c:if>
                                    <c:if test="${not empty searchKeyword}">
                                        <c:param name="search" value="${searchKeyword}"/>
                                    </c:if>
                                    <c:param name="sortBy" value="${sortBy}"/>
                                    <c:param name="sortOrder" value="${sortOrder}"/>
                                    <c:if test="${minPrice != null}">
                                        <c:param name="minPrice" value="${minPrice}"/>
                                    </c:if>
                                    <c:if test="${maxPrice != null}">
                                        <c:param name="maxPrice" value="${maxPrice}"/>
                                    </c:if>
                                </c:url>
                                <a class="page-link" href="${nextPageUrl}">
                                    <i class="bi bi-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </div>

    <!-- Product Detail Modal - Include BEFORE footer to ensure modal HTML is available -->
    <jsp:include page="/views/common/productModal.jsp" />
    
    <jsp:include page="/views/common/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Functions are already defined in head, but ensure they're still available
        // Override with full implementations if available from productModal
        if (typeof window.openProductModalFull === 'function') {
            window.openProductModal = window.openProductModalFull;
        }
        
        // Handle filter form submission
        document.addEventListener('DOMContentLoaded', function() {
            const filterForm = document.getElementById('filterForm');
            if (filterForm) {
                filterForm.addEventListener('submit', function(e) {
                    // Handle price range
                    const priceRange = document.querySelector('input[name="priceRange"]:checked');
                    if (priceRange) {
                        if (priceRange.value === '0-1000000' && priceRange.id === 'price_0') {
                            // Tất cả - không thêm min/max price
                        } else {
                            const [min, max] = priceRange.value.split('-');
                            const minInput = document.createElement('input');
                            minInput.type = 'hidden';
                            minInput.name = 'minPrice';
                            minInput.value = min;
                            this.appendChild(minInput);
                            
                            const maxInput = document.createElement('input');
                            maxInput.type = 'hidden';
                            maxInput.name = 'maxPrice';
                            maxInput.value = max;
                            this.appendChild(maxInput);
                        }
                    }
                });
            }
            
            // Ensure openProductModal is available after productModal loads
            setTimeout(function() {
                if (typeof window.openProductModalFull === 'function') {
                    window.openProductModal = window.openProductModalFull;
                    console.log('openProductModal overridden with full implementation');
                }
            }, 500);
        });
    </script>
</body>
</html>

