<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gửi yêu cầu hỗ trợ - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        .support-form-container {
            padding: 3rem 0;
            max-width: 800px;
            margin: 0 auto;
        }
        
        .form-section {
            background: #2c2c2c;
            border-radius: 15px;
            padding: 3rem;
        }
        
        .form-title {
            font-size: 2rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
        }
        
        .form-subtitle {
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
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Gửi yêu cầu hỗ trợ" />
    </jsp:include>

    <div class="support-form-container">
        <div class="container">
            <div class="form-section">
                <h1 class="form-title"><i class="bi bi-headset"></i> Gửi yêu cầu hỗ trợ</h1>
                <p class="form-subtitle">Chúng tôi sẽ phản hồi yêu cầu của bạn trong thời gian sớm nhất</p>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <form method="post" action="${pageContext.request.contextPath}/support-request">
                    <input type="hidden" name="action" value="create">
                    
                    <div class="mb-4">
                        <label for="subject" class="form-label">
                            Tiêu đề <span class="required-asterisk">*</span>
                        </label>
                        <input type="text" 
                               class="form-control" 
                               id="subject" 
                               name="subject" 
                               placeholder="Nhập tiêu đề yêu cầu hỗ trợ" 
                               required
                               maxlength="255"
                               value="${subject != null ? subject : ''}">
                    </div>
                    
                    <div class="mb-4">
                        <label for="message" class="form-label">
                            Nội dung <span class="required-asterisk">*</span>
                        </label>
                        <textarea class="form-control" 
                                  id="message" 
                                  name="message" 
                                  rows="8" 
                                  placeholder="Mô tả chi tiết vấn đề hoặc yêu cầu của bạn..." 
                                  required
                                  maxlength="2000">${message != null ? message : ''}</textarea>
                        <small class="text-muted">Tối đa 2000 ký tự</small>
                    </div>
                    
                    <div class="d-flex gap-3">
                        <button type="submit" class="submit-btn">
                            <i class="bi bi-send"></i> Gửi yêu cầu
                        </button>
                        <a href="${pageContext.request.contextPath}/support-request" class="btn btn-secondary" style="border-radius: 25px; padding: 0.75rem 2rem;">
                            <i class="bi bi-arrow-left"></i> Quay lại
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

