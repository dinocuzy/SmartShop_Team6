<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- Product Detail Modal -->
<div class="modal fade" id="productDetailModal" tabindex="-1" aria-labelledby="productDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered">
        <div class="modal-content product-modal-content">
            <div class="modal-header product-modal-header">
                <button type="button" class="btn-close btn-close-white" aria-label="Close" id="modalCloseButton"></button>
            </div>
            <div class="modal-body product-modal-body">
                <div class="row g-4">
                    <!-- Left: Product Image -->
                    <div class="col-md-6">
                        <div class="product-image-container">
                            <div class="product-image-wrapper">
                                <img id="modalProductImage" src="" alt="Product" class="product-image-large-modal">
                                <button class="image-nav-btn image-nav-left" onclick="changeImage(-1)">
                                    <i class="bi bi-chevron-left"></i>
                                </button>
                                <button class="image-nav-btn image-nav-right" onclick="changeImage(1)">
                                    <i class="bi bi-chevron-right"></i>
                                </button>
                            </div>
                            <!-- Promotional Banner -->
                            <div class="promo-banner-modal">
                                <div class="promo-banner-content">
                                    <div class="promo-text-large">Mừng khai trương cửa hàng</div>
                                    <div class="promo-text-small">DEAL HOT QUÀ NGON</div>
                                </div>
                                <div class="promo-characters">
                                    <div class="promo-char promo-char-left">🧧</div>
                                    <div class="promo-char promo-char-right">🍊</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Right: Product Info -->
                    <div class="col-md-6">
                        <h2 id="modalProductName" class="product-name-modal"></h2>
                        <a href="#" class="compare-link" id="modalCompareLink">
                            <i class="bi bi-arrow-left"></i> So sánh
                        </a>
                        
                        <div class="product-meta-modal">
                            <div class="meta-item">
                                <span class="meta-label">Danh mục:</span>
                                <span id="modalCategory" class="meta-value">Đang cập nhật</span>
                            </div>
                            <div class="meta-item">
                                <span class="meta-label">Mã sản phẩm:</span>
                                <span id="modalProductCode" class="meta-value">Đang cập nhật</span>
                            </div>
                        </div>
                        
                        <div class="price-section-modal">
                            <div class="price-wrapper-modal">
                                <div class="price-current-modal" id="modalPriceCurrent"></div>
                                <div class="price-old-wrapper-modal">
                                    <div class="price-old-modal" id="modalPriceOld"></div>
                                    <span class="discount-percent-modal" id="modalDiscountPercent"></span>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Description -->
                        <div class="description-section-modal">
                            <label class="description-label">Mô tả sản phẩm</label>
                            <div class="description-content" id="modalDescription">
                                Đang tải mô tả...
                            </div>
                        </div>
                        
                        <!-- Quantity Selector -->
                        <div class="quantity-section-modal">
                            <label class="quantity-label">Số lượng</label>
                            <div class="quantity-control-modal">
                                <button class="quantity-btn-modal" onclick="changeQuantity(-1)">-</button>
                                <input type="number" id="modalQuantity" class="quantity-input-modal" value="1" min="1" readonly>
                                <button class="quantity-btn-modal" onclick="changeQuantity(1)">+</button>
                            </div>
                        </div>
                        
                        <!-- Action Buttons -->
                        <div class="action-buttons-modal">
                            <button class="btn-add-cart-modal" onclick="addToCartFromModal()">
                                Thêm vào giỏ
                            </button>
                            <button class="btn-buy-now-modal" onclick="buyNowFromModal()">
                                Mua ngay
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .product-modal-content {
        background: #1a1a1a;
        border: none;
        border-radius: 15px;
        color: white;
    }
    
    .product-modal-header {
        border-bottom: 1px solid #4a4a4a;
        padding: 1rem;
    }
    
    .product-modal-body {
        padding: 2rem;
    }
    
    .product-image-container {
        position: relative;
    }
    
    .product-image-wrapper {
        position: relative;
        border-radius: 10px;
        overflow: hidden;
        background: white;
        margin-bottom: 1rem;
    }
    
    .product-image-large-modal {
        width: 100%;
        height: 400px;
        object-fit: contain;
        display: block;
    }
    
    .image-nav-btn {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        background: rgba(0, 0, 0, 0.5);
        border: none;
        color: white;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: background 0.3s;
    }
    
    .image-nav-btn:hover {
        background: rgba(0, 0, 0, 0.7);
    }
    
    .image-nav-left {
        left: 10px;
    }
    
    .image-nav-right {
        right: 10px;
    }
    
    .promo-banner-modal {
        background: #AA0000;
        border: 3px solid #ff0000;
        border-radius: 10px;
        padding: 1rem;
        position: relative;
        overflow: hidden;
    }
    
    .promo-banner-content {
        text-align: center;
        color: white;
        font-weight: bold;
    }
    
    .promo-text-large {
        font-size: 1.1rem;
        margin-bottom: 0.25rem;
    }
    
    .promo-text-small {
        font-size: 0.9rem;
    }
    
    .promo-characters {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        pointer-events: none;
    }
    
    .promo-char {
        font-size: 2rem;
    }
    
    .promo-char-left {
        margin-left: 1rem;
    }
    
    .promo-char-right {
        margin-right: 1rem;
    }
    
    .product-name-modal {
        font-size: 1.8rem;
        font-weight: bold;
        color: white;
        margin-bottom: 0.5rem;
    }
    
    .compare-link {
        color: #4a9eff;
        text-decoration: none;
        font-size: 0.9rem;
        display: inline-block;
        margin-bottom: 1.5rem;
    }
    
    .compare-link:hover {
        text-decoration: underline;
        color: #6bb6ff;
    }
    
    .product-meta-modal {
        margin-bottom: 1.5rem;
    }
    
    .meta-item {
        margin-bottom: 0.5rem;
        font-size: 0.9rem;
    }
    
    .meta-label {
        color: #b0b0b0;
    }
    
    .meta-value {
        color: white;
        margin-left: 0.5rem;
    }
    
    .price-section-modal {
        margin-bottom: 2rem;
    }
    
    .price-wrapper-modal {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }
    
    .price-current-modal {
        font-size: 2rem;
        font-weight: bold;
        color: #dc3545;
    }
    
    .price-old-wrapper-modal {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }
    
    .price-old-modal {
        font-size: 0.9rem;
        color: #6a6a6a;
        text-decoration: line-through;
    }
    
    .discount-percent-modal {
        background: #dc3545;
        color: white;
        padding: 0.25rem 0.75rem;
        border-radius: 5px;
        font-size: 0.85rem;
        font-weight: bold;
    }
    
    .description-section-modal {
        margin-bottom: 1.5rem;
    }
    
    .description-label {
        display: block;
        color: white;
        font-weight: 500;
        margin-bottom: 0.75rem;
    }
    
    .description-content {
        background: #2c2c2c;
        border: 1px solid #4a4a4a;
        border-radius: 8px;
        padding: 1rem;
        color: #b0b0b0;
        font-size: 0.95rem;
        line-height: 1.6;
        max-height: 200px;
        overflow-y: auto;
    }
    
    .quantity-section-modal {
        margin-bottom: 2rem;
    }
    
    .quantity-label {
        display: block;
        color: white;
        font-weight: 500;
        margin-bottom: 0.75rem;
    }
    
    .quantity-control-modal {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .quantity-btn-modal {
        width: 40px;
        height: 40px;
        border: 1px solid #4a4a4a;
        background: #2c2c2c;
        color: white;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .quantity-btn-modal:hover {
        background: #4a4a4a;
    }
    
    .quantity-input-modal {
        width: 80px;
        height: 40px;
        text-align: center;
        border: 1px solid #4a4a4a;
        background: #2c2c2c;
        color: white;
        border-radius: 8px;
    }
    
    .action-buttons-modal {
        display: flex;
        gap: 1rem;
    }
    
    .btn-add-cart-modal {
        flex: 1;
        background: #dc3545;
        color: white;
        border: none;
        border-radius: 25px;
        padding: 0.75rem 1.5rem;
        font-size: 1.1rem;
        font-weight: bold;
        cursor: pointer;
        transition: background 0.3s;
    }
    
    .btn-add-cart-modal:hover {
        background: #c82333;
    }
    
    .btn-buy-now-modal {
        flex: 1;
        background: transparent;
        color: #dc3545;
        border: 2px solid #dc3545;
        border-radius: 25px;
        padding: 0.75rem 1.5rem;
        font-size: 1.1rem;
        font-weight: bold;
        cursor: pointer;
        transition: all 0.3s;
    }
    
    .btn-buy-now-modal:hover {
        background: #dc3545;
        color: white;
    }
</style>

<script>
    // Expose function to global scope IMMEDIATELY - override placeholder if exists
    // Always override to ensure full implementation is used
    // Store full implementation in a separate variable first
    window.openProductModalFull = function(productID) {
        console.log('openProductModal called with productID:', productID);
        // Load product data via AJAX
        fetch('${pageContext.request.contextPath}/product/api?id=' + productID)
            .then(response => {
                console.log('Response status:', response.status);
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
                
                currentProductData = data;
                currentQuantity = 1;
                maxStock = data.stock || 999;
                
                // Populate modal
                document.getElementById('modalProductName').textContent = data.productName;
                document.getElementById('modalProductCode').textContent = '#' + data.productID;
                document.getElementById('modalProductImage').src = data.imageUrl || '';
                
                // Category
                if (data.categoryName) {
                    document.getElementById('modalCategory').textContent = data.categoryName;
                } else {
                    document.getElementById('modalCategory').textContent = 'Chưa phân loại';
                }
                
                // Price
                const priceCurrent = formatCurrency(data.discountedPrice);
                
                // Parse giá từ JSON (có thể là số hoặc string)
                const originalPriceNum = parseFloat(data.originalPrice) || 0;
                const discountedPriceNum = parseFloat(data.discountedPrice) || 0;
                
                // Kiểm tra xem có giảm giá không (so sánh originalPrice và discountedPrice)
                // Chấp nhận sự khác biệt nhỏ do làm tròn (0.01)
                const hasDiscount = originalPriceNum > discountedPriceNum + 0.01;
                
                const priceOld = hasDiscount ? formatCurrency(originalPriceNum) : '';
                const discountPercentValue = data.discountPercent && parseInt(data.discountPercent) > 0 ? 
                                           '-' + parseInt(data.discountPercent) + '%' : '';
                
                document.getElementById('modalPriceCurrent').textContent = priceCurrent;
                
                const priceOldElement = document.getElementById('modalPriceOld');
                const discountPercentElement = document.getElementById('modalDiscountPercent');
                const priceOldWrapper = priceOldElement.parentElement;
                
                // Hiển thị giá cũ nếu có giảm giá
                if (hasDiscount && priceOld) {
                    priceOldElement.textContent = priceOld;
                    if (discountPercentValue) {
                        discountPercentElement.textContent = discountPercentValue;
                        discountPercentElement.style.display = 'inline-block';
                    } else {
                        discountPercentElement.style.display = 'none';
                    }
                    priceOldWrapper.style.display = 'flex';
                } else {
                    priceOldWrapper.style.display = 'none';
                }
                
                // Debug log để kiểm tra
                console.log('Product data:', {
                    originalPrice: data.originalPrice,
                    originalPriceNum: originalPriceNum,
                    discountedPrice: data.discountedPrice,
                    discountedPriceNum: discountedPriceNum,
                    discountPercent: data.discountPercent,
                    hasDiscount: hasDiscount,
                    categoryName: data.categoryName
                });
                
                // Description
                const descriptionElement = document.getElementById('modalDescription');
                if (data.description && data.description.trim() !== '') {
                    descriptionElement.textContent = data.description;
                } else {
                    descriptionElement.textContent = 'Chưa có mô tả cho sản phẩm này.';
                }
                
                // Stock status
                if (data.stockStatus === 'OutOfStock' || data.stock <= 0) {
                    document.querySelector('.btn-add-cart-modal').disabled = true;
                    document.querySelector('.btn-buy-now-modal').disabled = true;
                } else {
                    document.querySelector('.btn-add-cart-modal').disabled = false;
                    document.querySelector('.btn-buy-now-modal').disabled = false;
                    maxStock = data.stock;
                }
                
                // Update quantity input max
                document.getElementById('modalQuantity').max = maxStock;
                document.getElementById('modalQuantity').value = 1;
                currentQuantity = 1;
                
                // Show modal - ensure element exists and Bootstrap is ready
                function showModal() {
                    const modalElement = document.getElementById('productDetailModal');
                    if (modalElement) {
                        try {
                            // Check if Bootstrap is available
                            if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                                const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
                                currentModalInstance = modal; // Store instance for later use
                                modal.show();
                                console.log('Modal shown successfully with Bootstrap');
                                
                                // Setup close handlers after showing
                                setTimeout(setupModalCloseHandlers, 50);
                                return true;
                            } else {
                                // Fallback: show modal manually
                                console.warn('Bootstrap Modal not available, showing modal manually');
                                modalElement.style.display = 'block';
                                modalElement.classList.add('show');
                                document.body.classList.add('modal-open');
                                const backdrop = document.createElement('div');
                                backdrop.className = 'modal-backdrop fade show';
                                backdrop.id = 'productModalBackdrop';
                                backdrop.onclick = function(e) {
                                    e.preventDefault();
                                    e.stopPropagation();
                                    console.log('Manual backdrop clicked');
                                    closeProductModal();
                                };
                                document.body.appendChild(backdrop);
                                
                                // Setup close handlers
                                setTimeout(setupModalCloseHandlers, 50);
                                console.log('Modal shown manually');
                                return true;
                            }
                        } catch (error) {
                            console.error('Error showing modal:', error);
                            return false;
                        }
                    } else {
                        console.error('productDetailModal element not found');
                        return false;
                    }
                }
                
                // Try to show modal immediately
                if (!showModal()) {
                    // If modal element not found, wait and retry
                    console.warn('Modal element not ready, waiting and retrying...');
                    setTimeout(function() {
                        if (!showModal()) {
                            setTimeout(function() {
                                if (!showModal()) {
                                    alert('Không thể hiển thị modal. Vui lòng refresh trang và thử lại.');
                                }
                            }, 1000);
                        }
                    }, 500);
                }
            })
            .catch(error => {
                console.error('Error loading product:', error);
                alert('Lỗi khi tải thông tin sản phẩm: ' + error.message);
            });
    };
    
    // Now override the placeholder with full implementation IMMEDIATELY
    window.openProductModal = window.openProductModalFull;
    console.log('openProductModal FULL implementation is now active');
    
    let currentProductData = null;
    let currentQuantity = 1;
    let maxStock = 999;
    let currentModalInstance = null; // Store modal instance for programmatic control
    
    // Function to close modal
    function closeProductModal() {
        const modalElement = document.getElementById('productDetailModal');
        if (modalElement) {
            if (currentModalInstance && typeof currentModalInstance.hide === 'function') {
                // Use Bootstrap modal instance
                currentModalInstance.hide();
            } else if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                // Try to get existing instance
                try {
                    const modal = bootstrap.Modal.getInstance(modalElement);
                    if (modal) {
                        modal.hide();
                    } else {
                        // Manually close
                        closeModalManually();
                    }
                } catch (e) {
                    closeModalManually();
                }
            } else {
                // Manually close
                closeModalManually();
            }
        }
    }
    
    function closeModalManually() {
        const modalElement = document.getElementById('productDetailModal');
        if (modalElement) {
            modalElement.style.display = 'none';
            modalElement.classList.remove('show');
            document.body.classList.remove('modal-open');
            document.body.style.overflow = '';
            document.body.style.paddingRight = '';
            
            // Remove backdrop if exists
            const backdrop = document.getElementById('productModalBackdrop');
            if (backdrop) {
                backdrop.remove();
            }
            // Also remove any Bootstrap-created backdrops
            const bsBackdrops = document.querySelectorAll('.modal-backdrop');
            bsBackdrops.forEach(bd => bd.remove());
        }
    }
    
    // Setup close button and backdrop click handlers when DOM is ready
    function setupModalCloseHandlers() {
        const modalElement = document.getElementById('productDetailModal');
        if (!modalElement) return;
        
        // Close button handler - use ID selector for more reliable targeting
        const closeButton = document.getElementById('modalCloseButton') || modalElement.querySelector('.btn-close');
        if (closeButton && !closeButton.dataset.closeHandlerAdded) {
            closeButton.dataset.closeHandlerAdded = 'true';
            closeButton.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();
                console.log('Close button clicked');
                closeProductModal();
                return false;
            }, true); // Use capture phase
            
            // Also add onclick as backup
            closeButton.onclick = function(e) {
                e.preventDefault();
                e.stopPropagation();
                console.log('Close button onclick');
                closeProductModal();
                return false;
            };
        }
        
        // Backdrop click handler - only add once
        if (!modalElement.dataset.backdropHandlerAdded) {
            modalElement.dataset.backdropHandlerAdded = 'true';
            modalElement.addEventListener('click', function(e) {
                if (e.target === modalElement) {
                    console.log('Backdrop clicked');
                    closeProductModal();
                }
            }, true);
        }
        
        // ESC key handler - only add once globally
        if (!window.escKeyHandlerAdded) {
            window.escKeyHandlerAdded = true;
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape' || e.keyCode === 27) {
                    const modalEl = document.getElementById('productDetailModal');
                    if (modalEl && modalEl.classList.contains('show')) {
                        console.log('ESC key pressed');
                        closeProductModal();
                    }
                }
            }, true);
        }
        
        // Listen for Bootstrap modal hidden event to cleanup
        if (!modalElement.dataset.bsHandlerAdded) {
            modalElement.dataset.bsHandlerAdded = 'true';
            modalElement.addEventListener('hidden.bs.modal', function() {
                console.log('Modal hidden event fired');
                closeModalManually();
            });
        }
    }
    
    // Initialize close handlers when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', setupModalCloseHandlers);
    } else {
        setupModalCloseHandlers();
    }
    
    // Also try after delays to ensure modal is fully loaded
    setTimeout(setupModalCloseHandlers, 500);
    setTimeout(setupModalCloseHandlers, 1000);
    
    function changeQuantity(delta) {
        const newQuantity = currentQuantity + delta;
        if (newQuantity >= 1 && newQuantity <= maxStock) {
            currentQuantity = newQuantity;
            document.getElementById('modalQuantity').value = newQuantity;
        } else if (newQuantity > maxStock) {
            alert('Số lượng không được vượt quá số lượng tồn kho: ' + maxStock);
        }
    }
    
    function changeImage(direction) {
        // Placeholder for image carousel
        console.log('Change image:', direction);
    }
    
    function addToCartFromModal() {
        if (!currentProductData) return;
        
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/cart';
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'add';
        form.appendChild(actionInput);
        
        const productIDInput = document.createElement('input');
        productIDInput.type = 'hidden';
        productIDInput.name = 'productID';
        productIDInput.value = currentProductData.productID;
        form.appendChild(productIDInput);
        
        const quantityInput = document.createElement('input');
        quantityInput.type = 'hidden';
        quantityInput.name = 'quantity';
        quantityInput.value = currentQuantity;
        form.appendChild(quantityInput);
        
        document.body.appendChild(form);
        form.submit();
    }
    
    function buyNowFromModal() {
        if (!currentProductData) return;
        
        // Add to cart first, then redirect to checkout
        addToCartFromModal();
        setTimeout(() => {
            window.location.href = '${pageContext.request.contextPath}/checkout';
        }, 500);
    }
    
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }
    
</script>

