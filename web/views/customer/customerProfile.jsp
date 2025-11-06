<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin cá nhân - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .profile-container {
            max-width: 900px;
            margin: 2rem auto;
        }
        .profile-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 2rem;
        }
        .nav-tabs .nav-link {
            color: #495057;
        }
        .nav-tabs .nav-link.active {
            color: #0d6efd;
            font-weight: 600;
        }
        .address-card {
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 1rem;
            margin-bottom: 1rem;
            position: relative;
        }
        .address-card.default {
            border-color: #198754;
            background-color: #f8fff9;
        }
        .address-badge {
            position: absolute;
            top: 10px;
            right: 10px;
        }
        .order-item {
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 1rem;
            margin-bottom: 1rem;
        }
        .status-badge {
            font-size: 0.875rem;
            padding: 0.25rem 0.75rem;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-dark bg-primary mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/customer/dashboard">
                <i class="bi bi-shop"></i> SmartShop
            </a>
            <div>
                <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-light btn-sm me-2">
                    <i class="bi bi-house"></i> Về trang chủ
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right"></i> Đăng xuất
                </a>
            </div>
        </div>
    </nav>

    <div class="container profile-container">
        <div class="profile-card">
            <h3 class="mb-4"><i class="bi bi-person-circle"></i> Thông tin cá nhân</h3>
            
            <!-- Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty user}">
                    <!-- Tabs Navigation -->
                    <ul class="nav nav-tabs mb-4" id="profileTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="info-tab" data-bs-toggle="tab" 
                                    data-bs-target="#info" type="button" role="tab">
                                <i class="bi bi-person"></i> Thông tin cá nhân
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="address-tab" data-bs-toggle="tab" 
                                    data-bs-target="#address" type="button" role="tab">
                                <i class="bi bi-geo-alt"></i> Địa chỉ
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="password-tab" data-bs-toggle="tab" 
                                    data-bs-target="#password" type="button" role="tab">
                                <i class="bi bi-shield-lock"></i> Đổi mật khẩu
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="orders-tab" data-bs-toggle="tab" 
                                    data-bs-target="#orders" type="button" role="tab">
                                <i class="bi bi-bag"></i> Lịch sử đơn hàng
                            </button>
                        </li>
                    </ul>

                    <!-- Tab Content -->
                    <div class="tab-content" id="profileTabContent">
                        <!-- Tab 1: Thông tin cá nhân -->
                        <div class="tab-pane fade show active" id="info" role="tabpanel">
                            <form method="post" action="${pageContext.request.contextPath}/customer/profile">
                                <input type="hidden" name="action" value="update">
                                
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="fullName" class="form-label">Họ và tên <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="fullName" name="fullName" 
                                               value="${user.fullName != null ? user.fullName : ''}" required>
                                    </div>
                                    
                                    <div class="col-md-6 mb-3">
                                        <label for="email" class="form-label">Email</label>
                                        <input type="email" class="form-control" id="email" 
                                               value="${user.email != null ? user.email : ''}" disabled>
                                        <small class="text-muted">Email không thể thay đổi</small>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="phone" class="form-label">Số điện thoại</label>
                                        <input type="text" class="form-control" id="phone" name="phone" 
                                               value="${user.phone != null ? user.phone : ''}" 
                                               placeholder="Ví dụ: 0123456789">
                                    </div>
                                    
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Vai trò</label>
                                        <input type="text" class="form-control" 
                                               value="${user.roleName != null ? user.roleName : 'N/A'}" disabled>
                                    </div>
                                </div>
                                
                                <div class="mb-3">
                                    <label class="form-label">Ngày tham gia</label>
                                    <c:choose>
                                        <c:when test="${user.createdAt != null}">
                                            <fmt:formatDate var="formattedDate" value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                            <input type="text" class="form-control" value="${formattedDate}" disabled>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="text" class="form-control" value="N/A" disabled>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-save"></i> Lưu thay đổi
                                    </button>
                                </div>
                            </form>
                        </div>

                        <!-- Tab 2: Địa chỉ -->
                        <div class="tab-pane fade" id="address" role="tabpanel">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h5 class="mb-0">Danh sách địa chỉ</h5>
                                <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" 
                                        data-bs-target="#addressModal" onclick="openAddAddressModal()">
                                    <i class="bi bi-plus-circle"></i> Thêm địa chỉ mới
                                </button>
                            </div>
                            
                            <c:choose>
                                <c:when test="${not empty addresses && addresses.size() > 0}">
                                    <div class="address-list">
                                        <c:forEach var="addr" items="${addresses}">
                                            <div class="address-card ${addr.default ? 'default' : ''}">
                                                <c:if test="${addr.default}">
                                                    <span class="badge bg-success address-badge">Mặc định</span>
                                                </c:if>
                                                <div class="d-flex justify-content-between align-items-start">
                                                    <div class="flex-grow-1">
                                                        <h6 class="mb-2">
                                                            <i class="bi bi-person"></i> 
                                                            ${addr.fullName != null ? addr.fullName : 'N/A'}
                                                        </h6>
                                                        <p class="mb-1">
                                                            <i class="bi bi-telephone"></i> 
                                                            ${addr.phone != null ? addr.phone : '-'}
                                                        </p>
                                                        <p class="mb-0 text-muted">
                                                            <i class="bi bi-geo-alt"></i> 
                                                            ${addr.fullAddress != null ? addr.fullAddress : '-'}
                                                        </p>
                                                    </div>
                                                    <div class="btn-group-vertical ms-2">
                                                        <c:if test="${!addr.default}">
                                                            <form method="post" action="${pageContext.request.contextPath}/customer/profile" 
                                                                  class="d-inline" onsubmit="return confirm('Đặt địa chỉ này làm mặc định?');">
                                                                <input type="hidden" name="action" value="setDefaultAddress">
                                                                <input type="hidden" name="addressID" value="${addr.addressID}">
                                                                <button type="submit" class="btn btn-sm btn-outline-success mb-1">
                                                                    <i class="bi bi-star"></i> Đặt mặc định
                                                                </button>
                                                            </form>
                                                        </c:if>
                                                        <button type="button" class="btn btn-sm btn-outline-primary mb-1 edit-address-btn" 
                                                                data-bs-toggle="modal" 
                                                                data-bs-target="#addressModal" 
                                                                data-address-id="${addr.addressID}"
                                                                data-full-name="${addr.fullName != null ? addr.fullName : ''}"
                                                                data-phone="${addr.phone != null ? addr.phone : ''}"
                                                                data-line1="${addr.line1 != null ? addr.line1 : ''}"
                                                                data-line2="${addr.line2 != null ? addr.line2 : ''}"
                                                                data-city="${addr.city != null ? addr.city : ''}"
                                                                data-district="${addr.district != null ? addr.district : ''}"
                                                                data-ward="${addr.ward != null ? addr.ward : ''}"
                                                                data-country="${addr.country != null ? addr.country : 'Việt Nam'}"
                                                                data-postal-code="${addr.postalCode != null ? addr.postalCode : ''}"
                                                                data-is-default="${addr.default}">
                                                            <i class="bi bi-pencil"></i> Sửa
                                                        </button>
                                                        <form method="post" action="${pageContext.request.contextPath}/customer/profile" 
                                                              class="d-inline" onsubmit="return confirm('Bạn có chắc muốn xóa địa chỉ này?');">
                                                            <input type="hidden" name="action" value="deleteAddress">
                                                            <input type="hidden" name="addressID" value="${addr.addressID}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                                <i class="bi bi-trash"></i> Xóa
                                                            </button>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-info text-center">
                                        <i class="bi bi-info-circle"></i> Bạn chưa có địa chỉ nào.
                                        <br>
                                        <button type="button" class="btn btn-primary btn-sm mt-2" 
                                                data-bs-toggle="modal" data-bs-target="#addressModal" 
                                                onclick="openAddAddressModal()">
                                            <i class="bi bi-plus-circle"></i> Thêm địa chỉ đầu tiên
                                        </button>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Tab 3: Đổi mật khẩu -->
                        <div class="tab-pane fade" id="password" role="tabpanel">
                            <form method="post" action="${pageContext.request.contextPath}/customer/profile" 
                                  onsubmit="return validatePasswordForm()">
                                <input type="hidden" name="action" value="changePassword">
                                
                                <div class="mb-3">
                                    <label for="currentPassword" class="form-label">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" id="currentPassword" 
                                           name="currentPassword" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" id="newPassword" 
                                           name="newPassword" required minlength="6">
                                    <small class="text-muted">Mật khẩu phải có ít nhất 6 ký tự</small>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" id="confirmPassword" 
                                           name="confirmPassword" required minlength="6">
                                </div>
                                
                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-shield-check"></i> Đổi mật khẩu
                                    </button>
                                </div>
                            </form>
                        </div>

                        <!-- Tab 4: Lịch sử đơn hàng -->
                        <div class="tab-pane fade" id="orders" role="tabpanel">
                            <h5 class="mb-3">Đơn hàng của tôi</h5>
                            <c:choose>
                                <c:when test="${not empty orders && orders.size() > 0}">
                                    <c:forEach var="order" items="${orders}">
                                        <div class="order-item">
                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                <div>
                                                    <h6 class="mb-1">Đơn hàng #${order.orderID}</h6>
                                                    <small class="text-muted">
                                                        <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" />
                                                    </small>
                                                </div>
                                                <span class="badge bg-primary status-badge">${order.orderStatus}</span>
                                            </div>
                                            <div class="d-flex justify-content-between align-items-center">
                                                <div>
                                                    <strong>Tổng tiền: 
                                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" 
                                                                          currencySymbol="₫" maxFractionDigits="0"/>
                                                    </strong>
                                                </div>
                                                <a href="${pageContext.request.contextPath}/customer/orders?orderID=${order.orderID}" 
                                                   class="btn btn-sm btn-outline-primary">
                                                    <i class="bi bi-eye"></i> Xem chi tiết
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-info text-center">
                                        <i class="bi bi-bag-x"></i> Bạn chưa có đơn hàng nào.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-warning">
                        Không thể tải thông tin người dùng.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Address Modal -->
    <div class="modal fade" id="addressModal" tabindex="-1" aria-labelledby="addressModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addressModalLabel">Thêm địa chỉ mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/customer/profile" id="addressForm">
                    <input type="hidden" name="action" id="addressAction" value="addAddress">
                    <input type="hidden" name="addressID" id="addressID">
                    
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="addrFullName" class="form-label">Họ tên người nhận <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addrFullName" name="fullName" required>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="addrPhone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addrPhone" name="phone" required>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="addrLine1" class="form-label">Địa chỉ (số nhà, tên đường) <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="addrLine1" name="line1" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="addrLine2" class="form-label">Địa chỉ phụ (tùy chọn)</label>
                            <input type="text" class="form-control" id="addrLine2" name="line2">
                        </div>
                        
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="addrWard" class="form-label">Phường/Xã</label>
                                <input type="text" class="form-control" id="addrWard" name="ward">
                            </div>
                            
                            <div class="col-md-4 mb-3">
                                <label for="addrDistrict" class="form-label">Quận/Huyện</label>
                                <input type="text" class="form-control" id="addrDistrict" name="district">
                            </div>
                            
                            <div class="col-md-4 mb-3">
                                <label for="addrCity" class="form-label">Tỉnh/Thành phố <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addrCity" name="city" required>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="addrCountry" class="form-label">Quốc gia <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addrCountry" name="country" 
                                       value="Việt Nam" required>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="addrPostalCode" class="form-label">Mã bưu điện</label>
                                <input type="text" class="form-control" id="addrPostalCode" name="postalCode">
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="addrIsDefault" name="isDefault" value="true">
                                <label class="form-check-label" for="addrIsDefault">
                                    Đặt làm địa chỉ mặc định
                                </label>
                            </div>
                        </div>
                    </div>
                    
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Lưu địa chỉ
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function openAddAddressModal() {
            document.getElementById('addressModalLabel').textContent = 'Thêm địa chỉ mới';
            document.getElementById('addressAction').value = 'addAddress';
            document.getElementById('addressID').value = '';
            document.getElementById('addressForm').reset();
            document.getElementById('addrCountry').value = 'Việt Nam';
        }

        function openEditAddressModal(button) {
            const btn = button;
            document.getElementById('addressModalLabel').textContent = 'Sửa địa chỉ';
            document.getElementById('addressAction').value = 'updateAddress';
            document.getElementById('addressID').value = btn.getAttribute('data-address-id') || '';
            document.getElementById('addrFullName').value = btn.getAttribute('data-full-name') || '';
            document.getElementById('addrPhone').value = btn.getAttribute('data-phone') || '';
            document.getElementById('addrLine1').value = btn.getAttribute('data-line1') || '';
            document.getElementById('addrLine2').value = btn.getAttribute('data-line2') || '';
            document.getElementById('addrCity').value = btn.getAttribute('data-city') || '';
            document.getElementById('addrDistrict').value = btn.getAttribute('data-district') || '';
            document.getElementById('addrWard').value = btn.getAttribute('data-ward') || '';
            document.getElementById('addrCountry').value = btn.getAttribute('data-country') || 'Việt Nam';
            document.getElementById('addrPostalCode').value = btn.getAttribute('data-postal-code') || '';
            document.getElementById('addrIsDefault').checked = btn.getAttribute('data-is-default') === 'true';
        }
        
        // Event listeners for edit address buttons
        document.addEventListener('DOMContentLoaded', function() {
            const editButtons = document.querySelectorAll('.edit-address-btn');
            editButtons.forEach(function(btn) {
                btn.addEventListener('click', function() {
                    openEditAddressModal(this);
                    const modal = new bootstrap.Modal(document.getElementById('addressModal'));
                    modal.show();
                });
            });
        });

        function validatePasswordForm() {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (newPassword !== confirmPassword) {
                alert('Mật khẩu mới và xác nhận không khớp!');
                return false;
            }
            
            if (newPassword.length < 6) {
                alert('Mật khẩu phải có ít nhất 6 ký tự!');
                return false;
            }
            
            return true;
        }

        // Auto-dismiss alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html>

