<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartShop - Trang chủ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- Define functions IMMEDIATELY in head to ensure they're available when onclick handlers are evaluated -->
    <script>
        // Set context path
        window.contextPath = '${pageContext.request.contextPath}';
        
        // Define addToCompare function immediately (before compare.js loads)
        window.addToCompare = function(productID) {
            if (!productID) {
                alert('Lỗi: Không có ID sản phẩm');
                return Promise.reject('ProductID is required');
            }
            
            productID = parseInt(productID);
            const STORAGE_KEY = 'compare_products';
            const MAX_ITEMS = 2; // Chỉ so sánh 2 sản phẩm
            
            try {
                // Get current list from localStorage
                const stored = localStorage.getItem(STORAGE_KEY);
                let productIDs = stored ? JSON.parse(stored) : [];
                
                // Check max items
                if (productIDs.length >= MAX_ITEMS) {
                    alert('Bạn chỉ có thể so sánh tối đa ' + MAX_ITEMS + ' sản phẩm. Vui lòng xóa một sản phẩm trước khi thêm mới.');
                    return Promise.reject('Maximum compare items reached');
                }
                
                // Check if already in list
                if (productIDs.includes(productID)) {
                    alert('Sản phẩm này đã có trong danh sách so sánh');
                    return Promise.reject('Product already in compare list');
                }
                
                // Add to localStorage immediately
                productIDs.push(productID);
                localStorage.setItem(STORAGE_KEY, JSON.stringify(productIDs));
                
                // Update UI if possible
                const buttons = document.querySelectorAll('[data-compare-product-id="' + productID + '"]');
                buttons.forEach(function(btn) {
                    btn.classList.add('active');
                    if (btn.classList.contains('action-icon')) {
                        btn.style.color = '#28a745';
                        btn.style.borderColor = '#28a745';
                        btn.title = 'Đã thêm vào so sánh';
                    }
                });
                
                // Show alert
                alert('Đã thêm sản phẩm vào danh sách so sánh');
                
                // Nếu đã có đủ 2 sản phẩm, tự động mở modal so sánh
                if (productIDs.length === MAX_ITEMS) {
                    // Đợi một chút để đảm bảo UI đã cập nhật
                    setTimeout(function() {
                        if (typeof window.showCompareModal === 'function') {
                            window.showCompareModal();
                        }
                    }, 300);
                }
                
                // Return success immediately
                return Promise.resolve({ success: true, useLocalStorage: true });
            } catch (e) {
                console.error('Error in addToCompare:', e);
                alert('Có lỗi xảy ra khi thêm sản phẩm vào danh sách so sánh');
                return Promise.reject(e);
            }
        };
        
        // Define openProductModal function early as placeholder
        // Will be fully implemented when productModal.jsp is loaded at end of body
        // For now, make it try to load the full implementation if available
        // Placeholder will delegate to full implementation when available
        window.openProductModal = function(productID) {
            console.log('openProductModal called with productID:', productID);
            // Check if full implementation is available (will be set by productModal.jsp)
            if (window.openProductModalFull && typeof window.openProductModalFull === 'function') {
                console.log('Delegating to full implementation');
                return window.openProductModalFull(productID);
            }
            // Fallback: load product data
            console.warn('Full implementation not ready yet, loading product data...');
            fetch('${pageContext.request.contextPath}/product/api?id=' + productID)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP error! status: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        alert('Lỗi: ' + data.error);
                        return;
                    }
                    console.log('Product loaded, trying to show modal...');
                    // Wait a bit for modal element to be available
                    setTimeout(function() {
                        const modalElement = document.getElementById('productDetailModal');
                        console.log('Modal element:', modalElement);
                        console.log('Bootstrap available:', typeof bootstrap !== 'undefined');
                        
                        if (modalElement) {
                            // Try to populate modal fields if they exist
                            try {
                                const nameEl = document.getElementById('modalProductName');
                                const codeEl = document.getElementById('modalProductCode');
                                const imageEl = document.getElementById('modalProductImage');
                                const priceEl = document.getElementById('modalPriceCurrent');
                                
                                if (nameEl) nameEl.textContent = data.productName;
                                if (codeEl) codeEl.textContent = '#' + data.productID;
                                if (imageEl) imageEl.src = data.imageUrl || '';
                                if (priceEl) {
                                    priceEl.textContent = new Intl.NumberFormat('vi-VN', {
                                        style: 'currency',
                                        currency: 'VND'
                                    }).format(data.discountedPrice);
                                }
                            } catch (e) {
                                console.error('Error populating modal:', e);
                            }
                            
                            // Try to show modal using Bootstrap
                            if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                                try {
                                    const modal = new bootstrap.Modal(modalElement);
                                    modal.show();
                                    console.log('Modal shown using Bootstrap');
                                } catch (e) {
                                    console.error('Error showing modal with Bootstrap:', e);
                                    // Fallback: show manually
                                    modalElement.style.display = 'block';
                                    modalElement.classList.add('show');
                                    document.body.classList.add('modal-open');
                                    const backdrop = document.createElement('div');
                                    backdrop.className = 'modal-backdrop fade show';
                                    backdrop.id = 'productModalBackdrop';
                                    document.body.appendChild(backdrop);
                                }
                            } else {
                                // Bootstrap not available, show manually
                                console.warn('Bootstrap not available, showing modal manually');
                                modalElement.style.display = 'block';
                                modalElement.classList.add('show');
                                document.body.classList.add('modal-open');
                                const backdrop = document.createElement('div');
                                backdrop.className = 'modal-backdrop fade show';
                                backdrop.id = 'productModalBackdrop';
                                backdrop.onclick = function() {
                                    modalElement.style.display = 'none';
                                    modalElement.classList.remove('show');
                                    document.body.classList.remove('modal-open');
                                    this.remove();
                                };
                                document.body.appendChild(backdrop);
                            }
                        } else {
                            console.error('Modal element not found');
                            alert('Sản phẩm: ' + data.productName + '\nGiá: ' + new Intl.NumberFormat('vi-VN', {
                                style: 'currency',
                                currency: 'VND'
                            }).format(data.discountedPrice) + '\n\nModal chưa được tải. Vui lòng đợi và thử lại.');
                        }
                    }, 1000); // Wait 1 second for modal to be included (may need more time)
                    
                    // Also try again after 2 seconds
                    setTimeout(function() {
                        const modalElement = document.getElementById('productDetailModal');
                        if (modalElement && !modalElement.classList.contains('show')) {
                            console.log('Retrying to show modal after 2 seconds...');
                            // Use full implementation if available now
                            if (window.openProductModalFull && typeof window.openProductModalFull === 'function') {
                                window.openProductModalFull(productID);
                            }
                        }
                    }, 2000);
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Lỗi khi tải thông tin sản phẩm: ' + error.message);
                });
        };
        
        // Define addToCart function early
        if (typeof window.addToCart === 'undefined') {
            window.addToCart = function(productID, redirectUrl) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/cart';

                const productIDInput = document.createElement('input');
                productIDInput.type = 'hidden';
                productIDInput.name = 'productID';
                productIDInput.value = productID;
                form.appendChild(productIDInput);

                const quantityInput = document.createElement('input');
                quantityInput.type = 'hidden';
                quantityInput.name = 'quantity';
                quantityInput.value = 1;
                form.appendChild(quantityInput);

                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'add';
                form.appendChild(actionInput);

                if (redirectUrl) {
                    const redirectInput = document.createElement('input');
                    redirectInput.type = 'hidden';
                    redirectInput.name = 'redirect';
                    redirectInput.value = redirectUrl;
                    form.appendChild(redirectInput);
                }

                document.body.appendChild(form);
                form.submit();
            };
        }
        
        // Define toggleWishlist function early
        if (typeof window.toggleWishlist === 'undefined') {
            window.toggleWishlist = function(productID, redirectUrl) {
                let currentPath = window.location.pathname + window.location.search;
                if (!redirectUrl || redirectUrl === '${pageContext.request.contextPath}/home') {
                    redirectUrl = currentPath;
                }
                
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/wishlist';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'toggle';
                form.appendChild(actionInput);
                
                const productIDInput = document.createElement('input');
                productIDInput.type = 'hidden';
                productIDInput.name = 'productID';
                productIDInput.value = productID;
                form.appendChild(productIDInput);
                
                if (redirectUrl) {
                    const redirectInput = document.createElement('input');
                    redirectInput.type = 'hidden';
                    redirectInput.name = 'redirect';
                    redirectInput.value = redirectUrl;
                    form.appendChild(redirectInput);
                }
                
                document.body.appendChild(form);
                form.submit();
            };
        }
        
        // Define toggleChatbot function early as placeholder
        // Will be fully implemented when chatbot.jsp is loaded in footer
        window.toggleChatbot = window.toggleChatbot || function() {
            console.log('toggleChatbot placeholder called');
            const chatWindow = document.getElementById('chatbotWindow');
            if (chatWindow) {
                const isActive = chatWindow.classList.contains('active');
                chatWindow.classList.toggle('active');
                console.log('Chat window toggled manually, isActive now:', !isActive);
                
                if (!isActive) {
                    // Window is opening - try to call full implementation if available
                    // Otherwise wait a bit and try again
                    setTimeout(function() {
                        if (window.toggleChatbotFull && typeof window.toggleChatbotFull === 'function') {
                            console.log('Full implementation available, calling it');
                            // Don't call toggleChatbotFull here because window is already toggled
                            // Just re-initialize handlers
                            if (window.initChatbotButton && typeof window.initChatbotButton === 'function') {
                                window.initChatbotButton();
                            } else {
                                initChatbotHandlers();
                            }
                            
                            const input = document.getElementById('chatbotInput');
                            if (input) {
                                setTimeout(() => input.focus(), 100);
                            }
                        } else {
                            // Initialize handlers manually, but keep checking for full implementation
                            console.log('Full implementation not ready, using placeholder handlers');
                            initChatbotHandlers();
                            
                            // Keep retrying to get full implementation
                            let retryCount = 0;
                            const maxRetries = 10;
                            const retryInterval = setInterval(function() {
                                retryCount++;
                                if (window.initChatbotButton && typeof window.initChatbotButton === 'function') {
                                    console.log('Full implementation found after retry, re-initializing...');
                                    window.initChatbotButton();
                                    clearInterval(retryInterval);
                                } else if (retryCount >= maxRetries) {
                                    console.log('Max retries reached, stopping');
                                    clearInterval(retryInterval);
                                }
                            }, 200);
                            
                            const input = document.getElementById('chatbotInput');
                            if (input) {
                                setTimeout(() => input.focus(), 100);
                            }
                        }
                    }, 50);
                }
            } else {
                console.error('chatbotWindow not found - chatbot may not be loaded yet');
            }
            return false;
        };
        
        // Helper function to initialize chatbot handlers
        function initChatbotHandlers() {
            console.log('Initializing chatbot handlers from placeholder');
            
            // Initialize send button
            const sendButton = document.querySelector('#chatbotWidget .chatbot-send-btn');
            if (sendButton) {
                sendButton.onclick = function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    console.log('Send button clicked (placeholder handler)');
                    
                    // Try to call full implementation, with multiple retries
                    let retryCount = 0;
                    const maxRetries = 20; // Try for 2 seconds (20 * 100ms)
                    const trySend = function() {
                        if (typeof window.sendChatbotMessage === 'function') {
                            console.log('Calling full sendChatbotMessage implementation');
                            window.sendChatbotMessage();
                        } else {
                            retryCount++;
                            if (retryCount < maxRetries) {
                                console.log('sendChatbotMessage not available yet, retrying... (' + retryCount + '/' + maxRetries + ')');
                                setTimeout(trySend, 100);
                            } else {
                                console.error('sendChatbotMessage not available after max retries');
                                alert('Chatbot đang được tải. Vui lòng đợi và thử lại.');
                            }
                        }
                    };
                    
                    // Start trying immediately
                    trySend();
                    return false;
                };
                sendButton.style.pointerEvents = 'all';
                sendButton.style.cursor = 'pointer';
            }
            
            // Initialize close button
            const closeButton = document.querySelector('#chatbotWidget .chatbot-header .btn-close');
            if (closeButton) {
                closeButton.onclick = function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    console.log('Close button clicked (placeholder handler)');
                    const chatWindow = document.getElementById('chatbotWindow');
                    if (chatWindow) {
                        chatWindow.classList.remove('active');
                    }
                    return false;
                };
            }
        }
    </script>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #1a1a1a;
            color: #fff;
        }
        
        /* Hero Banner Section */
        .hero-banner {
            background: linear-gradient(135deg, #1a1a1a 0%, #2d1b3d 100%);
            padding: 4rem 0;
            position: relative;
            overflow: visible;
        }
        
        .hero-banner .row {
            display: flex;
            flex-wrap: wrap;
            margin-left: -15px;
            margin-right: -15px;
        }
        
        .hero-banner .col-lg-8 {
            flex: 0 0 66.666667%;
            max-width: 66.666667%;
            padding-left: 15px;
            padding-right: 15px;
        }
        
        .hero-content {
            position: relative;
            display: flex;
            flex-direction: column;
            border-radius: 15px;
            padding: 1rem;
            overflow: hidden;
        }
        
        /* Carousel làm background mờ cho toàn bộ khối */
        .hero-content::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: 0;
            background-image: var(--blur-bg-image, url('${pageContext.request.contextPath}/images/promotion-banner-1.png'));
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            filter: blur(8px);
            opacity: 0.4;
            transition: background-image 0.5s ease;
        }
        
        /* Overlay nhẹ để text dễ đọc */
        .hero-content::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: 1;
            background: rgba(26, 26, 26, 0.2);
        }
        
        /* Tất cả nội dung bên trong phải có z-index cao hơn */
        .hero-text-top,
        .promotion-carousel-hero,
        .feature-cards-below {
            position: relative;
            z-index: 2;
        }
        
        /* Text trên cùng */
        .hero-text-top {
            width: 100%;
            text-align: center;
            margin-bottom: 1rem;
            flex-shrink: 0;
        }
        
        /* Carousel ở giữa */
        .promotion-carousel-hero {
            position: relative;
            width: 100%;
            flex: 1;
            min-height: 200px;
            border-radius: 15px;
            overflow: hidden;
            margin-bottom: 1rem;
        }
        
        .promotion-carousel-hero .carousel {
            width: 100%;
            height: 100%;
        }
        
        .promotion-carousel-hero .carousel-inner {
            width: 100%;
            height: 100%;
        }
        
        .promotion-carousel-hero .carousel-item {
            width: 100%;
            height: 100%;
            position: relative; /* Cần thiết cho fallback absolute positioning */
        }
        
        .promotion-banner-hero-img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block !important;
            min-height: 400px;
            position: relative;
            z-index: 1;
            filter: none !important;
            opacity: 1 !important;
        }
        
        /* Fallback khi ảnh không load được */
        .promotion-banner-fallback {
            width: 100%;
            height: 100%;
            min-height: 400px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            font-size: 2rem;
            font-weight: bold;
            text-align: center;
            padding: 2rem;
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: 1;
        }
        
        /* Promotion Banner với gradient background */
        .promotion-banner-gradient {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            position: relative;
            overflow: hidden;
        }
        
        .promotion-banner-gradient::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
            animation: rotate 20s linear infinite;
        }
        
        @keyframes rotate {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
        
        .promotion-banner-content {
            position: relative;
            z-index: 2;
            text-align: center;
            color: white;
            padding: 2rem;
        }
        
        .promotion-banner-title {
            font-size: 2.5rem;
            font-weight: bold;
            margin-bottom: 0.5rem;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        
        .promotion-banner-discount {
            font-size: 4rem;
            font-weight: bold;
            margin: 1rem 0;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        
        .promotion-banner-description {
            font-size: 1.2rem;
            opacity: 0.9;
            text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
        }
        
        /* Gradient variants cho các promotions khác nhau */
        .promotion-gradient-1 {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .promotion-gradient-2 {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        .promotion-gradient-3 {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        .promotion-gradient-4 {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
        
        .promotion-gradient-5 {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }
        
        .promotion-gradient-default {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        /* Overlay để text dễ đọc - ẩn để ảnh carousel hiển thị tự nhiên */
        .carousel-background-overlay {
            display: none;
        }
        
        /* Ẩn carousel controls (prev/next buttons) */
        .promotion-carousel-hero .carousel-control-prev,
        .promotion-carousel-hero .carousel-control-next {
            display: none !important;
        }
        
        /* Style cho carousel indicators (3 gạch ngang) */
        .promotion-carousel-hero .carousel-indicators {
            position: absolute;
            bottom: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 5;
            margin: 0;
            padding: 0;
            display: flex;
            gap: 10px;
            justify-content: center;
            align-items: center;
        }
        
        .promotion-carousel-hero .carousel-indicators button {
            width: 50px;
            height: 4px;
            background-color: rgba(255, 255, 255, 0.5);
            border: none;
            border-radius: 2px;
            cursor: pointer;
            transition: all 0.3s ease;
            padding: 0;
            margin: 0;
            opacity: 0.5;
        }
        
        .promotion-carousel-hero .carousel-indicators button.active {
            background-color: rgba(255, 255, 255, 1);
            opacity: 1;
            width: 60px;
        }
        
        .promotion-carousel-hero .carousel-indicators button:hover {
            background-color: rgba(255, 255, 255, 0.8);
            opacity: 0.8;
        }
        
        /* Nội dung hiển thị phía trên carousel */
        .hero-content-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: 3;
            padding: 2rem;
            width: 100%;
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            pointer-events: none;
        }
        
        .hero-content-overlay * {
            pointer-events: auto;
        }
        
        .hero-text {
            font-size: 3rem;
            font-weight: bold;
            line-height: 1.2;
            margin-bottom: 1rem;
            width: 100%;
            display: block;
            text-align: center;
        }
        
        .hero-text-main {
            color: #fff;
            text-shadow: 0 0 20px rgba(139, 92, 246, 0.8), 0 0 40px rgba(139, 92, 246, 0.5), 2px 2px 4px rgba(0, 0, 0, 0.8);
        }
        
        .hero-text-sub {
            color: #ff6b35;
            text-shadow: 0 0 20px rgba(255, 107, 53, 0.8), 2px 2px 4px rgba(0, 0, 0, 0.8);
        }
        
        .hero-dots {
            color: #8b5cf6;
        }
        
        .hero-subtitle {
            font-size: 1.2rem;
            color: #fff;
            margin-bottom: 0;
            width: 100%;
            display: block;
            text-align: center;
        }
        
        .feature-cards {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
            margin-bottom: 2rem;
            width: 100%;
        }
        
        /* Feature cards nằm dưới carousel */
        .feature-cards-below {
            display: flex;
            gap: 0.75rem;
            flex-wrap: wrap;
            width: 100%;
            justify-content: center;
            flex-shrink: 0;
        }
        
        .feature-card {
            background: rgba(139, 92, 246, 0.3);
            border: 1px solid rgba(139, 92, 246, 0.6);
            border-radius: 10px;
            padding: 1rem 1.5rem;
            font-weight: bold;
            color: #fff;
            font-size: 0.9rem;
            backdrop-filter: blur(5px);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
        }
        
        .hero-promo-section {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        
        
        .hero-promo-cards {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        
        .promo-card {
            border-radius: 15px;
            padding: 1.5rem;
            color: white;
            position: relative;
            overflow: hidden;
            min-height: 200px;
        }
        
        .promo-card-with-bg {
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
        }
        
        .promo-card-bg-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.5) 100%);
            z-index: 1;
            transition: opacity 0.3s ease;
        }
        
        .promo-card:hover .promo-card-bg-overlay {
            opacity: 0.5;
        }
        
        .promo-card-title {
            font-size: 1.2rem;
            font-weight: bold;
            margin-bottom: 0.5rem;
        }
        
        .promo-card-text {
            font-size: 0.9rem;
            margin-bottom: 0.5rem;
            opacity: 0.9;
        }
        
        .promo-card-price {
            font-size: 1.1rem;
            font-weight: bold;
            margin-bottom: 1rem;
        }
        
        
        .promo-card-btn {
            background: white;
            color: #dc3545;
            border: none;
            padding: 0.5rem 1.5rem;
            border-radius: 20px;
            font-weight: bold;
            text-decoration: none;
            display: inline-block;
            transition: transform 0.3s;
        }
        
        .promo-card-btn:hover {
            transform: scale(1.05);
            color: #dc3545;
        }
        
        /* Categories Grid */
        .categories-grid-section {
            padding: 3rem 0;
        }
        
        .section-title {
            font-size: 2rem;
            font-weight: bold;
            color: white;
            margin-bottom: 2rem;
        }
        
        .category-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            text-decoration: none;
            color: white;
            transition: transform 0.3s;
        }
        
        .category-item:hover {
            transform: translateY(-5px);
            color: white;
            text-decoration: none;
        }
        
        .category-icon-wrapper {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 0.5rem;
        }
        
        .category-icon-wrapper i {
            font-size: 2rem;
            color: white;
        }
        
        .category-name {
            font-size: 0.9rem;
            text-align: center;
        }
        
        /* Golden Hour Deal */
        .golden-hour-deal {
            background: linear-gradient(135deg, #1a1a1a 0%, #2d1b3d 100%);
            padding: 3rem 0;
            border-radius: 15px;
            margin: 3rem 0;
        }
        
        .countdown-timer {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin: 1.5rem 0;
        }
        
        .time-box {
            background: #2c2c2c;
            border: 2px solid #8b5cf6;
            border-radius: 10px;
            padding: 1rem;
            text-align: center;
            min-width: 80px;
        }
        
        .time-box span {
            display: block;
            font-size: 2rem;
            font-weight: bold;
            color: #ff6b35;
        }
        
        .deal-stages {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin-top: 2rem;
        }
        
        .deal-stage {
            background: #2c2c2c;
            border: 2px solid #4a4a4a;
            border-radius: 10px;
            padding: 1rem 1.5rem;
            text-align: center;
        }
        
        .deal-stage.active {
            border-color: #ff6b35;
            background: rgba(255, 107, 53, 0.1);
        }
        
        /* Carousel Controls Styling */
        .promotion-carousel-hero .carousel-control-prev,
        .promotion-carousel-hero .carousel-control-next {
            width: 40px;
            height: 40px;
            background: rgba(0, 0, 0, 0.5);
            border-radius: 50%;
            top: 50%;
            transform: translateY(-50%);
            opacity: 0.7;
        }
        
        .promotion-carousel-hero .carousel-control-prev {
            left: 10px;
        }
        
        .promotion-carousel-hero .carousel-control-next {
            right: 10px;
        }
        
        .promotion-carousel-hero .carousel-control-prev:hover,
        .promotion-carousel-hero .carousel-control-next:hover {
            opacity: 1;
            background: rgba(0, 0, 0, 0.7);
        }
        
        .promotion-carousel-hero .carousel-control-prev-icon,
        .promotion-carousel-hero .carousel-control-next-icon {
            width: 20px;
            height: 20px;
        }
        
        /* Best-selling Products */
        .best-selling-products {
            padding: 3rem 0;
        }
        
        .section-title-large {
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
        }
        
        .section-nav {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .section-nav-link {
            color: #b0b0b0;
            text-decoration: none;
            transition: color 0.3s;
        }
        
        .section-nav-link:hover,
        .section-nav-link.active {
            color: white;
            text-decoration: none;
        }
        
        .products-scroll {
            display: flex;
            gap: 1.5rem;
            overflow-x: auto;
            padding: 1rem 0;
            scroll-behavior: smooth;
        }
        
        .products-scroll::-webkit-scrollbar {
            height: 8px;
        }
        
        .products-scroll::-webkit-scrollbar-track {
            background: #2c2c2c;
            border-radius: 10px;
        }
        
        .products-scroll::-webkit-scrollbar-thumb {
            background: #8b5cf6;
            border-radius: 10px;
        }
        
        .product-card-home {
            min-width: 250px;
            background: #2c2c2c;
            border-radius: 15px;
            overflow: hidden;
            transition: transform 0.3s;
            display: flex;
            flex-direction: column;
            height: 100%;
            position: relative;
            z-index: 1;
            /* Đảm bảo product card không bị chatbot che */
        }
        
        .product-card-home:hover {
            transform: translateY(-5px);
        }
        
        .product-image-home {
            width: 100%;
            height: 200px;
            min-width: 100%;
            min-height: 200px;
            object-fit: cover;
            object-position: center;
            display: block;
            flex-shrink: 0;
        }
        
        .product-info-home {
            padding: 1rem;
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }
        
        .product-info-home .add-to-cart-btn {
            margin-top: auto;
        }
        
        .product-name-home {
            font-weight: bold;
            color: white;
            margin-bottom: 0.5rem;
            font-size: 1rem;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            line-height: 1.4;
            min-height: 2.8rem;
            max-height: 2.8rem;
        }
        
        .product-name-home a {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .product-price-home {
            font-size: 1.3rem;
            font-weight: bold;
            color: #dc3545;
            margin-bottom: 0.25rem;
        }
        
        .product-old-price-home {
            font-size: 0.9rem;
            color: #6a6a6a;
            text-decoration: line-through;
            margin-bottom: 0.5rem;
        }
        
        .product-status {
            font-size: 0.85rem;
            color: #b0b0b0;
            margin-bottom: 0.5rem;
        }
        
        .add-to-cart-btn {
            width: 100%;
            background: #dc3545;
            color: white;
            border: none;
            padding: 0.5rem;
            border-radius: 8px;
            font-weight: bold;
            position: relative;
            z-index: 2;
            /* Z-index cao hơn để đảm bảo có thể click được */
            pointer-events: auto;
            /* Đảm bảo có thể click */
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .add-to-cart-btn:hover {
            background: #c82333;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
        }
        
        .add-to-cart-btn:active {
            transform: translateY(0);
        }
        
        .product-discount-badge {
            position: absolute;
            top: 10px;
            right: 10px;
            background: #dc3545;
            color: white;
            padding: 0.25rem 0.75rem;
            border-radius: 5px;
            font-size: 0.85rem;
            font-weight: bold;
            z-index: 1;
        }
        
        /* Products Grid Section (from shop) */
        .products-grid-section {
            padding: 3rem 0;
        }
        
        .product-card {
            border: none;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.3);
            transition: transform 0.3s, box-shadow 0.3s;
            height: 100%;
            background: #2c2c2c;
            display: flex;
            flex-direction: column;
            position: relative;
            z-index: 1;
            /* Đảm bảo product card không bị chatbot che */
        }
        
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(139, 92, 246, 0.3);
        }
        
        .product-image {
            width: 100%;
            height: 250px;
            min-width: 100%;
            min-height: 250px;
            object-fit: cover;
            object-position: center;
            background: #1a1a1a;
            display: block;
            flex-shrink: 0;
        }
        
        .product-card .card-body {
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }
        
        .product-card .card-title {
            font-size: 1rem;
            font-weight: 600;
            color: white;
            margin-bottom: 0.5rem;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            line-height: 1.4;
            min-height: 2.8rem;
            max-height: 2.8rem;
        }
        
        .product-card .card-title a {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .product-price {
            font-size: 1.5rem;
            font-weight: bold;
            color: #dc3545;
        }
        
        /* Bottom Service Bar */
        .service-bar {
            background: #2c2c2c;
            padding: 3rem 0;
            margin-top: 3rem;
        }
        
        .service-item {
            text-align: center;
            color: white;
        }
        
        .service-icon {
            font-size: 2.5rem;
            color: #dc3545;
            margin-bottom: 1rem;
        }
        
        /* Floating Buttons */
        .floating-buttons {
            position: fixed;
            bottom: 100px;
            right: 2rem;
            z-index: 998; /* Thấp hơn chatbot (z-index: 901) */
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        
        .floating-btn {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #dc3545;
            color: white;
            border: none;
            font-size: 1.5rem;
            cursor: pointer;
            box-shadow: 0 4px 15px rgba(220, 53, 69, 0.4);
            transition: transform 0.3s;
        }
        
        .floating-btn:hover {
            transform: scale(1.1);
        }
    </style>
</head>
<body>
    <jsp:include page="/views/common/header.jsp">
        <jsp:param name="active" value="home" />
    </jsp:include>

    <!-- Hero Banner Section -->
    <section class="hero-banner">
        <div class="container">
            <div class="row">
                <div class="col-lg-8 hero-content">
                    <!-- Text trên cùng -->
                    <div class="hero-text-top">
                        <div class="hero-text">
                            <span class="hero-text-main">KHAI TRƯƠNG</span>
                            <span class="hero-dots"> • </span>
                            <span class="hero-text-sub">CỬA HÀNG</span>
                        </div>
                        <p class="hero-subtitle">Giảm giá lên đến 30% - Ưu đãi đặc biệt</p>
                    </div>
                    
                    <!-- Carousel ở giữa -->
                    <div class="promotion-carousel-hero">
                        <div id="promotionCarouselHero" class="carousel slide" data-bs-ride="carousel" data-bs-interval="4000">
                            <div class="carousel-inner">
                                <!-- Banner 1 -->
                                <div class="carousel-item active">
                                    <img src="${pageContext.request.contextPath}/images/promotion-banner-1.png" 
                                         class="d-block w-100 promotion-banner-hero-img" 
                                         alt="Khuyến mãi 1"
                                         data-carousel-index="1"
                                         onerror="if(typeof handleBannerImageError === 'function') handleBannerImageError(this, 1); else this.style.display='none';">
                                </div>
                                <!-- Banner 2 -->
                                <div class="carousel-item">
                                    <img src="${pageContext.request.contextPath}/images/promotion-banner-2.png" 
                                         class="d-block w-100 promotion-banner-hero-img" 
                                         alt="Khuyến mãi 2"
                                         data-carousel-index="2"
                                         onerror="if(typeof handleBannerImageError === 'function') handleBannerImageError(this, 2); else this.style.display='none';">
                                </div>
                                <!-- Banner 3 -->
                                <div class="carousel-item">
                                    <img src="${pageContext.request.contextPath}/images/promotion-banner-3.jpg" 
                                         class="d-block w-100 promotion-banner-hero-img" 
                                         alt="Khuyến mãi 3"
                                         data-carousel-index="3"
                                         onerror="if(typeof handleBannerImageError === 'function') handleBannerImageError(this, 3); else this.style.display='none';">
                                </div>
                            </div>
                            <!-- Carousel Indicators -->
                            <div class="carousel-indicators">
                                <button type="button" data-bs-target="#promotionCarouselHero" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
                                <button type="button" data-bs-target="#promotionCarouselHero" data-bs-slide-to="1" aria-label="Slide 2"></button>
                                <button type="button" data-bs-target="#promotionCarouselHero" data-bs-slide-to="2" aria-label="Slide 3"></button>
                            </div>
                        </div>
                        <!-- Overlay để text dễ đọc -->
                        <div class="carousel-background-overlay"></div>
                    </div>
                    
                    <!-- Feature cards ở dưới -->
                    <div class="feature-cards-below">
                        <div class="feature-card">BẢO HÀNH NHANH CHÓNG</div>
                        <div class="feature-card">TRẢ GÓP 0%</div>
                        <div class="feature-card">FREESHIP</div>
                        <div class="feature-card">CHÍNH HÃNG 100%</div>
                    </div>
                </div>
                <div class="col-lg-4 hero-promo-section">
                    <!-- Promo Cards với background từ ảnh sản phẩm -->
                    <div class="hero-promo-cards">
                        <c:if test="${not empty featuredProducts}">
                            <c:forEach var="product" items="${featuredProducts}" varStatus="status">
                                <div class="promo-card promo-card-with-bg" 
                                     data-promo-index="${status.index}"
                                     <c:if test="${not empty product.imageUrl}">data-product-image="${product.imageUrl}"</c:if>
                                     style="position: relative; overflow: hidden;">
                                    <div class="promo-card-bg-overlay"></div>
                                    <div class="promo-card-content" style="position: relative; z-index: 2;">
                                        <h5 class="promo-card-title">${product.productName != null ? product.productName : 'SẢN PHẨM'}</h5>
                                        <p class="promo-card-text">Khai trương cửa hàng - Giảm lên đến 30%</p>
                                        <p class="promo-card-price">
                                            Giá chỉ từ 
                                            <fmt:formatNumber value="${product.price}" type="currency" 
                                                currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                        </p>
                                        <a href="javascript:void(0);" 
                                           onclick="openProductModal(${product.productID})"
                                           class="btn promo-card-btn">XEM NGAY</a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Main Content -->
    <div class="container my-5">
        <!-- Categories Grid -->
        <section class="mb-5 categories-grid-section">
            <h2 class="section-title text-center mb-4"><i class="bi bi-grid-3x3-gap-fill"></i> Danh mục sản phẩm</h2>
            <div class="row g-3 justify-content-center">
                <c:if test="${not empty categories}">
                    <c:forEach var="category" items="${categories}">
                        <div class="col-lg-1 col-md-2 col-sm-3 col-4">
                            <a href="${pageContext.request.contextPath}/shop?category=${category.categoryID}" class="category-item">
                                <div class="category-icon-wrapper">
                                    <i class="bi bi-tag"></i>
                                </div>
                                <span class="category-name">${category.categoryName}</span>
                            </a>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        </section>

        <!-- Golden Hour Deal Section -->
        <section class="mb-5 golden-hour-deal">
            <h2 class="section-title text-center mb-4"><i class="bi bi-lightning-fill text-warning"></i> KHAI TRƯƠNG CỬA HÀNG</h2>
            <div class="text-center mb-3">
                <p class="text-white-50 mb-1">Nhanh lên nào! Sự kiện khai trương sẽ kết thúc sau</p>
                <div id="countdown" class="countdown-timer">
                    <div class="time-box"><span id="days">00</span> Ngày</div>
                    <div class="time-box"><span id="hours">00</span> Giờ</div>
                    <div class="time-box"><span id="minutes">00</span> Phút</div>
                    <div class="time-box"><span id="seconds">00</span> Giây</div>
                </div>
            </div>
            <div class="deal-stages">
                <div class="deal-stage active">
                    <span>7/11 - 7/12</span>
                    <small>Đang diễn ra</small>
                </div>
                <div class="deal-stage">
                    <span>11/11</span>
                    <small>Sắp diễn ra</small>
                </div>
                <div class="deal-stage">
                    <span>12/12</span>
                    <small>Sắp diễn ra</small>
                </div>
            </div>
            
        </section>

        <!-- Best-selling Products Section -->
        <section class="mb-5 best-selling-products">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="section-title-large mb-0 text-primary"><i class="bi bi-fire"></i> Sản phẩm bán chạy</h2>
                <div class="section-nav">
                    <a href="#" class="section-nav-link active">Sản phẩm gợi ý</a>
                    <span style="color: #4a4a4a;">•</span>
                    <a href="${pageContext.request.contextPath}/shop?category=4" class="section-nav-link">Phụ Kiện</a>
                    <span style="color: #4a4a4a;">•</span>
                    <a href="${pageContext.request.contextPath}/shop?category=5" class="section-nav-link">Âm Thanh</a>
                    <span style="color: #4a4a4a;">•</span>
                    <a href="${pageContext.request.contextPath}/shop?category=6" class="section-nav-link">Smart Watch</a>
                    <a href="${pageContext.request.contextPath}/shop" class="section-nav-link" style="margin-left: 1rem;">
                        Xem thêm <i class="bi bi-chevron-right"></i>
                    </a>
                </div>
            </div>
            
            <div class="products-scroll" id="productsScroll">
                <c:forEach var="product" items="${bestSellingProducts}">
                    <div class="product-card-home" style="position: relative;">
                        <c:if test="${product.price != null}">
                            <span class="product-discount-badge">-17%</span>
                        </c:if>
                        <c:choose>
                            <c:when test="${not empty product.imageUrl}">
                                <img src="${product.imageUrl}" class="product-image-home" alt="${product.productName}">
                            </c:when>
                            <c:otherwise>
                                <div class="product-image-home d-flex align-items-center justify-content-center bg-light">
                                    <i class="bi bi-image" style="font-size: 3rem; color: #ccc;"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="product-info-home">
                            <div class="product-name-home">
                                <a href="javascript:void(0);" 
                                   onclick="openProductModal(${product.productID})"
                                   style="color: white; text-decoration: none;">
                                    ${product.productName}
                                </a>
                            </div>
                            <div class="product-price-home">
                                <fmt:formatNumber value="${product.price}" type="currency" 
                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                            </div>
                            <div class="product-old-price-home">
                                <fmt:formatNumber value="${product.price * 1.2}" type="currency" 
                                    currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                            </div>
                            <div class="product-status">Vừa mở bán</div>
                            <button class="btn add-to-cart-btn" 
                                    onclick="addToCart(${product.productID}, '${pageContext.request.contextPath}/home')"
                                    style="position: relative; z-index: 3; pointer-events: auto; cursor: pointer;">
                                Thêm vào giỏ
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>
        
        <!-- Recommended Products Based on Views Section -->
        <c:if test="${not empty recommendedProducts}">
            <section class="mb-5 best-selling-products">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="section-title-large mb-0 text-success"><i class="bi bi-star-fill"></i> Sản phẩm gợi ý dựa trên lượt xem</h2>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-success">
                        Xem thêm <i class="bi bi-arrow-right"></i>
                    </a>
                </div>
                
                <div class="products-scroll">
                    <c:forEach var="product" items="${recommendedProducts}">
                        <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                            <div class="product-card-home" style="position: relative;">
                                <c:choose>
                                    <c:when test="${not empty product.imageUrl}">
                                        <img src="${product.imageUrl}" class="product-image-home" alt="${product.productName}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="product-image-home d-flex align-items-center justify-content-center bg-light">
                                            <i class="bi bi-image" style="font-size: 3rem; color: #ccc;"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="product-info-home">
                                    <div class="product-name-home">
                                        <a href="javascript:void(0);" 
                                           onclick="openProductModal(${product.productID})"
                                           style="color: white; text-decoration: none;">
                                            ${product.productName}
                                        </a>
                                    </div>
                                    <div class="product-price-home">
                                        <fmt:formatNumber value="${product.price}" type="currency" 
                                            currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                    </div>
                                    <div class="product-status">Được xem nhiều</div>
                                    <button class="btn add-to-cart-btn" 
                                            onclick="addToCart(${product.productID}, '${pageContext.request.contextPath}/home')">
                                        Thêm vào giỏ
                                    </button>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </section>
        </c:if>
        
        <!-- All Products Grid Section -->
        <section class="products-grid-section">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="section-title-large mb-0"><i class="bi bi-shop"></i> Tất cả sản phẩm</h2>
                <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline-primary">
                    Xem tất cả <i class="bi bi-arrow-right"></i>
                </a>
            </div>
            
            <c:if test="${not empty products}">
                <div class="row g-4">
                    <c:forEach var="product" items="${products}">
                        <div class="col-md-3 col-sm-6">
                            <div class="card product-card">
                                <c:if test="${product.special}">
                                    <span class="badge bg-warning text-dark" style="position: absolute; top: 10px; right: 10px; z-index: 1;">
                                        <i class="bi bi-star-fill"></i> Đặc biệt
                                    </span>
                                </c:if>
                                <c:choose>
                                    <c:when test="${not empty product.imageUrl}">
                                        <img src="${product.imageUrl}" class="product-image" alt="${product.productName}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="product-image d-flex align-items-center justify-content-center">
                                            <i class="bi bi-image" style="font-size: 3rem; color: #ccc;"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="card-body">
                                    <h5 class="card-title">
                                        <a href="javascript:void(0);" 
                                           onclick="openProductModal(${product.productID})"
                                           class="text-decoration-none text-white">
                                            ${product.productName}
                                        </a>
                                    </h5>
                                    <p class="card-text text-muted small">
                                        ${product.categoryName != null ? product.categoryName : 'Chưa phân loại'}
                                    </p>
                                    <div class="mb-2">
                                        <span class="product-price">
                                            <fmt:formatNumber value="${product.price}" type="currency" 
                                                currencyCode="VND" currencySymbol="₫" groupingUsed="true"/>
                                        </span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <c:choose>
                                            <c:when test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                                <span class="badge bg-success">Còn hàng (${product.stock})</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Hết hàng</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="d-grid gap-2" style="position: relative; z-index: 2; pointer-events: auto;">
                                        <c:if test="${product.stockStatus == 'InStock' && product.stock > 0}">
                                            <button class="btn btn-primary btn-sm" 
                                                    onclick="addToCart(${product.productID}, '${pageContext.request.contextPath}/home')"
                                                    style="position: relative; z-index: 3; pointer-events: auto; cursor: pointer;">
                                                <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                                            </button>
                                        </c:if>
                                        <div class="btn-group" role="group" style="position: relative; z-index: 3; pointer-events: auto;">
                                            <button class="btn btn-outline-secondary btn-sm" 
                                                    onclick="openProductModal(${product.productID})"
                                                    title="Xem chi tiết"
                                                    style="position: relative; z-index: 4; pointer-events: auto; cursor: pointer;">
                                                <i class="bi bi-eye"></i> Xem
                                            </button>
                                            <button type="button" class="btn btn-outline-info btn-sm compare-btn" 
                                                    data-compare-product-id="${product.productID}"
                                                    onclick="if(typeof window.addToCompare === 'function'){window.addToCompare(${product.productID});}else{alert('Chức năng so sánh đang tải, vui lòng thử lại sau vài giây.');}"
                                                    style="position: relative; z-index: 4; pointer-events: auto; cursor: pointer;"
                                                    title="So sánh sản phẩm">
                                                <i class="bi bi-arrow-left-right"></i>
                                            </button>
                                            <button class="btn btn-outline-danger btn-sm" 
                                                    onclick="toggleWishlist(${product.productID})"
                                                    title="Thêm vào yêu thích"
                                                    style="position: relative; z-index: 4; pointer-events: auto; cursor: pointer;">
                                                <i class="bi bi-heart"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Product pagination" class="mt-4">
                        <ul class="pagination justify-content-center">
                            <c:if test="${currentPage > 1}">
                                <li class="page-item">
                                    <a class="page-link" href="${pageContext.request.contextPath}/home?page=${currentPage - 1}${categoryID > 0 ? '&category=' : ''}${categoryID > 0 ? categoryID : ''}${searchKeyword != null ? '&search=' : ''}${searchKeyword != null ? searchKeyword : ''}">
                                        <i class="bi bi-chevron-left"></i>
                                    </a>
                                </li>
                            </c:if>
                            
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/home?page=${i}${categoryID > 0 ? '&category=' : ''}${categoryID > 0 ? categoryID : ''}${searchKeyword != null ? '&search=' : ''}${searchKeyword != null ? searchKeyword : ''}">
                                            ${i}
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${i == currentPage - 3 || i == currentPage + 3}">
                                    <li class="page-item disabled">
                                        <span class="page-link">...</span>
                                    </li>
                                </c:if>
                            </c:forEach>
                            
                            <c:if test="${currentPage < totalPages}">
                                <li class="page-item">
                                    <a class="page-link" href="${pageContext.request.contextPath}/home?page=${currentPage + 1}${categoryID > 0 ? '&category=' : ''}${categoryID > 0 ? categoryID : ''}${searchKeyword != null ? '&search=' : ''}${searchKeyword != null ? searchKeyword : ''}">
                                        <i class="bi bi-chevron-right"></i>
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </c:if>
            </c:if>
        </section>
    </div>

    <!-- Bottom Service Bar -->
    <section class="service-bar">
        <div class="container">
            <div class="row g-4">
                <div class="col-lg-2 col-md-4 col-sm-6">
                    <div class="service-item">
                        <i class="bi bi-truck service-icon"></i>
                        <p>GIAO HỎA TỐC<br><small>Nội thành Đà Nẵng trong 4h</small></p>
                    </div>
                </div>
                <div class="col-lg-2 col-md-4 col-sm-6">
                    <div class="service-item">
                        <i class="bi bi-credit-card service-icon"></i>
                        <p>TRẢ GÓP ƯU ĐÃI<br><small>Hỗ trợ vay với lãi suất thấp</small></p>
                    </div>
                </div>
                <div class="col-lg-2 col-md-4 col-sm-6">
                    <div class="service-item">
                        <i class="bi bi-fire service-icon"></i>
                        <p>DEAL HOT<br><small>Ưu đãi đặc biệt</small></p>
                    </div>
                </div>
                <div class="col-lg-2 col-md-4 col-sm-6">
                    <div class="service-item">
                        <i class="bi bi-arrow-repeat service-icon"></i>
                        <p>MIỄN PHÍ ĐỔI TRẢ<br><small>Trong vòng 30 ngày miễn phí</small></p>
                    </div>
                </div>
                <div class="col-lg-2 col-md-4 col-sm-6">
                    <div class="service-item">
                        <i class="bi bi-headset service-icon"></i>
                        <p>HỖ TRỢ 24/7<br><small>Hỗ trợ khách hàng 24/7</small></p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Product Detail Modal - Include BEFORE footer to ensure it's loaded early -->
    <jsp:include page="/views/common/productModal.jsp" />
    
    <jsp:include page="/views/common/footer.jsp" />

    <!-- Floating Action Buttons (Scroll to top và Cart) -->
    <div class="floating-buttons">
        <button class="btn btn-danger rounded-circle mb-2" onclick="window.scrollTo({top: 0, behavior: 'smooth'})">
            <i class="bi bi-arrow-up"></i>
        </button>
        <a href="${pageContext.request.contextPath}/cart" class="btn btn-danger rounded-circle">
            <i class="bi bi-cart"></i>
        </a>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Functions are already defined in <head> section
        
        // Set background images for promo cards from product images
        function setPromoCardBackgrounds() {
            const promoCards = document.querySelectorAll('.promo-card-with-bg[data-product-image]');
            promoCards.forEach(function(card) {
                const imageUrl = card.getAttribute('data-product-image');
                if (imageUrl) {
                    card.style.backgroundImage = 'url(' + imageUrl + ')';
                    card.style.backgroundSize = 'cover';
                    card.style.backgroundPosition = 'center';
                    card.style.backgroundRepeat = 'no-repeat';
                }
            });
        }
        
        // Set hero-content height bằng với promo cards
        function setCarouselHeight() {
            const heroContent = document.querySelector('.hero-content');
            const promoCardsContainer = document.querySelector('.hero-promo-cards');
            
            if (heroContent && promoCardsContainer) {
                // Lấy chiều cao của promo cards
                const promoCardsHeight = promoCardsContainer.offsetHeight;
                
                // Set chiều cao cho hero-content = chiều cao của promo cards
                heroContent.style.height = promoCardsHeight + 'px';
                heroContent.style.minHeight = promoCardsHeight + 'px';
                
                console.log('Hero content height set to:', promoCardsHeight, 'px (matching promo cards)');
            } else {
                // Retry nếu chưa có
                setTimeout(setCarouselHeight, 200);
            }
        }
        
        // Cập nhật background mờ khi carousel thay đổi
        function updateBlurredBackground() {
            const carousel = document.getElementById('promotionCarouselHero');
            const heroContent = document.querySelector('.hero-content');
            
            if (!carousel || !heroContent) return;
            
            // Lấy ảnh đang active
            const activeItem = carousel.querySelector('.carousel-item.active');
            if (activeItem) {
                const activeImg = activeItem.querySelector('.promotion-banner-hero-img');
                if (activeImg && activeImg.src) {
                    // Cập nhật background cho hero-content
                    heroContent.style.setProperty('--blur-bg-image', 'url(' + activeImg.src + ')');
                }
            }
        }
        
        // Lắng nghe sự kiện khi carousel slide
        function setupCarouselBackgroundUpdate() {
            const carousel = document.getElementById('promotionCarouselHero');
            if (carousel) {
                carousel.addEventListener('slid.bs.carousel', function() {
                    updateBlurredBackground();
                });
                // Cập nhật lần đầu
                updateBlurredBackground();
            }
        }
        
        // Hàm xử lý khi ảnh banner không load được
        function handleBannerImageError(img, index) {
            console.log('Banner image error for index:', index);
            
            // Ẩn ảnh lỗi
            img.style.display = 'none';
            
            // Tìm carousel item chứa ảnh
            const carouselItem = img.closest('.carousel-item');
            if (!carouselItem) return;
            
            // Kiểm tra xem đã có fallback chưa
            if (carouselItem.querySelector('.promotion-banner-fallback')) {
                return; // Đã có fallback rồi
            }
            
            // Tạo fallback gradient (chỉ overlay, không có text)
            const fallback = document.createElement('div');
            fallback.className = 'promotion-banner-fallback';
            
            // Chọn gradient theo index
            const gradients = [
                'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
            ];
            fallback.style.background = gradients[index - 1] || gradients[0];
            
            // Không thêm text, chỉ giữ gradient overlay
            fallback.innerHTML = '';
            
            carouselItem.appendChild(fallback);
            setCarouselHeight();
        }
        
        // Initialize on page load
        window.addEventListener('DOMContentLoaded', function() {
            setPromoCardBackgrounds();
            setupCarouselBackgroundUpdate();
            
            // Kiểm tra và xử lý các ảnh banner
            const carouselImages = document.querySelectorAll('.promotion-banner-hero-img');
            console.log('Found carousel images:', carouselImages.length);
            
            carouselImages.forEach(function(img) {
                const index = parseInt(img.getAttribute('data-carousel-index') || '1');
                console.log('Checking image:', img.src, 'Index:', index);
                
                // Đảm bảo ảnh hiển thị
                img.style.display = 'block';
                img.style.visibility = 'visible';
                img.style.opacity = '1';
                
                // Kiểm tra xem ảnh đã load thành công chưa
                if (img.complete) {
                    // Ảnh đã load xong
                    if (img.naturalWidth === 0 || img.naturalHeight === 0) {
                        // Ảnh lỗi (không có kích thước)
                        console.error('Image failed to load (no dimensions):', img.src, 'Width:', img.naturalWidth, 'Height:', img.naturalHeight);
                        handleBannerImageError(img, index);
                    } else {
                        // Ảnh load thành công
                        console.log('Image loaded successfully:', img.src, 'Width:', img.naturalWidth, 'Height:', img.naturalHeight);
                        setCarouselHeight();
                    }
                } else {
                    // Đợi ảnh load
                    img.addEventListener('load', function() {
                        console.log('Image loaded:', img.src, 'Width:', img.naturalWidth, 'Height:', img.naturalHeight);
                        setCarouselHeight();
                    });
                    img.addEventListener('error', function() {
                        console.error('Image load error:', img.src);
                        handleBannerImageError(img, index);
                    });
                }
            });
            
            // Delay để đảm bảo DOM và images đã render xong
            setTimeout(setCarouselHeight, 100);
            setTimeout(setCarouselHeight, 300);
            setTimeout(setCarouselHeight, 500);
        });
        
        window.addEventListener('load', function() {
            setPromoCardBackgrounds();
            setCarouselHeight();
            
            // Kiểm tra lại các ảnh sau khi page load xong
            const carouselImages = document.querySelectorAll('.promotion-banner-hero-img');
            carouselImages.forEach(function(img) {
                if (img.style.display !== 'none' && (img.naturalWidth === 0 || img.naturalHeight === 0)) {
                    const index = parseInt(img.getAttribute('data-carousel-index') || '1');
                    handleBannerImageError(img, index);
                }
            });
            
            // Retry sau khi tất cả images load xong
            setTimeout(setCarouselHeight, 200);
            setTimeout(setCarouselHeight, 500);
        });
        
        // Cập nhật lại khi resize window
        let resizeTimeout;
        window.addEventListener('resize', function() {
            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(setCarouselHeight, 250);
        });
        
        // Countdown Timer Logic - Sử dụng thời gian kết thúc khuyến mãi từ database
        <c:choose>
            <c:when test="${not empty latestPromotion && not empty latestPromotion.endDate}">
                // Chuyển đổi endDate từ Java Date sang JavaScript Date
                <fmt:formatDate value="${latestPromotion.endDate}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="formattedEndDate" />
                const endDate = new Date('${formattedEndDate}');
                const countdownDate = endDate.getTime();
                
                const countdownTimer = setInterval(function() {
                    const now = new Date().getTime();
                    const distance = countdownDate - now;

                    if (distance < 0) {
                        clearInterval(countdownTimer);
                        const countdownElement = document.getElementById("countdown");
                        if (countdownElement) {
                            countdownElement.innerHTML = '<div class="alert alert-warning">Sự kiện đã kết thúc</div>';
                        }
                        return;
                    }

                    const days = Math.floor(distance / (1000 * 60 * 60 * 24));
                    const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                    const seconds = Math.floor((distance % (1000 * 60)) / 1000);

                    const daysElement = document.getElementById("days");
                    const hoursElement = document.getElementById("hours");
                    const minutesElement = document.getElementById("minutes");
                    const secondsElement = document.getElementById("seconds");
                    
                    if (daysElement) daysElement.innerHTML = days < 10 ? "0" + days : days;
                    if (hoursElement) hoursElement.innerHTML = hours < 10 ? "0" + hours : hours;
                    if (minutesElement) minutesElement.innerHTML = minutes < 10 ? "0" + minutes : minutes;
                    if (secondsElement) secondsElement.innerHTML = seconds < 10 ? "0" + seconds : seconds;
                }, 1000);
            </c:when>
            <c:otherwise>
                // Không có promotion nào, ẩn countdown hoặc hiển thị thông báo
                const countdownElement = document.getElementById("countdown");
                if (countdownElement) {
                    countdownElement.innerHTML = '<div class="alert alert-info">Hiện tại không có khuyến mãi nào</div>';
                }
            </c:otherwise>
        </c:choose>

        // Horizontal Scroll for Products
        const productsScroll = document.getElementById('productsScroll');
        if (productsScroll) {
            let isDown = false;
            let startX;
            let scrollLeft;

            productsScroll.addEventListener('mousedown', (e) => {
                isDown = true;
                productsScroll.classList.add('active');
                startX = e.pageX - productsScroll.offsetLeft;
                scrollLeft = productsScroll.scrollLeft;
            });
            productsScroll.addEventListener('mouseleave', () => {
                isDown = false;
                productsScroll.classList.remove('active');
            });
            productsScroll.addEventListener('mouseup', () => {
                isDown = false;
                productsScroll.classList.remove('active');
            });
            productsScroll.addEventListener('mousemove', (e) => {
                if (!isDown) return;
                e.preventDefault();
                const x = e.pageX - productsScroll.offsetLeft;
                const walk = (x - startX) * 1;
                productsScroll.scrollLeft = scrollLeft - walk;
            });
        }

        // Functions already defined at the top of this script block
        
        // Handle subscribe notification
        window.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const subscribe = urlParams.get('subscribe');
            const message = urlParams.get('message');
            
            if (subscribe) {
                let alertType = 'info';
                let alertMessage = 'Cảm ơn bạn đã đăng ký!';
                
                if (subscribe === 'success') {
                    alertType = 'success';
                    alertMessage = message || 'Đăng ký thành công! Cảm ơn bạn đã đăng ký nhận ưu đãi.';
                } else if (subscribe === 'error') {
                    alertType = 'danger';
                    alertMessage = message || 'Đăng ký thất bại. Vui lòng thử lại.';
                } else if (subscribe === 'info') {
                    alertType = 'info';
                    alertMessage = message || 'Email này đã được đăng ký.';
                }
                
                // Create and show alert
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-' + alertType + ' alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3';
                alertDiv.style.zIndex = '9999';
                alertDiv.style.minWidth = '400px';
                
                // Determine icon class
                let iconClass = 'info-circle';
                if (alertType === 'success') {
                    iconClass = 'check-circle';
                } else if (alertType === 'danger') {
                    iconClass = 'exclamation-triangle';
                }
                
                alertDiv.innerHTML = 
                    '<i class="bi bi-' + iconClass + '"></i> ' +
                    alertMessage +
                    '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
                document.body.appendChild(alertDiv);
                
                // Auto remove after 5 seconds
                setTimeout(function() {
                    if (alertDiv.parentNode) {
                        alertDiv.remove();
                    }
                }, 5000);
            }
        });
    </script>
</body>
</html>

