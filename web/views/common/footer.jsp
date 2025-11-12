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

<!-- Compare Modal -->
<div class="modal fade" id="compareModal" tabindex="-1" aria-labelledby="compareModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-scrollable">
        <div class="modal-content" style="background-color: #2c2c2c; color: #fff;">
            <div class="modal-header" style="border-bottom: 1px solid #444;">
                <h5 class="modal-title" id="compareModalLabel">
                    <i class="bi bi-arrow-left-right"></i> So sánh sản phẩm
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <!-- Product 1 -->
                    <div class="col-md-6">
                        <div class="text-center mb-3">
                            <img id="compareProduct1Image" src="" alt="Product 1" 
                                 style="max-width: 200px; max-height: 200px; object-fit: cover; border-radius: 8px;">
                        </div>
                        <h5 id="compareProduct1Name" class="text-center mb-3"></h5>
                        <div class="text-center mb-3">
                            <div id="compareProduct1Price" style="font-size: 1.5rem; font-weight: bold; color: #dc3545;"></div>
                            <div id="compareProduct1OldPrice" style="font-size: 0.9rem; text-decoration: line-through; color: #6a6a6a;"></div>
                        </div>
                        <table class="table table-dark table-striped">
                            <tr>
                                <td><strong>Danh mục:</strong></td>
                                <td id="compareProduct1Category"></td>
                            </tr>
                            <tr>
                                <td><strong>Tồn kho:</strong></td>
                                <td id="compareProduct1Stock"></td>
                            </tr>
                            <tr>
                                <td><strong>Mô tả:</strong></td>
                                <td id="compareProduct1Description" style="font-size: 0.9rem;"></td>
                            </tr>
                        </table>
                        <div class="text-center">
                            <button id="compareProduct1ViewBtn" class="btn btn-primary">
                                <i class="bi bi-eye"></i> Xem chi tiết
                            </button>
                        </div>
                    </div>
                    
                    <!-- Product 2 -->
                    <div class="col-md-6">
                        <div class="text-center mb-3">
                            <img id="compareProduct2Image" src="" alt="Product 2" 
                                 style="max-width: 200px; max-height: 200px; object-fit: cover; border-radius: 8px;">
                        </div>
                        <h5 id="compareProduct2Name" class="text-center mb-3"></h5>
                        <div class="text-center mb-3">
                            <div id="compareProduct2Price" style="font-size: 1.5rem; font-weight: bold; color: #dc3545;"></div>
                            <div id="compareProduct2OldPrice" style="font-size: 0.9rem; text-decoration: line-through; color: #6a6a6a;"></div>
                        </div>
                        <table class="table table-dark table-striped">
                            <tr>
                                <td><strong>Danh mục:</strong></td>
                                <td id="compareProduct2Category"></td>
                            </tr>
                            <tr>
                                <td><strong>Tồn kho:</strong></td>
                                <td id="compareProduct2Stock"></td>
                            </tr>
                            <tr>
                                <td><strong>Mô tả:</strong></td>
                                <td id="compareProduct2Description" style="font-size: 0.9rem;"></td>
                            </tr>
                        </table>
                        <div class="text-center">
                            <button id="compareProduct2ViewBtn" class="btn btn-primary">
                                <i class="bi bi-eye"></i> Xem chi tiết
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="border-top: 1px solid #444;">
                <a href="${pageContext.request.contextPath}/compare" class="btn btn-outline-light">
                    <i class="bi bi-arrow-left-right"></i> Xem trang so sánh đầy đủ
                </a>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<!-- Compare List JavaScript -->
<script>
    // Set context path for compare.js (if not already set in head)
    if (typeof window.contextPath === 'undefined') {
        window.contextPath = '${pageContext.request.contextPath}';
    }
</script>
<script src="${pageContext.request.contextPath}/js/compare.js"></script>