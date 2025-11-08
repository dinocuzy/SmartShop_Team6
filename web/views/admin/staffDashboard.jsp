<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Dashboard - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        :root {
            --sidebar-width: 250px;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }
        
        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            height: 100vh;
            width: var(--sidebar-width);
            background: linear-gradient(180deg, #1a1a1a 0%, #2c2c2c 100%);
            color: white;
            overflow-y: auto;
            z-index: 1000;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            border-right: 3px solid #3498db;
        }
        
        .sidebar-header {
            padding: 1.5rem;
            background: rgba(0,0,0,0.2);
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        
        .sidebar-header h4 {
            margin: 0;
            font-weight: 600;
            color: white;
        }
        
        .sidebar-menu {
            padding: 1rem 0;
        }
        
        .sidebar-menu .nav-link {
            color: rgba(255,255,255,0.8);
            padding: 0.75rem 1.5rem;
            transition: all 0.3s;
            border-left: 3px solid transparent;
        }
        
        .sidebar-menu .nav-link:hover,
        .sidebar-menu .nav-link.active {
            background: rgba(52, 152, 219, 0.2);
            color: white;
            border-left-color: #3498db;
        }
        
        .main-content {
            margin-left: var(--sidebar-width);
            padding: 2rem;
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
        
        .stat-card.order {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .stat-card.payment {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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
        
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-top: 2rem;
        }
        
        .quick-action-btn {
            padding: 1.5rem;
            border-radius: 10px;
            text-align: center;
            background: white;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: all 0.3s;
            text-decoration: none;
            color: #333;
        }
        
        .quick-action-btn:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
            color: #333;
        }
        
        .quick-action-btn i {
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
            color: #3498db;
        }
        
        .card {
            border: none;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px 10px 0 0 !important;
            padding: 1rem 1.5rem;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="sidebar-header">
            <h4><i class="bi bi-shop"></i> SmartShop</h4>
            <small class="text-white-50">Staff Panel</small>
        </div>
        <nav class="sidebar-menu">
            <a href="${pageContext.request.contextPath}/staff/dashboard" class="nav-link active">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/admin/orders" class="nav-link">
                <i class="bi bi-cart-check"></i> Đơn hàng
            </a>
            <a href="${pageContext.request.contextPath}/admin/orders?view=payments" class="nav-link">
                <i class="bi bi-wallet2"></i> Thanh toán
            </a>
            <a href="${pageContext.request.contextPath}/admin/notifications" class="nav-link">
                <i class="bi bi-bell"></i> Thông báo
            </a>
            <hr class="text-white-50">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">
                <i class="bi bi-house"></i> Về trang chủ
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="nav-link">
                <i class="bi bi-box-arrow-right"></i> Đăng xuất
            </a>
        </nav>
    </div>
    
    <!-- Main Content -->
    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0"><i class="bi bi-speedometer2"></i> Staff Dashboard</h1>
            <div class="text-muted">
                <i class="bi bi-calendar"></i> <span id="currentDate"></span>
            </div>
        </div>
        
        <c:if test="${not empty sessionScope.blockedMessage}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${sessionScope.blockedMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="blockedMessage" scope="session"/>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- Statistics Cards -->
        <div class="row">
            <div class="col-md-6">
                <div class="stat-card order">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-2">Tổng đơn hàng</h6>
                            <h2 class="mb-0">${totalOrders}</h2>
                        </div>
                        <i class="bi bi-cart-check" style="font-size: 3rem; opacity: 0.5;"></i>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="stat-card payment">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-2">Giao dịch thanh toán</h6>
                            <h2 class="mb-0">${totalPayments}</h2>
                        </div>
                        <i class="bi bi-wallet2" style="font-size: 3rem; opacity: 0.5;"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Order Status Cards -->
        <div class="row">
            <div class="col-md-4">
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
            
            <div class="col-md-4">
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
            
            <div class="col-md-4">
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
        
        <!-- Quick Actions -->
        <div class="card mt-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-lightning"></i> Thao tác nhanh</h5>
            </div>
            <div class="card-body">
                <div class="quick-actions">
                    <a href="${pageContext.request.contextPath}/admin/orders" class="quick-action-btn">
                        <i class="bi bi-list-check"></i>
                        <div><strong>Xem đơn hàng</strong></div>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=Pending" class="quick-action-btn">
                        <i class="bi bi-exclamation-circle"></i>
                        <div><strong>Đơn chờ xử lý</strong></div>
                        <small class="text-muted">${pendingOrders} đơn</small>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=Processing" class="quick-action-btn">
                        <i class="bi bi-gear"></i>
                        <div><strong>Đơn đang xử lý</strong></div>
                        <small class="text-muted">${processingOrders} đơn</small>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=Unpaid" class="quick-action-btn">
                        <i class="bi bi-wallet2"></i>
                        <div><strong>Đơn chưa thanh toán</strong></div>
                        <small class="text-muted">${unpaidOrders} đơn</small>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/notifications" class="quick-action-btn">
                        <i class="bi bi-bell"></i>
                        <div><strong>Thông báo</strong></div>
                    </a>
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

