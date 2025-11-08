<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Header chung cho Customer Pages -->
<nav class="navbar navbar-expand-lg navbar-custom navbar-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/shop">
            <i class="bi bi-shop"></i> SmartShop
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link ${param.active == 'home' || param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/shop">
                        <i class="bi bi-house"></i> Trang chủ
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.active == 'orders' ? 'active' : ''}" href="${pageContext.request.contextPath}/customer/orders">
                        <i class="bi bi-cart-check"></i> Đơn hàng của tôi
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.active == 'cart' ? 'active' : ''}" href="${pageContext.request.contextPath}/cart">
                        <i class="bi bi-cart"></i> Giỏ hàng
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.active == 'profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/customer/profile">
                        <i class="bi bi-person"></i> Thông tin cá nhân
                    </a>
                </li>
            </ul>
            <ul class="navbar-nav">
                <c:if test="${not empty currentUser}">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle"></i> ${currentUser.fullName}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/customer/profile">
                                <i class="bi bi-person"></i> Thông tin cá nhân</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                <i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
                        </ul>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>

