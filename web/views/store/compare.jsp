<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>So sánh sản phẩm - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
            padding-top: 20px;
            padding-bottom: 50px;
        }
        
        .compare-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .compare-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .compare-table {
            background: #2c2c2c;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        
        .compare-table table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .compare-table th {
            background: #3a3a3a;
            color: #fff;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            border-bottom: 2px solid #555;
        }
        
        .compare-table td {
            padding: 15px;
            border-bottom: 1px solid #444;
            vertical-align: top;
        }
        
        .compare-table tr:hover {
            background: #333;
        }
        
        .product-image-compare {
            width: 150px;
            height: 150px;
            object-fit: cover;
            border-radius: 8px;
            margin-bottom: 10px;
        }
        
        .product-name-compare {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 10px;
            color: #fff;
        }
        
        .product-name-compare a {
            color: #fff;
            text-decoration: none;
        }
        
        .product-name-compare a:hover {
            color: #8b5cf6;
        }
        
        .product-price-compare {
            font-size: 1.3rem;
            font-weight: bold;
            color: #dc3545;
            margin-bottom: 5px;
        }
        
        .product-old-price-compare {
            font-size: 0.9rem;
            text-decoration: line-through;
            color: #6a6a6a;
        }
        
        .remove-btn {
            background: #dc3545;
            border: none;
            color: #fff;
            padding: 8px 15px;
            border-radius: 5px;
            cursor: pointer;
            transition: background 0.3s;
        }
        
        .remove-btn:hover {
            background: #c82333;
        }
        
        .empty-compare {
            text-align: center;
            padding: 60px 20px;
            background: #2c2c2c;
            border-radius: 10px;
        }
        
        .empty-compare i {
            font-size: 5rem;
            color: #6a6a6a;
            margin-bottom: 20px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 12px 30px;
            border-radius: 8px;
            font-weight: 600;
        }
        
        .btn-primary:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
        }
        
        .clear-all-btn {
            background: #dc3545;
            border: none;
            color: #fff;
            padding: 10px 20px;
            border-radius: 8px;
            margin-left: 10px;
        }
        
        .clear-all-btn:hover {
            background: #c82333;
        }
        
        .attribute-label {
            font-weight: 600;
            color: #b0b0b0;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <div class="compare-container">
        <div class="compare-header">
            <h1><i class="bi bi-arrow-left-right"></i> So sánh sản phẩm</h1>
            <p class="mb-0">So sánh các sản phẩm yêu thích của bạn</p>
        </div>
        
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% session.removeAttribute("successMessage"); %>
        </c:if>
        
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% session.removeAttribute("errorMessage"); %>
        </c:if>
        
        <c:choose>
            <c:when test="${empty compareProducts}">
                <div class="empty-compare">
                    <i class="bi bi-inbox"></i>
                    <h3>Danh sách so sánh trống</h3>
                    <p class="text-muted">Bạn chưa có sản phẩm nào trong danh sách so sánh</p>
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-3">
                        <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <span class="text-muted">Đang so sánh <strong>${compareCount}</strong> sản phẩm</span>
                    </div>
                    <div>
                        <form method="post" action="${pageContext.request.contextPath}/compare" style="display: inline;">
                            <input type="hidden" name="action" value="clear">
                            <button type="submit" class="clear-all-btn" onclick="return confirm('Bạn có chắc muốn xóa toàn bộ danh sách so sánh?')">
                                <i class="bi bi-trash"></i> Xóa tất cả
                            </button>
                        </form>
                    </div>
                </div>
                
                <div class="compare-table">
                    <table>
                        <thead>
                            <tr>
                                <th>Thuộc tính</th>
                                <c:forEach var="product" items="${compareProducts}">
                                    <th style="text-align: center; position: relative;">
                                        <form method="post" action="${pageContext.request.contextPath}/compare" style="position: absolute; top: 5px; right: 5px;">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="productID" value="${product.productID}">
                                            <button type="submit" class="remove-btn" title="Xóa khỏi danh sách so sánh">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </form>
                                        <img src="${product.imageUrl != null ? product.imageUrl : '/img/default-product.png'}" 
                                             alt="${product.productName}" 
                                             class="product-image-compare">
                                        <div class="product-name-compare">
                                            <a href="javascript:void(0)" onclick="openProductModal(${product.productID})">
                                                ${product.productName}
                                            </a>
                                        </div>
                                        <div class="product-price-compare">
                                            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                                        </div>
                                    </th>
                                </c:forEach>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td class="attribute-label">Tên sản phẩm</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>${product.productName}</td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td class="attribute-label">Giá</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>
                                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                                    </td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td class="attribute-label">Mô tả</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>${product.description != null ? product.description : 'N/A'}</td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td class="attribute-label">Màu sắc</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>${product.color != null ? product.color : 'N/A'}</td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td class="attribute-label">Kích thước</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>${product.size != null ? product.size : 'N/A'}</td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td class="attribute-label">Tồn kho</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>
                                        <span class="badge ${product.stock > 0 ? 'bg-success' : 'bg-danger'}">
                                            ${product.stock} sản phẩm
                                        </span>
                                    </td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td class="attribute-label">Trạng thái</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>
                                        <span class="badge ${product.stockStatus == 'InStock' ? 'bg-success' : 'bg-danger'}">
                                            ${product.stockStatus}
                                        </span>
                                    </td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td class="attribute-label">Hành động</td>
                                <c:forEach var="product" items="${compareProducts}">
                                    <td>
                                        <a href="${pageContext.request.contextPath}/cart?action=add&productID=${product.productID}" 
                                           class="btn btn-primary btn-sm">
                                            <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                                        </a>
                                    </td>
                                </c:forEach>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <jsp:include page="/views/common/footer.jsp" />
    <jsp:include page="/views/common/productModal.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function openProductModal(productID) {
            // Load product detail via AJAX and show modal
            fetch('${pageContext.request.contextPath}/product/api?id=' + productID)
                .then(response => response.json())
                .then(data => {
                    // Populate modal with product data
                    document.getElementById('productModalLabel').textContent = data.productName;
                    document.getElementById('productModalImage').src = data.imageUrl || '/img/default-product.png';
                    document.getElementById('productModalPrice').textContent = new Intl.NumberFormat('vi-VN', {
                        style: 'currency',
                        currency: 'VND'
                    }).format(data.price);
                    document.getElementById('productModalDescription').textContent = data.description || 'Không có mô tả';
                    
                    // Show modal
                    const modal = new bootstrap.Modal(document.getElementById('productModal'));
                    modal.show();
                })
                .catch(error => {
                    console.error('Error loading product:', error);
                    alert('Không thể tải thông tin sản phẩm');
                });
        }
    </script>
</body>
</html>

