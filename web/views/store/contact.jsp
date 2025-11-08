<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liên hệ - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .contact-container {
            padding: 3rem 0;
        }
        
        .company-info-section {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 3rem;
            margin-bottom: 3rem;
        }
        
        .company-name {
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
            margin-bottom: 2rem;
        }
        
        .info-item {
            display: flex;
            align-items: flex-start;
            gap: 1rem;
            margin-bottom: 1.5rem;
        }
        
        .info-icon {
            font-size: 1.5rem;
            color: #dc3545;
            margin-top: 0.25rem;
        }
        
        .info-label {
            font-weight: 600;
            color: white;
            margin-bottom: 0.25rem;
        }
        
        .info-value {
            color: #b0b0b0;
        }
        
        .info-value.hotline {
            color: #dc3545;
            font-weight: bold;
        }
        
        .contact-form-section {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 3rem;
        }
        
        .form-title {
            font-size: 2rem;
            font-weight: bold;
            color: white;
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
            border-color: #dc3545;
            box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
        }
        
        .form-control::placeholder {
            color: #6a6a6a;
        }
        
        .required-asterisk {
            color: #dc3545;
        }
        
        .submit-btn {
            background: #dc3545;
            color: white;
            border: none;
            border-radius: 25px;
            padding: 0.75rem 2rem;
            font-size: 1.1rem;
            font-weight: bold;
            width: 100%;
            transition: background 0.3s;
        }
        
        .submit-btn:hover {
            background: #c82333;
            color: white;
        }
        
        .alert {
            border-radius: 10px;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp">
        <jsp:param name="active" value="contact" />
    </jsp:include>
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Liên hệ" />
    </jsp:include>

    <div class="contact-container">
        <div class="container">
            <!-- Company Information Section -->
            <div class="company-info-section">
                <h1 class="company-name">Công ty TNHH SmartShop</h1>
                
                <div class="info-item">
                    <i class="bi bi-geo-alt-fill info-icon"></i>
                    <div>
                        <div class="info-label">Địa chỉ</div>
                        <div class="info-value">Đà Nẵng, Việt Nam</div>
                    </div>
                </div>
                
                <div class="info-item">
                    <i class="bi bi-telephone-fill info-icon"></i>
                    <div>
                        <div class="info-label">Hotline</div>
                        <div class="info-value hotline">0833347220</div>
                    </div>
                </div>
                
                <div class="info-item">
                    <i class="bi bi-envelope-fill info-icon"></i>
                    <div>
                        <div class="info-label">Email</div>
                        <div class="info-value" id="contactEmail">smartshop686868@gmail.com</div>
                    </div>
                </div>
            </div>
            
            <!-- Contact Form Section -->
            <div class="contact-form-section">
                <h2 class="form-title">Liên hệ với chúng tôi</h2>
                
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
                
                <form method="post" action="${pageContext.request.contextPath}/contact" id="contactForm">
                    <div class="mb-3">
                        <label for="fullName" class="form-label">
                            Họ tên<span class="required-asterisk">*</span>
                        </label>
                        <input type="text" class="form-control" id="fullName" name="fullName" 
                               placeholder="Nhập họ tên của bạn" required
                               value="${fullName != null ? fullName : (param.fullName != null ? param.fullName : '')}">
                    </div>
                    
                    <div class="mb-3">
                        <label for="email" class="form-label">
                            Email<span class="required-asterisk">*</span>
                        </label>
                        <input type="email" class="form-control" id="email" name="email" 
                               placeholder="Nhập email của bạn" required
                               value="${email != null ? email : (param.email != null ? param.email : '')}">
                    </div>
                    
                    <div class="mb-3">
                        <label for="phone" class="form-label">
                            Số điện thoại<span class="required-asterisk">*</span>
                        </label>
                        <input type="tel" class="form-control" id="phone" name="phone" 
                               placeholder="Nhập số điện thoại của bạn" required
                               value="${phone != null ? phone : (param.phone != null ? param.phone : '')}">
                    </div>
                    
                    <div class="mb-4">
                        <label for="content" class="form-label">
                            Nhập nội dung<span class="required-asterisk">*</span>
                        </label>
                        <textarea class="form-control" id="content" name="content" rows="6" 
                                  placeholder="Nhập nội dung liên hệ của bạn" required>${content != null ? content : (param.content != null ? param.content : '')}</textarea>
                    </div>
                    
                    <button type="submit" class="submit-btn">
                        Gửi liên hệ của bạn
                    </button>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

