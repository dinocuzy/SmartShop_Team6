<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Footer chung -->
<footer class="bg-dark text-white py-5 mt-5" style="background-color: #1a1a1a !important;">
    <div class="container">
        <div class="row g-4">
            <!-- Company Info -->
            <div class="col-lg-3 col-md-6">
                <h5 class="mb-3">
                    <i class="bi bi-shop" style="color: #dc3545;"></i> 
                    <span style="background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;">SmartShop</span>
                </h5>
                <p class="text-white-50" style="color: #b0b0b0 !important;">
                    Chuyên cung cấp các thiết bị điện máy, điện tử, Gaming Gear
                </p>
                <p class="text-white-50" style="color: #b0b0b0 !important; font-size: 0.9rem;">
                    Mã số thuế: 12345678910
                </p>
            </div>
            
            <!-- Customer Support -->
            <div class="col-lg-2 col-md-6">
                <h6 class="mb-3" style="color: white;">Hỗ trợ khách hàng</h6>
                <ul class="list-unstyled">
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/stores" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                            Hệ thống cửa hàng
                        </a>
                    </li>
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/faq" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                            Câu hỏi thường gặp
                        </a>
                    </li>
                    <li class="mb-2">
                        <c:choose>
                            <c:when test="${not empty sessionScope.currentUser}">
                                <a href="${pageContext.request.contextPath}/customer/orders" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                                    Kiểm tra đơn hàng
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/login" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                                    Kiểm tra đơn hàng
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/contact" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                            Liên hệ
                        </a>
                    </li>
                </ul>
            </div>
            
            <!-- Policies -->
            <div class="col-lg-2 col-md-6">
                <h6 class="mb-3" style="color: white;">Chính sách</h6>
                <ul class="list-unstyled">
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/policy/warranty" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                            Chính sách bảo hành
                        </a>
                    </li>
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/policy/return" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                            Chính sách đổi trả
                        </a>
                    </li>
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/policy/privacy" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                            Chính sách bảo mật
                        </a>
                    </li>
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/policy/installment" class="text-white-50" style="color: #b0b0b0 !important; text-decoration: none;">
                            Chính sách trả góp
                        </a>
                    </li>
                </ul>
            </div>
            
            <!-- Subscribe -->
            <div class="col-lg-5 col-md-6">
                <h6 class="mb-3" style="color: white;">Đăng ký nhận ưu đãi</h6>
                <p class="text-white-50 mb-3" style="color: #b0b0b0 !important;">
                    Đăng ký để nhận thông tin sản phẩm mới và các chương trình khuyến mãi độc quyền
                </p>
                <p class="text-white-50 mb-3" style="color: #b0b0b0 !important; font-size: 0.9rem;">
                    <strong style="color: #8b5cf6;">68.000+</strong> người theo dõi
                </p>
                <form class="d-flex gap-2" method="post" action="${pageContext.request.contextPath}/subscribe">
                    <input type="email" class="form-control" placeholder="Email của bạn..." 
                           style="background-color: #2c2c2c; border: 1px solid #4a4a4a; color: white;"
                           required>
                    <button type="submit" class="btn" 
                            style="background: #dc3545; color: white; border: none; white-space: nowrap;">
                        Đăng ký
                    </button>
                </form>
            </div>
        </div>
        
        <hr style="border-color: #4a4a4a; margin: 2rem 0;">
        
        <div class="text-center">
            <p class="mb-0" style="color: #b0b0b0;">&copy; 2024 SmartShop. All rights reserved.</p>
        </div>
    </div>
</footer>

<!-- AI Chatbot Widget - Hiển thị trên mọi trang -->
<jsp:include page="/views/common/chatbot.jsp" />
