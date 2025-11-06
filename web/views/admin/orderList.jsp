<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Đơn hàng - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-cart-check"></i> Quản lý Đơn hàng</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#orderModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm đơn hàng mới
                </button>
            </div>
        </div>

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
                <form method="get" action="${pageContext.request.contextPath}/admin/orders" class="row g-3">
                    <input type="hidden" name="action" value="list">
                    
                    <div class="col-md-3">
                        <label for="search" class="form-label">Tìm kiếm:</label>
                        <input type="text" class="form-control" id="search" name="search" 
                               value="${searchKeyword}" placeholder="Tìm theo tên khách hàng...">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="status" class="form-label">Lọc theo trạng thái:</label>
                        <select class="form-select" id="status" name="status">
                            <option value="all" ${status == 'all' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="Pending" ${status == 'Pending' ? 'selected' : ''}>Đang chờ</option>
                            <option value="Processing" ${status == 'Processing' ? 'selected' : ''}>Đang xử lý</option>
                            <option value="Shipped" ${status == 'Shipped' ? 'selected' : ''}>Đã giao hàng</option>
                            <option value="Delivered" ${status == 'Delivered' ? 'selected' : ''}>Đã nhận hàng</option>
                            <option value="Cancelled" ${status == 'Cancelled' ? 'selected' : ''}>Đã hủy</option>
                        </select>
                    </div>
                    
                    <div class="col-md-3">
                        <label for="userID" class="form-label">Lọc theo khách hàng:</label>
                        <select class="form-select" id="userID" name="userID">
                            <option value="0" ${userID == 0 ? 'selected' : ''}>Tất cả khách hàng</option>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.userID}" ${userID == user.userID ? 'selected' : ''}>
                                    ${user.fullName} (${user.email})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="col-md-3">
                        <label for="sortBy" class="form-label">Sắp xếp theo:</label>
                        <select class="form-select" id="sortBy" name="sortBy">
                            <option value="OrderID" ${sortBy == 'OrderID' ? 'selected' : ''}>ID</option>
                            <option value="OrderDate" ${sortBy == 'OrderDate' ? 'selected' : ''}>Ngày đặt</option>
                            <option value="TotalAmount" ${sortBy == 'TotalAmount' ? 'selected' : ''}>Tổng tiền</option>
                            <option value="OrderStatus" ${sortBy == 'OrderStatus' ? 'selected' : ''}>Trạng thái</option>
                        </select>
                    </div>
                    
                    <div class="col-md-2">
                        <label for="sortOrder" class="form-label">Thứ tự:</label>
                        <select class="form-select" id="sortOrder" name="sortOrder">
                            <option value="ASC" ${empty sortOrder || sortOrder == 'ASC' ? 'selected' : ''}>Tăng dần</option>
                            <option value="DESC" ${sortOrder == 'DESC' ? 'selected' : ''}>Giảm dần</option>
                        </select>
                    </div>
                    
                    <div class="col-md-10">
                        <button type="submit" class="btn btn-outline-primary">
                            <i class="bi bi-search"></i> Tìm kiếm
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/orders?action=list" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-clockwise"></i> Làm mới
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Orders Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách đơn hàng 
                    <span class="badge bg-primary">${totalOrders}</span> đơn hàng
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Khách hàng</th>
                                <th>Ngày đặt</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái đơn</th>
                                <th>Trạng thái thanh toán</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty orders}">
                                    <c:forEach var="order" items="${orders}">
                                        <tr>
                                            <td>${order.orderID}</td>
                                            <td>
                                                <strong>${order.userName != null ? order.userName : 'ID: ' += order.userID}</strong>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td>
                                                <strong class="text-primary">
                                                    <fmt:formatNumber value="${order.totalAmount}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${order.orderStatus == 'Pending'}">
                                                        <span class="badge bg-warning text-dark">Đang chờ</span>
                                                    </c:when>
                                                    <c:when test="${order.orderStatus == 'Processing'}">
                                                        <span class="badge bg-info">Đang xử lý</span>
                                                    </c:when>
                                                    <c:when test="${order.orderStatus == 'Shipped'}">
                                                        <span class="badge bg-primary">Đã giao hàng</span>
                                                    </c:when>
                                                    <c:when test="${order.orderStatus == 'Delivered'}">
                                                        <span class="badge bg-success">Đã nhận hàng</span>
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
                                                <c:choose>
                                                    <c:when test="${empty order.paymentStatus}">
                                                        <span class="badge bg-secondary">Chưa thanh toán</span>
                                                    </c:when>
                                                    <c:when test="${order.paymentStatus == 'Paid'}">
                                                        <span class="badge bg-success">Đã thanh toán</span>
                                                    </c:when>
                                                    <c:when test="${order.paymentStatus == 'Pending'}">
                                                        <span class="badge bg-warning text-dark">Chờ thanh toán</span>
                                                    </c:when>
                                                    <c:when test="${order.paymentStatus == 'Failed'}">
                                                        <span class="badge bg-danger">Thanh toán thất bại</span>
                                                    </c:when>
                                                    <c:when test="${order.paymentStatus == 'Refunded'}">
                                                        <span class="badge bg-info">Đã hoàn tiền</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">${order.paymentStatus}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/admin/orders?action=edit&orderID=${order.orderID}" 
                                                   class="btn btn-sm btn-warning me-1"
                                                   title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/admin/orders?action=delete&orderID=${order.orderID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa đơn hàng này?');"
                                                   title="Xóa">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="7" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                            <p class="mt-2">Không có đơn hàng nào</p>
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
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" 
                                   href="${pageContext.request.contextPath}/admin/orders?action=list&page=${currentPage - 1}&search=${searchKeyword}&status=${status}&userID=${userID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
                                    <i class="bi bi-chevron-left"></i>
                                </a>
                            </li>
                            
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
                                               href="${pageContext.request.contextPath}/admin/orders?action=list&page=${i}&search=${searchKeyword}&status=${status}&userID=${userID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
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
                            
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" 
                                   href="${pageContext.request.contextPath}/admin/orders?action=list&page=${currentPage + 1}&search=${searchKeyword}&status=${status}&userID=${userID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
                                    <i class="bi bi-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Order Modal (Add/Edit) -->
    <div class="modal fade" id="orderModal" tabindex="-1" aria-labelledby="orderModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/orders" id="orderForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="orderID" id="modalOrderID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="orderModalLabel">
                            <i class="bi bi-cart-check"></i> <span id="modalTitle">Thêm đơn hàng mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalUserID" class="form-label">Khách hàng <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalUserID" name="userID" required onchange="loadAddressesForUser(this.value)">
                                    <option value="">-- Chọn khách hàng --</option>
                                    <c:forEach var="user" items="${users}">
                                        <option value="${user.userID}" ${action == 'edit' && order.userID == user.userID ? 'selected' : ''}>
                                            ${user.fullName} (${user.email})
                                        </option>
                                    </c:forEach>
                                </select>
                                <c:if test="${action == 'edit' && not empty order.userName}">
                                    <small class="form-text text-muted">Khách hàng hiện tại: <strong>${order.userName}</strong></small>
                                </c:if>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalOrderStatus" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalOrderStatus" name="orderStatus" required>
                                    <option value="Pending">Đang chờ</option>
                                    <option value="Processing">Đang xử lý</option>
                                    <option value="Shipped">Đã giao hàng</option>
                                    <option value="Delivered">Đã nhận hàng</option>
                                    <option value="Cancelled">Đã hủy</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalOrderDate" class="form-label">Ngày đặt hàng</label>
                                <input type="date" class="form-control" id="modalOrderDate" name="orderDate">
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalTotalAmount" class="form-label">Tổng tiền <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <input type="number" class="form-control" id="modalTotalAmount" name="totalAmount" 
                                           step="0.01" min="0" required>
                                    <span class="input-group-text">₫</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalBillingAddress" class="form-label">Địa chỉ thanh toán</label>
                                <input type="text" class="form-control" id="modalBillingAddress" name="billingAddress" 
                                       placeholder="Địa chỉ cửa hàng (mặc định)" value="123 Đường ABC, Quận XYZ, TP. Hồ Chí Minh">
                                <small class="form-text text-muted">Địa chỉ thanh toán mặc định là địa chỉ cửa hàng</small>
                                <input type="hidden" id="modalBillingAddressID" name="billingAddressID" value="">
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalShippingAddressID" class="form-label">Địa chỉ giao hàng <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalShippingAddressID" name="shippingAddressID" onchange="updateShippingAddressDisplay()">
                                    <option value="">-- Chọn địa chỉ giao hàng --</option>
                                    <c:forEach var="address" items="${userAddresses}">
                                        <option value="${address.addressID}" 
                                                ${action == 'edit' && order.shippingAddressID != null && order.shippingAddressID == address.addressID ? 'selected' : ''}
                                                data-address-fullname="${fn:escapeXml(address.fullName != null ? address.fullName : '')}"
                                                data-address-phone="${fn:escapeXml(address.phone != null ? address.phone : '')}"
                                                data-address-line1="${fn:escapeXml(address.line1 != null ? address.line1 : '')}"
                                                data-address-line2="${fn:escapeXml(address.line2 != null ? address.line2 : '')}"
                                                data-address-ward="${fn:escapeXml(address.ward != null ? address.ward : '')}"
                                                data-address-district="${fn:escapeXml(address.district != null ? address.district : '')}"
                                                data-address-city="${fn:escapeXml(address.city != null ? address.city : '')}"
                                                data-address-country="${fn:escapeXml(address.country != null ? address.country : '')}"
                                                data-address-postalcode="${fn:escapeXml(address.postalCode != null ? address.postalCode : '')}">
                                            ${address.fullName != null ? address.fullName : 'Address'} - 
                                            ${address.line1 != null ? address.line1 : ''} 
                                            ${address.city != null ? ', ' += address.city : ''}
                                            ${address['default'] ? ' (Mặc định)' : ''}
                                        </option>
                                    </c:forEach>
                                </select>
                                <c:if test="${empty userAddresses}">
                                    <small class="form-text text-warning">Khách hàng này chưa có địa chỉ. Vui lòng thêm địa chỉ trong phần quản lý người dùng.</small>
                                </c:if>
                                <div id="shippingAddressDisplay" class="mt-2 p-2 bg-light rounded" style="display: none;">
                                    <small class="text-muted" id="shippingAddressText"></small>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalNote" class="form-label">Ghi chú</label>
                            <textarea class="form-control" id="modalNote" name="note" rows="3"></textarea>
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function openAddModal() {
            try {
                const modalElement = document.getElementById('orderModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('orderModalLabel').innerHTML = '<i class="bi bi-cart-check"></i> Thêm đơn hàng mới';
                document.getElementById('orderForm').reset();
                document.getElementById('modalOrderID').value = '';
                document.getElementById('modalOrderStatus').value = 'Pending';
                document.getElementById('modalBillingAddress').value = '123 Đường ABC, Quận XYZ, TP. Hồ Chí Minh';
                document.getElementById('modalShippingAddressID').value = '';
                document.getElementById('shippingAddressDisplay').style.display = 'none';
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('modalOrderDate').value = today;
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

        function openEditModalFromData(orderID) {
            // Redirect đến action=edit để load đầy đủ thông tin order và addresses
            window.location.href = '${pageContext.request.contextPath}/admin/orders?action=edit&orderID=' + orderID;
        }
        
        function updateShippingAddressDisplay() {
            const select = document.getElementById('modalShippingAddressID');
            const display = document.getElementById('shippingAddressDisplay');
            const text = document.getElementById('shippingAddressText');
            const selectedOption = select.options[select.selectedIndex];
            
            if (selectedOption && selectedOption.value) {
                const line1 = selectedOption.getAttribute('data-address-line1') || '';
                const line2 = selectedOption.getAttribute('data-address-line2') || '';
                const ward = selectedOption.getAttribute('data-address-ward') || '';
                const district = selectedOption.getAttribute('data-address-district') || '';
                const city = selectedOption.getAttribute('data-address-city') || '';
                const country = selectedOption.getAttribute('data-address-country') || '';
                const postalCode = selectedOption.getAttribute('data-address-postalcode') || '';
                
                let addressText = line1;
                if (line2) addressText += ', ' + line2;
                if (ward) addressText += ', ' + ward;
                if (district) addressText += ', ' + district;
                if (city) addressText += ', ' + city;
                if (country) addressText += ', ' + country;
                if (postalCode) addressText += ' ' + postalCode;
                
                text.textContent = addressText;
                display.style.display = 'block';
            } else {
                display.style.display = 'none';
            }
        }
        
        function loadAddressesForUser(userID) {
            // Load addresses của user khi chọn user (có thể dùng AJAX hoặc reload)
            // Hiện tại sẽ reload page với userID để load addresses
            if (userID && userID > 0) {
                // Có thể implement AJAX call ở đây để load addresses động
                // Tạm thời sẽ load khi submit form
            }
        }

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
                }, 300);
            }
            
            // Nếu đang ở chế độ edit, mở modal và load dữ liệu
            <c:if test="${action == 'edit' && not empty order}">
                const modalElement = document.getElementById('orderModal');
                if (modalElement) {
                    const modal = new bootstrap.Modal(modalElement);
                    modal.show();
                    
                    // Set giá trị cho form
                    document.getElementById('modalOrderID').value = '${order.orderID}';
                    document.getElementById('modalUserID').value = '${order.userID}';
                    document.getElementById('modalOrderStatus').value = '${order.orderStatus}';
                    document.getElementById('modalTotalAmount').value = '${order.totalAmount}';
                    <c:if test="${not empty order.orderDate}">
                        document.getElementById('modalOrderDate').value = '<fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd" />';
                    </c:if>
                    document.getElementById('modalNote').value = '${fn:escapeXml(order.note != null ? order.note : "")}';
                    
                    // Set shipping address nếu có
                    <c:if test="${order.shippingAddressID != null}">
                        document.getElementById('modalShippingAddressID').value = '${order.shippingAddressID}';
                        updateShippingAddressDisplay();
                    </c:if>
                    
                    // Update modal title
                    document.getElementById('orderModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa đơn hàng';
                }
            </c:if>
        });
    </script>
</body>
</html>

