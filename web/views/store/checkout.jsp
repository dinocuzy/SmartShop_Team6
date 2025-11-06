<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán - SmartShop</title>
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
        
        .checkout-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .order-summary-card {
            background: #f8f9fa;
            border-radius: 15px;
            padding: 2rem;
            position: sticky;
            top: 20px;
        }
        
        .cart-item-summary {
            border-bottom: 1px solid #eee;
            padding: 1rem 0;
        }
        
        .cart-item-summary:last-child {
            border-bottom: none;
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
        <h2 class="mb-4"><i class="bi bi-credit-card"></i> Thanh toán</h2>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <form method="post" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">
            <input type="hidden" name="action" value="placeOrder">
            
            <div class="row">
                <div class="col-lg-8">
                    <!-- Shipping Address -->
                    <div class="checkout-card">
                        <h5 class="mb-3"><i class="bi bi-geo-alt"></i> Địa chỉ giao hàng</h5>
                        <c:choose>
                            <c:when test="${not empty addresses}">
                                <div class="mb-3">
                                    <label class="form-label">Chọn địa chỉ giao hàng</label>
                                    <select class="form-select" name="shippingAddressID" id="shippingAddressID" required>
                                        <option value="">-- Chọn địa chỉ --</option>
                                        <c:forEach var="address" items="${addresses}">
                                            <option value="${address.addressID}" ${address.default ? 'selected' : ''}>
                                                ${address.fullName} - ${address.phone} - ${address.fullAddress}
                                                <c:if test="${address.default}"> (Mặc định)</c:if>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="text-end">
                                    <a href="${pageContext.request.contextPath}/customer/profile" class="btn btn-sm btn-outline-primary">
                                        <i class="bi bi-plus"></i> Thêm địa chỉ mới
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-warning">
                                    <i class="bi bi-exclamation-triangle"></i> Bạn chưa có địa chỉ. 
                                    <a href="${pageContext.request.contextPath}/customer/profile">Thêm địa chỉ ngay</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <!-- Billing Address -->
                    <div class="checkout-card">
                        <h5 class="mb-3"><i class="bi bi-receipt"></i> Địa chỉ thanh toán</h5>
                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" id="sameAsShipping" checked>
                            <label class="form-check-label" for="sameAsShipping">
                                Giống địa chỉ giao hàng
                            </label>
                        </div>
                        <select class="form-select" name="billingAddressID" id="billingAddressID">
                            <option value="">-- Chọn địa chỉ --</option>
                            <c:forEach var="address" items="${addresses}">
                                <option value="${address.addressID}">
                                    ${address.fullName} - ${address.phone} - ${address.fullAddress}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <!-- Payment Method -->
                    <div class="checkout-card">
                        <h5 class="mb-3"><i class="bi bi-wallet2"></i> Phương thức thanh toán</h5>
                        <c:choose>
                            <c:when test="${not empty paymentMethods}">
                                <div class="row">
                                    <c:forEach var="method" items="${paymentMethods}">
                                        <div class="col-md-6 mb-3">
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="paymentMethodID" 
                                                       id="paymentMethod${method.paymentMethodID}" 
                                                       value="${method.paymentMethodID}" required>
                                                <label class="form-check-label" for="paymentMethod${method.paymentMethodID}">
                                                    <strong>${method.methodName}</strong>
                                                    <c:if test="${not empty method.provider}">
                                                        <br><small class="text-muted">${method.provider}</small>
                                                    </c:if>
                                                </label>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-warning">
                                    <i class="bi bi-exclamation-triangle"></i> Không có phương thức thanh toán nào
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <!-- Note -->
                    <div class="checkout-card">
                        <h5 class="mb-3"><i class="bi bi-chat-left-text"></i> Ghi chú đơn hàng</h5>
                        <textarea class="form-control" name="note" rows="3" 
                                  placeholder="Ghi chú cho đơn hàng (tùy chọn)"></textarea>
                    </div>
                </div>
                
                <!-- Order Summary -->
                <div class="col-lg-4">
                    <div class="order-summary-card">
                        <h5 class="mb-3"><i class="bi bi-bag-check"></i> Tóm tắt đơn hàng</h5>
                        <hr>
                        
                        <div class="mb-3">
                            <c:forEach var="item" items="${cart.items}">
                                <div class="cart-item-summary">
                                    <div class="d-flex justify-content-between">
                                        <div>
                                            <strong>${item.productName}</strong>
                                            <br>
                                            <small class="text-muted">x${item.quantity}</small>
                                        </div>
                                        <div class="text-end">
                                            <strong>
                                                <fmt:formatNumber value="${item.subtotal}" type="currency" 
                                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                            </strong>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <hr>
                        
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tạm tính:</span>
                            <strong>
                                <fmt:formatNumber value="${cart.total}" type="currency" 
                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                            </strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Phí vận chuyển:</span>
                            <strong>Miễn phí</strong>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between mb-3">
                            <span><strong>Tổng cộng:</strong></span>
                            <strong class="text-danger" style="font-size: 1.5rem;">
                                <fmt:formatNumber value="${cart.total}" type="currency" 
                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                            </strong>
                        </div>
                        
                        <button type="submit" class="btn btn-primary w-100 btn-lg">
                            <i class="bi bi-check-circle"></i> Đặt hàng
                        </button>
                        
                        <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary w-100 mt-2">
                            <i class="bi bi-arrow-left"></i> Quay lại giỏ hàng
                        </a>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white text-center py-4 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 SmartShop. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Xử lý "Giống địa chỉ giao hàng"
        document.getElementById('sameAsShipping').addEventListener('change', function() {
            const billingSelect = document.getElementById('billingAddressID');
            if (this.checked) {
                billingSelect.value = document.getElementById('shippingAddressID').value;
                billingSelect.disabled = true;
            } else {
                billingSelect.disabled = false;
            }
        });
        
        // Đồng bộ billing address khi shipping address thay đổi
        document.getElementById('shippingAddressID').addEventListener('change', function() {
            if (document.getElementById('sameAsShipping').checked) {
                document.getElementById('billingAddressID').value = this.value;
            }
        });
    </script>
</body>
</html>

