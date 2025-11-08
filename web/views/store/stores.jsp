<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ thống cửa hàng - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #ffffff;
            min-height: 100vh;
        }
        
        .stores-container {
            padding: 3rem 0;
            background-color: #1a1a1a;
        }
        
        .stores-header {
            text-align: center;
            margin-bottom: 3rem;
        }
        
        .stores-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
        }
        
        .stores-subtitle {
            color: #b0b0b0;
            font-size: 1.1rem;
        }
        
        .store-card {
            background-color: #2c2c2c;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            border: 1px solid #444;
            transition: transform 0.3s, box-shadow 0.3s;
        }
        
        .store-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(220, 53, 69, 0.3);
        }
        
        .store-name {
            font-size: 1.5rem;
            font-weight: bold;
            color: #8b5cf6;
            margin-bottom: 1rem;
        }
        
        .store-info {
            color: #cccccc;
            margin-bottom: 0.75rem;
            display: flex;
            align-items: start;
            gap: 0.75rem;
        }
        
        .store-info i {
            color: #dc3545;
            margin-top: 0.25rem;
        }
        
        .store-hours {
            background-color: #3a3a3a;
            border-radius: 8px;
            padding: 1rem;
            margin-top: 1rem;
        }
        
        .store-hours-title {
            color: #8b5cf6;
            font-weight: bold;
            margin-bottom: 0.5rem;
        }
        
        .map-container {
            margin-top: 2rem;
            border-radius: 10px;
            overflow: hidden;
            height: 400px;
            background-color: #2c2c2c;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Hệ thống cửa hàng" />
    </jsp:include>

    <div class="stores-container">
        <div class="container">
            <div class="stores-header">
                <h1 class="stores-title"><i class="bi bi-shop"></i> Hệ thống cửa hàng SmartShop</h1>
                <p class="stores-subtitle">Tìm cửa hàng gần bạn nhất</p>
            </div>
            
            <div class="row justify-content-center">
                <!-- Cửa hàng Đà Nẵng -->
                <div class="col-lg-8">
                    <div class="store-card">
                        <h3 class="store-name"><i class="bi bi-geo-alt-fill"></i> SmartShop Đà Nẵng</h3>
                        <div class="store-info">
                            <i class="bi bi-geo-alt"></i>
                            <div>
                                <strong>Địa chỉ:</strong><br>
                                Đà Nẵng, Việt Nam
                            </div>
                        </div>
                        <div class="store-info">
                            <i class="bi bi-telephone"></i>
                            <div>
                                <strong>Hotline:</strong> 0833347220
                            </div>
                        </div>
                        <div class="store-info">
                            <i class="bi bi-envelope"></i>
                            <div>
                                <strong>Email:</strong> smartshop686868@gmail.com
                            </div>
                        </div>
                        <div class="store-hours">
                            <div class="store-hours-title">Giờ mở cửa:</div>
                            <div>Thứ 2 - Chủ nhật: 8:00 - 22:00</div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="row mt-4">
                <div class="col-12">
                    <div class="store-card">
                        <h3 class="store-name"><i class="bi bi-info-circle"></i> Thông tin chung</h3>
                        <div class="store-info">
                            <i class="bi bi-telephone"></i>
                            <div>
                                <strong>Hotline tổng đài:</strong> 0833347220 (8:00 - 22:00 hàng ngày)
                            </div>
                        </div>
                        <div class="store-info">
                            <i class="bi bi-envelope"></i>
                            <div>
                                <strong>Email hỗ trợ:</strong> smartshop686868@gmail.com
                            </div>
                        </div>
                        <div class="store-info">
                            <i class="bi bi-globe"></i>
                            <div>
                                <strong>Website:</strong> www.smartshop.com
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

