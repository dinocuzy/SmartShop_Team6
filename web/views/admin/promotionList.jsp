<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Khuyến mãi - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-gift"></i> Quản lý Khuyến mãi</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#promotionModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm khuyến mãi mới
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
                <form method="get" action="${pageContext.request.contextPath}/admin/promotions" class="row g-3">
                    <input type="hidden" name="action" value="list">
                    <div class="col-md-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="showAll" name="showAll" value="true" ${showAll ? 'checked' : ''} onchange="this.form.submit()">
                            <label class="form-check-label" for="showAll">
                                <i class="bi bi-eye"></i> Hiển thị tất cả (bao gồm khuyến mãi đã vô hiệu hóa)
                            </label>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Promotions Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách khuyến mãi 
                    <span class="badge bg-primary">${fn:length(promotions)}</span> khuyến mãi
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Mô tả</th>
                                <th>Giảm giá</th>
                                <th>Ngày bắt đầu</th>
                                <th>Ngày kết thúc</th>
                                <th>Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty promotions}">
                                    <c:forEach var="promotion" items="${promotions}">
                                        <tr>
                                            <td>${promotion.promotionID}</td>
                                            <td><strong>${promotion.title}</strong></td>
                                            <td>
                                                <c:if test="${not empty promotion.description}">
                                                    ${fn:substring(promotion.description, 0, 50)}${fn:length(promotion.description) > 50 ? '...' : ''}
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${promotion.discountPercent != null}">
                                                    <span class="badge bg-danger">-${promotion.discountPercent}%</span>
                                                </c:if>
                                                <c:if test="${promotion.discountAmount != null}">
                                                    <span class="badge bg-warning text-dark">
                                                        -<fmt:formatNumber value="${promotion.discountAmount}" type="currency" 
                                                            currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                                    </span>
                                                </c:if>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${promotion.startDate}" pattern="dd/MM/yyyy" />
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${promotion.endDate}" pattern="dd/MM/yyyy" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${promotion.active && promotion.valid}">
                                                        <span class="badge bg-success">Đang hoạt động</span>
                                                    </c:when>
                                                    <c:when test="${promotion.active}">
                                                        <span class="badge bg-warning text-dark">Chưa/Đã hết hạn</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Tắt</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <button type="button" class="btn btn-sm btn-warning me-1" 
                                                        onclick="openEditModalFromData(${promotion.promotionID})"
                                                        data-promotion-id="${promotion.promotionID}"
                                                        data-promotion-title="${fn:escapeXml(promotion.title)}"
                                                        data-promotion-description="${fn:escapeXml(promotion.description)}"
                                                        data-promotion-discountpercent="${promotion.discountPercent}"
                                                        data-promotion-discountamount="${promotion.discountAmount}"
                                                        data-promotion-startdate="<fmt:formatDate value='${promotion.startDate}' pattern='yyyy-MM-dd' />"
                                                        data-promotion-enddate="<fmt:formatDate value='${promotion.endDate}' pattern='yyyy-MM-dd' />"
                                                        data-promotion-active="${promotion.active}"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/promotions?action=delete&promotionID=${promotion.promotionID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa khuyến mãi này?');"
                                                   title="Xóa">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                            <p class="mt-2">Không có khuyến mãi nào</p>
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

    <!-- Promotion Modal (Add/Edit) -->
    <div class="modal fade" id="promotionModal" tabindex="-1" aria-labelledby="promotionModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/promotions" id="promotionForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="promotionID" id="modalPromotionID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="promotionModalLabel">
                            <i class="bi bi-gift"></i> <span id="modalTitle">Thêm khuyến mãi mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="modalTitle" class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="modalTitle" name="title" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="modalDescription" name="description" rows="3"></textarea>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalDiscountPercent" class="form-label">Giảm giá theo %</label>
                                <div class="input-group">
                                    <input type="number" class="form-control" id="modalDiscountPercent" name="discountPercent" 
                                           step="0.01" min="0" max="100">
                                    <span class="input-group-text">%</span>
                                </div>
                                <small class="form-text text-muted">Nhập từ 0-100</small>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalDiscountAmount" class="form-label">Giảm giá số tiền</label>
                                <div class="input-group">
                                    <input type="number" class="form-control" id="modalDiscountAmount" name="discountAmount" 
                                           step="0.01" min="0">
                                    <span class="input-group-text">₫</span>
                                </div>
                                <small class="form-text text-muted">Chọn một trong hai: % hoặc số tiền</small>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalStartDate" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="modalStartDate" name="startDate" required>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalEndDate" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="modalEndDate" name="endDate" required>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="modalIsActive" name="isActive" value="true" checked>
                                <label class="form-check-label" for="modalIsActive">
                                    Kích hoạt khuyến mãi
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
                const modalElement = document.getElementById('promotionModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('promotionModalLabel').innerHTML = '<i class="bi bi-gift"></i> Thêm khuyến mãi mới';
                document.getElementById('promotionForm').reset();
                document.getElementById('modalPromotionID').value = '';
                document.getElementById('modalIsActive').checked = true;
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('modalStartDate').value = today;
                document.getElementById('modalEndDate').value = today;
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

        function openEditModalFromData(promotionID) {
            try {
                const button = document.querySelector('button[data-promotion-id="' + promotionID + '"]');
                if (!button) {
                    console.error('Không tìm thấy button với promotionID:', promotionID);
                    alert('Không tìm thấy thông tin khuyến mãi!');
                    return;
                }
                const modalElement = document.getElementById('promotionModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('promotionModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa khuyến mãi';
                document.getElementById('modalPromotionID').value = button.getAttribute('data-promotion-id') || '';
                document.getElementById('modalTitle').value = button.getAttribute('data-promotion-title') || '';
                document.getElementById('modalDescription').value = button.getAttribute('data-promotion-description') || '';
                const discountPercent = button.getAttribute('data-promotion-discountpercent');
                document.getElementById('modalDiscountPercent').value = discountPercent && discountPercent !== 'null' ? discountPercent : '';
                const discountAmount = button.getAttribute('data-promotion-discountamount');
                document.getElementById('modalDiscountAmount').value = discountAmount && discountAmount !== 'null' ? discountAmount : '';
                const startDate = button.getAttribute('data-promotion-startdate');
                if (startDate) {
                    document.getElementById('modalStartDate').value = startDate;
                }
                const endDate = button.getAttribute('data-promotion-enddate');
                if (endDate) {
                    document.getElementById('modalEndDate').value = endDate;
                }
                const isActive = button.getAttribute('data-promotion-active') === 'true';
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

