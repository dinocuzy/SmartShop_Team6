<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tin tức - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #ffffff;
            min-height: 100vh;
        }
        
        .news-container {
            padding: 3rem 0;
            background-color: #1a1a1a;
        }
        
        .news-header {
            text-align: center;
            margin-bottom: 3rem;
        }
        
        .news-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
        }
        
        .news-subtitle {
            color: #b0b0b0;
            font-size: 1.1rem;
        }
        
        .news-card {
            background-color: #2c2c2c;
            border-radius: 15px;
            overflow: hidden;
            margin-bottom: 2rem;
            border: 1px solid #444;
            transition: transform 0.3s, box-shadow 0.3s;
            height: 100%;
        }
        
        .news-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(220, 53, 69, 0.3);
        }
        
        .news-image {
            width: 100%;
            height: 250px;
            object-fit: cover;
            background-color: #1a1a1a;
        }
        
        .news-content {
            padding: 1.5rem;
        }
        
        .news-date {
            color: #8b5cf6;
            font-size: 0.9rem;
            margin-bottom: 0.5rem;
        }
        
        .news-title-card {
            font-size: 1.3rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .news-excerpt {
            color: #cccccc;
            line-height: 1.8;
            margin-bottom: 1rem;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .news-read-more {
            color: #dc3545;
            text-decoration: none;
            font-weight: 600;
        }
        
        .news-read-more:hover {
            color: #c82333;
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Tin tức" />
    </jsp:include>

    <div class="news-container">
        <div class="container">
            <div class="news-header">
                <h1 class="news-title"><i class="bi bi-newspaper"></i> Tin tức & Sự kiện</h1>
                <p class="news-subtitle">Cập nhật những tin tức mới nhất về công nghệ và sản phẩm</p>
            </div>
            
            <div class="row">
                <!-- News Item 1 -->
                <div class="col-lg-4 col-md-6">
                    <div class="news-card">
                        <div class="news-image d-flex align-items-center justify-content-center" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                            <i class="bi bi-newspaper" style="font-size: 4rem; color: rgba(255,255,255,0.3);"></i>
                        </div>
                        <div class="news-content">
                            <div class="news-date"><i class="bi bi-calendar"></i> 15/01/2024</div>
                            <h3 class="news-title-card">Ra mắt sản phẩm mới: Gaming Gear 2024</h3>
                            <p class="news-excerpt">
                                SmartShop tự hào giới thiệu bộ sưu tập Gaming Gear mới nhất năm 2024 với nhiều tính năng đột phá và thiết kế hiện đại...
                            </p>
                            <span class="news-read-more">Đang cập nhật...</span>
                        </div>
                    </div>
                </div>
                
                <!-- News Item 2 -->
                <div class="col-lg-4 col-md-6">
                    <div class="news-card">
                        <div class="news-image d-flex align-items-center justify-content-center" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                            <i class="bi bi-gift" style="font-size: 4rem; color: rgba(255,255,255,0.3);"></i>
                        </div>
                        <div class="news-content">
                            <div class="news-date"><i class="bi bi-calendar"></i> 10/01/2024</div>
                            <h3 class="news-title-card">Khai trương cửa hàng - Giảm giá lên đến 30%</h3>
                            <p class="news-excerpt">
                                Chương trình khuyến mãi đặc biệt nhân dịp khai trương cửa hàng với nhiều ưu đãi hấp dẫn, giảm giá lên đến 30%...
                            </p>
                            <span class="news-read-more">Đang cập nhật...</span>
                        </div>
                    </div>
                </div>
                
                <!-- News Item 3 -->
                <div class="col-lg-4 col-md-6">
                    <div class="news-card">
                        <div class="news-image d-flex align-items-center justify-content-center" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                            <i class="bi bi-truck" style="font-size: 4rem; color: rgba(255,255,255,0.3);"></i>
                        </div>
                        <div class="news-content">
                            <div class="news-date"><i class="bi bi-calendar"></i> 05/01/2024</div>
                            <h3 class="news-title-card">Miễn phí vận chuyển toàn quốc</h3>
                            <p class="news-excerpt">
                                Từ ngày 01/01/2024, SmartShop áp dụng chính sách miễn phí vận chuyển cho tất cả đơn hàng từ 500.000₫ trở lên...
                            </p>
                            <span class="news-read-more">Đang cập nhật...</span>
                        </div>
                    </div>
                </div>
                
                <!-- News Item 4 -->
                <div class="col-lg-4 col-md-6">
                    <div class="news-card">
                        <div class="news-image d-flex align-items-center justify-content-center" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
                            <i class="bi bi-star" style="font-size: 4rem; color: rgba(255,255,255,0.3);"></i>
                        </div>
                        <div class="news-content">
                            <div class="news-date"><i class="bi bi-calendar"></i> 28/12/2023</div>
                            <h3 class="news-title-card">Đánh giá sản phẩm: Tai nghe Gaming mới</h3>
                            <p class="news-excerpt">
                                Cùng xem đánh giá chi tiết về dòng tai nghe gaming mới nhất với chất lượng âm thanh vượt trội và thiết kế ergonomic...
                            </p>
                            <span class="news-read-more">Đang cập nhật...</span>
                        </div>
                    </div>
                </div>
                
                <!-- News Item 5 -->
                <div class="col-lg-4 col-md-6">
                    <div class="news-card">
                        <div class="news-image d-flex align-items-center justify-content-center" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
                            <i class="bi bi-shield-check" style="font-size: 4rem; color: rgba(255,255,255,0.3);"></i>
                        </div>
                        <div class="news-content">
                            <div class="news-date"><i class="bi bi-calendar"></i> 20/12/2023</div>
                            <h3 class="news-title-card">Chính sách bảo hành mới 2024</h3>
                            <p class="news-excerpt">
                                SmartShop cập nhật chính sách bảo hành mới với nhiều ưu đãi hơn, thời gian bảo hành dài hơn cho khách hàng...
                            </p>
                            <span class="news-read-more">Đang cập nhật...</span>
                        </div>
                    </div>
                </div>
                
                <!-- News Item 6 -->
                <div class="col-lg-4 col-md-6">
                    <div class="news-card">
                        <div class="news-image d-flex align-items-center justify-content-center" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
                            <i class="bi bi-people" style="font-size: 4rem; color: rgba(255,255,255,0.3);"></i>
                        </div>
                        <div class="news-content">
                            <div class="news-date"><i class="bi bi-calendar"></i> 15/12/2023</div>
                            <h3 class="news-title-card">Khách hàng thứ 100.000 nhận quà đặc biệt</h3>
                            <p class="news-excerpt">
                                Chúc mừng khách hàng thứ 100.000 của SmartShop! Chúng tôi dành tặng nhiều phần quà giá trị và ưu đãi đặc biệt...
                            </p>
                            <span class="news-read-more">Đang cập nhật...</span>
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

