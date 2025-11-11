<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<style>
    .navbar-top {
        background-color: #8B0000;
        color: white;
        padding: 0.5rem 0;
        font-size: 0.9rem;
        position: relative;
        z-index: 1081; /* Cao hơn header (1080) một chút để dropdown có thể hiển thị trên header */
    }
    
    .navbar-top .container {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .navbar-top-left {
        display: flex;
        align-items: center;
        gap: 1.5rem;
        flex-wrap: wrap;
    }
    
    .navbar-top-link {
        color: white;
        text-decoration: none;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        transition: opacity 0.3s;
        white-space: nowrap;
    }
    
    .navbar-top-link:hover {
        opacity: 0.8;
        color: white;
        text-decoration: none;
    }
    
    .navbar-top-link i {
        font-size: 1rem;
    }
    
    .navbar-top-dropdown {
        position: relative;
        z-index: 1095 !important; /* Đảm bảo dropdown container có z-index cao hơn header */
    }
    
    .navbar-top-dropdown-toggle {
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 0.25rem;
    }
    
    .navbar-top-dropdown-menu {
        opacity: 0;
        visibility: hidden;
        position: absolute;
        top: 100%;
        left: 0;
        background-color: white;
        min-width: 200px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        z-index: 1095 !important; /* Cao hơn header (1080) để hiển thị trên header */
        margin-top: 0.5rem;
        border-radius: 4px;
        padding: 0.5rem 0;
        transition: opacity 0.5s ease, visibility 0.5s ease, transform 0.5s ease;
        will-change: opacity, transform; /* Tối ưu performance cho animation */
        transform: translateY(-10px);
        pointer-events: none;
        display: block; /* Luôn có display block để transition hoạt động */
    }
    
    /* Khi hover hoặc có class show - dùng JavaScript để control */
    .navbar-top-dropdown-menu.show {
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
        pointer-events: auto;
    }
    
    /* Fallback cho CSS hover nếu JavaScript chưa load */
    .navbar-top-dropdown:hover .navbar-top-dropdown-menu {
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
        pointer-events: auto;
    }
    
    .navbar-top-dropdown-item {
        color: #333;
        text-decoration: none;
        display: block;
        padding: 0.5rem 1rem;
        transition: background-color 0.3s;
    }
    
    .navbar-top-dropdown-item:hover {
        background-color: #f8f9fa;
        color: #333;
        text-decoration: none;
    }
    
    .navbar-top-right {
        display: flex;
        align-items: center;
        gap: 1.5rem;
        flex-wrap: wrap;
    }
    
    .navbar-top-nav-arrows {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin: 0 1rem;
    }
    
    .navbar-top-nav-arrow {
        color: white;
        cursor: pointer;
        font-size: 1.2rem;
        transition: opacity 0.3s;
        user-select: none;
    }
    
    .navbar-top-nav-arrow:hover {
        opacity: 0.7;
    }
    
    .navbar-top-nav-arrow.disabled {
        opacity: 0.3;
        cursor: not-allowed;
    }
    
    @media (max-width: 768px) {
        .navbar-top-left,
        .navbar-top-right {
            gap: 0.75rem;
            font-size: 0.85rem;
        }
        
        .navbar-top-nav-arrows {
            display: none;
        }
    }
</style>

<nav class="navbar-top">
    <div class="container">
        <div class="navbar-top-left">
            <!-- Danh mục sản phẩm -->
            <a href="${pageContext.request.contextPath}/shop" class="navbar-top-link">
                <i class="bi bi-list"></i>
                <span>Danh mục sản phẩm</span>
            </a>
            
            <!-- Khuyến mãi (Dropdown) -->
            <div class="navbar-top-dropdown">
                <div class="navbar-top-link navbar-top-dropdown-toggle">
                    <span>Khuyến mãi</span>
                    <i class="bi bi-chevron-down" style="font-size: 0.7rem;"></i>
                </div>
                <div class="navbar-top-dropdown-menu">
                    <a href="${pageContext.request.contextPath}/shop?promotion=active" class="navbar-top-dropdown-item">
                        <i class="bi bi-tag"></i> Khuyến mãi đang diễn ra
                    </a>
                    <a href="${pageContext.request.contextPath}/shop?promotion=upcoming" class="navbar-top-dropdown-item">
                        <i class="bi bi-clock"></i> Khuyến mãi sắp tới
                    </a>
                </div>
            </div>
            
            <!-- Dịch vụ (Dropdown) -->
            <div class="navbar-top-dropdown">
                <div class="navbar-top-link navbar-top-dropdown-toggle">
                    <span>Dịch vụ</span>
                    <i class="bi bi-chevron-down" style="font-size: 0.7rem;"></i>
                </div>
                <div class="navbar-top-dropdown-menu">
                    <a href="${pageContext.request.contextPath}/shop" class="navbar-top-dropdown-item">
                        <i class="bi bi-truck"></i> Giao hàng nhanh
                    </a>
                    <a href="${pageContext.request.contextPath}/shop" class="navbar-top-dropdown-item">
                        <i class="bi bi-arrow-repeat"></i> Đổi trả dễ dàng
                    </a>
                    <a href="${pageContext.request.contextPath}/shop" class="navbar-top-dropdown-item">
                        <i class="bi bi-shield-check"></i> Bảo hành chính hãng
                    </a>
                </div>
            </div>
            
            <!-- Tin Tức -->
            <a href="${pageContext.request.contextPath}/news" class="navbar-top-link">
                <span>Tin Tức</span>
            </a>
            
            <!-- Liên hệ -->
            <a href="${pageContext.request.contextPath}/contact" class="navbar-top-link">
                <span>Liên hệ</span>
            </a>
            
            <!-- Kiểm tra đơn hàng -->
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <a href="${pageContext.request.contextPath}/customer/orders" class="navbar-top-link">
                        <span>Kiểm tra đơn hàng</span>
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="navbar-top-link">
                        <span>Kiểm tra đơn hàng</span>
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Navigation Arrows (có thể dùng cho carousel hoặc scroll menu) -->
        <div class="navbar-top-nav-arrows">
            <span class="navbar-top-nav-arrow" onclick="scrollNavLeft()">
                <i class="bi bi-chevron-left"></i>
            </span>
            <span class="navbar-top-nav-arrow" onclick="scrollNavRight()">
                <i class="bi bi-chevron-right"></i>
            </span>
        </div>
        
        <div class="navbar-top-right">
            <!-- Hệ thống cửa hàng -->
            <a href="${pageContext.request.contextPath}/stores" class="navbar-top-link">
                <i class="bi bi-shop"></i>
                <span>Hệ thống cửa hàng</span>
            </a>
            
            <!-- Hotline -->
            <a href="tel:0833347220" class="navbar-top-link">
                <i class="bi bi-telephone"></i>
                <span>Hotline: 0833347220</span>
            </a>
        </div>
    </div>
</nav>

<script>
    // Function để scroll navbar (nếu menu quá dài trên mobile)
    function scrollNavLeft() {
        const navLeft = document.querySelector('.navbar-top-left');
        if (navLeft) {
            navLeft.scrollBy({ left: -200, behavior: 'smooth' });
        }
    }
    
    function scrollNavRight() {
        const navLeft = document.querySelector('.navbar-top-left');
        if (navLeft) {
            navLeft.scrollBy({ left: 200, behavior: 'smooth' });
        }
    }
    
    // Thêm delay trước khi đóng dropdown khi mouse rời khỏi - thời gian lâu hơn
    (function() {
        // Đợi DOM load xong
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', initDropdownDelays);
        } else {
            initDropdownDelays();
        }
        
        function initDropdownDelays() {
            let closeTimeouts = new Map(); // Dùng Map để lưu timeout cho mỗi dropdown
            const dropdowns = document.querySelectorAll('.navbar-top-dropdown');
            
            dropdowns.forEach(function(dropdown) {
                const menu = dropdown.querySelector('.navbar-top-dropdown-menu');
                if (!menu) return;
                
                // Đảm bảo menu có display block để transition hoạt động
                menu.style.display = 'block';
                
                // Khi hover vào dropdown
                dropdown.addEventListener('mouseenter', function(e) {
                    // Hủy timeout đóng nếu có
                    const timeout = closeTimeouts.get(dropdown);
                    if (timeout) {
                        clearTimeout(timeout);
                        closeTimeouts.delete(dropdown);
                    }
                    // Hiển thị dropdown với animation
                    menu.classList.add('show');
                });
                
                // Khi rời khỏi dropdown
                dropdown.addEventListener('mouseleave', function(e) {
                    // Đợi 700ms trước khi đóng (thời gian lâu hơn để người dùng có thời gian di chuyển vào menu)
                    const timeout = setTimeout(function() {
                        // Kiểm tra lại xem có đang hover vào dropdown hoặc menu không
                        if (!dropdown.matches(':hover') && !menu.matches(':hover')) {
                            menu.classList.remove('show');
                        }
                        closeTimeouts.delete(dropdown);
                    }, 700); // Delay 700ms trước khi bắt đầu đóng - lâu hơn
                    closeTimeouts.set(dropdown, timeout);
                });
                
                // Khi hover vào menu - giữ menu mở
                menu.addEventListener('mouseenter', function(e) {
                    const timeout = closeTimeouts.get(dropdown);
                    if (timeout) {
                        clearTimeout(timeout);
                        closeTimeouts.delete(dropdown);
                    }
                    menu.classList.add('show');
                });
                
                // Khi rời khỏi menu
                menu.addEventListener('mouseleave', function(e) {
                    const timeout = setTimeout(function() {
                        if (!dropdown.matches(':hover') && !menu.matches(':hover')) {
                            menu.classList.remove('show');
                        }
                        closeTimeouts.delete(dropdown);
                    }, 700); // Delay 700ms - lâu hơn
                    closeTimeouts.set(dropdown, timeout);
                });
            });
        }
    })();
</script>

