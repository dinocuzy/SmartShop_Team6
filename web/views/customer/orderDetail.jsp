<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng #${order.orderID} - SmartShop</title>
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
        
        .order-detail-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .order-item-row {
            border-bottom: 1px solid #f0f0f0;
            padding: 1rem 0;
        }
        
        .order-item-row:last-child {
            border-bottom: none;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header-customer.jsp">
        <jsp:param name="active" value="orders" />
    </jsp:include>
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Chi tiết đơn hàng #${order.orderID}" />
    </jsp:include>
    
    <!-- Main Content -->
    <div class="container py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="bi bi-receipt"></i> Chi tiết đơn hàng #${order.orderID}</h2>
            <a href="${pageContext.request.contextPath}/customer/orders" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Quay lại
            </a>
        </div>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
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
        
        <!-- Order Info -->
        <div class="order-detail-card">
            <div class="row mb-4">
                <div class="col-md-6">
                    <h5 class="mb-3"><i class="bi bi-info-circle"></i> Thông tin đơn hàng</h5>
                    <table class="table table-borderless">
                        <tr>
                            <td><strong>Mã đơn hàng:</strong></td>
                            <td>#${order.orderID}</td>
                        </tr>
                        <tr>
                            <td><strong>Ngày đặt:</strong></td>
                            <td>
                                <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" />
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Trạng thái:</strong></td>
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
                        </tr>
                        <c:if test="${not empty order.note}">
                            <tr>
                                <td><strong>Ghi chú:</strong></td>
                                <td>${order.note}</td>
                            </tr>
                        </c:if>
                    </table>
                </div>
                <div class="col-md-6">
                    <h5 class="mb-3"><i class="bi bi-cash-coin"></i> Tổng thanh toán</h5>
                    <div class="bg-light p-3 rounded">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tạm tính:</span>
                            <strong>
                                <fmt:formatNumber value="${order.totalAmount}" type="currency" 
                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                            </strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Phí vận chuyển:</span>
                            <strong>Miễn phí</strong>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <span><strong>Tổng cộng:</strong></span>
                            <strong class="text-danger" style="font-size: 1.5rem;">
                                <fmt:formatNumber value="${order.totalAmount}" type="currency" 
                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                            </strong>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Order Items -->
        <div class="order-detail-card">
            <h5 class="mb-3"><i class="bi bi-list-ul"></i> Sản phẩm trong đơn hàng</h5>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Đơn giá</th>
                            <th>Số lượng</th>
                            <th class="text-end">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${orderItems}">
                            <tr class="order-item-row">
                                <td>
                                    <strong>${item.productName != null ? item.productName : 'Sản phẩm #' += item.productID}</strong>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${item.unitPrice}" type="currency" 
                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                </td>
                                <td>${item.quantity}</td>
                                <td class="text-end">
                                    <strong>
                                        <fmt:formatNumber value="${item.unitPrice * item.quantity}" type="currency" 
                                            currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                    </strong>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- Actions -->
        <div class="text-center">
            <a href="${pageContext.request.contextPath}/customer/orders" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Quay lại danh sách đơn hàng
            </a>
            <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">
                <i class="bi bi-shop"></i> Tiếp tục mua sắm
            </a>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

