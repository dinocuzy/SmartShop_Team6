<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Thông báo - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-bell"></i> Quản lý Thông báo</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#notificationModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm thông báo mới
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
                <form method="get" action="${pageContext.request.contextPath}/admin/notifications" class="row g-3">
                    <input type="hidden" name="action" value="list">
                    
                    <div class="col-md-4">
                        <label for="userID" class="form-label">Lọc theo người dùng:</label>
                        <select class="form-select" id="userID" name="userID">
                            <option value="">Tất cả người dùng</option>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.userID}" ${param.userID == user.userID ? 'selected' : ''}>
                                    ${user.fullName} (${user.email})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="col-md-4">
                        <label class="form-label">Lọc theo trạng thái:</label>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="unreadOnly" name="unreadOnly" value="true" ${param.unreadOnly == 'true' ? 'checked' : ''}>
                            <label class="form-check-label" for="unreadOnly">
                                Chỉ hiển thị thông báo chưa đọc
                            </label>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <label class="form-label">&nbsp;</label>
                        <div>
                            <button type="submit" class="btn btn-outline-primary">
                                <i class="bi bi-search"></i> Lọc
                            </button>
                            <a href="${pageContext.request.contextPath}/admin/notifications?action=list" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-clockwise"></i> Làm mới
                            </a>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Notifications Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách thông báo 
                    <span class="badge bg-primary">${fn:length(notifications)}</span> thông báo
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Người dùng</th>
                                <th>Tiêu đề</th>
                                <th>Nội dung</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty notifications}">
                                    <c:forEach var="notification" items="${notifications}">
                                        <tr class="${notification.read ? '' : 'table-warning'}">
                                            <td>${notification.notificationID}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/users?action=edit&userID=${notification.userID}" 
                                                   class="text-decoration-none">
                                                    <strong>User ID: ${notification.userID}</strong>
                                                </a>
                                            </td>
                                            <td><strong>${notification.title}</strong></td>
                                            <td>
                                                <c:if test="${not empty notification.content}">
                                                    ${fn:substring(notification.content, 0, 50)}${fn:length(notification.content) > 50 ? '...' : ''}
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${notification.read}">
                                                        <span class="badge bg-success">Đã đọc</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning text-dark">Chưa đọc</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${notification.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td class="text-center">
                                                <c:if test="${!notification.read}">
                                                    <a href="${pageContext.request.contextPath}/admin/notifications?action=markRead&notificationID=${notification.notificationID}" 
                                                       class="btn btn-sm btn-success me-1"
                                                       title="Đánh dấu đã đọc">
                                                        <i class="bi bi-check-circle"></i>
                                                    </a>
                                                </c:if>
                                                <button type="button" class="btn btn-sm btn-warning me-1" 
                                                        onclick="openEditModalFromData(${notification.notificationID})"
                                                        data-notification-id="${notification.notificationID}"
                                                        data-notification-userid="${notification.userID}"
                                                        data-notification-title="${fn:escapeXml(notification.title)}"
                                                        data-notification-content="${fn:escapeXml(notification.content)}"
                                                        data-notification-read="${notification.read}"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/notifications?action=delete&notificationID=${notification.notificationID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa thông báo này?');"
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
                                            <p class="mt-2">Không có thông báo nào</p>
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

    <!-- Notification Modal (Add/Edit) -->
    <div class="modal fade" id="notificationModal" tabindex="-1" aria-labelledby="notificationModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/notifications" id="notificationForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="notificationID" id="modalNotificationID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="notificationModalLabel">
                            <i class="bi bi-bell"></i> <span id="modalTitle">Thêm thông báo mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="modalUserID" class="form-label">Người dùng <span class="text-danger">*</span></label>
                            <select class="form-select" id="modalUserID" name="userID" required>
                                <option value="">-- Chọn người dùng --</option>
                                <c:forEach var="user" items="${users}">
                                    <option value="${user.userID}">${user.fullName} (${user.email})</option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalTitle" class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="modalTitle" name="title" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalContent" class="form-label">Nội dung <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="modalContent" name="content" rows="4" required></textarea>
                        </div>
                        
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="modalIsRead" name="isRead" value="true">
                                <label class="form-check-label" for="modalIsRead">
                                    Đánh dấu là đã đọc
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
                const modalElement = document.getElementById('notificationModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('notificationModalLabel').innerHTML = '<i class="bi bi-bell"></i> Thêm thông báo mới';
                document.getElementById('notificationForm').reset();
                document.getElementById('modalNotificationID').value = '';
                document.getElementById('modalIsRead').checked = false;
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

        function openEditModalFromData(notificationID) {
            try {
                const button = document.querySelector('button[data-notification-id="' + notificationID + '"]');
                if (!button) {
                    console.error('Không tìm thấy button với notificationID:', notificationID);
                    alert('Không tìm thấy thông tin thông báo!');
                    return;
                }
                const modalElement = document.getElementById('notificationModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('notificationModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa thông báo';
                document.getElementById('modalNotificationID').value = button.getAttribute('data-notification-id') || '';
                document.getElementById('modalUserID').value = button.getAttribute('data-notification-userid') || '';
                document.getElementById('modalTitle').value = button.getAttribute('data-notification-title') || '';
                document.getElementById('modalContent').value = button.getAttribute('data-notification-content') || '';
                const isRead = button.getAttribute('data-notification-read') === 'true';
                document.getElementById('modalIsRead').checked = isRead;
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

