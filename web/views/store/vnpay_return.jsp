<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kết quả thanh toán - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }
        
        .result-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 3rem;
            max-width: 600px;
            margin: 3rem auto;
            text-align: center;
        }
        
        .success-icon {
            color: #28a745;
            font-size: 4rem;
            margin-bottom: 1rem;
        }
        
        .error-icon {
            color: #dc3545;
            font-size: 4rem;
            margin-bottom: 1rem;
        }
        
        .result-title {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 1rem;
        }
        
        .result-info {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1.5rem;
            margin: 1.5rem 0;
            text-align: left;
        }
        
        .info-row {
            display: flex;
            justify-content: space-between;
            padding: 0.5rem 0;
            border-bottom: 1px solid #dee2e6;
        }
        
        .info-row:last-child {
            border-bottom: none;
        }
        
        .info-label {
            font-weight: 600;
            color: #6c757d;
        }
        
        .info-value {
            color: #212529;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="result-card">
            <c:choose>
                <c:when test="${success == true}">
                    <i class="bi bi-check-circle-fill success-icon"></i>
                    <h2 class="result-title text-success">Thanh toán thành công!</h2>
                    <p class="text-muted">Đơn hàng của bạn đã được thanh toán thành công.</p>
                    
                    <div class="result-info">
                        <div class="info-row">
                            <span class="info-label">Mã đơn hàng:</span>
                            <span class="info-value">#${orderID}</span>
                        </div>
                        <c:if test="${not empty amount}">
                            <div class="info-row">
                                <span class="info-label">Số tiền:</span>
                                <span class="info-value">
                                    <fmt:formatNumber value="${amount}" type="currency" 
                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                </span>
                            </div>
                        </c:if>
                        <c:if test="${not empty transactionNo}">
                            <div class="info-row">
                                <span class="info-label">Mã giao dịch:</span>
                                <span class="info-value">${transactionNo}</span>
                            </div>
                        </c:if>
                        <c:if test="${not empty bankCode}">
                            <div class="info-row">
                                <span class="info-label">Ngân hàng:</span>
                                <span class="info-value">${bankCode}</span>
                            </div>
                        </c:if>
                        <c:if test="${not empty payDate}">
                            <div class="info-row">
                                <span class="info-label">Thời gian:</span>
                                <span class="info-value">${payDate}</span>
                            </div>
                        </c:if>
                    </div>
                    
                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/customer/orders?orderID=${orderID}" 
                           class="btn btn-primary me-2">
                            <i class="bi bi-receipt"></i> Xem đơn hàng
                        </a>
                        <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-secondary">
                            <i class="bi bi-house"></i> Về trang chủ
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <i class="bi bi-x-circle-fill error-icon"></i>
                    <h2 class="result-title text-danger">Thanh toán thất bại!</h2>
                    <p class="text-muted">
                        <c:choose>
                            <c:when test="${not empty errorMessage}">
                                ${errorMessage}
                            </c:when>
                            <c:otherwise>
                                Đã xảy ra lỗi trong quá trình thanh toán. Vui lòng thử lại.
                            </c:otherwise>
                        </c:choose>
                    </p>
                    
                    <c:if test="${not empty orderID}">
                        <div class="result-info">
                            <div class="info-row">
                                <span class="info-label">Mã đơn hàng:</span>
                                <span class="info-value">#${orderID}</span>
                            </div>
                        </div>
                    </c:if>
                    
                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary me-2">
                            <i class="bi bi-arrow-left"></i> Thử lại thanh toán
                        </a>
                        <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary me-2">
                            <i class="bi bi-cart"></i> Về giỏ hàng
                        </a>
                        <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-secondary">
                            <i class="bi bi-house"></i> Về trang chủ
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <c:if test="${success == true}">
        <script>
            // Xóa giỏ hàng sau khi thanh toán thành công
            // (Nếu chưa xóa trong callback servlet)
            if (sessionStorage.getItem('clearCart') !== 'true') {
                // Giỏ hàng sẽ được xóa trong callback servlet
                sessionStorage.setItem('clearCart', 'true');
            }
        </script>
    </c:if>
</body>
</html>

