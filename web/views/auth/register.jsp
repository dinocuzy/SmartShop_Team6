<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .register-container {
            min-height: calc(100vh - 400px);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 3rem 0;
        }
        
        .register-card {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 3rem;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
            width: 100%;
            max-width: 600px;
        }
        
        .register-title {
            font-size: 2rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
        }
        
        .register-subtitle {
            color: #b0b0b0;
            margin-bottom: 2rem;
        }
        
        .register-link {
            color: #8b5cf6;
            text-decoration: none;
        }
        
        .register-link:hover {
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
        
        .register-btn {
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
        
        .register-btn:hover {
            background: #c82333;
            color: white;
        }
        
        .alert {
            border-radius: 10px;
            margin-bottom: 1.5rem;
        }
        
        .password-strength {
            font-size: 0.85rem;
            margin-top: 0.25rem;
        }
        
        .divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 1.5rem 0;
        }
        
        .divider::before,
        .divider::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid #4a4a4a;
        }
        
        .divider span {
            padding: 0 1rem;
            color: #b0b0b0;
            font-size: 0.9rem;
        }
        
        .google-login-btn {
            width: 100%;
            padding: 0.75rem 1rem;
            background-color: white;
            color: #333;
            border: 1px solid #dadce0;
            border-radius: 25px;
            font-size: 1rem;
            font-weight: 500;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.75rem;
            transition: all 0.3s;
            text-decoration: none;
            margin-top: 1rem;
        }
        
        .google-login-btn:hover {
            background-color: #f8f9fa;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            color: #333;
            text-decoration: none;
        }
        
        .google-login-btn img {
            width: 20px;
            height: 20px;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Đăng ký tài khoản" />
    </jsp:include>

    <!-- Register Form -->
    <div class="register-container">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 col-lg-6">
                    <div class="register-card">
                        <h1 class="register-title">Đăng ký tài khoản</h1>
                        <p class="register-subtitle">
                            Bạn đã có tài khoản? 
                            <a href="${pageContext.request.contextPath}/login" class="register-link">Đăng nhập tại đây</a>
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
                        
                        <form method="post" action="${pageContext.request.contextPath}/register" id="registerForm">
                            <div class="row">
                                <div class="col-md-12 mb-3">
                                    <label for="fullName" class="form-label">Họ và tên <span style="color: #dc3545;">*</span></label>
                                    <input type="text" class="form-control" id="fullName" name="fullName" 
                                           placeholder="Nhập họ và tên" required 
                                           value="${fullName != null ? fullName : (param.fullName != null ? param.fullName : '')}">
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-12 mb-3">
                                    <label for="email" class="form-label">Email <span style="color: #dc3545;">*</span></label>
                                    <input type="email" class="form-control" id="email" name="email" 
                                           placeholder="Email" required 
                                           value="${email != null ? email : (param.email != null ? param.email : '')}">
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-12 mb-3">
                                    <label for="phone" class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control" id="phone" name="phone" 
                                           placeholder="Số điện thoại (tùy chọn)" 
                                           value="${phone != null ? phone : (param.phone != null ? param.phone : '')}">
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="password" class="form-label">Mật khẩu <span style="color: #dc3545;">*</span></label>
                                    <input type="password" class="form-control" id="password" name="password" 
                                           placeholder="Mật khẩu (tối thiểu 6 ký tự)" required minlength="6">
                                    <small class="password-strength text-muted">Mật khẩu phải có ít nhất 6 ký tự</small>
                                </div>
                                
                                <div class="col-md-6 mb-3">
                                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu <span style="color: #dc3545;">*</span></label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                           placeholder="Nhập lại mật khẩu" required minlength="6">
                                </div>
                            </div>
                            
                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="agreeTerms" required>
                                <label class="form-check-label" for="agreeTerms" style="color: #b0b0b0;">
                                    Tôi đồng ý với <a href="${pageContext.request.contextPath}/policy/privacy" class="register-link">Chính sách bảo mật</a> 
                                    và <a href="${pageContext.request.contextPath}/policy/terms" class="register-link">Điều khoản sử dụng</a>
                                </label>
                            </div>
                            
                            <button type="submit" class="register-btn">
                                Đăng ký
                            </button>
                        </form>
                        
                        <!-- Divider -->
                        <div class="divider">
                            <span>Hoặc</span>
                        </div>
                        
                        <!-- Google Login Button -->
                        <a href="${pageContext.request.contextPath}/auth/google/login" class="google-login-btn">
                            <svg width="20" height="20" viewBox="0 0 24 24">
                                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                            </svg>
                            <span>Đăng ký với Google</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Validate password match
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Mật khẩu xác nhận không khớp!');
                return false;
            }
            
            if (password.length < 6) {
                e.preventDefault();
                alert('Mật khẩu phải có ít nhất 6 ký tự!');
                return false;
            }
        });
        
        // Real-time password match validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            
            if (confirmPassword && password !== confirmPassword) {
                this.setCustomValidity('Mật khẩu xác nhận không khớp');
            } else {
                this.setCustomValidity('');
            }
        });
    </script>
</body>
</html>

