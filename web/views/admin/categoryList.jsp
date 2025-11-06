<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Danh mục - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-tags"></i> Quản lý Danh mục</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#categoryModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm danh mục mới
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

        <!-- Categories Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách danh mục 
                    <span class="badge bg-primary">${fn:length(categories)}</span> danh mục
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Tên danh mục</th>
                                <th>Mô tả</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty categories}">
                                    <c:forEach var="category" items="${categories}">
                                        <tr>
                                            <td>${category.categoryID}</td>
                                            <td><strong>${category.categoryName}</strong></td>
                                            <td>${category.description}</td>
                                            <td class="text-center">
                                                <button type="button" class="btn btn-sm btn-warning me-1" 
                                                        onclick="openEditModalFromData(${category.categoryID})"
                                                        data-category-id="${category.categoryID}"
                                                        data-category-name="${fn:escapeXml(category.categoryName)}"
                                                        data-category-description="${fn:escapeXml(category.description)}"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                                <a href="${pageContext.request.contextPath}/admin/categories?action=delete&categoryID=${category.categoryID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa danh mục này?');"
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
                                            <p class="mt-2">Không có danh mục nào</p>
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

    <!-- Category Modal (Add/Edit) -->
    <div class="modal fade" id="categoryModal" tabindex="-1" aria-labelledby="categoryModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/categories" id="categoryForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="categoryID" id="modalCategoryID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="categoryModalLabel">
                            <i class="bi bi-tags"></i> <span id="modalTitle">Thêm danh mục mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="modalCategoryName" class="form-label">Tên danh mục <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="modalCategoryName" name="categoryName" required>
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
                const modalElement = document.getElementById('categoryModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                
                document.getElementById('categoryModalLabel').innerHTML = '<i class="bi bi-tags"></i> Thêm danh mục mới';
                document.getElementById('categoryForm').reset();
                document.getElementById('modalCategoryID').value = '';
                
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

        function openEditModalFromData(categoryID) {
            try {
                const button = document.querySelector('button[data-category-id="' + categoryID + '"]');
                if (!button) {
                    console.error('Không tìm thấy button với categoryID:', categoryID);
                    alert('Không tìm thấy thông tin danh mục!');
                    return;
                }
                
                const modalElement = document.getElementById('categoryModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                
                document.getElementById('categoryModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa danh mục';
                
                document.getElementById('modalCategoryID').value = button.getAttribute('data-category-id') || '';
                document.getElementById('modalCategoryName').value = button.getAttribute('data-category-name') || '';
                document.getElementById('modalDescription').value = button.getAttribute('data-category-description') || '';
                
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
