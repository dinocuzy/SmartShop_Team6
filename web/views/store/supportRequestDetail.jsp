<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết yêu cầu hỗ trợ - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .detail-container {
            padding: 3rem 0;
            max-width: 900px;
            margin: 0 auto;
        }
        
        .detail-card {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .status-badge {
            padding: 8px 20px;
            border-radius: 25px;
            font-size: 1rem;
            font-weight: 600;
        }
        
        .status-open {
            background: #ffc107;
            color: #000;
        }
        
        .status-inprogress {
            background: #17a2b8;
            color: #fff;
        }
        
        .status-closed {
            background: #28a745;
            color: #fff;
        }
        
        .info-item {
            margin-bottom: 1rem;
        }
        
        .info-label {
            font-weight: 600;
            color: #b0b0b0;
            margin-bottom: 0.25rem;
        }
        
        .info-value {
            color: #fff;
        }
        
        .message-box {
            background: #1a1a1a;
            border: 1px solid #4a4a4a;
            border-radius: 10px;
            padding: 1.5rem;
            margin-top: 1rem;
            white-space: pre-wrap;
            word-wrap: break-word;
        }
        
        .alert {
            border-radius: 10px;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Chi tiết yêu cầu hỗ trợ" />
    </jsp:include>

    <div class="detail-container">
        <div class="container">
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% session.removeAttribute("successMessage"); %>
            </c:if>
            
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${sessionScope.errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% session.removeAttribute("errorMessage"); %>
            </c:if>
            
            <c:if test="${empty supportRequest}">
                <div class="alert alert-warning">
                    Không tìm thấy yêu cầu hỗ trợ
                </div>
                <a href="${pageContext.request.contextPath}/support-request" class="btn btn-primary">
                    <i class="bi bi-arrow-left"></i> Quay lại
                </a>
            </c:if>
            
            <c:if test="${not empty supportRequest}">
                <div class="detail-card">
                    <div class="d-flex justify-content-between align-items-start mb-4">
                        <div>
                            <h2 class="mb-2">${supportRequest.subject}</h2>
                            <span class="status-badge status-${supportRequest.status.toLowerCase()}">
                                <c:choose>
                                    <c:when test="${supportRequest.status == 'Open'}">Đang mở</c:when>
                                    <c:when test="${supportRequest.status == 'InProgress'}">Đang xử lý</c:when>
                                    <c:when test="${supportRequest.status == 'Closed'}">Đã đóng</c:when>
                                    <c:otherwise>${supportRequest.status}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/support-request" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> Quay lại
                            </a>
                        </div>
                    </div>
                    
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">Người gửi</div>
                                <div class="info-value">${supportRequest.userName != null ? supportRequest.userName : 'N/A'}</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">Email</div>
                                <div class="info-value">${supportRequest.userEmail != null ? supportRequest.userEmail : 'N/A'}</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">Ngày gửi</div>
                                <div class="info-value">
                                    <fmt:formatDate value="${supportRequest.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="info-item">
                                <div class="info-label">Trạng thái</div>
                                <div class="info-value">
                                    <span class="status-badge status-${supportRequest.status.toLowerCase()}">
                                        <c:choose>
                                            <c:when test="${supportRequest.status == 'Open'}">Đang mở</c:when>
                                            <c:when test="${supportRequest.status == 'InProgress'}">Đang xử lý</c:when>
                                            <c:when test="${supportRequest.status == 'Closed'}">Đã đóng</c:when>
                                            <c:otherwise>${supportRequest.status}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-label">Nội dung</div>
                        <div class="message-box">${supportRequest.message}</div>
                    </div>
                </div>
                
                <c:if test="${isAdmin}">
                    <div class="detail-card">
                        <h4 class="mb-3">Cập nhật trạng thái</h4>
                        <form method="post" action="${pageContext.request.contextPath}/support-request">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="requestID" value="${supportRequest.requestID}">
                            
                            <div class="row">
                                <div class="col-md-8">
                                    <select name="status" class="form-select" style="background: #1a1a1a; color: #fff; border: 1px solid #4a4a4a;">
                                        <option value="Open" ${supportRequest.status == 'Open' ? 'selected' : ''}>Đang mở</option>
                                        <option value="InProgress" ${supportRequest.status == 'InProgress' ? 'selected' : ''}>Đang xử lý</option>
                                        <option value="Closed" ${supportRequest.status == 'Closed' ? 'selected' : ''}>Đã đóng</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <button type="submit" class="btn btn-primary w-100">
                                        <i class="bi bi-check-circle"></i> Cập nhật
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </c:if>
            </c:if>
        </div>
    </div>
    
    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

