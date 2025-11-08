<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yêu cầu hỗ trợ - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .support-list-container {
            padding: 3rem 0;
        }
        
        .list-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
        }
        
        .stats-card {
            background: #2c2c2c;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            border-left: 4px solid;
        }
        
        .stats-card.open {
            border-left-color: #ffc107;
        }
        
        .stats-card.inprogress {
            border-left-color: #17a2b8;
        }
        
        .stats-card.closed {
            border-left-color: #28a745;
        }
        
        .request-card {
            background: #2c2c2c;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            border-left: 4px solid;
            transition: transform 0.3s, box-shadow 0.3s;
        }
        
        .request-card:hover {
            transform: translateX(5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        
        .request-card.open {
            border-left-color: #ffc107;
        }
        
        .request-card.inprogress {
            border-left-color: #17a2b8;
        }
        
        .request-card.closed {
            border-left-color: #28a745;
        }
        
        .status-badge {
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.85rem;
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
        
        .alert {
            border-radius: 10px;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Yêu cầu hỗ trợ" />
    </jsp:include>

    <div class="support-list-container">
        <div class="container">
            <div class="list-header text-center">
                <h1><i class="bi bi-headset"></i> Yêu cầu hỗ trợ</h1>
                <p class="mb-0">Quản lý các yêu cầu hỗ trợ của bạn</p>
            </div>
            
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
            
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <a href="${pageContext.request.contextPath}/support-request?action=create" class="btn btn-primary">
                        <i class="bi bi-plus-circle"></i> Tạo yêu cầu mới
                    </a>
                </div>
            </div>
            
            <c:if test="${isAdmin}">
                <div class="row mb-4">
                    <div class="col-md-4">
                        <div class="stats-card open">
                            <h5><i class="bi bi-exclamation-circle"></i> Đang mở</h5>
                            <h2>${openCount}</h2>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stats-card inprogress">
                            <h5><i class="bi bi-clock-history"></i> Đang xử lý</h5>
                            <h2>${inProgressCount}</h2>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stats-card closed">
                            <h5><i class="bi bi-check-circle"></i> Đã đóng</h5>
                            <h2>${closedCount}</h2>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${empty requests or requests.size() == 0}">
                    <div class="text-center py-5">
                        <i class="bi bi-inbox" style="font-size: 4rem; color: #6a6a6a;"></i>
                        <h3 class="mt-3">Chưa có yêu cầu hỗ trợ</h3>
                        <p class="text-muted">Bạn chưa có yêu cầu hỗ trợ nào</p>
                        <a href="${pageContext.request.contextPath}/support-request?action=create" class="btn btn-primary mt-3">
                            <i class="bi bi-plus-circle"></i> Tạo yêu cầu mới
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="request" items="${requests}">
                        <div class="request-card ${request.status.toLowerCase()}">
                            <div class="d-flex justify-content-between align-items-start">
                                <div class="flex-grow-1">
                                    <h5 class="mb-2">
                                        <a href="${pageContext.request.contextPath}/support-request?action=view&id=${request.requestID}" 
                                           class="text-white text-decoration-none">
                                            ${request.subject}
                                        </a>
                                    </h5>
                                    <p class="text-muted mb-2" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                                        ${request.message}
                                    </p>
                                    <div class="d-flex gap-3 align-items-center">
                                        <span class="status-badge status-${request.status.toLowerCase()}">
                                            <c:choose>
                                                <c:when test="${request.status == 'Open'}">Đang mở</c:when>
                                                <c:when test="${request.status == 'InProgress'}">Đang xử lý</c:when>
                                                <c:when test="${request.status == 'Closed'}">Đã đóng</c:when>
                                                <c:otherwise>${request.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                        <small class="text-muted">
                                            <i class="bi bi-calendar"></i> 
                                            <fmt:formatDate value="${request.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                        </small>
                                        <c:if test="${isAdmin}">
                                            <small class="text-muted">
                                                <i class="bi bi-person"></i> ${request.userName != null ? request.userName : 'N/A'}
                                            </small>
                                        </c:if>
                                    </div>
                                </div>
                                <div>
                                    <a href="${pageContext.request.contextPath}/support-request?action=view&id=${request.requestID}" 
                                       class="btn btn-sm btn-outline-light">
                                        <i class="bi bi-eye"></i> Xem chi tiết
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

