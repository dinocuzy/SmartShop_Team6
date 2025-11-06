<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Thanh toán - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-wallet2"></i> Quản lý Thanh toán</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#paymentModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm thanh toán mới
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

        <!-- Payments Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách thanh toán 
                    <span class="badge bg-primary">${not empty payments ? fn:length(payments) : 0}</span> giao dịch
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Đơn hàng</th>
                                <th>Phương thức</th>
                                <th>Số tiền</th>
                                <th>Trạng thái</th>
                                <th>Ngày thanh toán</th>
                                <th>Mã giao dịch</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty payments}">
                                    <c:forEach var="payment" items="${payments}">
                                        <tr>
                                            <td>${payment.paymentID}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/orders?action=edit&orderID=${payment.orderID}" 
                                                   class="text-decoration-none">
                                                    <strong>Đơn #${payment.orderID}</strong>
                                                </a>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty payment.methodName}">
                                                        <span class="badge bg-info">${payment.methodName}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">ID: ${payment.paymentMethodID}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <strong class="text-success">
                                                    <fmt:formatNumber value="${payment.amount}" type="currency" 
                                                        currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${payment.paymentStatus == 'Completed'}">
                                                        <span class="badge bg-success">Hoàn thành</span>
                                                    </c:when>
                                                    <c:when test="${payment.paymentStatus == 'Pending'}">
                                                        <span class="badge bg-warning text-dark">Đang chờ</span>
                                                    </c:when>
                                                    <c:when test="${payment.paymentStatus == 'Failed'}">
                                                        <span class="badge bg-danger">Thất bại</span>
                                                    </c:when>
                                                    <c:when test="${payment.paymentStatus == 'Refunded'}">
                                                        <span class="badge bg-secondary">Đã hoàn tiền</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">${payment.paymentStatus}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:if test="${not empty payment.paymentDate}">
                                                    <fmt:formatDate value="${payment.paymentDate}" pattern="dd/MM/yyyy HH:mm" />
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${not empty payment.transactionCode}">
                                                    <code>${payment.transactionCode}</code>
                                                </c:if>
                                            </td>
                                            <td class="text-center">
                                                <c:set var="paymentDateFormatted" value="" />
                                                <c:if test="${not empty payment.paymentDate}">
                                                    <fmt:formatDate var="paymentDateFormatted" value="${payment.paymentDate}" pattern="yyyy-MM-dd" />
                                                </c:if>
                                                <button type="button" class="btn btn-sm btn-warning me-1" 
                                                        onclick="openEditModalFromData(${payment.paymentID})"
                                                        data-payment-id="${payment.paymentID}"
                                                        data-payment-orderid="${payment.orderID}"
                                                        data-payment-methodid="${payment.paymentMethodID}"
                                                        data-payment-amount="${payment.amount}"
                                                        data-payment-status="${fn:escapeXml(payment.paymentStatus)}"
                                                        data-payment-date="${paymentDateFormatted}"
                                                        data-payment-transaction="${fn:escapeXml(payment.transactionCode)}"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                            <p class="mt-2">Không có giao dịch thanh toán nào</p>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Payment Modal (Add/Edit) -->
    <div class="modal fade" id="paymentModal" tabindex="-1" aria-labelledby="paymentModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/payments" id="paymentForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="paymentID" id="modalPaymentID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="paymentModalLabel">
                            <i class="bi bi-wallet2"></i> <span id="modalTitle">Thêm thanh toán mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalOrderID" class="form-label">Đơn hàng <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalOrderID" name="orderID" required>
                                    <option value="">-- Chọn đơn hàng --</option>
                                    <c:if test="${not empty orders}">
                                        <c:forEach var="order" items="${orders}">
                                            <option value="${order.orderID}">
                                                Đơn #${order.orderID} - 
                                                <c:choose>
                                                    <c:when test="${not empty order.userName}">${order.userName}</c:when>
                                                    <c:otherwise>User ID: ${order.userID}</c:otherwise>
                                                </c:choose>
                                            </option>
                                        </c:forEach>
                                    </c:if>
                                </select>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalPaymentMethodID" class="form-label">Phương thức thanh toán <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalPaymentMethodID" name="paymentMethodID" required>
                                    <option value="">-- Chọn phương thức --</option>
                                    <c:if test="${not empty paymentMethods}">
                                        <c:forEach var="method" items="${paymentMethods}">
                                            <option value="${method.paymentMethodID}">${method.methodName}</option>
                                        </c:forEach>
                                    </c:if>
                                </select>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalAmount" class="form-label">Số tiền <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <input type="number" class="form-control" id="modalAmount" name="amount" 
                                           step="0.01" min="0" required>
                                    <span class="input-group-text">₫</span>
                                </div>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalPaymentStatus" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalPaymentStatus" name="paymentStatus" required>
                                    <option value="Pending">Đang chờ</option>
                                    <option value="Completed">Hoàn thành</option>
                                    <option value="Failed">Thất bại</option>
                                    <option value="Refunded">Đã hoàn tiền</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalPaymentDate" class="form-label">Ngày thanh toán</label>
                                <input type="date" class="form-control" id="modalPaymentDate" name="paymentDate">
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalTransactionCode" class="form-label">Mã giao dịch</label>
                                <input type="text" class="form-control" id="modalTransactionCode" name="transactionCode" 
                                       placeholder="VD: TXN123456789">
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function openAddModal() {
            try {
                const modalElement = document.getElementById('paymentModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('paymentModalLabel').innerHTML = '<i class="bi bi-wallet2"></i> Thêm thanh toán mới';
                document.getElementById('paymentForm').reset();
                document.getElementById('modalPaymentID').value = '';
                document.getElementById('modalPaymentStatus').value = 'Pending';
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('modalPaymentDate').value = today;
                let modal = bootstrap.Modal.getInstance(modalElement);
                if (!modal) {
                    modal = new bootstrap.Modal(modalElement);
                }
                modal.show();
            } catch (error) {
                console.error('Lỗi khi mở modal thêm mới:', error);
                alert('Có lỗi xảy ra khi mở form thêm mới!');
            }
        }

        function openEditModalFromData(paymentID) {
            try {
                const button = document.querySelector('button[data-payment-id="' + paymentID + '"]');
                if (!button) {
                    console.error('Không tìm thấy button với paymentID:', paymentID);
                    alert('Không tìm thấy thông tin thanh toán!');
                    return;
                }
                const modalElement = document.getElementById('paymentModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('paymentModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa thanh toán';
                document.getElementById('modalPaymentID').value = button.getAttribute('data-payment-id') || '';
                document.getElementById('modalOrderID').value = button.getAttribute('data-payment-orderid') || '';
                document.getElementById('modalPaymentMethodID').value = button.getAttribute('data-payment-methodid') || '';
                document.getElementById('modalAmount').value = button.getAttribute('data-payment-amount') || '0';
                document.getElementById('modalPaymentStatus').value = button.getAttribute('data-payment-status') || 'Pending';
                const paymentDate = button.getAttribute('data-payment-date');
                if (paymentDate) {
                    document.getElementById('modalPaymentDate').value = paymentDate;
                }
                document.getElementById('modalTransactionCode').value = button.getAttribute('data-payment-transaction') || '';
                let modal = bootstrap.Modal.getInstance(modalElement);
                if (!modal) {
                    modal = new bootstrap.Modal(modalElement);
                }
                modal.show();
            } catch (error) {
                console.error('Lỗi khi mở modal chỉnh sửa:', error);
                alert('Có lỗi xảy ra khi mở form chỉnh sửa!');
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
        });
    </script>
</body>
</html>

