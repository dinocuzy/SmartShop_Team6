<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Dashboard - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }
        
        .navbar-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .stat-card {
            border-radius: 10px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s;
            color: white;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
        }
        
        .stat-card.total {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .stat-card.pending {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }
        
        .stat-card.processing {
            background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
        }
        
        .stat-card.delivered {
            background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
            color: #333;
        }
        
        .product-card {
            border: none;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        }
        
        .order-table {
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-custom navbar-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/customer/dashboard">
                <i class="bi bi-shop"></i> SmartShop
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/customer/dashboard">
                            <i class="bi bi-house"></i> Trang chủ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/customer/orders">
                            <i class="bi bi-cart-check"></i> Đơn hàng của tôi
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/cart">
                            <i class="bi bi-cart"></i> Giỏ hàng
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/customer/profile">
                            <i class="bi bi-person"></i> Thông tin cá nhân
                        </a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle"></i> ${currentUser.fullName}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/customer/profile">
                                <i class="bi bi-person"></i> Thông tin cá nhân</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                <i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    
    <!-- Main Content -->
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0"><i class="bi bi-speedometer2"></i> Dashboard của tôi</h1>
            <div class="text-muted">
                <i class="bi bi-calendar"></i> <span id="currentDate"></span>
            </div>
        </div>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- Welcome Message -->
        <div class="alert alert-info">
            <h5 class="alert-heading"><i class="bi bi-person-circle"></i> Chào mừng, ${currentUser.fullName}!</h5>
            <p class="mb-0">Email: ${currentUser.email} | SĐT: ${currentUser.phone}</p>
        </div>
        
        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stat-card total">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-2">Tổng đơn hàng</h6>
                            <h2 class="mb-0">${totalOrders}</h2>
                        </div>
                        <i class="bi bi-cart-check" style="font-size: 3rem; opacity: 0.5;"></i>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="stat-card pending">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-2">Đơn chờ xử lý</h6>
                            <h2 class="mb-0">${pendingOrders}</h2>
                        </div>
                        <i class="bi bi-clock-history" style="font-size: 3rem; opacity: 0.5;"></i>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="stat-card processing">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-2">Đang xử lý</h6>
                            <h2 class="mb-0">${processingOrders}</h2>
                        </div>
                        <i class="bi bi-gear" style="font-size: 3rem; opacity: 0.5;"></i>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="stat-card delivered">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-2">Đã giao</h6>
                            <h2 class="mb-0">${deliveredOrders}</h2>
                        </div>
                        <i class="bi bi-check-circle" style="font-size: 3rem; opacity: 0.5;"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Recent Orders -->
        <div class="row">
            <div class="col-md-8">
                <div class="card order-table">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-list-ul"></i> Đơn hàng gần đây</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>Mã đơn</th>
                                        <th>Ngày đặt</th>
                                        <th>Tổng tiền</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty userOrders}">
                                            <c:forEach var="order" items="${userOrders}" begin="0" end="4">
                                                <tr>
                                                    <td><strong>#${order.orderID}</strong></td>
                                                    <td>
                                                        <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy" />
                                                    </td>
                                                    <td>
                                                        <strong class="text-success">
                                                            <fmt:formatNumber value="${order.totalAmount}" type="currency" 
                                                                currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                        </strong>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${order.orderStatus == 'Pending'}">
                                                                <span class="badge bg-warning text-dark">Chờ xử lý</span>
                                                            </c:when>
                                                            <c:when test="${order.orderStatus == 'Processing'}">
                                                                <span class="badge bg-info">Đang xử lý</span>
                                                            </c:when>
                                                            <c:when test="${order.orderStatus == 'Delivered'}">
                                                                <span class="badge bg-success">Đã giao</span>
                                                            </c:when>
                                                            <c:when test="${order.orderStatus == 'Cancelled'}">
                                                                <span class="badge bg-danger">Đã hủy</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">${order.orderStatus}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/customer/orders?orderID=${order.orderID}" 
                                                           class="btn btn-sm btn-outline-primary">
                                                            <i class="bi bi-eye"></i> Xem
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="5" class="text-center text-muted py-4">
                                                    <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                                    <p class="mt-2">Bạn chưa có đơn hàng nào</p>
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${totalOrders > 5}">
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/customer/orders" class="btn btn-primary">
                                    Xem tất cả đơn hàng
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
            
            <!-- Quick Links -->
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-lightning"></i> Thao tác nhanh</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary">
                                <i class="bi bi-cart"></i> Giỏ hàng
                            </a>
                            <a href="${pageContext.request.contextPath}/customer/profile" class="btn btn-outline-info">
                                <i class="bi bi-person"></i> Thông tin cá nhân
                            </a>
                            <a href="${pageContext.request.contextPath}/customer/orders" class="btn btn-outline-secondary">
                                <i class="bi bi-cart-check"></i> Đơn hàng của tôi
                            </a>
                            <a href="${pageContext.request.contextPath}/index" class="btn btn-outline-success">
                                <i class="bi bi-shop"></i> Mua sắm
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Hiển thị ngày hiện tại
        const now = new Date();
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('currentDate').textContent = now.toLocaleDateString('vi-VN', options);
    </script>
</body>
</html>

