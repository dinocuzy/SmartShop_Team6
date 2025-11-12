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
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .cart-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
            margin-bottom: 2rem;
        }
        
        /* Promotional Banner */
        .promo-banner {
            background: #2c2c2c;
            border: 2px solid #ff6b35;
            border-radius: 10px;
            padding: 1rem 1.5rem;
            margin-bottom: 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .promo-text {
            color: white;
            font-size: 1rem;
        }
        
        .promo-progress {
            flex: 1;
            height: 8px;
            background: #4a4a4a;
            border-radius: 10px;
            margin: 0 1rem;
            position: relative;
            overflow: hidden;
        }
        
        .promo-progress-bar {
            height: 100%;
            background: #ff6b35;
            border-radius: 10px;
            transition: width 0.3s;
        }
        
        .promo-icon {
            color: #b0b0b0;
            font-size: 1.2rem;
        }
        
        /* Cart Table */
        .cart-table-container {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .cart-table {
            width: 100%;
            color: white;
        }
        
        .cart-table thead th {
            border-bottom: 2px solid #4a4a4a;
            padding: 1rem 0;
            font-weight: 600;
            color: #b0b0b0;
            text-align: left;
        }
        
        .cart-table tbody td {
            padding: 1.5rem 0;
            border-bottom: 1px solid #4a4a4a;
            vertical-align: middle;
        }
        
        .cart-table tbody tr:last-child td {
            border-bottom: none;
        }
        
        .product-info-cart {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .product-image-cart {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 8px;
            background: white;
        }
        
        .product-name-cart {
            font-weight: 600;
            color: white;
            margin-bottom: 0.25rem;
        }
        
        .product-code {
            font-size: 0.85rem;
            color: #b0b0b0;
        }
        
        .price-cart {
            color: #dc3545;
            font-weight: bold;
            font-size: 1.1rem;
        }
        
        .quantity-control {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .quantity-btn {
            width: 35px;
            height: 35px;
            border: 1px solid #4a4a4a;
            background: #2c2c2c;
            color: white;
            border-radius: 5px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .quantity-btn:hover {
            background: #4a4a4a;
            border-color: #6a6a6a;
        }
        
        .quantity-input-cart {
            width: 60px;
            height: 35px;
            text-align: center;
            border: 1px solid #4a4a4a;
            background: #2c2c2c;
            color: white;
            border-radius: 5px;
        }
        
        .quantity-input-cart:focus {
            outline: none;
            border-color: #8b5cf6;
        }
        
        .remove-btn {
            background: transparent;
            border: none;
            color: #dc3545;
            font-size: 1.5rem;
            cursor: pointer;
            transition: transform 0.3s;
        }
        
        .remove-btn:hover {
            transform: scale(1.2);
            color: #c82333;
        }
        
        /* Summary Panel */
        .summary-panel {
            background: #2c2c2c;
            border: 2px solid #dc3545;
            border-radius: 15px;
            padding: 2rem;
            position: sticky;
            top: 20px;
        }
        
        .summary-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem 0;
            border-bottom: 1px solid #4a4a4a;
        }
        
        .summary-item:last-child {
            border-bottom: none;
        }
        
        .summary-label {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            color: white;
            font-weight: 500;
        }
        
        .summary-label i {
            color: #8b5cf6;
        }
        
        .summary-change-link {
            color: #8b5cf6;
            text-decoration: none;
            font-size: 0.9rem;
        }
        
        .summary-change-link:hover {
            color: #a78bfa;
            text-decoration: none;
        }
        
        .total-section {
            margin-top: 2rem;
            padding-top: 2rem;
            border-top: 2px solid #4a4a4a;
        }
        
        .total-label {
            font-size: 1.5rem;
            font-weight: bold;
            color: white;
            margin-bottom: 0.5rem;
        }
        
        .total-amount {
            font-size: 2rem;
            font-weight: bold;
            color: #dc3545;
        }
        
        .discount-note {
            font-size: 0.85rem;
            color: #b0b0b0;
            margin-top: 1rem;
            text-align: center;
        }
        
        .checkout-btn {
            background: #dc3545;
            color: white;
            border: none;
            border-radius: 25px;
            padding: 1rem 2rem;
            font-size: 1.2rem;
            font-weight: bold;
            width: 100%;
            margin-top: 1.5rem;
            transition: background 0.3s;
        }
        
        .checkout-btn:hover {
            background: #c82333;
            color: white;
        }
        
        .empty-cart {
            text-align: center;
            padding: 4rem 2rem;
            color: #b0b0b0;
        }
        
        .empty-cart-icon {
            font-size: 5rem;
            color: #4a4a4a;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp">
        <jsp:param name="active" value="cart" />
    </jsp:include>
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Giỏ hàng" />
    </jsp:include>

    <!-- Main Content -->
    <div class="container my-5">
        <h1 class="cart-title">Giỏ hàng</h1>
        
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
                <!-- Promotional Banner -->
                <c:if test="${not empty sessionScope.cart && !sessionScope.cart.isEmpty()}">
                    <c:set var="cartTotal" value="${sessionScope.cart.total}" />
                    <c:set var="targetAmount" value="5000000" />
                    <c:set var="remainingAmount" value="${targetAmount - cartTotal.doubleValue()}" />
                    <c:if test="${remainingAmount > 0}">
                        <div class="promo-banner">
                            <div class="promo-text">
                                Bạn cần mua thêm 
                                <strong style="color: #ff6b35;">
                                    <fmt:formatNumber value="${remainingAmount}" type="currency" 
                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                </strong> 
                                để được giảm 50k
                            </div>
                            <div class="promo-progress">
                                <div class="promo-progress-bar" 
                                     style="width: ${(cartTotal.doubleValue() / targetAmount) * 100}%"></div>
                            </div>
                            <i class="bi bi-trash promo-icon"></i>
                        </div>
                    </c:if>
                </c:if>
                
                <!-- Cart Table -->
                <div class="cart-table-container">
                    <c:choose>
                        <c:when test="${not empty sessionScope.cart && !sessionScope.cart.isEmpty()}">
                            <table class="cart-table">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>Đơn giá</th>
                                        <th>Số lượng</th>
                                        <th>Tạm tính</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${sessionScope.cart.items}">
                                        <tr>
                                            <td>
                                                <div class="product-info-cart">
                                                    <c:choose>
                                                        <c:when test="${not empty item.imageUrl}">
                                                            <img src="${item.imageUrl}" class="product-image-cart" alt="${item.productName}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="product-image-cart d-flex align-items-center justify-content-center">
                                                                <i class="bi bi-image" style="font-size: 2rem; color: #4a4a4a;"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <div>
                                                        <div class="product-name-cart">${item.productName}</div>
                                                        <div class="product-code">Mã: #${item.productID}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="price-cart">
                                                    <fmt:formatNumber value="${item.price}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="quantity-control">
                                                    <button class="quantity-btn" 
                                                            onclick="updateQuantity(${item.productID}, ${item.quantity - 1}, ${item.stock})">-</button>
                                                    <input type="number" class="quantity-input-cart" 
                                                           value="${item.quantity}" 
                                                           min="1" max="${item.stock}"
                                                           id="quantity-${item.productID}"
                                                           readonly>
                                                    <button class="quantity-btn" 
                                                            onclick="updateQuantity(${item.productID}, ${item.quantity + 1}, ${item.stock})">+</button>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="price-cart">
                                                    <fmt:formatNumber value="${item.subtotal}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </div>
                                            </td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/cart" class="d-inline">
                                                    <input type="hidden" name="action" value="remove">
                                                    <input type="hidden" name="productID" value="${item.productID}">
                                                    <button type="submit" class="remove-btn" 
                                                            onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?')">
                                                        <i class="bi bi-x-lg"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-cart">
                                <i class="bi bi-cart-x empty-cart-icon"></i>
                                <h3>Giỏ hàng của bạn đang trống</h3>
                                <p class="mt-3">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
                                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-3">
                                    <i class="bi bi-shop"></i> Tiếp tục mua sắm
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <!-- Summary Panel -->
            <div class="col-lg-4">
                <div class="summary-panel">
                    <!-- Tổng cộng -->
                    <div class="total-section">
                        <div class="total-label">TỔNG CỘNG</div>
                        <div class="total-amount">
                            <c:choose>
                                <c:when test="${not empty sessionScope.cart && !sessionScope.cart.isEmpty()}">
                                    <fmt:formatNumber value="${sessionScope.cart.total}" type="currency" 
                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                </c:when>
                                <c:otherwise>
                                    0₫
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="discount-note">
                            Nhập mã giảm giá ở trang thanh toán
                        </div>
                    </div>
                    
                    <!-- Checkout Button -->
                    <c:if test="${not empty sessionScope.cart && !sessionScope.cart.isEmpty()}">
                        <button class="checkout-btn" onclick="window.location.href='${pageContext.request.contextPath}/checkout'">
                            THANH TOÁN <i class="bi bi-arrow-right"></i>
                        </button>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />

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
    </script>
</body>
</html>
