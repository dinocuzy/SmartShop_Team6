<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${policyType == 'warranty'}">Chính sách bảo hành - SmartShop</c:when>
            <c:when test="${policyType == 'return'}">Chính sách đổi trả - SmartShop</c:when>
            <c:when test="${policyType == 'privacy'}">Chính sách bảo mật - SmartShop</c:when>
            <c:when test="${policyType == 'terms'}">Điều khoản sử dụng - SmartShop</c:when>
            <c:when test="${policyType == 'installment'}">Chính sách trả góp - SmartShop</c:when>
            <c:otherwise>Chính sách - SmartShop</c:otherwise>
        </c:choose>
    </title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #ffffff;
            min-height: 100vh;
        }
        
        .policy-container {
            padding: 3rem 0;
            background-color: #1a1a1a;
        }
        
        .policy-content {
            background-color: #2c2c2c;
            border-radius: 15px;
            padding: 3rem;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
        }
        
        .policy-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 3px solid #dc3545;
        }
        
        .policy-section {
            margin-bottom: 2rem;
        }
        
        .policy-section h3 {
            color: #8b5cf6;
            font-size: 1.5rem;
            margin-bottom: 1rem;
            margin-top: 2rem;
        }
        
        .policy-section h4 {
            color: #a78bfa;
            font-size: 1.2rem;
            margin-bottom: 0.75rem;
            margin-top: 1.5rem;
        }
        
        .policy-section p {
            color: #cccccc;
            line-height: 1.8;
            margin-bottom: 1rem;
        }
        
        .policy-section ul, .policy-section ol {
            color: #cccccc;
            line-height: 1.8;
            margin-left: 2rem;
            margin-bottom: 1rem;
        }
        
        .policy-section li {
            margin-bottom: 0.5rem;
        }
        
        .highlight-box {
            background-color: #3a3a3a;
            border-left: 4px solid #dc3545;
            padding: 1.5rem;
            margin: 1.5rem 0;
            border-radius: 5px;
        }
        
        .contact-info {
            background-color: #2c2c2c;
            border: 2px solid #8b5cf6;
            border-radius: 10px;
            padding: 1.5rem;
            margin-top: 2rem;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="${policyType == 'warranty' ? 'Chính sách bảo hành' : 
                                                      policyType == 'return' ? 'Chính sách đổi trả' : 
                                                      policyType == 'privacy' ? 'Chính sách bảo mật' : 
                                                      policyType == 'terms' ? 'Điều khoản sử dụng' : 
                                                      policyType == 'installment' ? 'Chính sách trả góp' : 'Chính sách'}" />
    </jsp:include>

    <div class="policy-container">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <div class="policy-content">
                        <c:choose>
                            <c:when test="${policyType == 'warranty'}">
                                <h1 class="policy-title"><i class="bi bi-shield-check"></i> Chính sách bảo hành</h1>
                                
                                <div class="policy-section">
                                    <h3>1. Điều kiện bảo hành</h3>
                                    <p>SmartShop cam kết bảo hành tất cả sản phẩm chính hãng theo tiêu chuẩn của nhà sản xuất:</p>
                                    <ul>
                                        <li>Sản phẩm còn trong thời hạn bảo hành</li>
                                        <li>Còn tem bảo hành và hóa đơn mua hàng</li>
                                        <li>Không bị hỏng do lỗi người dùng (rơi vỡ, vào nước, v.v.)</li>
                                        <li>Không bị can thiệp, sửa chữa bởi bên thứ ba</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>2. Thời gian bảo hành</h3>
                                    <p>Thời gian bảo hành tùy thuộc vào từng sản phẩm và nhà sản xuất:</p>
                                    <ul>
                                        <li><strong>Điện thoại, Laptop:</strong> 12-24 tháng</li>
                                        <li><strong>Tai nghe, Loa:</strong> 12-18 tháng</li>
                                        <li><strong>Phụ kiện:</strong> 6-12 tháng</li>
                                        <li><strong>Gaming Gear:</strong> 12-36 tháng (tùy sản phẩm)</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>3. Quy trình bảo hành</h3>
                                    <ol>
                                        <li>Liên hệ hotline: <strong>0833347220</strong> hoặc mang sản phẩm đến cửa hàng</li>
                                        <li>Nhân viên kiểm tra và xác nhận điều kiện bảo hành</li>
                                        <li>Tiến hành bảo hành tại trung tâm bảo hành chính hãng</li>
                                        <li>Thời gian bảo hành: 7-15 ngày làm việc (tùy sản phẩm)</li>
                                    </ol>
                                </div>
                                
                                <div class="highlight-box">
                                    <h4><i class="bi bi-info-circle"></i> Lưu ý quan trọng</h4>
                                    <p>Vui lòng giữ lại hóa đơn và tem bảo hành để được hỗ trợ tốt nhất. Sản phẩm bảo hành sẽ được kiểm tra kỹ lưỡng trước khi nhận.</p>
                                </div>
                            </c:when>
                            
                            <c:when test="${policyType == 'return'}">
                                <h1 class="policy-title"><i class="bi bi-arrow-repeat"></i> Chính sách đổi trả</h1>
                                
                                <div class="policy-section">
                                    <h3>1. Điều kiện đổi trả</h3>
                                    <p>Khách hàng có thể đổi/trả sản phẩm trong các trường hợp sau:</p>
                                    <ul>
                                        <li>Sản phẩm bị lỗi do nhà sản xuất</li>
                                        <li>Sản phẩm không đúng với mô tả trên website</li>
                                        <li>Giao nhầm sản phẩm, sai màu, sai dung lượng</li>
                                        <li>Đổi sản phẩm trong vòng 7 ngày (nếu còn nguyên seal, chưa sử dụng)</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>2. Thời gian đổi trả</h3>
                                    <ul>
                                        <li><strong>Đổi hàng:</strong> Trong vòng 7 ngày kể từ ngày nhận hàng</li>
                                        <li><strong>Trả hàng:</strong> Trong vòng 3 ngày kể từ ngày nhận hàng</li>
                                        <li>Sản phẩm phải còn nguyên seal, chưa sử dụng, còn đầy đủ phụ kiện</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>3. Quy trình đổi trả</h3>
                                    <ol>
                                        <li>Liên hệ hotline: <strong>0833347220</strong> hoặc email: <strong>smartshop686868@gmail.com</strong></li>
                                        <li>Cung cấp mã đơn hàng và lý do đổi/trả</li>
                                        <li>Nhân viên xác nhận và hướng dẫn gửi hàng về</li>
                                        <li>Sau khi kiểm tra, chúng tôi sẽ xử lý đổi/trả trong 3-5 ngày làm việc</li>
                                    </ol>
                                </div>
                                
                                <div class="highlight-box">
                                    <h4><i class="bi bi-exclamation-triangle"></i> Lưu ý</h4>
                                    <p>Sản phẩm đã sử dụng, mất seal, thiếu phụ kiện sẽ không được đổi/trả. Phí vận chuyển đổi/trả do khách hàng chịu (trừ trường hợp lỗi từ phía SmartShop).</p>
                                </div>
                            </c:when>
                            
                            <c:when test="${policyType == 'privacy'}">
                                <h1 class="policy-title"><i class="bi bi-shield-lock"></i> Chính sách bảo mật</h1>
                                
                                <div class="policy-section">
                                    <h3>1. Thu thập thông tin</h3>
                                    <p>SmartShop thu thập thông tin cá nhân của khách hàng khi:</p>
                                    <ul>
                                        <li>Đăng ký tài khoản trên website</li>
                                        <li>Đặt hàng và thanh toán</li>
                                        <li>Liên hệ với bộ phận chăm sóc khách hàng</li>
                                        <li>Đăng ký nhận thông tin khuyến mãi</li>
                                    </ul>
                                    <p>Thông tin thu thập bao gồm: Họ tên, Email, Số điện thoại, Địa chỉ giao hàng.</p>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>2. Sử dụng thông tin</h3>
                                    <p>Thông tin cá nhân được sử dụng để:</p>
                                    <ul>
                                        <li>Xử lý đơn hàng và giao hàng</li>
                                        <li>Gửi thông tin khuyến mãi, sản phẩm mới</li>
                                        <li>Cải thiện dịch vụ và trải nghiệm khách hàng</li>
                                        <li>Liên hệ hỗ trợ khách hàng khi cần</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>3. Bảo mật thông tin</h3>
                                    <p>SmartShop cam kết:</p>
                                    <ul>
                                        <li>Không chia sẻ thông tin cá nhân cho bên thứ ba (trừ đối tác vận chuyển, thanh toán)</li>
                                        <li>Sử dụng công nghệ mã hóa SSL để bảo vệ thông tin</li>
                                        <li>Chỉ nhân viên có thẩm quyền mới được truy cập thông tin khách hàng</li>
                                        <li>Tuân thủ các quy định về bảo vệ dữ liệu cá nhân</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>4. Quyền của khách hàng</h3>
                                    <p>Khách hàng có quyền:</p>
                                    <ul>
                                        <li>Yêu cầu xem, sửa đổi, xóa thông tin cá nhân</li>
                                        <li>Từ chối nhận email marketing</li>
                                        <li>Yêu cầu ngừng sử dụng thông tin cá nhân</li>
                                    </ul>
                                </div>
                            </c:when>
                            
                            <c:when test="${policyType == 'terms'}">
                                <h1 class="policy-title"><i class="bi bi-file-text"></i> Điều khoản sử dụng</h1>
                                
                                <div class="policy-section">
                                    <h3>1. Điều khoản chung</h3>
                                    <p>Khi truy cập và sử dụng website SmartShop, khách hàng đồng ý tuân thủ các điều khoản sau:</p>
                                    <ul>
                                        <li>Khách hàng phải cung cấp thông tin chính xác, đầy đủ</li>
                                        <li>Không sử dụng website cho mục đích bất hợp pháp</li>
                                        <li>Không cố gắng xâm nhập, phá hoại hệ thống</li>
                                        <li>Tuân thủ các quy định về bản quyền và sở hữu trí tuệ</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>2. Đặt hàng và thanh toán</h3>
                                    <ul>
                                        <li>Giá sản phẩm có thể thay đổi mà không cần báo trước</li>
                                        <li>Đơn hàng chỉ được xác nhận sau khi thanh toán thành công</li>
                                        <li>SmartShop có quyền từ chối đơn hàng nếu phát hiện gian lận</li>
                                        <li>Khách hàng chịu trách nhiệm về thông tin đặt hàng</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>3. Giao hàng</h3>
                                    <ul>
                                        <li>Thời gian giao hàng: 1-3 ngày (nội thành), 3-7 ngày (tỉnh thành khác)</li>
                                        <li>Phí vận chuyển được tính theo địa chỉ giao hàng</li>
                                        <li>SmartShop không chịu trách nhiệm nếu khách hàng cung cấp sai địa chỉ</li>
                                        <li>Khách hàng cần kiểm tra hàng hóa trước khi ký nhận</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>4. Trách nhiệm</h3>
                                    <p>SmartShop không chịu trách nhiệm về:</p>
                                    <ul>
                                        <li>Thiệt hại do lỗi kỹ thuật, lỗi mạng</li>
                                        <li>Thông tin sai lệch do khách hàng cung cấp</li>
                                        <li>Thiệt hại gián tiếp phát sinh từ việc sử dụng website</li>
                                    </ul>
                                </div>
                            </c:when>
                            
                            <c:when test="${policyType == 'installment'}">
                                <h1 class="policy-title"><i class="bi bi-credit-card"></i> Chính sách trả góp</h1>
                                
                                <div class="policy-section">
                                    <h3>1. Điều kiện trả góp</h3>
                                    <p>Khách hàng có thể mua trả góp các sản phẩm có giá trị từ 3.000.000₫ trở lên:</p>
                                    <ul>
                                        <li>Có CMND/CCCD còn hiệu lực</li>
                                        <li>Độ tuổi từ 18-65 tuổi</li>
                                        <li>Có thu nhập ổn định</li>
                                        <li>Không có nợ xấu tại các ngân hàng, công ty tài chính</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>2. Kỳ hạn trả góp</h3>
                                    <ul>
                                        <li><strong>3 tháng:</strong> Lãi suất 0% (áp dụng cho một số sản phẩm)</li>
                                        <li><strong>6 tháng:</strong> Lãi suất từ 1.5%/tháng</li>
                                        <li><strong>12 tháng:</strong> Lãi suất từ 1.8%/tháng</li>
                                        <li><strong>24 tháng:</strong> Lãi suất từ 2.0%/tháng</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>3. Đối tác tài chính</h3>
                                    <p>SmartShop hợp tác với các đối tác uy tín:</p>
                                    <ul>
                                        <li>HD Saison</li>
                                        <li>Home Credit</li>
                                        <li>FE Credit</li>
                                        <li>ACS (Asia Commercial Bank)</li>
                                    </ul>
                                </div>
                                
                                <div class="policy-section">
                                    <h3>4. Quy trình đăng ký</h3>
                                    <ol>
                                        <li>Chọn sản phẩm và phương thức trả góp tại trang thanh toán</li>
                                        <li>Điền thông tin đăng ký trả góp</li>
                                        <li>Chờ phê duyệt từ đối tác tài chính (5-15 phút)</li>
                                        <li>Ký hợp đồng và nhận hàng</li>
                                    </ol>
                                </div>
                                
                                <div class="highlight-box">
                                    <h4><i class="bi bi-info-circle"></i> Lưu ý</h4>
                                    <p>Lãi suất có thể thay đổi tùy theo chính sách của đối tác tài chính và hồ sơ của khách hàng. Vui lòng liên hệ hotline <strong>0833347220</strong> để được tư vấn chi tiết.</p>
                                </div>
                            </c:when>
                            
                            <c:otherwise>
                                <h1 class="policy-title">Chính sách</h1>
                                <p>Trang chính sách không tồn tại.</p>
                            </c:otherwise>
                        </c:choose>
                        
                        <div class="contact-info">
                            <h4><i class="bi bi-telephone"></i> Liên hệ hỗ trợ</h4>
                            <p><strong>Hotline:</strong> 0833347220</p>
                            <p><strong>Email:</strong> smartshop686868@gmail.com</p>
                            <p><strong>Thời gian:</strong> 8:00 - 22:00 hàng ngày</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

