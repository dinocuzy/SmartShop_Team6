<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Người dùng - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3"><i class="bi bi-people"></i> Quản lý Người dùng</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#userModal" onclick="openAddModal()">
                    <i class="bi bi-plus-circle"></i> Thêm người dùng mới
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
                <form method="get" action="${pageContext.request.contextPath}/admin/users" class="row g-3">
                    <input type="hidden" name="action" value="list">
                    
                    <div class="col-md-4">
                        <label for="search" class="form-label">Tìm kiếm theo tên/email:</label>
                        <input type="text" class="form-control" id="search" name="search" 
                               value="${searchKeyword}" placeholder="Nhập tên hoặc email...">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="roleID" class="form-label">Lọc theo vai trò:</label>
                        <select class="form-select" id="roleID" name="roleID">
                            <option value="0" ${roleID == 0 ? 'selected' : ''}>Tất cả vai trò</option>
                            <c:forEach var="role" items="${roles}">
                                <option value="${role.roleID}" ${roleID == role.roleID ? 'selected' : ''}>
                                    ${role.roleName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="col-md-3">
                        <label for="sortBy" class="form-label">Sắp xếp theo:</label>
                        <select class="form-select" id="sortBy" name="sortBy">
                            <option value="UserID" ${sortBy == 'UserID' ? 'selected' : ''}>ID</option>
                            <option value="FullName" ${sortBy == 'FullName' ? 'selected' : ''}>Tên</option>
                            <option value="Email" ${sortBy == 'Email' ? 'selected' : ''}>Email</option>
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
                                <i class="bi bi-eye"></i> Hiển thị tất cả (bao gồm người dùng đã vô hiệu hóa)
                            </label>
                        </div>
                        <button type="submit" class="btn btn-outline-primary">
                            <i class="bi bi-search"></i> Tìm kiếm
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/users?action=list" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-clockwise"></i> Làm mới
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Users Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Danh sách người dùng 
                    <span class="badge bg-primary">${totalUsers}</span> người dùng
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Địa chỉ</th>
                                <th>Vai trò</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty users}">
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>${user.userID}</td>
                                            <td><strong>${user.fullName}</strong></td>
                                            <td>${user.email}</td>
                                            <td>${user.phone != null ? user.phone : '-'}</td>
                                            <td>
                                                <c:set var="userAddress" value="${userAddressesMap[user.userID]}" />
                                                <c:choose>
                                                    <c:when test="${not empty userAddress}">
                                                        <div class="small">
                                                            <div class="mb-1">
                                                                <i class="bi bi-geo-alt text-primary"></i>
                                                                <strong>${userAddress.fullName != null && not empty userAddress.fullName ? userAddress.fullName : user.fullName}</strong>
                                                                <c:if test="${userAddress.phone != null && not empty userAddress.phone}">
                                                                    <span class="text-muted ms-2">
                                                                        <i class="bi bi-telephone"></i> ${userAddress.phone}
                                                                    </span>
                                                                </c:if>
                                                            </div>
                                                            <div class="text-muted">
                                                                <c:choose>
                                                                    <c:when test="${not empty userAddress.fullAddress}">
                                                                        ${userAddress.fullAddress}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <c:if test="${not empty userAddress.line1}">${userAddress.line1}</c:if>
                                                                        <c:if test="${not empty userAddress.line2}">, ${userAddress.line2}</c:if>
                                                                        <c:if test="${not empty userAddress.ward}">, ${userAddress.ward}</c:if>
                                                                        <c:if test="${not empty userAddress.district}">, ${userAddress.district}</c:if>
                                                                        <c:if test="${not empty userAddress.city}">, ${userAddress.city}</c:if>
                                                                        <c:if test="${not empty userAddress.country}">, ${userAddress.country}</c:if>
                                                                        <c:if test="${not empty userAddress.postalCode}"> ${userAddress.postalCode}</c:if>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                            <c:if test="${userAddress.isDefault == true}">
                                                                <span class="badge bg-success mt-1">
                                                                    <i class="bi bi-star-fill"></i> Mặc định
                                                                </span>
                                                            </c:if>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">
                                                            <i class="bi bi-dash-circle"></i> Chưa có địa chỉ
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty user.roleName}">
                                                        <span class="badge bg-secondary">${user.roleName}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">ID: ${user.roleID}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.active}">
                                                        <span class="badge bg-success">Hoạt động</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">Tắt</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy" />
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/admin/users?action=edit&userID=${user.userID}" 
                                                   class="btn btn-sm btn-warning me-1"
                                                        title="Chỉnh sửa">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/admin/users?action=delete&userID=${user.userID}" 
                                                   class="btn btn-sm btn-danger"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa người dùng này?');"
                                                   title="Xóa">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="9" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                            <p class="mt-2">Không có người dùng nào</p>
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
                                   href="${pageContext.request.contextPath}/admin/users?action=list&page=${currentPage - 1}&search=${searchKeyword}&roleID=${roleID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
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
                                               href="${pageContext.request.contextPath}/admin/users?action=list&page=${i}&search=${searchKeyword}&roleID=${roleID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
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
                                   href="${pageContext.request.contextPath}/admin/users?action=list&page=${currentPage + 1}&search=${searchKeyword}&roleID=${roleID}&sortBy=${sortBy}&sortOrder=${sortOrder}">
                                    <i class="bi bi-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>

    <!-- User Modal (Add/Edit) -->
    <div class="modal fade" id="userModal" tabindex="-1" aria-labelledby="userModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/users" id="userForm">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="userID" id="modalUserID">
                    
                    <div class="modal-header">
                        <h5 class="modal-title" id="userModalLabel">
                            <i class="bi bi-people"></i> <span id="modalTitle">Thêm người dùng mới</span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalFullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="modalFullName" name="fullName" required>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalEmail" class="form-label">Email <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="modalEmail" name="email" required>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalPasswordHash" class="form-label">Mật khẩu <span id="passwordRequired" class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="modalPasswordHash" name="passwordHash">
                                <small class="form-text text-muted" id="passwordHint">Để trống nếu không muốn đổi mật khẩu (khi chỉnh sửa)</small>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="modalPhone" class="form-label">Số điện thoại</label>
                                <input type="text" class="form-control" id="modalPhone" name="phone">
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="modalRoleID" class="form-label">Vai trò <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalRoleID" name="roleID" required>
                                    <option value="">-- Chọn vai trò --</option>
                                    <c:forEach var="role" items="${roles}">
                                        <option value="${role.roleID}">${role.roleName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Trạng thái</label>
                                <div class="form-check mt-2">
                                    <input class="form-check-input" type="checkbox" id="modalIsActive" name="isActive" value="true" checked>
                                    <label class="form-check-label" for="modalIsActive">
                                        Kích hoạt tài khoản
                                    </label>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Address Section (chỉ hiển thị khi edit) -->
                        <div id="addressSection" style="display: none;">
                            <hr class="my-4">
                            <h6 class="mb-3"><i class="bi bi-geo-alt"></i> Địa chỉ</h6>
                            
                            <!-- Select Address to Edit -->
                            <div class="mb-3">
                                <label for="addressSelect" class="form-label">Chọn địa chỉ để chỉnh sửa:</label>
                                <select class="form-select" id="addressSelect" onchange="loadAddressData()">
                                    <option value="0">-- Thêm địa chỉ mới --</option>
                                    <c:forEach var="address" items="${addresses}">
                                        <option value="${address.addressID}" 
                                                data-address-id="${address.addressID}"
                                                data-address-fullname="${fn:escapeXml(address.fullName != null ? address.fullName : '')}"
                                                data-address-phone="${fn:escapeXml(address.phone != null ? address.phone : '')}"
                                                data-address-line1="${fn:escapeXml(address.line1 != null ? address.line1 : '')}"
                                                data-address-line2="${fn:escapeXml(address.line2 != null ? address.line2 : '')}"
                                                data-address-city="${fn:escapeXml(address.city != null ? address.city : '')}"
                                                data-address-district="${fn:escapeXml(address.district != null ? address.district : '')}"
                                                data-address-ward="${fn:escapeXml(address.ward != null ? address.ward : '')}"
                                                data-address-country="${fn:escapeXml(address.country != null ? address.country : '')}"
                                                data-address-postalcode="${fn:escapeXml(address.postalCode != null ? address.postalCode : '')}"
                                                data-address-isdefault="${address.isDefault}">
                                            ${address.fullName != null ? address.fullName : 'Address'} - 
                                            ${address.line1 != null ? address.line1 : ''} 
                                            ${address.city != null ? ', ' += address.city : ''}
                                            ${address.isDefault ? ' (Mặc định)' : ''}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <input type="hidden" name="addressID" id="modalAddressID">
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="modalAddressFullName" class="form-label">Họ tên người nhận</label>
                                    <input type="text" class="form-control" id="modalAddressFullName" name="addressFullName">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="modalAddressPhone" class="form-label">Số điện thoại</label>
                                    <input type="text" class="form-control" id="modalAddressPhone" name="addressPhone">
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="modalAddressLine1" class="form-label">Địa chỉ dòng 1 <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="modalAddressLine1" name="addressLine1" 
                                       placeholder="Số nhà, tên đường" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="modalAddressLine2" class="form-label">Địa chỉ dòng 2</label>
                                <input type="text" class="form-control" id="modalAddressLine2" name="addressLine2" 
                                       placeholder="Tên chung cư, tòa nhà...">
                            </div>
                            
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="modalAddressWard" class="form-label">Phường/Xã</label>
                                    <input type="text" class="form-control" id="modalAddressWard" name="addressWard" placeholder="Phường/Xã">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="modalAddressDistrict" class="form-label">Quận/Huyện</label>
                                    <input type="text" class="form-control" id="modalAddressDistrict" name="addressDistrict" placeholder="Quận/Huyện">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="modalAddressCity" class="form-label">Thành phố/Tỉnh <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="modalAddressCity" name="addressCity" 
                                           placeholder="Thành phố/Tỉnh" required>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="modalAddressCountry" class="form-label">Quốc gia</label>
                                    <input type="text" class="form-control" id="modalAddressCountry" name="addressCountry" 
                                           value="Vietnam" placeholder="Vietnam">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="modalAddressPostalCode" class="form-label">Mã bưu điện</label>
                                    <input type="text" class="form-control" id="modalAddressPostalCode" name="addressPostalCode" 
                                           placeholder="Mã bưu điện">
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" id="modalAddressIsDefault" name="addressIsDefault" value="true">
                                    <label class="form-check-label" for="modalAddressIsDefault">
                                        <i class="bi bi-star-fill"></i> Đặt làm địa chỉ mặc định
                                    </label>
                                </div>
                                <small class="form-text text-muted">
                                    Nếu đánh dấu, địa chỉ này sẽ được đặt làm mặc định cho khách hàng này
                                </small>
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
                const modalElement = document.getElementById('userModal');
                if (!modalElement) {
                    console.error('Không tìm thấy modal element!');
                    alert('Lỗi: Modal không tồn tại!');
                    return;
                }
                document.getElementById('userModalLabel').innerHTML = '<i class="bi bi-people"></i> Thêm người dùng mới';
                document.getElementById('userForm').reset();
                document.getElementById('modalUserID').value = '';
                document.getElementById('modalIsActive').checked = true;
                document.getElementById('passwordRequired').style.display = 'inline';
                document.getElementById('passwordHint').textContent = 'Mật khẩu là bắt buộc cho người dùng mới';
                document.getElementById('modalPasswordHash').required = true;
                // Ẩn phần address khi thêm mới
                document.getElementById('addressSection').style.display = 'none';
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

        // Khi modal được mở với action=edit, hiển thị phần address
        window.addEventListener('DOMContentLoaded', function() {
            <c:if test="${action == 'edit' && not empty user}">
                // Đợi một chút để đảm bảo Bootstrap đã load xong
                setTimeout(function() {
                    // Nếu đang ở chế độ edit, hiển thị modal và phần address
                    const modalElement = document.getElementById('userModal');
                    if (modalElement) {
                        // Set giá trị cho form user
                        document.getElementById('modalUserID').value = '${user.userID}';
                        document.getElementById('modalFullName').value = '${fn:escapeXml(user.fullName)}';
                        document.getElementById('modalEmail').value = '${fn:escapeXml(user.email)}';
                        <c:if test="${user.phone != null}">
                            document.getElementById('modalPhone').value = '${fn:escapeXml(user.phone)}';
                        </c:if>
                        document.getElementById('modalRoleID').value = '${user.roleID}';
                        document.getElementById('modalIsActive').checked = ${user.active};
                        
                        // Ẩn password required khi edit
                        const passwordRequired = document.getElementById('passwordRequired');
                        if (passwordRequired) {
                            passwordRequired.style.display = 'none';
                        }
                        const passwordHint = document.getElementById('passwordHint');
                        if (passwordHint) {
                            passwordHint.textContent = 'Để trống nếu không muốn đổi mật khẩu';
                        }
                        const modalPasswordHash = document.getElementById('modalPasswordHash');
                        if (modalPasswordHash) {
                            modalPasswordHash.required = false;
                        }
                        
                        // Update modal title
                        const userModalLabel = document.getElementById('userModalLabel');
                        if (userModalLabel) {
                            userModalLabel.innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa người dùng';
                        }
                        const modalTitle = document.getElementById('modalTitle');
                        if (modalTitle) {
                            modalTitle.textContent = 'Chỉnh sửa người dùng';
                        }
                        
                        // Hiển thị phần address
                        const addressSection = document.getElementById('addressSection');
                        if (addressSection) {
                            addressSection.style.display = 'block';
                        }
                        
                        // Load địa chỉ mặc định nếu có (chọn địa chỉ đầu tiên hoặc địa chỉ default)
                        const addressSelect = document.getElementById('addressSelect');
                        if (addressSelect && addressSelect.options.length > 1) {
                            // Tìm địa chỉ default trước, nếu không có thì chọn địa chỉ đầu tiên
                            let defaultAddressOption = null;
                            for (let i = 1; i < addressSelect.options.length; i++) {
                                const option = addressSelect.options[i];
                                if (option.getAttribute('data-address-isdefault') === 'true') {
                                    defaultAddressOption = option;
                                    break;
                                }
                            }
                            if (defaultAddressOption) {
                                addressSelect.value = defaultAddressOption.value;
                            } else {
                                addressSelect.value = addressSelect.options[1].value; // Chọn địa chỉ đầu tiên
                            }
                            loadAddressData();
                        } else if (addressSelect) {
                            // Nếu không có địa chỉ nào, chọn "Thêm địa chỉ mới"
                            addressSelect.value = '0';
                            loadAddressData();
                        }
                        
                        // Mở modal - kiểm tra xem Bootstrap có sẵn sàng chưa
                        try {
                            if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                                const modal = new bootstrap.Modal(modalElement, {
                                    backdrop: 'static',
                                    keyboard: false
                                });
                                modal.show();
                            } else {
                                // Fallback: dùng data attribute
                                modalElement.setAttribute('data-bs-toggle', 'modal');
                                modalElement.setAttribute('data-bs-target', '#userModal');
                                // Hoặc thử cách khác
                                modalElement.classList.add('show');
                                modalElement.style.display = 'block';
                                document.body.classList.add('modal-open');
                                const backdrop = document.createElement('div');
                                backdrop.className = 'modal-backdrop fade show';
                                backdrop.id = 'modalBackdrop';
                                document.body.appendChild(backdrop);
                            }
                        } catch (error) {
                            console.error('Error opening modal:', error);
                            // Fallback cuối cùng: thử mở bằng cách thêm class
                            modalElement.classList.add('show');
                            modalElement.style.display = 'block';
                        }
                    }
                }, 100);
            </c:if>
        });

        function openEditModalFromData(userID) {
            // Redirect đến action=edit để load đầy đủ thông tin user và addresses
            window.location.href = '${pageContext.request.contextPath}/admin/users?action=edit&userID=' + userID;
        }
        
        function loadAddressData() {
            const select = document.getElementById('addressSelect');
            const selectedOption = select.options[select.selectedIndex];
            
            if (selectedOption.value === '0') {
                // Thêm địa chỉ mới - xóa các trường
                document.getElementById('modalAddressID').value = '';
                document.getElementById('modalAddressFullName').value = '';
                document.getElementById('modalAddressPhone').value = '';
                document.getElementById('modalAddressLine1').value = '';
                document.getElementById('modalAddressLine2').value = '';
                document.getElementById('modalAddressWard').value = '';
                document.getElementById('modalAddressDistrict').value = '';
                document.getElementById('modalAddressCity').value = '';
                document.getElementById('modalAddressCountry').value = 'Vietnam';
                document.getElementById('modalAddressPostalCode').value = '';
                document.getElementById('modalAddressIsDefault').checked = false;
            } else {
                // Load dữ liệu từ selected option
                document.getElementById('modalAddressID').value = selectedOption.getAttribute('data-address-id') || '';
                document.getElementById('modalAddressFullName').value = selectedOption.getAttribute('data-address-fullname') || '';
                document.getElementById('modalAddressPhone').value = selectedOption.getAttribute('data-address-phone') || '';
                document.getElementById('modalAddressLine1').value = selectedOption.getAttribute('data-address-line1') || '';
                document.getElementById('modalAddressLine2').value = selectedOption.getAttribute('data-address-line2') || '';
                document.getElementById('modalAddressWard').value = selectedOption.getAttribute('data-address-ward') || '';
                document.getElementById('modalAddressDistrict').value = selectedOption.getAttribute('data-address-district') || '';
                document.getElementById('modalAddressCity').value = selectedOption.getAttribute('data-address-city') || '';
                document.getElementById('modalAddressCountry').value = selectedOption.getAttribute('data-address-country') || 'Vietnam';
                document.getElementById('modalAddressPostalCode').value = selectedOption.getAttribute('data-address-postalcode') || '';
                document.getElementById('modalAddressIsDefault').checked = selectedOption.getAttribute('data-address-isdefault') === 'true';
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

