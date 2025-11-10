<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<style>
    .breadcrumb-bar {
        background-color: #1a1a1a;
        padding: 0.75rem 0;
        border-bottom: 1px solid #4a4a4a;
    }
    
    .breadcrumb-text {
        color: #b0b0b0;
        font-size: 0.9rem;
    }
    
    .breadcrumb-link {
        color: #b0b0b0;
        text-decoration: none;
        transition: color 0.3s;
    }
    
    .breadcrumb-link:hover {
        color: #fff;
        text-decoration: none;
    }
    
    .breadcrumb-separator {
        color: #4a4a4a;
        margin: 0 0.5rem;
    }
    
    .breadcrumb-current {
        color: #fff;
    }
</style>

<div class="breadcrumb-bar">
    <div class="container">
        <div class="breadcrumb-text">
            <a href="${pageContext.request.contextPath}/home" class="breadcrumb-link">Trang chủ</a>
            <span class="breadcrumb-separator">/</span>
            <span class="breadcrumb-current">${param.currentPage != null ? param.currentPage : 'Trang chủ'}</span>
        </div>
    </div>
</div>

