<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 4rem 0;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="bi bi-shop"></i> SmartShop
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/login">
                    <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                </a>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container text-center">
            <h1 class="display-4 mb-4">Chào mừng đến với SmartShop</h1>
            <p class="lead mb-4">Cửa hàng trực tuyến thông minh của bạn</p>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-light btn-lg">
                <i class="bi bi-box-arrow-in-right"></i> Đăng nhập để tiếp tục
            </a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="container my-5">
        <c:if test="${param.logout == 'success'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> Đăng xuất thành công!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <div class="row">
            <div class="col-md-12 text-center">
                <h2>Vui lòng đăng nhập để sử dụng hệ thống</h2>
                <p class="text-muted">Nếu bạn chưa có tài khoản, vui lòng liên hệ quản trị viên.</p>
                <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-success btn-lg mt-3 me-2">
                        <i class="bi bi-shop"></i> Xem cửa hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg mt-3">
                        <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

