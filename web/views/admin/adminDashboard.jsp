<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - SmartShop</title>
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
            background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
            color: white;
            overflow-y: auto;
            z-index: 1000;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
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
        
        .menu-item {
            display: block;
            padding: 0.75rem 1.5rem;
            color: rgba(255,255,255,0.8);
            text-decoration: none;
            transition: all 0.3s;
            border-left: 3px solid transparent;
        }
        
        .menu-item:hover {
            background: rgba(255,255,255,0.1);
            color: white;
            border-left-color: #3498db;
            padding-left: 1.75rem;
        }
        
        .menu-item.active {
            background: rgba(255,255,255,0.15);
            color: white;
            border-left-color: #3498db;
        }
        
        .menu-item i {
            width: 20px;
            margin-right: 10px;
        }
        
        .main-content {
            margin-left: var(--sidebar-width);
            padding: 2rem;
        }
        
        .stats-card {
            border: none;
            border-radius: 10px;
            transition: transform 0.3s, box-shadow 0.3s;
            height: 100%;
        }
        
        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        
        .stats-card .card-body {
            padding: 1.5rem;
        }
        
        .stats-icon {
            width: 60px;
            height: 60px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            color: white;
            margin-bottom: 1rem;
        }
        
        .stats-number {
            font-size: 2rem;
            font-weight: bold;
            margin: 0.5rem 0;
        }
        
        .stats-label {
            color: #6c757d;
            font-size: 0.9rem;
            margin: 0;
        }
        
        .quick-links {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .quick-link-item {
            display: flex;
            align-items: center;
            padding: 1rem;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            margin-bottom: 0.75rem;
            text-decoration: none;
            color: #495057;
            transition: all 0.3s;
        }
        
        .quick-link-item:hover {
            background: #f8f9fa;
            border-color: #3498db;
            color: #3498db;
            transform: translateX(5px);
        }
        
        .quick-link-item i {
            font-size: 1.5rem;
            margin-right: 1rem;
            width: 40px;
            text-align: center;
        }
        
        @media (max-width: 768px) {
            .sidebar {
                transform: translateX(-100%);
                transition: transform 0.3s;
            }
            
            .sidebar.show {
                transform: translateX(0);
            }
            
            .main-content {
                margin-left: 0;
            }
            
            .menu-toggle {
                display: block;
            }
        }
        
        .menu-toggle {
            display: none;
            position: fixed;
            top: 1rem;
            left: 1rem;
            z-index: 1001;
            background: #2c3e50;
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <nav class="sidebar">
        <div class="sidebar-header">
            <h4><i class="bi bi-shop"></i> SmartShop Admin</h4>
        </div>
        <div class="sidebar-menu">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="menu-item active">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/admin/products?action=list" class="menu-item">
                <i class="bi bi-box-seam"></i> Sản phẩm
            </a>
            <a href="${pageContext.request.contextPath}/admin/categories?action=list" class="menu-item">
                <i class="bi bi-tags"></i> Danh mục
            </a>
            <a href="${pageContext.request.contextPath}/admin/orders?action=list" class="menu-item">
                <i class="bi bi-cart-check"></i> Đơn hàng
            </a>
            <a href="${pageContext.request.contextPath}/admin/users?action=list" class="menu-item">
                <i class="bi bi-people"></i> Người dùng
            </a>
            <a href="${pageContext.request.contextPath}/admin/payment-methods?action=list" class="menu-item">
                <i class="bi bi-wallet2"></i> Phương thức TT
            </a>
            <a href="${pageContext.request.contextPath}/admin/promotions?action=list" class="menu-item">
                <i class="bi bi-gift"></i> Khuyến mãi
            </a>
            <a href="${pageContext.request.contextPath}/admin/notifications?action=list" class="menu-item">
                <i class="bi bi-bell"></i> Thông báo
            </a>
            <hr style="border-color: rgba(255,255,255,0.2); margin: 1rem 1.5rem;">
            <a href="${pageContext.request.contextPath}/logout" class="menu-item">
                <i class="bi bi-box-arrow-right"></i> Đăng xuất
            </a>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="main-content">
        <button class="menu-toggle" onclick="toggleSidebar()">
            <i class="bi bi-list"></i>
        </button>

        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0"><i class="bi bi-speedometer2"></i> Dashboard</h1>
            <div class="text-muted">
                <i class="bi bi-calendar"></i> <span id="currentDate"></span>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row g-4 mb-4">
            <!-- Products Card -->
            <div class="col-md-3 col-sm-6">
                <div class="card stats-card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="stats-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                            <i class="bi bi-box-seam"></i>
                        </div>
                        <h3 class="stats-number text-primary">${totalProducts}</h3>
                        <p class="stats-label">Tổng sản phẩm</p>
                        <a href="${pageContext.request.contextPath}/admin/products?action=list" class="btn btn-sm btn-outline-primary">
                            Xem tất cả <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <!-- Users Card -->
            <div class="col-md-3 col-sm-6">
                <div class="card stats-card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="stats-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                            <i class="bi bi-people"></i>
                        </div>
                        <h3 class="stats-number text-danger">${totalUsers}</h3>
                        <p class="stats-label">Tổng người dùng</p>
                        <a href="${pageContext.request.contextPath}/admin/users?action=list" class="btn btn-sm btn-outline-danger">
                            Xem tất cả <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <!-- Orders Card -->
            <div class="col-md-3 col-sm-6">
                <div class="card stats-card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="stats-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                            <i class="bi bi-cart-check"></i>
                        </div>
                        <h3 class="stats-number text-info">${totalOrders}</h3>
                        <p class="stats-label">Tổng đơn hàng</p>
                        <a href="${pageContext.request.contextPath}/admin/orders?action=list" class="btn btn-sm btn-outline-info">
                            Xem tất cả <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <!-- Categories Card -->
            <div class="col-md-3 col-sm-6">
                <div class="card stats-card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="stats-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                            <i class="bi bi-tags"></i>
                        </div>
                        <h3 class="stats-number text-success">${totalCategories}</h3>
                        <p class="stats-label">Danh mục</p>
                        <a href="${pageContext.request.contextPath}/admin/categories?action=list" class="btn btn-sm btn-outline-success">
                            Xem tất cả <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Order Status and Quick Links -->
        <div class="row g-4">
            <!-- Order Status -->
            <div class="col-md-6">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white border-0 pb-0">
                        <h5 class="mb-0"><i class="bi bi-clipboard-data"></i> Trạng thái Đơn hàng</h5>
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-6">
                                <div class="p-3 bg-warning bg-opacity-10 rounded">
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-clock-history text-warning fs-3 me-3"></i>
                                        <div>
                                            <h3 class="mb-0 text-warning">${pendingOrders}</h3>
                                            <small class="text-muted">Đang chờ</small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 bg-info bg-opacity-10 rounded">
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-gear text-info fs-3 me-3"></i>
                                        <div>
                                            <h3 class="mb-0 text-info">${processingOrders}</h3>
                                            <small class="text-muted">Đang xử lý</small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 bg-success bg-opacity-10 rounded">
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-check-circle text-success fs-3 me-3"></i>
                                        <div>
                                            <h3 class="mb-0 text-success">${deliveredOrders}</h3>
                                            <small class="text-muted">Đã giao</small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="p-3 bg-primary bg-opacity-10 rounded">
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-credit-card text-primary fs-3 me-3"></i>
                                        <div>
                                            <h3 class="mb-0 text-primary">${totalPayments}</h3>
                                            <small class="text-muted">Thanh toán</small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Links -->
            <div class="col-md-6">
                <div class="quick-links">
                    <h5 class="mb-4"><i class="bi bi-lightning-charge"></i> Truy cập nhanh</h5>
                    <a href="${pageContext.request.contextPath}/admin/products?action=list&autoOpenModal=add" class="quick-link-item">
                        <i class="bi bi-plus-circle-fill text-primary"></i>
                        <div>
                            <strong>Thêm sản phẩm mới</strong>
                            <small class="d-block text-muted">Tạo sản phẩm mới cho cửa hàng</small>
                        </div>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders?action=list&status=Pending" class="quick-link-item">
                        <i class="bi bi-clock-history text-warning"></i>
                        <div>
                            <strong>Đơn hàng đang chờ</strong>
                            <small class="d-block text-muted">Xem và xử lý đơn hàng chờ xử lý</small>
                        </div>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/users?action=list&autoOpenModal=add" class="quick-link-item">
                        <i class="bi bi-person-plus-fill text-success"></i>
                        <div>
                            <strong>Thêm người dùng</strong>
                            <small class="d-block text-muted">Tạo tài khoản người dùng mới</small>
                        </div>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders?action=list&autoOpenModal=add" class="quick-link-item">
                        <i class="bi bi-cart-plus-fill text-info"></i>
                        <div>
                            <strong>Thêm đơn hàng mới</strong>
                            <small class="d-block text-muted">Tạo đơn hàng mới</small>
                        </div>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/promotions?action=list" class="quick-link-item">
                        <i class="bi bi-gift-fill text-danger"></i>
                        <div>
                            <strong>Khuyến mãi</strong>
                            <small class="d-block text-muted">${activePromotions} khuyến mãi đang hoạt động</small>
                        </div>
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Set current date
        const now = new Date();
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('currentDate').textContent = now.toLocaleDateString('vi-VN', options);
        
        // Toggle sidebar on mobile
        function toggleSidebar() {
            document.querySelector('.sidebar').classList.toggle('show');
        }
        
        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function(event) {
            const sidebar = document.querySelector('.sidebar');
            const menuToggle = document.querySelector('.menu-toggle');
            
            if (window.innerWidth <= 768) {
                if (!sidebar.contains(event.target) && !menuToggle.contains(event.target)) {
                    sidebar.classList.remove('show');
                }
            }
        });
        
        // Update active menu item based on current page
        const currentPath = window.location.pathname;
        document.querySelectorAll('.menu-item').forEach(item => {
            if (item.getAttribute('href') && currentPath.includes(item.getAttribute('href').split('?')[0])) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });
    </script>
</body>
</html>

