<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Phương thức Thanh toán - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-credit-card"></i> Quản lý Phương thức Thanh toán</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#paymentMethodModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm phương thức mới
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

        <!-- Filter Form -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/admin/payment-methods" class="row g-3">
                    <input type="hidden" name="action" value="list">
                    <div class="col-md-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="showAll" name="showAll" value="true" ${showAll ? 'checked' : ''} onchange="this.form.submit()">
                            <label class="form-check-label" for="showAll">
                                <i class="bi bi-eye"></i> Hiển thị tất cả (bao gồm phương thức thanh toán đã vô hiệu hóa)
                            </label>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách phương thức thanh toán 
                    <span class="badge bg-primary">${fn:length(paymentMethods)}</span> phương thức
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Tên phương thức</th>
                                <th>Nhà cung cấp</th>
                                <th>Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty paymentMethods}">
                                    <c:forEach var="method" items="${paymentMethods}">
                                        <tr>
                                            <td>${method.paymentMethodID}</td>
                                            <td><strong>${method.methodName}</strong></td>
                                            <td>${method.provider}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${method.active}">
                                                        <span class="badge bg-success">Hoạt động</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Tắt</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <button type="button" class="btn btn-sm btn-warning me-1" 
                                                        onclick="openEditModalFromData(${method.paymentMethodID})"
                                                        data-method-id="${method.paymentMethodID}"
                                                        data-method-name="${fn:escapeXml(method.methodName)}"
                                                        data-method-provider="${fn:escapeXml(method.provider)}"
                                                        data-method-active="${method.active}"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/payment-methods?action=delete&paymentMethodID=${method.paymentMethodID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa phương thức thanh toán này?');"
                                                   title="Xóa">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                            <p class="mt-2">Không có phương thức thanh toán nào</p>
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

    <div class="modal fade" id="paymentMethodModal" tabindex="-1" aria-labelledby="paymentMethodModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/payment-methods" id="paymentMethodForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="paymentMethodID" id="modalPaymentMethodID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="paymentMethodModalLabel">
                            <i class="bi bi-credit-card"></i> <span id="modalTitle">Thêm phương thức thanh toán mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="modalMethodName" class="form-label">Tên phương thức <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="modalMethodName" name="methodName" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalProvider" class="form-label">Nhà cung cấp</label>
                            <input type="text" class="form-control" id="modalProvider" name="provider" 
                                   placeholder="Ví dụ: VNPay, MoMo, PayPal...">
                        </div>
                        
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="modalIsActive" name="isActive" value="true" checked>
                                <label class="form-check-label" for="modalIsActive">
                                    Kích hoạt phương thức này
                                </label>
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
                const modalElement = document.getElementById('paymentMethodModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('paymentMethodModalLabel').innerHTML = '<i class="bi bi-credit-card"></i> Thêm phương thức thanh toán mới';
                document.getElementById('paymentMethodForm').reset();
                document.getElementById('modalPaymentMethodID').value = '';
                document.getElementById('modalIsActive').checked = true;
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

        function openEditModalFromData(paymentMethodID) {
            try {
                const button = document.querySelector('button[data-method-id="' + paymentMethodID + '"]');
                if (!button) {
                    console.error('Không tìm thấy button với paymentMethodID:', paymentMethodID);
                    alert('Không tìm thấy thông tin phương thức thanh toán!');
                    return;
                }
                const modalElement = document.getElementById('paymentMethodModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('paymentMethodModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa phương thức thanh toán';
                document.getElementById('modalPaymentMethodID').value = button.getAttribute('data-method-id') || '';
                document.getElementById('modalMethodName').value = button.getAttribute('data-method-name') || '';
                document.getElementById('modalProvider').value = button.getAttribute('data-method-provider') || '';
                const isActive = button.getAttribute('data-method-active') === 'true';
                document.getElementById('modalIsActive').checked = isActive;
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

