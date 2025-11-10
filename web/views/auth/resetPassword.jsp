<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .reset-password-container {
            min-height: calc(100vh - 400px);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 3rem 0;
        }
        
        .reset-password-card {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 3rem;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
            width: 100%;
            max-width: 500px;
        }
        
        .reset-password-title {
            font-size: 2rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
        }
        
        .reset-password-subtitle {
            color: #b0b0b0;
            margin-bottom: 2rem;
        }
        
        .form-label {
            color: white;
            font-weight: 500;
            margin-bottom: 0.5rem;
        }
        
        .form-control, .form-control:focus {
            background-color: #1a1a1a;
            border: 1px solid #4a4a4a;
            color: white;
            padding: 0.75rem 1rem;
        }
        
        .form-control:focus {
            border-color: #8b5cf6;
            box-shadow: 0 0 0 0.2rem rgba(139, 92, 246, 0.25);
        }
        
        .form-control::placeholder {
            color: #6a6a6a;
        }
        
        .reset-password-btn {
            background: #dc3545;
            color: white;
            border: none;
            border-radius: 25px;
            padding: 0.75rem 2rem;
            font-size: 1.1rem;
            font-weight: bold;
            width: 100%;
            margin-top: 1.5rem;
            transition: background 0.3s;
        }
        
        .reset-password-btn:hover {
            background: #c82333;
            color: white;
        }
        
        .alert {
            border-radius: 10px;
            margin-bottom: 1.5rem;
        }
        
        .back-to-login-link {
            color: #8b5cf6;
            text-decoration: none;
            display: block;
            text-align: center;
            margin-top: 1.5rem;
        }
        
        .back-to-login-link:hover {
            color: #a78bfa;
            text-decoration: underline;
        }
        
        .email-info {
            background: #1a1a1a;
            border: 1px solid #4a4a4a;
            padding: 0.75rem 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            color: #b0b0b0;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Đặt lại mật khẩu" />
    </jsp:include>

    <!-- Reset Password Form -->
    <div class="reset-password-container">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <div class="reset-password-card">
                        <h1 class="reset-password-title">Đặt lại mật khẩu</h1>
                        <p class="reset-password-subtitle">
                            Nhập mật khẩu mới cho tài khoản của bạn
                        </p>
                
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i> <c:out value="${errorMessage}"/>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="bi bi-check-circle"></i> <c:out value="${successMessage}"/>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                            <div class="text-center">
                                <a href="${pageContext.request.contextPath}/login" class="reset-password-btn" style="display: inline-block; text-decoration: none;">
                                    Đăng nhập ngay
                                </a>
                            </div>
                        </c:if>
                
                        <c:if test="${not empty token && empty successMessage}">
                            <form method="post" action="${pageContext.request.contextPath}/reset-password" id="resetPasswordForm">
                                <input type="hidden" name="token" value="${token}">
                                
                                <c:if test="${not empty email}">
                                    <div class="email-info">
                                        <i class="bi bi-envelope"></i> Email: <c:out value="${email}"/>
                                    </div>
                                </c:if>
                                
                                <div class="mb-3">
                                    <label for="password" class="form-label">
                                        Mật khẩu mới <span style="color: #dc3545;">*</span>
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="password" 
                                           name="password" 
                                           placeholder="Nhập mật khẩu mới" 
                                           required 
                                           minlength="6"
                                           autofocus>
                                    <small class="text-muted">Tối thiểu 6 ký tự</small>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">
                                        Xác nhận mật khẩu <span style="color: #dc3545;">*</span>
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="confirmPassword" 
                                           name="confirmPassword" 
                                           placeholder="Nhập lại mật khẩu" 
                                           required 
                                           minlength="6">
                                </div>
                                
                                <button type="submit" class="reset-password-btn">
                                    Đặt lại mật khẩu
                                </button>
                            </form>
                        </c:if>
                
                        <c:if test="${empty token && empty successMessage}">
                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle"></i> Token không hợp lệ hoặc đã hết hạn.
                            </div>
                            <div class="text-center">
                                <a href="${pageContext.request.contextPath}/forgot-password" class="reset-password-btn" style="display: inline-block; text-decoration: none;">
                                    Yêu cầu link mới
                                </a>
                            </div>
                        </c:if>
                        
                        <c:if test="${empty successMessage}">
                            <a href="${pageContext.request.contextPath}/login" class="back-to-login-link">
                                <i class="bi bi-arrow-left"></i> Quay lại đăng nhập
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Validate password match
        const form = document.getElementById('resetPasswordForm');
        if (form) {
            form.addEventListener('submit', function(e) {
                const password = document.getElementById('password').value;
                const confirmPassword = document.getElementById('confirmPassword').value;
                
                if (password !== confirmPassword) {
                    e.preventDefault();
                    alert('Mật khẩu xác nhận không khớp!');
                    return false;
                }
            });
        }
    </script>
</body>
</html>

