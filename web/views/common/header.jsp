<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Top Navigation Bar (Below Header) -->
<jsp:include page="/views/common/navbar-top.jsp" />

<style>
    .header-custom {
        background-color: #2c2c2c;
        border-bottom: 2px solid #8b5cf6;
        padding: 1rem 0;
        position: relative;
        z-index: 1050; /* Cao hơn chatbot để click được vào dropdown */
    }
    
    .logo-container {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }
    
    .logo-circle {
        width: 50px;
        height: 50px;
        background-color: #dc3545;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: bold;
        font-size: 1.5rem;
        flex-shrink: 0;
    }
    
    .logo-text {
        font-size: 1.5rem;
        font-weight: bold;
        background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }
    
    .search-container {
        display: flex;
        align-items: center;
        gap: 0;
        max-width: 600px;
        margin: 0 auto;
    }
    
    .category-dropdown-wrapper {
        position: relative;
        flex-shrink: 0;
    }
    
    .category-dropdown {
        background-color: #3a3a3a;
        color: #b0b0b0;
        border: none;
        border-radius: 25px 0 0 25px;
        padding: 0.75rem 1.5rem;
        padding-right: 2.5rem;
        font-size: 0.9rem;
        cursor: pointer;
        appearance: none;
        -webkit-appearance: none;
        -moz-appearance: none;
        min-width: 180px;
        height: 45px;
    }
    
    .category-dropdown:focus {
        outline: none;
        background-color: #3a3a3a;
    }
    
    .category-dropdown-wrapper::after {
        content: '▼';
        position: absolute;
        right: 1rem;
        top: 50%;
        transform: translateY(-50%);
        color: #b0b0b0;
        pointer-events: none;
        font-size: 0.7rem;
    }
    
    .search-input {
        flex: 1;
        background-color: #3a3a3a;
        color: #fff;
        border: none;
        border-left: 1px solid #4a4a4a;
        border-right: 1px solid #4a4a4a;
        padding: 0.75rem 1.5rem;
        font-size: 0.9rem;
        height: 45px;
    }
    
    .search-input::placeholder {
        color: #b0b0b0;
    }
    
    .search-input:focus {
        outline: none;
        background-color: #3a3a3a;
        color: #fff;
    }
    
    .search-btn {
        background-color: #dc3545;
        color: white;
        border: none;
        border-radius: 0 25px 25px 0;
        padding: 0.75rem 1.5rem;
        cursor: pointer;
        height: 45px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: background-color 0.3s;
    }
    
    .search-btn:hover {
        background-color: #c82333;
    }
    
    .header-actions {
        display: flex;
        align-items: center;
        gap: 2rem;
    }
    
    .dropdown {
        position: relative !important; /* Đảm bảo dropdown có position relative */
    }
    
    .action-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-decoration: none;
        color: #b0b0b0;
        transition: color 0.3s;
        position: relative;
        cursor: pointer; /* Thêm cursor pointer */
    }
    
    .action-item:hover {
        color: #fff;
        text-decoration: none;
    }
    
    .action-icon {
        font-size: 1.5rem;
        margin-bottom: 0.25rem;
    }
    
    .action-label {
        font-size: 0.75rem;
        text-align: center;
    }
    
    .action-label-line {
        display: block;
    }
    
    .cart-badge {
        position: absolute;
        top: -5px;
        right: -5px;
        background-color: #dc3545;
        color: white;
        border-radius: 50%;
        width: 20px;
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.7rem;
        font-weight: bold;
    }
    
    .user-dropdown-menu {
        background-color: #3a3a3a !important;
        border: 1px solid #4a4a4a !important;
        border-radius: 8px;
        padding: 0.5rem 0;
        min-width: 200px;
        position: absolute !important;
        top: calc(100% + 0.5rem) !important;
        right: 0 !important;
        left: auto !important;
        margin: 0 !important;
        z-index: 9999 !important; /* Rất cao để hiển thị trên tất cả */
        display: none !important; /* Ẩn mặc định */
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
        opacity: 1 !important;
        visibility: hidden !important;
        pointer-events: none !important;
        /* Disable Bootstrap Popper positioning */
        transform: none !important;
        inset: auto !important;
    }
    
    .user-dropdown-menu.show {
        display: block !important; /* Hiển thị khi có class show */
        opacity: 1 !important;
        visibility: visible !important;
        pointer-events: auto !important;
        transform: none !important; /* Override Bootstrap Popper transform */
        inset: auto !important; /* Override Bootstrap Popper inset */
        top: calc(100% + 0.5rem) !important;
        right: 0 !important;
        left: auto !important;
    }
    
    /* Test: Luôn hiển thị menu để debug - XÓA SAU KHI TEST */
    /* .user-dropdown-menu { display: block !important; visibility: visible !important; } */
    
    .dropdown-menu-end {
        right: 0 !important;
        left: auto !important;
    }
    
    .user-dropdown-item {
        color: #b0b0b0;
        padding: 0.5rem 1rem;
        text-decoration: none;
        display: block;
        transition: background-color 0.3s, color 0.3s;
    }
    
    .user-dropdown-item:hover {
        background-color: #4a4a4a;
        color: #fff;
        text-decoration: none;
    }
    
    @media (max-width: 992px) {
        .search-container {
            max-width: 100%;
            margin: 1rem 0;
        }
        
        .header-actions {
            gap: 1rem;
        }
    }
</style>

<nav class="header-custom">
    <div class="container">
        <div class="row align-items-center">
            <!-- Logo -->
            <div class="col-md-2">
                <a href="${pageContext.request.contextPath}/home" class="logo-container" style="text-decoration: none;">
                    <div class="logo-circle">
                        <i class="bi bi-shop"></i>
                    </div>
                    <div class="logo-text">SmartShop</div>
                </a>
            </div>
            
            <!-- Search Bar -->
            <div class="col-md-7">
                <form method="get" action="${pageContext.request.contextPath}/shop" class="search-container">
                    <div class="category-dropdown-wrapper">
                        <select name="category" id="headerCategorySelect" class="category-dropdown">
                            <option value="0">Danh mục sản phẩm</option>
                            <c:if test="${not empty categories}">
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.categoryID}" ${param.category == cat.categoryID ? 'selected' : ''}>
                                        ${cat.categoryName}
                                    </option>
                                </c:forEach>
                            </c:if>
                        </select>
                    </div>
                    <input type="text" name="search" class="search-input" 
                           placeholder="Tìm theo tên sản phẩm..." 
                           value="${param.search != null ? param.search : ''}">
                    <button type="submit" class="search-btn">
                        <i class="bi bi-search"></i>
                    </button>
                </form>
            </div>
            
            <!-- User Actions -->
            <div class="col-md-3">
                <div class="header-actions">
                    <!-- Account -->
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <div class="dropdown">
                                <a href="javascript:void(0);" class="action-item dropdown-toggle" id="userDropdown" 
                                   onclick="event.preventDefault(); event.stopPropagation(); toggleLoginDropdown(event);"
                                   aria-expanded="false" style="text-decoration: none; cursor: pointer;">
                                    <i class="bi bi-person-circle action-icon"></i>
                                    <span class="action-label">
                                        <span class="action-label-line">Welcome, ${sessionScope.currentUser.fullName}</span>
                                    </span>
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end user-dropdown-menu" id="userDropdownMenu" aria-labelledby="userDropdown">
                                    <li>
                                        <a class="user-dropdown-item" href="${pageContext.request.contextPath}/customer/dashboard">
                                            <i class="bi bi-speedometer2"></i> Trang cá nhân
                                        </a>
                                    </li>
                                    <li>
                                        <a class="user-dropdown-item" href="${pageContext.request.contextPath}/customer/profile">
                                            <i class="bi bi-person"></i> Thông tin cá nhân
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider" style="border-color: #4a4a4a;"></li>
                                    <li>
                                        <a class="user-dropdown-item" href="${pageContext.request.contextPath}/logout">
                                            <i class="bi bi-box-arrow-right"></i> Đăng xuất
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="dropdown">
                                <a href="javascript:void(0);" class="action-item dropdown-toggle" id="guestDropdown" 
                                   onclick="event.preventDefault(); event.stopPropagation(); toggleLoginDropdown(event);"
                                   aria-expanded="false" style="text-decoration: none; cursor: pointer;">
                                    <i class="bi bi-person-circle action-icon"></i>
                                    <span class="action-label">
                                        <span class="action-label-line">Đăng nhập</span>
                                    </span>
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end user-dropdown-menu" id="guestDropdownMenu" aria-labelledby="guestDropdown">
                                    <li>
                                        <a class="user-dropdown-item" href="${pageContext.request.contextPath}/login">
                                            <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                                        </a>
                                    </li>
                                    <li>
                                        <a class="user-dropdown-item" href="${pageContext.request.contextPath}/register">
                                            <i class="bi bi-person-plus"></i> Đăng ký
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    
                    <!-- Wishlist -->
                    <a href="${pageContext.request.contextPath}/wishlist" class="action-item" style="position: relative;">
                        <i class="bi bi-heart action-icon"></i>
                        <c:if test="${not empty sessionScope.wishlist and sessionScope.wishlist.size() > 0}">
                            <span class="cart-badge">${sessionScope.wishlist.size()}</span>
                        </c:if>
                        <span class="action-label">
                            <span class="action-label-line">Yêu thích</span>
                        </span>
                    </a>
                    
                    <!-- Cart -->
                    <a href="${pageContext.request.contextPath}/cart" class="action-item" style="position: relative;">
                        <i class="bi bi-cart action-icon"></i>
                        <c:if test="${not empty sessionScope.cart and sessionScope.cart.itemCount > 0}">
                            <span class="cart-badge">${sessionScope.cart.itemCount}</span>
                        </c:if>
                        <span class="action-label">
                            <span class="action-label-line">Giỏ hàng</span>
                        </span>
                    </a>
                </div>
            </div>
        </div>
    </div>
</nav>

<script>
    // Hàm toggle dropdown - được gọi trực tiếp từ onclick
    window.toggleLoginDropdown = function(event) {
        console.log('=== toggleLoginDropdown called ===', event);
        
        // Ngăn default behavior và propagation
        if (event) {
            event.preventDefault();
            event.stopPropagation();
            event.stopImmediatePropagation();
        }
        
        // Tìm dropdown menu từ event
        let toggleElement = event ? (event.currentTarget || event.target.closest('.dropdown-toggle') || event.target.closest('[id*="Dropdown"]')) : null;
        
        // Fallback: tìm bằng ID
        if (!toggleElement) {
            toggleElement = document.getElementById('guestDropdown') || document.getElementById('userDropdown');
        }
        
        console.log('Toggle element found:', toggleElement, toggleElement ? toggleElement.id : 'NOT FOUND');
        
        if (!toggleElement) {
            alert('Không tìm thấy nút dropdown!');
            return false;
        }
        
        const dropdown = toggleElement.closest('.dropdown');
        console.log('Dropdown container:', dropdown);
        
        if (!dropdown) {
            alert('Không tìm thấy container dropdown!');
            return false;
        }
        
        const menuElement = dropdown.querySelector('.dropdown-menu');
        console.log('Menu element:', menuElement);
        
        if (!menuElement) {
            alert('Không tìm thấy menu dropdown!');
            return false;
        }
        
        // Kiểm tra trạng thái hiện tại
        const isShown = menuElement.classList.contains('show');
        console.log('Current state - isShown:', isShown);
        console.log('Menu classes before:', menuElement.classList.toString());
        console.log('Menu display before:', window.getComputedStyle(menuElement).display);
        console.log('Menu visibility before:', window.getComputedStyle(menuElement).visibility);
        
        // Đóng tất cả dropdown khác
        document.querySelectorAll('.dropdown-menu.show').forEach(function(openMenu) {
            if (openMenu !== menuElement) {
                openMenu.classList.remove('show');
                const parentDropdown = openMenu.closest('.dropdown');
                if (parentDropdown) {
                    const parentToggle = parentDropdown.querySelector('[data-bs-toggle="dropdown"]');
                    if (parentToggle) {
                        parentToggle.setAttribute('aria-expanded', 'false');
                    }
                }
            }
        });
        
        // Toggle menu hiện tại
        if (isShown) {
            menuElement.classList.remove('show');
            toggleElement.setAttribute('aria-expanded', 'false');
            // Xóa inline styles từ Bootstrap Popper
            menuElement.style.transform = '';
            menuElement.style.inset = '';
            menuElement.removeAttribute('data-popper-placement');
            console.log('Menu HIDDEN');
        } else {
            // Đảm bảo override Bootstrap Popper styles TRƯỚC KHI thêm class show
            menuElement.style.transform = 'none';
            menuElement.style.inset = 'auto';
            menuElement.style.top = 'calc(100% + 0.5rem)';
            menuElement.style.right = '0';
            menuElement.style.left = 'auto';
            menuElement.style.position = 'absolute';
            menuElement.style.margin = '0';
            menuElement.removeAttribute('data-popper-placement');
            menuElement.removeAttribute('data-popper-reference-hidden');
            menuElement.removeAttribute('data-popper-escaped');
            
            // Thêm class show
            menuElement.classList.add('show');
            toggleElement.setAttribute('aria-expanded', 'true');
            
            // Force reflow để đảm bảo styles được apply
            menuElement.offsetHeight;
            
            // Đảm bảo lại styles sau khi Bootstrap có thể can thiệp
            setTimeout(function() {
                menuElement.style.transform = 'none';
                menuElement.style.inset = 'auto';
                menuElement.style.top = 'calc(100% + 0.5rem)';
                menuElement.style.right = '0';
                menuElement.style.left = 'auto';
                menuElement.style.position = 'absolute';
            }, 10);
            
            console.log('Menu SHOWN');
            console.log('Menu classes after:', menuElement.classList.toString());
            console.log('Menu display after:', window.getComputedStyle(menuElement).display);
            console.log('Menu visibility after:', window.getComputedStyle(menuElement).visibility);
            console.log('Menu z-index:', window.getComputedStyle(menuElement).zIndex);
            console.log('Menu position:', window.getComputedStyle(menuElement).position);
            console.log('Menu top:', window.getComputedStyle(menuElement).top);
            console.log('Menu right:', window.getComputedStyle(menuElement).right);
            console.log('Menu getBoundingClientRect:', menuElement.getBoundingClientRect());
        }
        
        return false;
    };
    
    // Đóng dropdown khi click bên ngoài (chỉ thêm một lần)
    if (!window.dropdownClickHandlerAdded) {
        window.dropdownClickHandlerAdded = true;
        // Đợi một chút để đảm bảo các event handlers khác đã được thiết lập
        setTimeout(function() {
            document.addEventListener('click', function(e) {
                // Kiểm tra xem click có phải vào dropdown toggle không
                const clickedToggle = e.target.closest('[onclick*="toggleLoginDropdown"]') || 
                                     e.target.closest('.dropdown-toggle') ||
                                     e.target.closest('.action-item');
                
                if (clickedToggle && clickedToggle.classList.contains('dropdown-toggle')) {
                    // Click vào toggle, không đóng
                    return;
                }
                
                // Kiểm tra xem click có phải vào menu items không
                if (e.target.closest('.dropdown-menu')) {
                    // Click vào menu, không đóng
                    return;
                }
                
                // Click bên ngoài dropdown, đóng tất cả
                const openMenus = document.querySelectorAll('.dropdown-menu.show');
                if (openMenus.length > 0) {
                    console.log('Closing dropdowns - clicked outside');
                    openMenus.forEach(function(menu) {
                        menu.classList.remove('show');
                        const parentDropdown = menu.closest('.dropdown');
                        if (parentDropdown) {
                            const toggle = parentDropdown.querySelector('[data-bs-toggle="dropdown"]');
                            if (toggle) {
                                toggle.setAttribute('aria-expanded', 'false');
                            }
                        }
                    });
                }
            }, true); // Use capture phase để chạy sau
        }, 1000);
    }
</script>
