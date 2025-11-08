<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Câu hỏi thường gặp - SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #ffffff;
            min-height: 100vh;
        }
        
        .faq-container {
            padding: 3rem 0;
            background-color: #1a1a1a;
        }
        
        .faq-header {
            text-align: center;
            margin-bottom: 3rem;
        }
        
        .faq-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
            margin-bottom: 1rem;
        }
        
        .faq-subtitle {
            color: #b0b0b0;
            font-size: 1.1rem;
        }
        
        .faq-item {
            background-color: #2c2c2c;
            border-radius: 10px;
            margin-bottom: 1rem;
            border: 1px solid #444;
            overflow: hidden;
        }
        
        .faq-question {
            background-color: #2c2c2c;
            border: none;
            color: white;
            padding: 1.5rem;
            width: 100%;
            text-align: left;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: background-color 0.3s;
        }
        
        .faq-question:hover {
            background-color: #3a3a3a;
        }
        
        .faq-question.active {
            background-color: #3a3a3a;
            color: #8b5cf6;
        }
        
        .faq-answer {
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.3s ease-out;
            background-color: #1a1a1a;
        }
        
        .faq-answer.show {
            max-height: 1000px;
            transition: max-height 0.5s ease-in;
        }
        
        .faq-answer-content {
            padding: 1.5rem;
            color: #cccccc;
            line-height: 1.8;
        }
        
        .faq-icon {
            transition: transform 0.3s;
        }
        
        .faq-question.active .faq-icon {
            transform: rotate(180deg);
        }
        
        .faq-category {
            margin-bottom: 3rem;
        }
        
        .faq-category-title {
            font-size: 1.8rem;
            font-weight: bold;
            color: #8b5cf6;
            margin-bottom: 1.5rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #8b5cf6;
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp" />
    
    <jsp:include page="/views/common/breadcrumb.jsp">
        <jsp:param name="currentPage" value="Câu hỏi thường gặp" />
    </jsp:include>

    <div class="faq-container">
        <div class="container">
            <div class="faq-header">
                <h1 class="faq-title"><i class="bi bi-question-circle"></i> Câu hỏi thường gặp</h1>
                <p class="faq-subtitle">Tìm câu trả lời cho các thắc mắc phổ biến về SmartShop</p>
            </div>
            
            <div class="row">
                <div class="col-lg-10 offset-lg-1">
                    <!-- Đặt hàng và thanh toán -->
                    <div class="faq-category">
                        <h2 class="faq-category-title"><i class="bi bi-cart-check"></i> Đặt hàng và thanh toán</h2>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Làm thế nào để đặt hàng trên SmartShop?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Để đặt hàng, bạn thực hiện các bước sau:</p>
                                    <ol>
                                        <li>Tìm kiếm sản phẩm bạn muốn mua</li>
                                        <li>Thêm sản phẩm vào giỏ hàng</li>
                                        <li>Kiểm tra giỏ hàng và nhấn "Thanh toán"</li>
                                        <li>Điền thông tin giao hàng và chọn phương thức thanh toán</li>
                                        <li>Xác nhận đơn hàng</li>
                                    </ol>
                                </div>
                            </div>
                        </div>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>SmartShop có những phương thức thanh toán nào?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Chúng tôi hỗ trợ các phương thức thanh toán sau:</p>
                                    <ul>
                                        <li><strong>Thanh toán khi nhận hàng (COD):</strong> Thanh toán bằng tiền mặt khi nhận hàng</li>
                                        <li><strong>VNPay:</strong> Thanh toán online qua VNPay</li>
                                        <li><strong>Trả góp:</strong> Mua trả góp qua các đối tác tài chính (HD Saison, Home Credit, FE Credit)</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Tôi có thể hủy đơn hàng sau khi đã đặt không?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Có, bạn có thể hủy đơn hàng trong các trường hợp sau:</p>
                                    <ul>
                                        <li>Đơn hàng chưa được xác nhận: Hủy trực tiếp trên website</li>
                                        <li>Đơn hàng đã xác nhận: Liên hệ hotline <strong>0833347220</strong> để hủy</li>
                                        <li>Đơn hàng đã giao: Không thể hủy, nhưng có thể đổi/trả theo chính sách</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Giao hàng -->
                    <div class="faq-category">
                        <h2 class="faq-category-title"><i class="bi bi-truck"></i> Giao hàng</h2>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Thời gian giao hàng là bao lâu?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Thời gian giao hàng phụ thuộc vào địa chỉ:</p>
                                    <ul>
                                        <li><strong>Nội thành Đà Nẵng:</strong> 1-2 ngày làm việc</li>
                                        <li><strong>Tỉnh thành khác:</strong> 3-7 ngày làm việc</li>
                                        <li><strong>Vùng sâu, vùng xa:</strong> 7-10 ngày làm việc</li>
                                    </ul>
                                    <p>Thời gian giao hàng được tính từ khi đơn hàng được xác nhận và thanh toán thành công.</p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Phí vận chuyển được tính như thế nào?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Phí vận chuyển được tính theo:</p>
                                    <ul>
                                        <li><strong>Miễn phí:</strong> Đơn hàng từ 500.000₫ trở lên (nội thành)</li>
                                        <li><strong>Nội thành:</strong> 30.000₫ - 50.000₫</li>
                                        <li><strong>Tỉnh thành khác:</strong> 50.000₫ - 100.000₫ (tùy khoảng cách)</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Tôi có thể thay đổi địa chỉ giao hàng sau khi đặt hàng không?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Có, bạn có thể thay đổi địa chỉ giao hàng bằng cách:</p>
                                    <ul>
                                        <li>Liên hệ hotline <strong>0833347220</strong> (trước khi đơn hàng được giao)</li>
                                        <li>Gửi email đến <strong>smartshop686868@gmail.com</strong> kèm mã đơn hàng</li>
                                        <li>Nếu đơn hàng đã được giao, không thể thay đổi địa chỉ</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Bảo hành và đổi trả -->
                    <div class="faq-category">
                        <h2 class="faq-category-title"><i class="bi bi-shield-check"></i> Bảo hành và đổi trả</h2>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Sản phẩm được bảo hành trong bao lâu?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Thời gian bảo hành tùy thuộc vào từng sản phẩm:</p>
                                    <ul>
                                        <li>Điện thoại, Laptop: 12-24 tháng</li>
                                        <li>Tai nghe, Loa: 12-18 tháng</li>
                                        <li>Phụ kiện: 6-12 tháng</li>
                                        <li>Gaming Gear: 12-36 tháng</li>
                                    </ul>
                                    <p>Chi tiết xem tại <a href="${pageContext.request.contextPath}/policy/warranty" style="color: #8b5cf6;">Chính sách bảo hành</a></p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Tôi có thể đổi/trả sản phẩm trong bao lâu?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Chính sách đổi/trả:</p>
                                    <ul>
                                        <li><strong>Đổi hàng:</strong> Trong vòng 7 ngày (sản phẩm còn nguyên seal, chưa sử dụng)</li>
                                        <li><strong>Trả hàng:</strong> Trong vòng 3 ngày (sản phẩm còn nguyên seal, chưa sử dụng)</li>
                                    </ul>
                                    <p>Chi tiết xem tại <a href="${pageContext.request.contextPath}/policy/return" style="color: #8b5cf6;">Chính sách đổi trả</a></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Tài khoản -->
                    <div class="faq-category">
                        <h2 class="faq-category-title"><i class="bi bi-person"></i> Tài khoản</h2>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Làm thế nào để đăng ký tài khoản?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Bạn có thể đăng ký tài khoản bằng cách:</p>
                                    <ol>
                                        <li>Nhấn vào nút "Đăng ký" ở góc trên bên phải</li>
                                        <li>Điền đầy đủ thông tin: Họ tên, Email, Mật khẩu</li>
                                        <li>Xác nhận đăng ký</li>
                                    </ol>
                                    <p>Hoặc <a href="${pageContext.request.contextPath}/register" style="color: #8b5cf6;">đăng ký tại đây</a></p>
                                </div>
                            </div>
                        </div>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Tôi quên mật khẩu, làm sao để lấy lại?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Bạn có thể lấy lại mật khẩu bằng cách:</p>
                                    <ol>
                                        <li>Nhấn vào "Quên mật khẩu?" trên trang đăng nhập</li>
                                        <li>Nhập email đã đăng ký</li>
                                        <li>Kiểm tra email và làm theo hướng dẫn</li>
                                    </ol>
                                    <p>Hoặc <a href="${pageContext.request.contextPath}/forgot-password" style="color: #8b5cf6;">lấy lại mật khẩu tại đây</a></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Khác -->
                    <div class="faq-category">
                        <h2 class="faq-category-title"><i class="bi bi-info-circle"></i> Khác</h2>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>Làm thế nào để liên hệ với SmartShop?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Bạn có thể liên hệ với chúng tôi qua:</p>
                                    <ul>
                                        <li><strong>Hotline:</strong> 0833347220 (8:00 - 22:00 hàng ngày)</li>
                                        <li><strong>Email:</strong> smartshop686868@gmail.com</li>
                                        <li><strong>Form liên hệ:</strong> <a href="${pageContext.request.contextPath}/contact" style="color: #8b5cf6;">Tại đây</a></li>
                                        <li><strong>Địa chỉ cửa hàng:</strong> Xem tại <a href="${pageContext.request.contextPath}/stores" style="color: #8b5cf6;">Hệ thống cửa hàng</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        
                        <div class="faq-item">
                            <button class="faq-question" onclick="toggleFaq(this)">
                                <span>SmartShop có hỗ trợ mua trả góp không?</span>
                                <i class="bi bi-chevron-down faq-icon"></i>
                            </button>
                            <div class="faq-answer">
                                <div class="faq-answer-content">
                                    <p>Có, SmartShop hỗ trợ mua trả góp cho sản phẩm từ 3.000.000₫ trở lên:</p>
                                    <ul>
                                        <li>Kỳ hạn: 3, 6, 12, 24 tháng</li>
                                        <li>Lãi suất: Từ 0% (tùy sản phẩm và kỳ hạn)</li>
                                        <li>Đối tác: HD Saison, Home Credit, FE Credit, ACS</li>
                                    </ul>
                                    <p>Chi tiết xem tại <a href="${pageContext.request.contextPath}/policy/installment" style="color: #8b5cf6;">Chính sách trả góp</a></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleFaq(button) {
            const faqItem = button.closest('.faq-item');
            const answer = faqItem.querySelector('.faq-answer');
            const isActive = button.classList.contains('active');
            
            // Close all other FAQs
            document.querySelectorAll('.faq-question').forEach(q => {
                if (q !== button) {
                    q.classList.remove('active');
                    q.closest('.faq-item').querySelector('.faq-answer').classList.remove('show');
                }
            });
            
            // Toggle current FAQ
            if (isActive) {
                button.classList.remove('active');
                answer.classList.remove('show');
            } else {
                button.classList.add('active');
                answer.classList.add('show');
            }
        }
    </script>
</body>
</html>

