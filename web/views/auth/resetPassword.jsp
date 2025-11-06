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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .reset-password-container {
            width: 100%;
            max-width: 450px;
            padding: 2rem;
        }
        
        .reset-password-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            overflow: hidden;
        }
        
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem;
            text-align: center;
        }
        
        .card-header h3 {
            margin: 0;
            font-weight: 600;
        }
        
        .card-body {
            padding: 2rem;
        }
        
        .form-floating {
            margin-bottom: 1rem;
        }
        
        .btn-submit {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 0.75rem;
            font-weight: 600;
            transition: transform 0.2s;
        }
        
        .btn-submit:hover {
            transform: translateY(-2px);
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
        }
        
        .alert {
            border-radius: 10px;
        }
    </style>
</head>
<body>
    <div class="reset-password-container">
        <div class="reset-password-card">
            <div class="card-header">
                <h3><i class="bi bi-key"></i> Đặt lại mật khẩu</h3>
                <p class="mb-0 mt-2">Nhập mật khẩu mới của bạn</p>
            </div>
            
            <div class="card-body">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty token}">
                    <form method="post" action="${pageContext.request.contextPath}/reset-password" id="resetPasswordForm">
                        <input type="hidden" name="token" value="${token}">
                        
                        <c:if test="${not empty email}">
                            <div class="alert alert-info">
                                <i class="bi bi-envelope"></i> Email: ${email}
                            </div>
                        </c:if>
                        
                        <div class="form-floating mb-3">
                            <input type="password" 
                                   class="form-control" 
                                   id="password" 
                                   name="password" 
                                   placeholder="Password" 
                                   required 
                                   minlength="6"
                                   autofocus>
                            <label for="password">
                                <i class="bi bi-lock"></i> Mật khẩu mới
                            </label>
                            <small class="text-muted">Tối thiểu 6 ký tự</small>
                        </div>
                        
                        <div class="form-floating mb-3">
                            <input type="password" 
                                   class="form-control" 
                                   id="confirmPassword" 
                                   name="confirmPassword" 
                                   placeholder="Confirm Password" 
                                   required 
                                   minlength="6">
                            <label for="confirmPassword">
                                <i class="bi bi-lock-fill"></i> Xác nhận mật khẩu
                            </label>
                        </div>
                        
                        <div class="d-grid mb-3">
                            <button type="submit" class="btn btn-primary btn-submit">
                                <i class="bi bi-check-circle"></i> Đặt lại mật khẩu
                            </button>
                        </div>
                    </form>
                </c:if>
                
                <c:if test="${empty token}">
                    <div class="alert alert-warning">
                        <i class="bi bi-exclamation-triangle"></i> Token không hợp lệ hoặc đã hết hạn.
                    </div>
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/forgot-password" class="btn btn-outline-primary">
                            Yêu cầu link mới
                        </a>
                    </div>
                </c:if>
                
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                        <i class="bi bi-arrow-left"></i> Quay lại đăng nhập
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Validate password match
        document.getElementById('resetPasswordForm')?.addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Mật khẩu xác nhận không khớp!');
                return false;
            }
        });
        
        // Auto-dismiss alert
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

