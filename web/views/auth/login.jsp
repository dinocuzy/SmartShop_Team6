<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .login-container {
            min-height: calc(100vh - 400px);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 3rem 0;
        }
        
        .login-card {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 3rem;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
            width: 100%;
            max-width: 500px;
        }
        
        .login-title {
            font-size: 2rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
        }
        
        .login-subtitle {
            color: #b0b0b0;
            margin-bottom: 2rem;
        }
        
        .login-link {
            color: #8b5cf6;
            text-decoration: none;
        }
        
        .login-link:hover {
            color: #a78bfa;
            text-decoration: underline;
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
        
        .forgot-password-link {
            color: #8b5cf6;
            text-decoration: none;
            font-size: 0.9rem;
        }
        
        .forgot-password-link:hover {
            color: #a78bfa;
            text-decoration: underline;
        }
        
        .login-btn {
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
        
        .login-btn:hover {
            background: #c82333;
            color: white;
        }
        
        .alert {
            border-radius: 10px;
            margin-bottom: 1.5rem;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Đăng nhập tài khoản" />
    </jsp:include>

    <!-- Login Form -->
    <div class="login-container">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
        <div class="login-card">
                        <h1 class="login-title">Đăng nhập tài khoản</h1>
                        <p class="login-subtitle">
                            Bạn chưa có tài khoản? 
                            <a href="${pageContext.request.contextPath}/register" class="login-link">Đăng ký tại đây</a>
                        </p>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="bi bi-check-circle"></i> ${successMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <form method="post" action="${pageContext.request.contextPath}/login">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email <span style="color: #dc3545;">*</span></label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       placeholder="Email" required 
                                       value="${param.email != null ? param.email : ''}">
                            </div>
                            
                            <div class="mb-3">
                                <label for="password" class="form-label">Mật khẩu <span style="color: #dc3545;">*</span></label>
                                <input type="password" class="form-control" id="password" name="password" 
                                       placeholder="Mật khẩu" required>
                    </div>
                    
                            <div class="mb-3">
                                <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password-link">
                                    Quên mật khẩu? Nhấn vào đây
                                </a>
                    </div>
                    
                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
                                <label class="form-check-label" for="rememberMe" style="color: #b0b0b0;">
                                    Ghi nhớ đăng nhập
                        </label>
                    </div>
                    
                            <button type="submit" class="login-btn">
                                Đăng nhập
                        </button>
                        </form>
                        
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
