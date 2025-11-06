<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Sản phẩm - SmartShop</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .table-responsive {
            min-height: 400px;
        }
        .sortable {
            cursor: pointer;
            user-select: none;
        }
        .sortable:hover {
            background-color: #f8f9fa;
        }
        .sort-icon {
            margin-left: 5px;
            font-size: 0.8em;
        }
    </style>
</head>
<body>
    <div class="container-fluid py-4">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-box-seam"></i> Quản lý Sản phẩm</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#productModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm sản phẩm mới
                </button>
            </div>
        </div>

        <!-- Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Search and Filter Form -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/admin/products" class="row g-3">
                    <input type="hidden" name="action" value="list">
                    
                    <div class="col-md-4">
                        <label for="search" class="form-label">Tìm kiếm theo tên:</label>
                        <input type="text" class="form-control" id="search" name="search" 
                               value="${searchKeyword}" placeholder="Nhập tên sản phẩm...">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="categoryID" class="form-label">Lọc theo danh mục:</label>
                        <select class="form-select" id="categoryID" name="categoryID">
                            <option value="0" ${categoryID == 0 ? 'selected' : ''}>Tất cả danh mục</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryID}" ${categoryID == category.categoryID ? 'selected' : ''}>
                                    ${category.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="col-md-3">
                        <label for="sortBy" class="form-label">Sắp xếp theo:</label>
                        <select class="form-select" id="sortBy" name="sortBy">
                            <option value="ProductID" ${sortBy == 'ProductID' ? 'selected' : ''}>ID</option>
                            <option value="ProductName" ${sortBy == 'ProductName' ? 'selected' : ''}>Tên sản phẩm</option>
                            <option value="Price" ${sortBy == 'Price' ? 'selected' : ''}>Giá</option>
                            <option value="Stock" ${sortBy == 'Stock' ? 'selected' : ''}>Tồn kho</option>
                            <option value="CreatedAt" ${sortBy == 'CreatedAt' ? 'selected' : ''}>Ngày tạo</option>
                        </select>
                    </div>
                    
                    <div class="col-md-2">
                        <label for="sortOrder" class="form-label">Thứ tự:</label>
                        <select class="form-select" id="sortOrder" name="sortOrder">
                            <option value="ASC" ${empty sortOrder || sortOrder == 'ASC' ? 'selected' : ''}>Tăng dần</option>
                            <option value="DESC" ${sortOrder == 'DESC' ? 'selected' : ''}>Giảm dần</option>
                        </select>
                    </div>
                    
                    <div class="col-md-12">
                        <div class="form-check mb-2">
                            <input class="form-check-input" type="checkbox" id="showAll" name="showAll" value="true" ${showAll ? 'checked' : ''}>
                            <label class="form-check-label" for="showAll">
                                <i class="bi bi-eye"></i> Hiển thị tất cả (bao gồm sản phẩm hết hàng/ngừng bán)
                            </label>
                        </div>
                        <button type="submit" class="btn btn-outline-primary">
                            <i class="bi bi-search"></i> Tìm kiếm
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/products?action=list" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-clockwise"></i> Làm mới
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Products Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách sản phẩm 
                    <span class="badge bg-primary">${totalProducts}</span> sản phẩm
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Tên sản phẩm</th>
                                <th>Giá</th>
                                <th>Tồn kho</th>
                                <th>Màu sắc</th>
                                <th>Danh mục</th>
                                <th>Trạng thái</th>
                                <th>Đặc biệt</th>
                                <th>Đã áp dụng giảm giá</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty products}">
                                    <c:forEach var="product" items="${products}">
                                        <tr>
                                            <td>${product.productID}</td>
                                            <td>
                                                <strong>${product.productName}</strong>
                                                <c:if test="${not empty product.slug}">
                                                    <br><small class="text-muted">/${product.slug}</small>
                                                </c:if>
                                            </td>
                                            <td>
                                                <strong class="text-primary">
                                                    <fmt:formatNumber value="${product.price}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <span class="badge ${product.stock > 10 ? 'bg-success' : product.stock > 0 ? 'bg-warning' : 'bg-danger'}">
                                                    ${product.stock}
                                                </span>
                                            </td>
                                            <td>
                                                <c:if test="${not empty product.color}">
                                                    <span class="badge bg-info">${product.color}</span>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty product.categoryName}">
                                                        <span class="badge bg-secondary">${product.categoryName}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">ID: ${product.categoryID}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.stockStatus == 'InStock' || empty product.stockStatus}">
                                                        <span class="badge bg-success">Còn hàng</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">${product.stockStatus}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:if test="${product.special}">
                                                    <span class="badge bg-warning text-dark">
                                                        <i class="bi bi-star-fill"></i> Đặc biệt
                                                    </span>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:set var="promotionID" value="${productPromotionMap[product.productID]}" />
                                                <c:if test="${not empty promotionID}">
                                                    <c:forEach var="promo" items="${promotions}">
                                                        <c:if test="${promo.promotionID == promotionID}">
                                                            <span class="badge bg-success">
                                                                <i class="bi bi-tag-fill"></i> ${promo.title}
                                                                <c:if test="${promo.discountPercent != null && promo.discountPercent > 0}">
                                                                    - ${promo.discountPercent}%
                                                                </c:if>
                                                                <c:if test="${promo.discountAmount != null && promo.discountAmount > 0}">
                                                                    - <fmt:formatNumber value="${promo.discountAmount}" type="currency" currencyCode="VND" currencySymbol="₫"/>
                                                                </c:if>
                                                            </span>
                                                        </c:if>
                                                    </c:forEach>
                                                </c:if>
                                                <c:if test="${empty promotionID}">
                                                    <span class="text-muted">-</span>
                                                </c:if>
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/admin/products?action=edit&productID=${product.productID}" 
                                                   class="btn btn-sm btn-warning me-1"
                                                   title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <button type="button" class="btn btn-sm btn-warning me-1 d-none" 
                                                        onclick="openEditModalFromData(${product.productID})"
                                                        data-product-id="${product.productID}"
                                                        data-product-name="${fn:escapeXml(product.productName)}"
                                                        data-product-slug="${fn:escapeXml(product.slug)}"
                                                        data-product-description="${fn:escapeXml(product.description)}"
                                                        data-product-price="${product.price}"
                                                        data-product-stock="${product.stock}"
                                                        data-product-size="${fn:escapeXml(product.size)}"
                                                        data-product-color="${fn:escapeXml(product.color)}"
                                                        data-product-category="${product.categoryID}"
                                                        data-product-stockstatus="${fn:escapeXml(product.stockStatus)}"
                                                        data-product-special="${product.special}"
                                                        data-product-imageurl="${fn:escapeXml(product.imageUrl)}"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/products?action=delete&productID=${product.productID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');"
                                                   title="Xóa">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="10" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                            <p class="mt-2">Không có sản phẩm nào</p>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <!-- Previous -->
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" 
                                   href="${pageContext.request.contextPath}/admin/products?action=list&page=${currentPage - 1}&search=${searchKeyword}&categoryID=${categoryID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
                                    <i class="bi bi-chevron-left"></i>
                                </a>
                            </li>
                            
                            <!-- Page Numbers -->
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:choose>
                                    <c:when test="${i == currentPage}">
                                        <li class="page-item active">
                                            <span class="page-link">${i}</span>
                                        </li>
                                    </c:when>
                                    <c:when test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                        <li class="page-item">
                                            <a class="page-link" 
                                               href="${pageContext.request.contextPath}/admin/products?action=list&page=${i}&search=${searchKeyword}&categoryID=${categoryID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:when>
                                    <c:when test="${i == currentPage - 3 || i == currentPage + 3}">
                                        <li class="page-item disabled">
                                            <span class="page-link">...</span>
                                        </li>
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                            
                            <!-- Next -->
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" 
                                   href="${pageContext.request.contextPath}/admin/products?action=list&page=${currentPage + 1}&search=${searchKeyword}&categoryID=${categoryID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
                                    <i class="bi bi-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Product Modal (Add/Edit) -->
    <div class="modal fade" id="productModal" tabindex="-1" aria-labelledby="productModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/products" id="productForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="productID" id="modalProductID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="productModalLabel">
                            <i class="bi bi-box-seam"></i> <span id="modalTitle">Thêm sản phẩm mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-8 mb-3">
                                <label for="modalProductName" class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="modalProductName" name="productName" required>
                            </div>
                            
                            <div class="col-md-4 mb-3">
                                <label for="modalSlug" class="form-label">Slug (URL friendly)</label>
                                <input type="text" class="form-control" id="modalSlug" name="slug" 
                                       placeholder="san-pham-mau-1">
                                <small class="form-text text-muted">Để trống sẽ tự động tạo</small>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="modalDescription" name="description" rows="3"></textarea>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalPrice" class="form-label">Giá gốc <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <input type="number" class="form-control" id="modalPrice" name="price" 
                                           step="0.01" min="0" required onchange="calculatePriceFromPromotion()">
                                    <span class="input-group-text">₫</span>
                                </div>
                                <small class="form-text text-muted">Giá gốc của sản phẩm (trước khi giảm giá)</small>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalPromotionID" class="form-label">Khuyến mãi</label>
                                <select class="form-select" id="modalPromotionID" name="promotionID" onchange="calculatePriceFromPromotion()">
                                    <option value="">-- Không có khuyến mãi --</option>
                                    <c:forEach var="promotion" items="${promotions}">
                                        <option value="${promotion.promotionID}" 
                                                data-discount-percent="${promotion.discountPercent != null ? promotion.discountPercent : 0}"
                                                data-discount-amount="${promotion.discountAmount != null ? promotion.discountAmount : 0}">
                                            ${promotion.title}
                                            <c:if test="${promotion.discountPercent != null && promotion.discountPercent > 0}">
                                                - Giảm ${promotion.discountPercent}%
                                            </c:if>
                                            <c:if test="${promotion.discountAmount != null && promotion.discountAmount > 0}">
                                                - Giảm <fmt:formatNumber value="${promotion.discountAmount}" type="currency" currencyCode="VND" currencySymbol="₫"/>
                                            </c:if>
                                        </option>
                                    </c:forEach>
                                </select>
                                <small class="form-text text-muted">Chọn khuyến mãi, giá sau giảm sẽ được tính tự động</small>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-12 mb-3">
                                <label for="modalPriceAfterDiscount" class="form-label">Giá sau giảm (sẽ được lưu)</label>
                                <div class="input-group">
                                    <input type="number" class="form-control" id="modalPriceAfterDiscount" name="priceAfterDiscount" 
                                           step="0.01" min="0" readonly style="background-color: #e9ecef;">
                                    <span class="input-group-text">₫</span>
                                </div>
                                <small class="form-text text-muted">Giá sau khi áp dụng khuyến mãi (tự động tính, đây là giá sẽ được lưu vào database)</small>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="modalStock" class="form-label">Tồn kho <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="modalStock" name="stock" min="0" required>
                            </div>
                            
                            <div class="col-md-4 mb-3">
                                <label for="modalSize" class="form-label">Kích thước</label>
                                <input type="text" class="form-control" id="modalSize" name="size" 
                                       placeholder="S, M, L, XL...">
                            </div>
                            
                            <div class="col-md-4 mb-3">
                                <label for="modalColor" class="form-label">Màu sắc</label>
                                <input type="text" class="form-control" id="modalColor" name="color" 
                                       placeholder="Đỏ, Đen, Trắng...">
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalCategoryID" class="form-label">Danh mục <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalCategoryID" name="categoryID" required>
                                    <option value="">-- Chọn danh mục --</option>
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.categoryID}">${category.categoryName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalStockStatus" class="form-label">Trạng thái tồn kho <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalStockStatus" name="stockStatus" required>
                                    <option value="InStock">Còn hàng</option>
                                    <option value="OutOfStock">Hết hàng</option>
                                    <option value="LowStock">Sắp hết hàng</option>
                                    <option value="PreOrder">Đặt trước</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalImageUrl" class="form-label">URL hình ảnh</label>
                            <input type="url" class="form-control" id="modalImageUrl" name="imageUrl" 
                                   placeholder="https://example.com/image.jpg">
                            <small class="form-text text-muted">Đường dẫn đến hình ảnh sản phẩm</small>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" id="modalIsSpecial" name="isSpecial" value="true">
                                    <label class="form-check-label" for="modalIsSpecial">
                                        <i class="bi bi-star-fill text-warning"></i> Sản phẩm đặc biệt
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Lưu
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Function để mở modal thêm mới
        function openAddModal() {
            try {
                const modalElement = document.getElementById('productModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                
                document.getElementById('productModalLabel').innerHTML = '<i class="bi bi-box-seam"></i> Thêm sản phẩm mới';
                document.getElementById('productForm').reset();
                document.getElementById('modalProductID').value = '';
                document.getElementById('modalStockStatus').value = 'InStock';
                document.getElementById('modalIsSpecial').checked = false;
                document.getElementById('modalPromotionID').value = '';
                document.getElementById('modalPriceAfterDiscount').value = '';
                
                // Đảm bảo modal được hiển thị (nếu chưa)
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (!modal) {
                    const newModal = new bootstrap.Modal(modalElement);
                    newModal.show();
                }
            } catch (error) {
                console.error('Lỗi khi mở modal thêm mới:', error);
                alert('Có lỗi xảy ra khi mở form thêm mới!');
            }
        }

        // Function để mở modal chỉnh sửa từ data attributes
        function openEditModalFromData(productID) {
            try {
                // Tìm button với productID tương ứng
                const button = document.querySelector('button[data-product-id="' + productID + '"]');
                if (!button) {
                    console.error('Không tìm thấy button với productID:', productID);
                    alert('Không tìm thấy thông tin sản phẩm!');
                    return;
                }
                
                // Kiểm tra modal element có tồn tại không
                const modalElement = document.getElementById('productModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                
                // Cập nhật tiêu đề modal
                document.getElementById('productModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa sản phẩm';
                
                // Set các giá trị từ data attributes
                document.getElementById('modalProductID').value = button.getAttribute('data-product-id') || '';
                document.getElementById('modalProductName').value = button.getAttribute('data-product-name') || '';
                document.getElementById('modalSlug').value = button.getAttribute('data-product-slug') || '';
                document.getElementById('modalDescription').value = button.getAttribute('data-product-description') || '';
                // Lấy giá hiện tại (có thể là giá sau giảm nếu có promotion)
                const price = parseFloat(button.getAttribute('data-product-price')) || 0;
                document.getElementById('modalPrice').value = price.toFixed(2);
                
                // Set promotion nếu có
                <c:if test="${not empty currentPromotionID}">
                    document.getElementById('modalPromotionID').value = '${currentPromotionID}';
                </c:if>
                
                // Tính lại giá sau giảm
                calculatePriceFromPromotion();
                document.getElementById('modalStock').value = button.getAttribute('data-product-stock') || '0';
                document.getElementById('modalSize').value = button.getAttribute('data-product-size') || '';
                document.getElementById('modalColor').value = button.getAttribute('data-product-color') || '';
                document.getElementById('modalCategoryID').value = button.getAttribute('data-product-category') || '';
                
                // Set stock status
                const stockStatus = button.getAttribute('data-product-stockstatus');
                if (stockStatus) {
                    document.getElementById('modalStockStatus').value = stockStatus;
                } else {
                    document.getElementById('modalStockStatus').value = 'InStock';
                }
                
                // Set is special
                const isSpecial = button.getAttribute('data-product-special');
                document.getElementById('modalIsSpecial').checked = (isSpecial === 'true');
                
                // Set image URL
                document.getElementById('modalImageUrl').value = button.getAttribute('data-product-imageurl') || '';
                
                // Kiểm tra xem đã có instance modal chưa, nếu có thì dùng lại, không thì tạo mới
                let modal = bootstrap.Modal.getInstance(modalElement);
                if (!modal) {
                    modal = new bootstrap.Modal(modalElement, {
                        backdrop: true,
                        keyboard: true
                    });
                }
                modal.show();
                
            } catch (error) {
                console.error('Lỗi khi mở modal chỉnh sửa:', error);
                alert('Có lỗi xảy ra khi mở form chỉnh sửa!');
            }
        }
        
        // Auto-generate slug from product name
        document.getElementById('modalProductName').addEventListener('input', function() {
            const slugInput = document.getElementById('modalSlug');
            if (!slugInput.value || slugInput.dataset.autoGenerated === 'true') {
                const productName = this.value.toLowerCase()
                    .normalize('NFD')
                    .replace(/[\u0300-\u036f]/g, '') // Remove diacritics
                    .replace(/đ/g, 'd')
                    .replace(/Đ/g, 'D')
                    .replace(/[^a-z0-9\s-]/g, '') // Remove special characters
                    .replace(/\s+/g, '-') // Replace spaces with hyphens
                    .replace(/-+/g, '-') // Replace multiple hyphens with single hyphen
                    .trim();
                slugInput.value = productName;
                slugInput.dataset.autoGenerated = 'true';
            }
        });
        
        // Clear auto-generated flag when user manually edits slug
        document.getElementById('modalSlug').addEventListener('input', function() {
            this.dataset.autoGenerated = 'false';
        });

        // Tính giá sau giảm từ promotion
        function calculatePriceFromPromotion() {
            const priceInput = document.getElementById('modalPrice');
            const promotionSelect = document.getElementById('modalPromotionID');
            const priceAfterDiscountInput = document.getElementById('modalPriceAfterDiscount');
            
            const originalPrice = parseFloat(priceInput.value) || 0;
            
            if (promotionSelect.value && promotionSelect.value !== '') {
                const selectedOption = promotionSelect.options[promotionSelect.selectedIndex];
                const discountPercent = parseFloat(selectedOption.getAttribute('data-discount-percent')) || 0;
                const discountAmount = parseFloat(selectedOption.getAttribute('data-discount-amount')) || 0;
                
                let newPrice = originalPrice;
                
                if (discountPercent > 0) {
                    // Giảm theo phần trăm
                    const discount = originalPrice * (discountPercent / 100);
                    newPrice = originalPrice - discount;
                } else if (discountAmount > 0) {
                    // Giảm theo số tiền cố định
                    newPrice = originalPrice - discountAmount;
                }
                
                if (newPrice < 0) {
                    newPrice = 0;
                }
                
                // Set giá sau giảm (giá này sẽ được lưu vào database)
                priceAfterDiscountInput.value = newPrice.toFixed(2);
            } else {
                // Không có promotion, giá sau giảm = giá gốc
                priceAfterDiscountInput.value = originalPrice.toFixed(2);
            }
        }
        
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);

        // Auto-open modal if parameter is present
        window.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('autoOpenModal') === 'add') {
                setTimeout(function() {
                    openAddModal();
                }, 300); // Small delay to ensure modal is ready
            }
            
            // Nếu đang ở chế độ edit, mở modal và load dữ liệu
            <c:if test="${action == 'edit' && not empty product}">
                setTimeout(function() {
                    const modalElement = document.getElementById('productModal');
                    if (modalElement) {
                        const modal = new bootstrap.Modal(modalElement);
                        modal.show();
                        
                        // Set các giá trị từ product object
                        document.getElementById('modalProductID').value = '${product.productID}';
                        document.getElementById('modalProductName').value = '${fn:escapeXml(product.productName)}';
                        document.getElementById('modalSlug').value = '${fn:escapeXml(product.slug != null ? product.slug : "")}';
                        document.getElementById('modalDescription').value = '${fn:escapeXml(product.description != null ? product.description : "")}';
                        document.getElementById('modalPrice').value = '${product.price}';
                        document.getElementById('modalStock').value = '${product.stock}';
                        document.getElementById('modalSize').value = '${fn:escapeXml(product.size != null ? product.size : "")}';
                        document.getElementById('modalColor').value = '${fn:escapeXml(product.color != null ? product.color : "")}';
                        document.getElementById('modalCategoryID').value = '${product.categoryID}';
                        document.getElementById('modalStockStatus').value = '${product.stockStatus != null ? product.stockStatus : "InStock"}';
                        document.getElementById('modalIsSpecial').checked = ${product.special};
                        document.getElementById('modalImageUrl').value = '${fn:escapeXml(product.imageUrl != null ? product.imageUrl : "")}';
                        
                        <c:if test="${not empty currentPromotionID}">
                            document.getElementById('modalPromotionID').value = '${currentPromotionID}';
                        </c:if>
                        
                        // Tính lại giá sau giảm
                        calculatePriceFromPromotion();
                    }
                }, 300);
            </c:if>
        });
    </script>
</body>
</html>
