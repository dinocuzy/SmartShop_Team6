<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Vai trò - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-person-badge"></i> Quản lý Vai trò</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#roleModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm vai trò mới
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

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách vai trò 
                    <span class="badge bg-primary">${fn:length(roles)}</span> vai trò
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Tên vai trò</th>
                                <th>Mô tả</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty roles}">
                                    <c:forEach var="role" items="${roles}">
                                        <tr>
                                            <td>${role.roleID}</td>
                                            <td><strong>${role.roleName}</strong></td>
                                            <td>${role.description}</td>
                                            <td class="text-center">
                                                <button type="button" class="btn btn-sm btn-warning me-1" 
                                                        onclick="openEditModalFromData(${role.roleID})"
                                                        data-role-id="${role.roleID}"
                                                        data-role-name="${fn:escapeXml(role.roleName)}"
                                                        data-role-description="${fn:escapeXml(role.description)}"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/roles?action=delete&roleID=${role.roleID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa vai trò này?');"
                                                   title="Xóa">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                            <p class="mt-2">Không có vai trò nào</p>
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

    <div class="modal fade" id="roleModal" tabindex="-1" aria-labelledby="roleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/roles" id="roleForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="roleID" id="modalRoleID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="roleModalLabel">
                            <i class="bi bi-person-badge"></i> <span id="modalTitle">Thêm vai trò mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="modalRoleName" class="form-label">Tên vai trò <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="modalRoleName" name="roleName" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="modalDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="modalDescription" name="description" rows="3"></textarea>
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
                const modalElement = document.getElementById('roleModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('roleModalLabel').innerHTML = '<i class="bi bi-person-badge"></i> Thêm vai trò mới';
                document.getElementById('roleForm').reset();
                document.getElementById('modalRoleID').value = '';
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

        function openEditModalFromData(roleID) {
            try {
                const button = document.querySelector('button[data-role-id="' + roleID + '"]');
                if (!button) {
                    console.error('Không tìm thấy button với roleID:', roleID);
                    alert('Không tìm thấy thông tin vai trò!');
                    return;
                }
                const modalElement = document.getElementById('roleModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('roleModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa vai trò';
                document.getElementById('modalRoleID').value = button.getAttribute('data-role-id') || '';
                document.getElementById('modalRoleName').value = button.getAttribute('data-role-name') || '';
                document.getElementById('modalDescription').value = button.getAttribute('data-role-description') || '';
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
