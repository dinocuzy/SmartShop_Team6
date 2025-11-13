<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <!-- AI Chatbot Widget - Chỉ hiển thị cho Customer hoặc chưa đăng nhập -->
    <c:choose>
        <c:when test="${empty sessionScope.currentUser || sessionScope.currentUser.roleName == 'Customer'}">
    <!-- Set session status for JavaScript -->
    <script>
        window.chatbotSessionExists = ${not empty sessionScope.currentUser};
        window.chatbotSessionUserId = ${not empty sessionScope.currentUser ? sessionScope.currentUser.userID : 'null'};
    </script>
    <div id="chatbotWidget" class="chatbot-widget">
        <div id="chatbotButton" class="chatbot-button"
            onclick="if(typeof window.toggleChatbot==='function'){window.toggleChatbot();}else{console.error('toggleChatbot not found');}">
            <i class="bi bi-chat-dots"></i>
        </div>
        <div id="chatbotWindow" class="chatbot-window">
            <div class="chatbot-header">
                <div class="d-flex align-items-center">
                    <i class="bi bi-person-badge me-2"></i>
                    <span>Nhân viên SmartShop</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <button type="button" class="chatbot-order-btn" id="chatbotOrderBtn" title="Đặt hàng" style="display: none;">
                        <i class="bi bi-cart-check"></i>
                    </button>
                    <button type="button" class="chatbot-clear-btn" id="chatbotClearBtn" title="Xóa lịch sử chat">
                        <i class="bi bi-trash"></i>
                    </button>
                <button type="button" class="btn-close btn-close-white"></button>
                </div>
            </div>
            <div id="chatbotMessages" class="chatbot-messages">
                <div class="chatbot-message bot-message">
                    <div class="message-content">
                        <i class="bi bi-person-badge"></i>
                        <span>Xin chào! Tôi là nhân viên SmartShop. Bạn cần hỗ trợ gì ạ?</span>
                    </div>
                </div>
            </div>
            <div id="chatbotSuggestions" class="chatbot-suggestions">
                <div class="chatbot-suggestion-item" onclick="useSuggestion('Bạn có sản phẩm nào đang giảm giá không?')">
                    <i class="bi bi-tag"></i> Sản phẩm giảm giá
                </div>
                <div class="chatbot-suggestion-item" onclick="useSuggestion('Tôi muốn tìm laptop')">
                    <i class="bi bi-laptop"></i> Tìm laptop
                </div>
                <div class="chatbot-suggestion-item" onclick="useSuggestion('Sản phẩm bán chạy nhất là gì?')">
                    <i class="bi bi-fire"></i> Sản phẩm bán chạy
                </div>
                <div class="chatbot-suggestion-item" onclick="useSuggestion('Bạn có chính sách đổi trả như thế nào?')">
                    <i class="bi bi-arrow-repeat"></i> Chính sách đổi trả
                </div>
            </div>
            <div class="chatbot-input-container">
                <input type="text" id="chatbotInput" class="chatbot-input" placeholder="Nhập câu hỏi của bạn..." 
                       onkeypress="if(event.key==='Enter'){if(typeof window.sendChatbotMessage==='function'){window.sendChatbotMessage();}else if(typeof window.sendChatbotMessageFull==='function'){window.sendChatbotMessageFull();}else{console.error('sendChatbotMessage not available');}}">
                <button type="button" class="chatbot-send-btn" 
                        onclick="if(typeof window.sendChatbotMessage==='function'){window.sendChatbotMessage();}else if(typeof window.sendChatbotMessageFull==='function'){window.sendChatbotMessageFull();}else{console.error('sendChatbotMessage not available');}">
                    <i class="bi bi-send"></i>
                </button>
            </div>
        </div>
    </div>
    
    <!-- Modal đặt hàng từ chatbot -->
    <div class="modal fade" id="chatbotOrderModal" tabindex="-1" aria-labelledby="chatbotOrderModalLabel" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="chatbotOrderModalLabel">
                        <i class="bi bi-cart-check me-2"></i>Đặt hàng
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div id="chatbotOrderError" class="alert alert-danger" style="display: none;"></div>
                    <form id="chatbotOrderForm">
                        <input type="hidden" name="action" value="placeOrder">
                        
                        <!-- Địa chỉ giao hàng -->
                        <div class="mb-3">
                            <label class="form-label"><i class="bi bi-geo-alt me-1"></i>Địa chỉ giao hàng <span class="text-danger">*</span></label>
                            <select class="form-select" name="shippingAddressID" id="chatbotShippingAddressID" required>
                                <option value="">-- Chọn địa chỉ --</option>
                            </select>
                            <div class="text-end mt-2">
                                <a href="${pageContext.request.contextPath}/customer/profile" class="btn btn-sm btn-outline-primary" target="_blank">
                                    <i class="bi bi-plus"></i> Thêm địa chỉ mới
                                </a>
                            </div>
                        </div>
                        
                        <!-- Địa chỉ thanh toán -->
                        <div class="mb-3">
                            <label class="form-label"><i class="bi bi-receipt me-1"></i>Địa chỉ thanh toán</label>
                            <div class="form-check mb-2">
                                <input class="form-check-input" type="checkbox" id="chatbotSameAsShipping" checked>
                                <label class="form-check-label" for="chatbotSameAsShipping">
                                    Giống địa chỉ giao hàng
                                </label>
                            </div>
                            <select class="form-select" name="billingAddressID" id="chatbotBillingAddressID">
                                <option value="">-- Chọn địa chỉ --</option>
                            </select>
                        </div>
                        
                        <!-- Phương thức thanh toán -->
                        <div class="mb-3">
                            <label class="form-label"><i class="bi bi-credit-card me-1"></i>Phương thức thanh toán <span class="text-danger">*</span></label>
                            <select class="form-select" name="paymentMethodID" id="chatbotPaymentMethodID" required>
                                <option value="">-- Chọn phương thức thanh toán --</option>
                            </select>
                        </div>
                        
                        <!-- Ghi chú -->
                        <div class="mb-3">
                            <label class="form-label"><i class="bi bi-pencil me-1"></i>Ghi chú (tùy chọn)</label>
                            <textarea class="form-control" name="note" id="chatbotOrderNote" rows="2" placeholder="Ghi chú cho đơn hàng..."></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" id="chatbotPlaceOrderBtn">
                        <i class="bi bi-check-circle me-1"></i>Đặt hàng
                    </button>
                </div>
            </div>
        </div>
    </div>
        </c:when>
        <c:otherwise>
            <!-- Chatbot bị ẩn cho Admin/Manager/Staff -->
        </c:otherwise>
    </c:choose>

    <!-- CRITICAL: Define sendChatbotMessage FULL IMPLEMENTATION IMMEDIATELY after HTML -->
    <!-- This ensures the function is ALWAYS available when HTML onclick handlers are parsed -->
    <script>
        (function() {
            // Storage keys
            const STORAGE_KEY_CONVERSATION_ID = 'chatbot_conversation_id';
            const STORAGE_KEY_MESSAGES = 'chatbot_messages';
            
            // Kiểm tra nếu cần xóa lịch sử chat (khi logout hoặc session kết thúc)
            const urlParams = new URLSearchParams(window.location.search);
            const clearChat = urlParams.get('clearChat');
            const logout = urlParams.get('logout');
            
            // Xóa lịch sử chat nếu có parameter clearChat hoặc logout
            if (clearChat === 'true' || logout === 'success') {
                try {
                    localStorage.removeItem(STORAGE_KEY_MESSAGES);
                    localStorage.removeItem(STORAGE_KEY_CONVERSATION_ID);
                    console.log('Chat history cleared due to session end');
                    
                    // Xóa parameter khỏi URL để tránh xóa lại khi refresh
                    if (window.history && window.history.replaceState) {
                        const newUrl = window.location.pathname + 
                            (window.location.search.replace(/[?&]clearChat=true/, '').replace(/[?&]logout=success/, '') || '');
                        window.history.replaceState({}, '', newUrl);
                    }
                } catch (e) {
                    console.error('Error clearing chat history:', e);
                }
            }
            
            // Kiểm tra session - nếu session thay đổi (user khác), xóa lịch sử chat
            // (chỉ kiểm tra khi không có clearChat parameter để tránh xóa nhiều lần)
            if (!clearChat && !logout) {
                try {
                    // Kiểm tra xem có session không và user ID có thay đổi không
                    const sessionExists = typeof window.chatbotSessionExists !== 'undefined' ? window.chatbotSessionExists : false;
                    const sessionUserId = typeof window.chatbotSessionUserId !== 'undefined' ? window.chatbotSessionUserId : null;
                    
                    // Lưu user ID hiện tại trong localStorage để so sánh
                    const STORAGE_KEY_USER_ID = 'chatbot_user_id';
                    const lastUserId = localStorage.getItem(STORAGE_KEY_USER_ID);
                    
                    // Nếu user ID thay đổi (user khác login hoặc logout), xóa lịch sử chat
                    if (sessionUserId !== null && lastUserId !== null && sessionUserId.toString() !== lastUserId) {
                        localStorage.removeItem(STORAGE_KEY_MESSAGES);
                        localStorage.removeItem(STORAGE_KEY_CONVERSATION_ID);
                        console.log('Chat history cleared due to user change');
                    }
                    
                    // Nếu không có session (logout) nhưng trước đó có user ID, xóa lịch sử
                    if (!sessionExists && lastUserId !== null) {
                        localStorage.removeItem(STORAGE_KEY_MESSAGES);
                        localStorage.removeItem(STORAGE_KEY_CONVERSATION_ID);
                        localStorage.removeItem(STORAGE_KEY_USER_ID);
                        console.log('Chat history cleared due to session end');
                    }
                    
                    // Cập nhật user ID hiện tại
                    if (sessionUserId !== null) {
                        localStorage.setItem(STORAGE_KEY_USER_ID, sessionUserId.toString());
                    } else {
                        localStorage.removeItem(STORAGE_KEY_USER_ID);
                    }
                } catch (e) {
                    console.error('Error checking session:', e);
                }
            }
            
            // Initialize conversation ID từ localStorage hoặc tạo mới
            if (typeof window.chatbotConversationId === 'undefined') {
                const savedConversationId = localStorage.getItem(STORAGE_KEY_CONVERSATION_ID);
                if (savedConversationId) {
                    window.chatbotConversationId = savedConversationId;
                } else {
                    window.chatbotConversationId = 'global_' + Date.now();
                    localStorage.setItem(STORAGE_KEY_CONVERSATION_ID, window.chatbotConversationId);
                }
            }
            
            // Helper function: escapeHtml
            function escapeHtml(text) {
                const div = document.createElement('div');
                div.textContent = text;
                return div.innerHTML;
            }
            
            // Function: Sử dụng gợi ý câu hỏi
            function useSuggestion(text) {
                const input = document.getElementById('chatbotInput');
                if (input) {
                    input.value = text;
                    input.focus();
                    // Tự động gửi sau 0.3s
                    setTimeout(function() {
                        if (typeof window.sendChatbotMessage === 'function') {
                            window.sendChatbotMessage();
                        } else if (typeof window.sendChatbotMessageFull === 'function') {
                            window.sendChatbotMessageFull();
                        }
                    }, 300);
                }
            }
            
            // Helper function: Ẩn gợi ý câu hỏi khi có tin nhắn
            function hideSuggestions() {
                const suggestionsContainer = document.getElementById('chatbotSuggestions');
                if (suggestionsContainer) {
                    suggestionsContainer.style.display = 'none';
                }
            }
            
            // Helper function: Hiển thị gợi ý câu hỏi khi chưa có tin nhắn
            function showSuggestions() {
                const suggestionsContainer = document.getElementById('chatbotSuggestions');
                const messagesContainer = document.getElementById('chatbotMessages');
                if (suggestionsContainer && messagesContainer) {
                    // Chỉ hiển thị nếu chỉ có message mặc định
                    const messages = messagesContainer.querySelectorAll('.chatbot-message');
                    if (messages.length <= 1) {
                        suggestionsContainer.style.display = 'flex';
                    } else {
                        suggestionsContainer.style.display = 'none';
                    }
                }
            }
            
            // Expose function globally
            window.useSuggestion = useSuggestion;
            
            // Save messages vào localStorage
            function saveMessagesToStorage() {
                try {
                    const messagesContainer = document.getElementById('chatbotMessages');
                    if (!messagesContainer) return;
                    
                    const messageElements = messagesContainer.querySelectorAll('.chatbot-message');
                    const messages = [];
                    
                    // Lưu messages theo thứ tự, kiểm tra product card message riêng
                    for (let i = 0; i < messageElements.length; i++) {
                        const msgEl = messageElements[i];
                        
                        // Bỏ qua loading messages
                        if (msgEl.id && msgEl.id.startsWith('loading-message-')) {
                            continue;
                        }
                        
                        // Bỏ qua products container (chỉ lưu product card messages riêng lẻ)
                        if (msgEl.classList.contains('chatbot-products-container')) {
                            continue;
                        }
                        
                        // Bỏ qua product card message (sẽ được xử lý cùng với message text trước đó)
                        if (msgEl.classList.contains('chatbot-product-card-message')) {
                            continue;
                        }
                        
                        const contentSpan = msgEl.querySelector('.message-content span');
                        if (contentSpan) {
                            const text = contentSpan.textContent || contentSpan.innerText;
                            // Chỉ lưu message nếu có text (không lưu message rỗng)
                            if (!text || text.trim().length === 0) {
                                continue;
                            }
                            
                            const type = msgEl.classList.contains('user-message') ? 'user' : 'bot';
                            
                            // Kiểm tra xem message tiếp theo có phải là product card message không
                            let productData = null;
                            if (i + 1 < messageElements.length) {
                                const nextMsgEl = messageElements[i + 1];
                                if (nextMsgEl.classList.contains('chatbot-product-card-message')) {
                                    // Lấy productID từ data attribute
                                    const productID = nextMsgEl.getAttribute('data-product-id');
                                    
                                    if (productID) {
                                        const productCard = nextMsgEl.querySelector('.chatbot-product-card');
                                        if (productCard) {
                                            // Lấy thông tin product card
                                            const productNameEl = productCard.querySelector('.chatbot-product-name');
                                            const productPriceEl = productCard.querySelector('.chatbot-product-price');
                                            const productImageEl = productCard.querySelector('.chatbot-product-image');
                                            const stockBadgeEl = productCard.querySelector('.chatbot-product-stock-badge');
                                            
                                            // Parse giá từ text (loại bỏ ký tự không phải số)
                                            let price = 0;
                                            if (productPriceEl) {
                                                const priceText = productPriceEl.textContent.replace(/[^\d]/g, '');
                                                price = parseFloat(priceText) || 0;
                                            }
                                            
                                            productData = {
                                                productID: parseInt(productID),
                                                productName: productNameEl ? productNameEl.textContent.trim() : '',
                                                productPrice: price,
                                                productImageUrl: productImageEl ? productImageEl.src : '',
                                                productStockStatus: stockBadgeEl && stockBadgeEl.textContent.includes('Còn') ? 'InStock' : 'OutOfStock'
                                            };
                                        }
                                    }
                                }
                            }
                            
                            messages.push({ 
                                text: text, 
                                type: type,
                                productData: productData
                            });
                        }
                    }
                    
                    localStorage.setItem(STORAGE_KEY_MESSAGES, JSON.stringify(messages));
                } catch (e) {
                    console.error('Error saving messages to localStorage:', e);
                }
            }
            
            // Load messages từ localStorage khi page load
            function loadMessagesFromStorage() {
                try {
                    const savedMessages = localStorage.getItem(STORAGE_KEY_MESSAGES);
                    if (savedMessages) {
                        const messages = JSON.parse(savedMessages);
                        const messagesContainer = document.getElementById('chatbotMessages');
                        if (messagesContainer && messages.length > 0) {
                            // Xóa message mặc định nếu có messages đã lưu
                            messagesContainer.innerHTML = '';
                            
                            // Xóa products container rỗng nếu có
                            const existingContainers = messagesContainer.querySelectorAll('.chatbot-products-container');
                            existingContainers.forEach(function(container) {
                                if (container.children.length === 0) {
                                    container.remove();
                                }
                            });
                            
                            // Restore messages (skip save để tránh loop)
                            // Chỉ load messages có text, bỏ qua messages chỉ có productData mà không có text
                            messages.forEach(function(msg) {
                                // Chỉ load message nếu có text hoặc là message bot có productData hợp lệ
                                if (msg.text && msg.text.trim().length > 0) {
                                    addMessage(msg.text, msg.type, true, msg.productData || null);
                                } else if (msg.type === 'bot' && msg.productData && msg.productData.productID) {
                                    // Nếu là bot message chỉ có productData, bỏ qua (không hiển thị product card đơn lẻ)
                                    console.log('Skipping message with only productData, no text');
                                }
                            });
                            
                            // Xóa products container rỗng sau khi load xong
                            setTimeout(function() {
                                const containers = messagesContainer.querySelectorAll('.chatbot-products-container');
                                containers.forEach(function(container) {
                                    if (container.children.length === 0) {
                                        container.remove();
                                        console.log('Removed empty products container');
                                    }
                                });
                            }, 200);
                            
                            // Save một lần sau khi load xong
                            setTimeout(saveMessagesToStorage, 100);
                            // Ẩn gợi ý nếu đã có messages
                            setTimeout(hideSuggestions, 150);
                        } else {
                            // Hiển thị gợi ý nếu chưa có messages
                            setTimeout(showSuggestions, 150);
                        }
                    } else {
                        // Hiển thị gợi ý nếu chưa có messages
                        setTimeout(showSuggestions, 150);
                    }
                } catch (e) {
                    console.error('Error loading messages from localStorage:', e);
                }
            }
            
            // Xóa lịch sử chat
            function clearChatHistory() {
                if (confirm('Bạn có chắc chắn muốn xóa toàn bộ lịch sử chat không?')) {
                    try {
                        // Xóa messages từ localStorage
                        localStorage.removeItem(STORAGE_KEY_MESSAGES);
                        
                        // Xóa conversation ID
                        localStorage.removeItem(STORAGE_KEY_CONVERSATION_ID);
                        
                        // Tạo conversation ID mới
                        window.chatbotConversationId = 'global_' + Date.now();
                        localStorage.setItem(STORAGE_KEY_CONVERSATION_ID, window.chatbotConversationId);
                        
                        // Xóa messages trên UI
                        const messagesContainer = document.getElementById('chatbotMessages');
                        if (messagesContainer) {
                            messagesContainer.innerHTML = '';
                            // Thêm message mặc định
                            const defaultMessage = document.createElement('div');
                            defaultMessage.className = 'chatbot-message bot-message';
                            defaultMessage.innerHTML = '<div class="message-content"><i class="bi bi-person-badge"></i><span>Xin chào! Tôi là nhân viên SmartShop. Bạn cần hỗ trợ gì ạ?</span></div>';
                            messagesContainer.appendChild(defaultMessage);
                        }
                        
                        // Hiển thị lại gợi ý câu hỏi
                        setTimeout(showSuggestions, 100);
                        
                        console.log('Chat history cleared by user');
                    } catch (e) {
                        console.error('Error clearing chat history:', e);
                        alert('Có lỗi xảy ra khi xóa lịch sử chat. Vui lòng thử lại.');
                    }
                }
            }
            
            // Helper function: addMessage
            function addMessage(text, type, skipSave, productData) {
                const messagesContainer = document.getElementById('chatbotMessages');
                if (!messagesContainer) return;
                
                // Ẩn gợi ý khi có tin nhắn mới (trừ message mặc định)
                if (text && text.trim().length > 0) {
                    hideSuggestions();
                }
                
                const messageDiv = document.createElement('div');
                messageDiv.className = 'chatbot-message ' + type + '-message';
                
                const contentDiv = document.createElement('div');
                contentDiv.className = 'message-content';
                
                if (type === 'bot') {
                    let formattedText = text
                        .replace(/\n+/g, '<br>')
                        .replace(/\s+/g, ' ')
                        .trim();
                    
                    if (!formattedText.includes('<br>') && formattedText.length > 80) {
                        formattedText = formattedText
                            .replace(/\.\s+/g, '.<br>')
                            .replace(/,\s+/g, ',<br>')
                            .replace(/:\s+/g, ':<br>');
                    }
                    
                    contentDiv.innerHTML = '<i class="bi bi-person-badge"></i><span>' + formattedText + '</span>';
                } else {
                    contentDiv.innerHTML = '<span>' + escapeHtml(text) + '</span>';
                }
                
                messageDiv.appendChild(contentDiv);
                messagesContainer.appendChild(messageDiv);
                
                // Nếu có productData, tạo product card riêng bên dưới message
                if (type === 'bot' && productData && productData.productID) {
                    const contextPath = '${pageContext.request.contextPath}';
                    const productID = productData.productID;
                    const productName = productData.productName || 'Sản phẩm';
                    const productPrice = productData.productPrice || 0;
                    const productStock = productData.productStock || 0;
                    const productStockStatus = productData.productStockStatus || '';
                    const productImageUrl = productData.productImageUrl || '';
                    
                    const isInStock = productStockStatus === 'InStock' && productStock > 0;
                    
                    // Format giá tiền
                    const formattedPrice = new Intl.NumberFormat('vi-VN', {
                        style: 'currency',
                        currency: 'VND'
                    }).format(productPrice);
                    
                    // Tạo product card message riêng
                    const productCardMessageDiv = document.createElement('div');
                    productCardMessageDiv.className = 'chatbot-message bot-message chatbot-product-card-message';
                    productCardMessageDiv.setAttribute('data-product-id', productID);
                    
                    const productCardDiv = document.createElement('div');
                    productCardDiv.className = 'chatbot-product-card';
                    
                    // Hình ảnh sản phẩm
                    const imageWrapper = document.createElement('div');
                    imageWrapper.className = 'chatbot-product-image-wrapper';
                    const productImage = document.createElement('img');
                    productImage.className = 'chatbot-product-image';
                    productImage.src = productImageUrl || (contextPath + '/images/placeholder.png');
                    productImage.alt = productName;
                    productImage.onerror = function() {
                        this.src = contextPath + '/images/placeholder.png';
                    };
                    imageWrapper.appendChild(productImage);
                    productCardDiv.appendChild(imageWrapper);
                    
                    // Thông tin sản phẩm
                    const productInfoDiv = document.createElement('div');
                    productInfoDiv.className = 'chatbot-product-info';
                    
                    // Tên sản phẩm
                    const productNameDiv = document.createElement('div');
                    productNameDiv.className = 'chatbot-product-name';
                    productNameDiv.textContent = productName;
                    productInfoDiv.appendChild(productNameDiv);
                    
                    // Giá và stock status cùng một dòng
                    const priceStockDiv = document.createElement('div');
                    priceStockDiv.className = 'chatbot-product-price-stock';
                    priceStockDiv.style.cssText = 'display: flex; justify-content: space-between; align-items: center; margin: 0.25rem 0;';
                    
                    // Giá
                    const productPriceDiv = document.createElement('div');
                    productPriceDiv.className = 'chatbot-product-price';
                    productPriceDiv.textContent = formattedPrice;
                    priceStockDiv.appendChild(productPriceDiv);
                    
                    // Stock status
                    if (isInStock) {
                        const stockBadge = document.createElement('span');
                        stockBadge.className = 'chatbot-product-stock-badge';
                        stockBadge.textContent = 'Còn hàng';
                        priceStockDiv.appendChild(stockBadge);
                    } else {
                        const stockBadge = document.createElement('span');
                        stockBadge.className = 'chatbot-product-stock-badge out-of-stock';
                        stockBadge.textContent = 'Hết hàng';
                        priceStockDiv.appendChild(stockBadge);
                    }
                    
                    productInfoDiv.appendChild(priceStockDiv);
                    
                    // Action buttons
                    const actionButtonsDiv = document.createElement('div');
                    actionButtonsDiv.className = 'chatbot-product-actions';
                    
                    // Nút "Xem chi tiết"
                    const viewDetailBtn = document.createElement('button');
                    viewDetailBtn.className = 'chatbot-action-btn chatbot-view-btn';
                    viewDetailBtn.innerHTML = '<i class="bi bi-eye"></i> Xem';
                    viewDetailBtn.onclick = function(e) {
                        e.preventDefault();
                        e.stopPropagation();
                        if (typeof window.openProductModal === 'function') {
                            window.openProductModal(productID);
                        } else if (typeof window.openProductModalFull === 'function') {
                            window.openProductModalFull(productID);
                        } else {
                            window.open(contextPath + '/product?id=' + productID, '_blank');
                        }
                    };
                    actionButtonsDiv.appendChild(viewDetailBtn);
                    
                    // Nút "Thêm vào giỏ" (chỉ hiển thị nếu còn hàng)
                    if (isInStock) {
                        const addToCartBtn = document.createElement('button');
                        addToCartBtn.className = 'chatbot-action-btn chatbot-add-cart-btn';
                        addToCartBtn.innerHTML = '<i class="bi bi-cart-plus"></i> Thêm';
                        addToCartBtn.onclick = function(e) {
                            e.preventDefault();
                            e.stopPropagation();
                            // Sử dụng cùng logic với nút giỏ hàng trên header và các trang khác
                            if (typeof window.addToCart === 'function') {
                                window.addToCart(productID, contextPath + '/home');
                            } else {
                                // Fallback: submit form giống như window.addToCart
                                const form = document.createElement('form');
                                form.method = 'POST';
                                form.action = contextPath + '/cart';
                                const actionInput = document.createElement('input');
                                actionInput.type = 'hidden';
                                actionInput.name = 'action';
                                actionInput.value = 'add';
                                form.appendChild(actionInput);
                                const productIDInput = document.createElement('input');
                                productIDInput.type = 'hidden';
                                productIDInput.name = 'productID';
                                productIDInput.value = productID;
                                form.appendChild(productIDInput);
                                // Thêm redirect để sau khi thêm xong sẽ quay về trang hiện tại
                                const redirectInput = document.createElement('input');
                                redirectInput.type = 'hidden';
                                redirectInput.name = 'redirect';
                                redirectInput.value = contextPath + '/home';
                                form.appendChild(redirectInput);
                                document.body.appendChild(form);
                                form.submit();
                            }
                        };
                        actionButtonsDiv.appendChild(addToCartBtn);
                    }
                    
                    productInfoDiv.appendChild(actionButtonsDiv);
                    productCardDiv.appendChild(productInfoDiv);
                    productCardMessageDiv.appendChild(productCardDiv);
                    
                    // Thêm product card message vào container (sau message text)
                    messagesContainer.appendChild(productCardMessageDiv);
                }
                
                setTimeout(() => {
                    messagesContainer.scrollTo({
                        top: messagesContainer.scrollHeight,
                        behavior: 'smooth'
                    });
                }, 100);
                
                // Save messages sau khi thêm message mới (trừ khi đang load từ storage)
                if (!skipSave) {
                    setTimeout(saveMessagesToStorage, 50);
                }
                
                return messageDiv;
            }
            
            // Helper function: addLoadingMessage
            function addLoadingMessage() {
                const messagesContainer = document.getElementById('chatbotMessages');
                if (!messagesContainer) return null;
                
                const messageDiv = document.createElement('div');
                messageDiv.className = 'chatbot-message bot-message';
                messageDiv.id = 'loading-message-' + Date.now();
                
                const contentDiv = document.createElement('div');
                contentDiv.className = 'message-content';
                contentDiv.innerHTML = '<i class="bi bi-person-badge"></i><span class="chatbot-loading"></span> <span>Đang suy nghĩ...</span>';
                
                messageDiv.appendChild(contentDiv);
                messagesContainer.appendChild(messageDiv);
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
                
                return messageDiv.id;
            }
            
            // Helper function: removeMessage
            function removeMessage(messageId) {
                const messageElement = document.getElementById(messageId);
                if (messageElement) {
                    messageElement.remove();
                }
            }
            
            // FULL IMPLEMENTATION: sendChatbotMessageFull
            function sendChatbotMessageFull() {
                const input = document.getElementById('chatbotInput');
                if (!input) {
                    console.error('Chatbot input not found');
                    return;
                }
                
                const message = input.value.trim();
                if (!message) {
                    return;
                }
                
                // Hiển thị user message
                addMessage(message, 'user');
                input.value = '';
                
                // Hiển thị loading
                const loadingId = addLoadingMessage();
                
                // Gửi request đến Chatbot API
                const contextPath = '${pageContext.request.contextPath}';
                fetch(contextPath + '/api/chatbot', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        message: message,
                        conversationId: window.chatbotConversationId
                    })
                })
                .then(response => response.json())
                .then(data => {
                    removeMessage(loadingId);
                    
                    // Debug: Log response
                    console.log('Chatbot response:', data);
                    console.log('Has products?', data.products && Array.isArray(data.products));
                    if (data.products) {
                        console.log('Products count:', data.products.length);
                    }
                    
                    if (data.success) {
                        let botMessage = data.message.trim().replace(/\s+/g, ' ');
                        
                        // Xóa tag [SEARCH_PRODUCTS] nếu có (tạm thời không hiển thị product cards)
                        botMessage = botMessage.replace(/\[SEARCH_PRODUCTS:\s*.+?\]/gi, '').trim();
                        
                        if (botMessage && botMessage.length > 0) {
                            // Hiển thị message text trước
                            addMessage(botMessage, 'bot', false, null);
                            
                            // Kiểm tra xem có danh sách sản phẩm trong response không
                            const messagesContainer = document.getElementById('chatbotMessages');
                            const contextPath = '${pageContext.request.contextPath}';
                            
                            if (data.products && Array.isArray(data.products) && data.products.length > 0) {
                                console.log('=== RENDERING PRODUCT CARDS ===');
                                console.log('Products count:', data.products.length);
                                
                                // Giới hạn chỉ hiển thị 3 sản phẩm
                                const maxProducts = 3;
                                const productsToShow = data.products.slice(0, maxProducts);
                                console.log('Showing', productsToShow.length, 'products (max', maxProducts, ')');
                                
                                // Tạo container cho các product cards
                                const productsContainer = document.createElement('div');
                                productsContainer.className = 'chatbot-products-container';
                                productsContainer.style.display = 'flex';
                                productsContainer.style.flexDirection = 'column';
                                productsContainer.style.gap = '0.75rem';
                                productsContainer.style.padding = '0.5rem 0.25rem';
                                productsContainer.style.width = '100%';
                                productsContainer.style.visibility = 'visible';
                                productsContainer.style.opacity = '1';
                                productsContainer.style.position = 'relative';
                                productsContainer.style.zIndex = '100';
                                
                                console.log('Container created:', productsContainer);
                                
                                // Hiển thị tối đa 3 product cards trong container
                                productsToShow.forEach(function(product, index) {
                                    console.log('Processing product', index + 1, ':', product.productID, product.productName);
                                    if (product && product.productID) {
                                        const isInStock = product.productStockStatus === 'InStock' && product.productStock > 0;
                                        const formattedPrice = new Intl.NumberFormat('vi-VN', {
                                            style: 'currency',
                                            currency: 'VND'
                                        }).format(product.productPrice || 0);
                                        
                                        // Tạo product card message
                                        const productCardMessageDiv = document.createElement('div');
                                        productCardMessageDiv.className = 'chatbot-message bot-message chatbot-product-card-message';
                                        productCardMessageDiv.setAttribute('data-product-id', product.productID);
                                        productCardMessageDiv.style.width = '100%';
                                        productCardMessageDiv.style.display = 'block';
                                        productCardMessageDiv.style.visibility = 'visible';
                                        productCardMessageDiv.style.opacity = '1';
                                        productCardMessageDiv.style.position = 'relative';
                                        productCardMessageDiv.style.zIndex = '101';
                                        
                                        const productCardDiv = document.createElement('div');
                                        productCardDiv.className = 'chatbot-product-card';
                                        productCardDiv.style.display = 'flex';
                                        productCardDiv.style.flexDirection = 'column';
                                        productCardDiv.style.width = '100%';
                                        productCardDiv.style.visibility = 'visible';
                                        productCardDiv.style.opacity = '1';
                                        productCardDiv.style.position = 'relative';
                                        productCardDiv.style.zIndex = '102';
                                        productCardDiv.style.pointerEvents = 'auto';
                                        
                                        // Hình ảnh sản phẩm
                                        const imageWrapper = document.createElement('div');
                                        imageWrapper.className = 'chatbot-product-image-wrapper';
                                        const productImage = document.createElement('img');
                                        productImage.className = 'chatbot-product-image';
                                        productImage.src = product.productImageUrl || (contextPath + '/images/placeholder.png');
                                        productImage.alt = product.productName;
                                        productImage.onerror = function() {
                                            this.src = contextPath + '/images/placeholder.png';
                                        };
                                        imageWrapper.appendChild(productImage);
                                        productCardDiv.appendChild(imageWrapper);
                                        
                                        // Thông tin sản phẩm
                                        const productInfoDiv = document.createElement('div');
                                        productInfoDiv.className = 'chatbot-product-info';
                                        
                                        // Tên sản phẩm
                                        const productNameDiv = document.createElement('div');
                                        productNameDiv.className = 'chatbot-product-name';
                                        productNameDiv.textContent = product.productName;
                                        productInfoDiv.appendChild(productNameDiv);
                                        
                                        // Category
                                        if (product.categoryName) {
                                            const categoryDiv = document.createElement('div');
                                            categoryDiv.className = 'chatbot-product-category';
                                            categoryDiv.style.cssText = 'font-size: 0.85rem; color: #888; margin: 0.25rem 0;';
                                            categoryDiv.textContent = product.categoryName;
                                            productInfoDiv.appendChild(categoryDiv);
                                        }
                                        
                                        // Giá và stock status
                                        const priceStockDiv = document.createElement('div');
                                        priceStockDiv.className = 'chatbot-product-price-stock';
                                        priceStockDiv.style.cssText = 'display: flex; justify-content: space-between; align-items: center; margin: 0.5rem 0;';
                                        
                                        // Giá
                                        const productPriceDiv = document.createElement('div');
                                        productPriceDiv.className = 'chatbot-product-price';
                                        productPriceDiv.textContent = formattedPrice;
                                        priceStockDiv.appendChild(productPriceDiv);
                                        
                                        // Stock status
                                        const stockBadge = document.createElement('span');
                                        stockBadge.className = 'chatbot-product-stock-badge' + (isInStock ? '' : ' out-of-stock');
                                        stockBadge.textContent = isInStock ? 'Còn hàng' : 'Hết hàng';
                                        priceStockDiv.appendChild(stockBadge);
                                        
                                        productInfoDiv.appendChild(priceStockDiv);
                                        
                                        // Action buttons
                                        const actionButtonsDiv = document.createElement('div');
                                        actionButtonsDiv.className = 'chatbot-product-actions';
                                        
                                        // Nút "Xem chi tiết"
                                        const viewDetailBtn = document.createElement('button');
                                        viewDetailBtn.className = 'chatbot-action-btn chatbot-view-btn';
                                        viewDetailBtn.innerHTML = '<i class="bi bi-eye"></i> Xem';
                                        viewDetailBtn.onclick = function(e) {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            if (typeof window.openProductModal === 'function') {
                                                window.openProductModal(product.productID);
                                            } else if (typeof window.openProductModalFull === 'function') {
                                                window.openProductModalFull(product.productID);
                                            } else {
                                                window.open(contextPath + '/product?id=' + product.productID, '_blank');
                                            }
                                        };
                                        actionButtonsDiv.appendChild(viewDetailBtn);
                                        
                                        // Nút "Thêm vào giỏ" (chỉ hiển thị nếu còn hàng)
                                        if (isInStock) {
                                            const addToCartBtn = document.createElement('button');
                                            addToCartBtn.className = 'chatbot-action-btn chatbot-add-cart-btn';
                                            addToCartBtn.innerHTML = '<i class="bi bi-cart-plus"></i> Thêm';
                                            addToCartBtn.onclick = function(e) {
                                                e.preventDefault();
                                                e.stopPropagation();
                                                // Sử dụng cùng logic với nút giỏ hàng trên header và các trang khác
                                                if (typeof window.addToCart === 'function') {
                                                    window.addToCart(product.productID, contextPath + '/home');
                                                } else {
                                                    // Fallback: submit form giống như window.addToCart
                                                    const form = document.createElement('form');
                                                    form.method = 'POST';
                                                    form.action = contextPath + '/cart';
                                                    const actionInput = document.createElement('input');
                                                    actionInput.type = 'hidden';
                                                    actionInput.name = 'action';
                                                    actionInput.value = 'add';
                                                    form.appendChild(actionInput);
                                                    const productIDInput = document.createElement('input');
                                                    productIDInput.type = 'hidden';
                                                    productIDInput.name = 'productID';
                                                    productIDInput.value = product.productID;
                                                    form.appendChild(productIDInput);
                                                    // Thêm redirect để sau khi thêm xong sẽ quay về trang hiện tại
                                                    const redirectInput = document.createElement('input');
                                                    redirectInput.type = 'hidden';
                                                    redirectInput.name = 'redirect';
                                                    redirectInput.value = contextPath + '/home';
                                                    form.appendChild(redirectInput);
                                                    document.body.appendChild(form);
                                                    form.submit();
                                                }
                                            };
                                            actionButtonsDiv.appendChild(addToCartBtn);
                                        }
                                        
                                        productInfoDiv.appendChild(actionButtonsDiv);
                                        productCardDiv.appendChild(productInfoDiv);
                                        productCardMessageDiv.appendChild(productCardDiv);
                                        
                                        // Thêm product card vào container ngang
                                        productsContainer.appendChild(productCardMessageDiv);
                                        console.log('Product card', index + 1, 'added to container');
                                    } else {
                                        console.warn('Skipping invalid product:', product);
                                    }
                                });
                                
                                // Thêm container ngang vào messages container
                                console.log('=== FINAL CHECK ===');
                                console.log('Products container children count:', productsContainer.children.length);
                                console.log('Messages container exists:', !!messagesContainer);
                                
                                if (messagesContainer) {
                                    if (productsContainer.children.length > 0) {
                                        // Xóa products container rỗng cũ nếu có
                                        const existingContainers = messagesContainer.querySelectorAll('.chatbot-products-container');
                                        existingContainers.forEach(function(container) {
                                            if (container.children.length === 0) {
                                                container.remove();
                                            }
                                        });
                                        
                                        // Thêm container vào DOM
                                        messagesContainer.appendChild(productsContainer);
                                        console.log('✅ Products container added to messages container');
                                        console.log('Container in DOM:', productsContainer.parentElement === messagesContainer);
                                        console.log('Container visible:', productsContainer.offsetWidth > 0 && productsContainer.offsetHeight > 0);
                                        
                                        // Force reflow để đảm bảo CSS được áp dụng
                                        void productsContainer.offsetHeight;
                                        
                                        // Kiểm tra lại sau một chút
                                        setTimeout(() => {
                                            console.log('After timeout - Container visible:', productsContainer.offsetWidth > 0 && productsContainer.offsetHeight > 0);
                                            console.log('Container computed style:', window.getComputedStyle(productsContainer).display);
                                            
                                            // Xóa container nếu vẫn rỗng
                                            if (productsContainer.children.length === 0) {
                                                productsContainer.remove();
                                                console.log('Removed empty products container after timeout');
                                            } else {
                                                // Scroll to bottom để hiển thị products
                                                messagesContainer.scrollTo({
                                                    top: messagesContainer.scrollHeight,
                                                    behavior: 'smooth'
                                                });
                                            }
                                        }, 200);
                                    } else {
                                        console.error('❌ Products container is empty! Not adding to DOM.');
                                        // Không thêm container rỗng vào DOM
                                    }
                                } else {
                                    console.error('❌ Messages container not found!');
                                }
                                
                            } else if (data.productID) {
                                // Backward compatibility: hiển thị 1 sản phẩm
                                const productData = {
                                    productID: data.productID,
                                    productName: data.productName || '',
                                    productPrice: data.productPrice || 0,
                                    productStock: data.productStock || 0,
                                    productStockStatus: data.productStockStatus || '',
                                    productImageUrl: data.productImageUrl || ''
                                };
                                addMessage('', 'bot', false, productData);
                            }
                            
                            // Save conversation ID và messages
                            localStorage.setItem(STORAGE_KEY_CONVERSATION_ID, window.chatbotConversationId);
                            saveMessagesToStorage();
                        }
                    } else {
                        let errorMsg = data.error || 'Vui lòng thử lại sau';
                        if (errorMsg.includes('quota') || errorMsg.includes('hết quota') || errorMsg.includes('billing')) {
                            errorMsg = errorMsg + '<br><br><small style="color: #b0b0b0;">💡 <strong>Gợi ý:</strong> ' +
                                       'Nếu bạn là quản trị viên, vui lòng kiểm tra tài khoản OpenAI và nạp tiền nếu cần. ' +
                                       'Người dùng có thể liên hệ bộ phận hỗ trợ qua email: smartshop686868@gmail.com hoặc hotline: 0833347220</small>';
                        }
                        addMessage('Xin lỗi, ' + errorMsg, 'bot');
                        saveMessagesToStorage();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    removeMessage(loadingId);
                    addMessage('Xin lỗi, không thể kết nối đến server. Vui lòng thử lại sau.', 'bot');
                    saveMessagesToStorage();
                });
            }
            
            // Load messages khi page load (sau khi DOM ready)
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', function() {
                    setTimeout(loadMessagesFromStorage, 100);
                    setTimeout(showSuggestions, 200);
                });
            } else {
                setTimeout(loadMessagesFromStorage, 100);
                setTimeout(showSuggestions, 200);
            }
            
            // Lưu messages trước khi page unload
            window.addEventListener('beforeunload', function() {
                saveMessagesToStorage();
            });
            
            // Lưu messages định kỳ (mỗi 5 giây)
            setInterval(saveMessagesToStorage, 5000);
            
            // EXPOSE FUNCTIONS IMMEDIATELY to window
            window.sendChatbotMessageFull = sendChatbotMessageFull;
            window.sendChatbotMessage = sendChatbotMessageFull;
            
            console.log('✓✓✓ sendChatbotMessage FULL IMPLEMENTATION defined and exposed IMMEDIATELY after HTML ✓✓✓');
            console.log('✓ Function type:', typeof window.sendChatbotMessage);
            console.log('✓ Ready to handle button clicks immediately!');
            
            // ============================================
            // CHỨC NĂNG ĐẶT HÀNG TỪ CHATBOT
            // ============================================
            
            // Kiểm tra và hiển thị nút đặt hàng
            function checkAndShowOrderButton() {
                const orderBtn = document.getElementById('chatbotOrderBtn');
                if (!orderBtn) return;
                
                // Kiểm tra xem user đã đăng nhập và có sản phẩm trong giỏ hàng không
                const contextPath = '${pageContext.request.contextPath}';
                fetch(contextPath + '/api/cart/check', {
                    method: 'GET',
                    credentials: 'same-origin'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.hasItems && data.isLoggedIn) {
                        orderBtn.style.display = 'flex';
                    } else {
                        orderBtn.style.display = 'none';
                    }
                })
                .catch(error => {
                    console.error('Error checking cart:', error);
                    orderBtn.style.display = 'none';
                });
            }
            
            // Load địa chỉ và phương thức thanh toán
            function loadOrderData() {
                const contextPath = '${pageContext.request.contextPath}';
                
                // Load địa chỉ
                fetch(contextPath + '/api/addresses', {
                    method: 'GET',
                    credentials: 'same-origin'
                })
                .then(response => response.json())
                .then(data => {
                    const shippingSelect = document.getElementById('chatbotShippingAddressID');
                    const billingSelect = document.getElementById('chatbotBillingAddressID');
                    
                    if (shippingSelect && billingSelect && data.addresses) {
                        // Clear options
                        shippingSelect.innerHTML = '<option value="">-- Chọn địa chỉ --</option>';
                        billingSelect.innerHTML = '<option value="">-- Chọn địa chỉ --</option>';
                        
                        // Add addresses
                        data.addresses.forEach(address => {
                            const optionText = `${address.fullName} - ${address.phone} - ${address.fullAddress}${address.isDefault ? ' (Mặc định)' : ''}`;
                            const shippingOption = new Option(optionText, address.addressID, address.isDefault, address.isDefault);
                            const billingOption = new Option(optionText, address.addressID);
                            shippingSelect.add(shippingOption);
                            billingSelect.add(billingOption);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error loading addresses:', error);
                });
                
                // Load phương thức thanh toán
                fetch(contextPath + '/api/payment-methods', {
                    method: 'GET',
                    credentials: 'same-origin'
                })
                .then(response => response.json())
                .then(data => {
                    const paymentSelect = document.getElementById('chatbotPaymentMethodID');
                    if (paymentSelect && data.paymentMethods) {
                        paymentSelect.innerHTML = '<option value="">-- Chọn phương thức thanh toán --</option>';
                        data.paymentMethods.forEach(method => {
                            const option = new Option(method.methodName, method.paymentMethodID);
                            paymentSelect.add(option);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error loading payment methods:', error);
                });
            }
            
            // Xử lý checkbox "Giống địa chỉ giao hàng"
            function setupSameAsShipping() {
                const checkbox = document.getElementById('chatbotSameAsShipping');
                const shippingSelect = document.getElementById('chatbotShippingAddressID');
                const billingSelect = document.getElementById('chatbotBillingAddressID');
                
                if (checkbox && shippingSelect && billingSelect) {
                    checkbox.addEventListener('change', function() {
                        if (this.checked) {
                            billingSelect.value = shippingSelect.value;
                            billingSelect.disabled = true;
                        } else {
                            billingSelect.disabled = false;
                        }
                    });
                    
                    // Cập nhật khi shipping address thay đổi
                    shippingSelect.addEventListener('change', function() {
                        if (checkbox.checked) {
                            billingSelect.value = this.value;
                        }
                    });
                }
            }
            
            // Xử lý đặt hàng
            function handlePlaceOrder() {
                const form = document.getElementById('chatbotOrderForm');
                const errorDiv = document.getElementById('chatbotOrderError');
                const placeOrderBtn = document.getElementById('chatbotPlaceOrderBtn');
                
                if (!form || !errorDiv || !placeOrderBtn) return;
                
                // Validate form
                const shippingAddressID = document.getElementById('chatbotShippingAddressID').value;
                const paymentMethodID = document.getElementById('chatbotPaymentMethodID').value;
                
                if (!shippingAddressID || !paymentMethodID) {
                    errorDiv.textContent = 'Vui lòng chọn địa chỉ giao hàng và phương thức thanh toán';
                    errorDiv.style.display = 'block';
                    return;
                }
                
                // Disable button
                placeOrderBtn.disabled = true;
                placeOrderBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Đang xử lý...';
                errorDiv.style.display = 'none';
                
                // Get form data
                const formData = new FormData(form);
                
                // Submit order
                const contextPath = '${pageContext.request.contextPath}';
                fetch(contextPath + '/checkout', {
                    method: 'POST',
                    credentials: 'same-origin',
                    body: formData
                })
                .then(response => {
                    if (response.redirected) {
                        // Redirect to order confirmation page
                        window.location.href = response.url;
                    } else {
                        return response.text();
                    }
                })
                .then(data => {
                    if (data) {
                        // Check if there's an error
                        if (data.includes('errorMessage') || data.includes('error')) {
                            errorDiv.textContent = 'Có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.';
                            errorDiv.style.display = 'block';
                            placeOrderBtn.disabled = false;
                            placeOrderBtn.innerHTML = '<i class="bi bi-check-circle me-1"></i>Đặt hàng';
                        }
                    }
                })
                .catch(error => {
                    console.error('Error placing order:', error);
                    errorDiv.textContent = 'Không thể kết nối đến server. Vui lòng thử lại sau.';
                    errorDiv.style.display = 'block';
                    placeOrderBtn.disabled = false;
                    placeOrderBtn.innerHTML = '<i class="bi bi-check-circle me-1"></i>Đặt hàng';
                });
            }
            
            // Setup event listeners
            function setupOrderButton() {
                const orderBtn = document.getElementById('chatbotOrderBtn');
                const orderModal = document.getElementById('chatbotOrderModal');
                const placeOrderBtn = document.getElementById('chatbotPlaceOrderBtn');
                
                if (orderBtn && orderModal) {
                    orderBtn.addEventListener('click', function() {
                        // Load data và mở modal
                        loadOrderData();
                        const modal = new bootstrap.Modal(orderModal);
                        modal.show();
                    });
                }
                
                if (orderModal) {
                    orderModal.addEventListener('show.bs.modal', function() {
                        loadOrderData();
                        setupSameAsShipping();
                    });
                }
                
                if (placeOrderBtn) {
                    placeOrderBtn.addEventListener('click', handlePlaceOrder);
                }
            }
            
            // Initialize order button functionality
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', function() {
                    checkAndShowOrderButton();
                    setupOrderButton();
                    // Check lại mỗi 5 giây
                    setInterval(checkAndShowOrderButton, 5000);
                });
            } else {
                checkAndShowOrderButton();
                setupOrderButton();
                // Check lại mỗi 5 giây
                setInterval(checkAndShowOrderButton, 5000);
            }
        })();
    </script>

    <style>
        /* Scope tất cả CSS của chatbot vào widget để không ảnh hưởng các element khác */
        #chatbotWidget {
            position: fixed;
            bottom: 20px;
            right: 20px;
            left: auto;
            top: auto;
            width: auto;
            height: auto;
            max-width: 400px;
            max-height: none;
            z-index: 1100;
            /* Cao hơn navbar-top (1081) và header (1080) để không bị che */
            pointer-events: none;
            /* Không chặn click ở container */
            margin: 0;
            padding: 0;
        }

        /* Tất cả các element con của chatbot có thể click */
        #chatbotWidget * {
            pointer-events: all !important;
        }

        /* Chatbot Button - Scoped to chatbot widget only */
        #chatbotWidget .chatbot-button {
            width: 60px !important;
            height: 60px !important;
            border-radius: 50% !important;
            background: linear-gradient(135deg, #dc3545 0%, #ff6b35 100%) !important;
            color: white !important;
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
            font-size: 1.5rem !important;
            cursor: pointer !important;
            box-shadow: 0 4px 20px rgba(220, 53, 69, 0.5) !important;
            transition: transform 0.3s, box-shadow 0.3s !important;
            position: relative !important;
            z-index: 1102 !important;
            /* Cao hơn widget container (1100) và navbar (1081) để đảm bảo button luôn clickable */
            pointer-events: all !important;
            /* Đảm bảo có thể click */
            user-select: none !important;
            /* Không cho phép select text */
            -webkit-user-select: none !important;
            -moz-user-select: none !important;
            -ms-user-select: none !important;
            touch-action: manipulation !important;
            /* Tối ưu cho touch */
            margin: 0 !important;
            padding: 0 !important;
            float: none !important;
            clear: none !important;
            /* Đảm bảo button không bị đẩy xuống */
        }

        #chatbotWidget .chatbot-button:hover {
            transform: scale(1.1);
            box-shadow: 0 6px 25px rgba(220, 53, 69, 0.7);
        }

        #chatbotWidget .chatbot-button:active {
            transform: scale(0.95);
        }

        #chatbotWidget .chatbot-button i {
            position: relative;
            pointer-events: none;
            /* Icon không chặn click */
        }
        

        /* Chatbot Window - Scoped to chatbot widget only */
        #chatbotWidget .chatbot-window {
            position: absolute;
            bottom: 70px;
            right: 0;
            width: 480px;
            max-width: calc(100vw - 40px);
            height: 550px;
            max-height: calc(100vh - 250px);
            background: #1a1a1a;
            border: 2px solid #dc3545;
            border-radius: 15px;
            box-shadow: 0 10px 50px rgba(220, 53, 69, 0.3);
            display: none;
            flex-direction: column;
            overflow: hidden;
            z-index: 1101;
            /* Cao hơn widget container (1100) và navbar (1081) để không bị che */
            visibility: hidden;
            opacity: 0;
            transform: translateY(20px);
            transition: opacity 0.3s ease, transform 0.3s ease, visibility 0.3s ease;
        }
        
        /* Đảm bảo chatbot không bị header/navbar che khi màn hình nhỏ */
        @media (max-height: 900px) {
            #chatbotWidget .chatbot-window {
                max-height: calc(100vh - 160px) !important;
                height: 600px !important;
            }
        }
        
        @media (max-height: 800px) {
            #chatbotWidget .chatbot-window {
                max-height: calc(100vh - 150px) !important;
                height: 550px !important;
            }
        }
        
        /* Responsive cho màn hình nhỏ hơn */
        @media (max-height: 700px) {
            #chatbotWidget .chatbot-window {
                max-height: calc(100vh - 140px) !important;
                height: 500px !important;
            }
        }
        
        @media (max-height: 600px) {
            #chatbotWidget .chatbot-window {
                max-height: calc(100vh - 130px) !important;
                height: 450px !important;
            }
        }
        
        /* Đảm bảo chatbot không bị navbar che - điều chỉnh bottom khi cần */
        @media (max-width: 768px) {
            #chatbotWidget {
                bottom: 10px;
                right: 10px;
            }
            
            #chatbotWidget .chatbot-window {
                bottom: 70px;
                max-height: calc(100vh - 200px) !important;
            }
        }
        
        /* Responsive width cho màn hình nhỏ */
        @media (max-width: 768px) {
            #chatbotWidget .chatbot-window {
                width: calc(100vw - 40px) !important;
                max-width: 500px !important;
            }
        }
        
        @media (max-width: 576px) {
            #chatbotWidget .chatbot-window {
                width: calc(100vw - 20px) !important;
                right: 10px !important;
            }
        }

        #chatbotWidget .chatbot-window.active {
            display: flex !important;
            visibility: visible !important;
            opacity: 1 !important;
            transform: translateY(0) !important;
            animation: slideUp 0.3s ease-out;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }

            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        #chatbotWidget .chatbot-header {
            background: linear-gradient(135deg, #dc3545 0%, #ff6b35 100%);
            color: white;
            padding: 1rem 1.25rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid rgba(255, 255, 255, 0.1);
        }

        #chatbotWidget .chatbot-header span {
            font-weight: 600;
            font-size: 1rem;
        }
        
        #chatbotWidget .chatbot-clear-btn {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.1);
            color: #ffffff;
            border: 1px solid rgba(255, 255, 255, 0.2);
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 0.9rem;
            padding: 0;
        }
        
        #chatbotWidget .chatbot-clear-btn:hover {
            background: rgba(220, 53, 69, 0.8);
            border-color: #dc3545;
            transform: scale(1.1);
        }
        
        #chatbotWidget .chatbot-clear-btn:active {
            transform: scale(0.95);
        }
        
        #chatbotWidget .chatbot-order-btn {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.2);
            color: white;
            border: 1px solid rgba(255, 255, 255, 0.3);
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 1rem;
            padding: 0;
        }
        
        #chatbotWidget .chatbot-order-btn:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: scale(1.1);
        }
        
        #chatbotWidget .chatbot-order-btn:active {
            transform: scale(0.95);
        }
        
        /* Modal đặt hàng từ chatbot - z-index cao hơn chatbot và navbar */
        #chatbotOrderModal {
            z-index: 1110 !important;
        }
        
        #chatbotOrderModal .modal-backdrop {
            z-index: 1109 !important;
        }

        #chatbotWidget .chatbot-messages {
            flex: 1;
            overflow-y: auto;
            padding: 1.25rem;
            display: flex;
            flex-direction: column;
            gap: 1rem;
            background: #1a1a1a;
        }

        #chatbotWidget .chatbot-messages::-webkit-scrollbar {
            width: 6px;
        }

        #chatbotWidget .chatbot-messages::-webkit-scrollbar-track {
            background: #1a1a1a;
        }

        #chatbotWidget .chatbot-messages::-webkit-scrollbar-thumb {
            background: #4a4a4a;
            border-radius: 10px;
        }

        #chatbotWidget .chatbot-messages::-webkit-scrollbar-thumb:hover {
            background: #6a6a6a;
        }

        #chatbotWidget .chatbot-message {
            max-width: 80%;
            padding: 0.875rem 1rem;
            border-radius: 15px;
            word-wrap: break-word;
            animation: messageSlide 0.3s ease-out;
            position: relative;
            z-index: 1;
        }
        
        /* Override cho product card message - không bị giới hạn max-width */
        #chatbotWidget .chatbot-product-card-message {
            max-width: none !important;
            padding: 0 !important;
            border-radius: 0 !important;
            animation: none !important;
            position: relative !important;
            z-index: 1000 !important;
        }

        @keyframes messageSlide {
            from {
                opacity: 0;
                transform: translateY(10px);
            }

            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        #chatbotWidget .user-message {
            background: linear-gradient(135deg, #dc3545 0%, #ff6b35 100%);
            color: white;
            align-self: flex-end;
            border-bottom-right-radius: 5px;
            box-shadow: 0 2px 10px rgba(220, 53, 69, 0.3);
        }

        #chatbotWidget .bot-message {
            background: #2c2c2c;
            color: #fff;
            align-self: flex-start;
            border-bottom-left-radius: 5px;
            border: 1px solid #4a4a4a;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
        }

        #chatbotWidget .message-content {
            display: flex;
            align-items: flex-start;
            gap: 0.5rem;
        }

        #chatbotWidget .message-content i {
            color: #ff6b35;
            margin-top: 0.125rem;
        }

        #chatbotWidget .chatbot-suggestions {
            padding: 0.75rem 1rem;
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            background: rgba(255, 255, 255, 0.03);
            border-top: 1px solid rgba(255, 255, 255, 0.1);
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            max-height: 120px;
            overflow-y: auto;
        }
        
        #chatbotWidget .chatbot-suggestions::-webkit-scrollbar {
            width: 4px;
            height: 4px;
        }
        
        #chatbotWidget .chatbot-suggestions::-webkit-scrollbar-track {
            background: transparent;
        }
        
        #chatbotWidget .chatbot-suggestions::-webkit-scrollbar-thumb {
            background: #4a4a4a;
            border-radius: 10px;
        }
        
        #chatbotWidget .chatbot-suggestion-item {
            padding: 0.5rem 0.75rem;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 20px;
            color: #ffffff;
            font-size: 0.85rem;
            cursor: pointer;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            gap: 0.4rem;
            white-space: nowrap;
        }
        
        #chatbotWidget .chatbot-suggestion-item i {
            font-size: 0.9rem;
            opacity: 0.8;
        }
        
        #chatbotWidget .chatbot-suggestion-item:hover {
            background: rgba(0, 123, 255, 0.3);
            border-color: rgba(0, 123, 255, 0.5);
            transform: translateY(-2px);
        }
        
        #chatbotWidget .chatbot-suggestion-item:active {
            transform: translateY(0);
        }

        #chatbotWidget .chatbot-input-container {
            display: flex;
            padding: 1rem 1.25rem;
            gap: 0.75rem;
            border-top: 2px solid #4a4a4a;
            background: #1a1a1a;
            position: relative;
            z-index: 5;
            /* Đảm bảo input container có thể tương tác */
            pointer-events: all !important;
        }

        #chatbotWidget .chatbot-input-container * {
            pointer-events: all !important;
            /* Tất cả element trong container đều có thể click */
        }

        #chatbotWidget .chatbot-input {
            flex: 1;
            background: #2c2c2c;
            border: 2px solid #4a4a4a;
            color: white;
            padding: 0.75rem 1.25rem;
            border-radius: 25px;
            outline: none;
            font-size: 0.95rem;
            transition: border-color 0.3s;
        }

        #chatbotWidget .chatbot-input:focus {
            border-color: #dc3545;
            box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.1);
        }

        #chatbotWidget .chatbot-input::placeholder {
            color: #6a6a6a;
        }

        #chatbotWidget .chatbot-send-btn {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            background: linear-gradient(135deg, #dc3545 0%, #ff6b35 100%);
            color: white;
            border: none;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer !important;
            transition: transform 0.3s, box-shadow 0.3s;
            font-size: 1.1rem;
            pointer-events: all !important;
            /* Đảm bảo có thể click */
            position: relative;
            z-index: 10;
            /* Đảm bảo nút trên cùng trong container */
            user-select: none;
            -webkit-user-select: none;
            touch-action: manipulation;
        }

        #chatbotWidget .chatbot-send-btn:hover {
            transform: scale(1.1);
            box-shadow: 0 4px 15px rgba(220, 53, 69, 0.5);
        }

        #chatbotWidget .chatbot-send-btn:active {
            transform: scale(0.95);
        }

        #chatbotWidget .chatbot-send-btn:disabled {
            opacity: 0.6;
            cursor: not-allowed !important;
            pointer-events: none !important;
        }

        #chatbotWidget .chatbot-send-btn i {
            pointer-events: none;
            /* Icon không chặn click */
        }

        #chatbotWidget .chatbot-loading {
            display: inline-block;
            width: 18px;
            height: 18px;
            border: 3px solid #4a4a4a;
            border-top-color: #dc3545;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        
        /* Container cho nhiều product cards - nằm ngang với scroll */
        #chatbotWidget .chatbot-products-container {
            margin-top: 0.5rem;
            margin-bottom: 0.5rem;
            max-width: 100% !important;
            width: 100% !important;
            align-self: flex-start !important;
            display: flex !important;
            flex-direction: column !important;
            gap: 0.75rem;
            padding: 0.5rem 0.25rem;
            scrollbar-width: thin;
            scrollbar-color: #4a4a4a #1a1a1a;
            -webkit-overflow-scrolling: touch;
            visibility: visible !important;
            opacity: 1 !important;
            background: transparent !important;
            position: relative !important;
            z-index: 100 !important;
        }
        
        #chatbotWidget .chatbot-products-container::-webkit-scrollbar {
            height: 6px;
        }
        
        #chatbotWidget .chatbot-products-container::-webkit-scrollbar-track {
            background: #1a1a1a;
            border-radius: 10px;
        }
        
        #chatbotWidget .chatbot-products-container::-webkit-scrollbar-thumb {
            background: #4a4a4a;
            border-radius: 10px;
        }
        
        #chatbotWidget .chatbot-products-container::-webkit-scrollbar-thumb:hover {
            background: #6a6a6a;
        }
        
        /* Product card message riêng - nằm trong container ngang */
        #chatbotWidget .chatbot-product-card-message {
            flex: 0 0 auto !important;
            width: 200px !important;
            min-width: 200px !important;
            max-width: 200px !important;
            visibility: visible !important;
            opacity: 1 !important;
            display: block !important;
            margin: 0 !important;
            padding: 0 !important;
            align-self: flex-start !important;
            background: transparent !important;
            border: none !important;
            box-shadow: none !important;
            position: relative !important;
            z-index: 101 !important;
        }
        
        /* Product card trong chatbot messages - nhỏ hơn và nằm ngang */
        #chatbotWidget .chatbot-product-card {
            background: #2d2d2d;
            border-radius: 10px;
            overflow: hidden;
            border: 1px solid #444;
            display: flex !important;
            flex-direction: column;
            width: 100%;
            height: auto;
            min-height: 280px;
            position: relative !important;
            z-index: 102 !important;
            pointer-events: auto !important;
            visibility: visible !important;
            opacity: 1 !important;
        }
        
        #chatbotWidget .chatbot-product-image-wrapper {
            width: 100%;
            height: 120px;
            min-height: 120px;
            overflow: hidden;
            background: #1a1a1a;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            border-radius: 8px 8px 0 0;
            flex-shrink: 0;
        }
        
        #chatbotWidget .chatbot-product-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
            transition: transform 0.3s;
        }
        
        #chatbotWidget .chatbot-product-card:hover .chatbot-product-image {
            transform: scale(1.05);
        }
        
        #chatbotWidget .chatbot-product-info {
            padding: 0.5rem 0.75rem;
            display: flex;
            flex-direction: column;
            gap: 0.375rem;
            flex: 1;
            min-width: 0;
        }
        
        #chatbotWidget .chatbot-product-name {
            font-size: 0.8rem;
            font-weight: 600;
            color: #ffffff;
            line-height: 1.3;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
            min-height: 2rem;
            max-height: 2rem;
        }
        
        #chatbotWidget .chatbot-product-price-stock {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 0.5rem;
        }
        
        #chatbotWidget .chatbot-product-price {
            font-size: 0.9rem;
            font-weight: bold;
            color: #dc3545;
            margin: 0;
        }
        
        #chatbotWidget .chatbot-product-stock-badge {
            display: inline-block;
            padding: 0.2rem 0.4rem;
            border-radius: 4px;
            font-size: 0.7rem;
            font-weight: 500;
            background: #28a745;
            color: #ffffff;
            white-space: nowrap;
            flex-shrink: 0;
        }
        
        #chatbotWidget .chatbot-product-stock-badge.out-of-stock {
            background: #6c757d;
        }
        
        /* Product action buttons trong chatbot messages */
        #chatbotWidget .chatbot-product-actions {
            margin-top: 0.5rem;
            display: flex;
            flex-direction: column;
            gap: 0.4rem;
        }
        
        #chatbotWidget .chatbot-action-btn {
            width: 100%;
            padding: 0.4rem 0.6rem;
            border-radius: 6px;
            font-size: 0.7rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
            border: none;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.25rem;
            position: relative;
            z-index: 11;
            pointer-events: auto;
        }
        
        #chatbotWidget .chatbot-view-btn {
            background: #4a4a4a;
            color: #ffffff;
            border: 1px solid #6a6a6a;
        }
        
        #chatbotWidget .chatbot-view-btn:hover {
            background: #5a5a5a;
            border-color: #dc3545;
            transform: translateY(-1px);
            box-shadow: 0 2px 6px rgba(220, 53, 69, 0.3);
        }
        
        #chatbotWidget .chatbot-add-cart-btn {
            background: linear-gradient(135deg, #dc3545 0%, #ff6b35 100%);
            color: #ffffff;
            border: 1px solid #dc3545;
        }
        
        #chatbotWidget .chatbot-add-cart-btn:hover {
            background: linear-gradient(135deg, #c82333 0%, #e55a2b 100%);
            transform: translateY(-1px);
            box-shadow: 0 3px 10px rgba(220, 53, 69, 0.4);
        }
        
        #chatbotWidget .chatbot-action-btn:active {
            transform: translateY(0);
        }
        
        /* Responsive cho product card */
        @media (max-width: 400px) {
            #chatbotWidget .chatbot-product-card {
                flex-direction: column;
            }
            
            #chatbotWidget .chatbot-product-image-wrapper {
                width: 100%;
                height: 120px;
            }
        }

        @keyframes spin {
            to {
                transform: rotate(360deg);
            }
        }


        @media (max-width: 576px) {
            #chatbotWidget .chatbot-window {
                width: calc(100vw - 40px);
                height: calc(100vh - 100px);
                right: -20px;
                bottom: 70px;
            }

            #chatbotWidget .chatbot-button {
                width: 55px;
                height: 55px;
                font-size: 1.3rem;
            }
        }
    </style>

<script>
    // Chỉ khởi tạo chatbot nếu chưa được khởi tạo
    if (typeof chatbotInitialized === 'undefined') {
        var chatbotInitialized = true;
        let chatbotConversationId = 'global_' + Date.now();
        
        // Define helper functions first (addMessage, addLoadingMessage, removeMessage) so they're available
        function addMessage(text, type) {
            const messagesContainer = document.getElementById('chatbotMessages');
            if (!messagesContainer) return;
            
            const messageDiv = document.createElement('div');
            messageDiv.className = 'chatbot-message ' + type + '-message';
            
            const contentDiv = document.createElement('div');
            contentDiv.className = 'message-content';
            
            if (type === 'bot') {
                // Format text: thay \n thành <br>, xử lý khoảng trắng thừa
                let formattedText = text
                    .replace(/\n+/g, '<br>')  // Thay newline thành <br>
                    .replace(/\s+/g, ' ')     // Xóa khoảng trắng thừa
                    .trim();

                // Tách câu dài thành nhiều đoạn ngắn hơn (tối đa 80 ký tự mỗi dòng)
                // Nhưng giữ nguyên nếu đã có <br>
                if (!formattedText.includes('<br>') && formattedText.length > 80) {
                    // Tách theo dấu chấm, phẩy, dấu hai chấm
                    formattedText = formattedText
                        .replace(/\.\s+/g, '.<br>')
                        .replace(/,\s+/g, ',<br>')
                        .replace(/:\s+/g, ':<br>');
                }
                
                contentDiv.innerHTML = '<i class="bi bi-person-badge"></i><span>' + formattedText + '</span>';
            } else {
                contentDiv.innerHTML = '<span>' + escapeHtml(text) + '</span>';
            }
            
            messageDiv.appendChild(contentDiv);
            messagesContainer.appendChild(messageDiv);
            
            // Scroll to bottom với animation mượt
            setTimeout(() => {
                messagesContainer.scrollTo({
                    top: messagesContainer.scrollHeight,
                    behavior: 'smooth'
                });
            }, 100);
            
            return messageDiv;
        }

        function addLoadingMessage() {
            const messagesContainer = document.getElementById('chatbotMessages');
            if (!messagesContainer) return null;

            const messageDiv = document.createElement('div');
            messageDiv.className = 'chatbot-message bot-message';
            messageDiv.id = 'loading-message-' + Date.now();

            const contentDiv = document.createElement('div');
            contentDiv.className = 'message-content';
            contentDiv.innerHTML = '<i class="bi bi-person-badge"></i><span class="chatbot-loading"></span> <span>Đang suy nghĩ...</span>';

            messageDiv.appendChild(contentDiv);
            messagesContainer.appendChild(messageDiv);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;

            return messageDiv.id;
        }

        function removeMessage(messageId) {
            const messageElement = document.getElementById(messageId);
            if (messageElement) {
                messageElement.remove();
            }
        }
        
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
        
        // ============================================
        // CRITICAL: Define sendChatbotMessageFull FIRST and expose IMMEDIATELY
        // This ensures the function is ALWAYS available when user clicks send
        // ============================================
        function sendChatbotMessageFull() {
            const input = document.getElementById('chatbotInput');
            if (!input) {
                console.error('Chatbot input not found');
                return;
            }

            const message = input.value.trim();
            
            if (!message) {
                return;
            }

            // Hiển thị user message
            addMessage(message, 'user');
            input.value = '';
            
            // Hiển thị loading
            const loadingId = addLoadingMessage();
            
                // Gửi request đến Chatbot API
                fetch('${pageContext.request.contextPath}/api/chatbot', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        message: message,
                        conversationId: chatbotConversationId
                    })
                })
            .then(response => response.json())
            .then(data => {
                // Xóa loading message
                removeMessage(loadingId);
                
                if (data.success) {
                    let botMessage = data.message;
                    
                    // Làm sạch message: xóa khoảng trắng thừa, newline thừa
                    botMessage = botMessage.trim().replace(/\s+/g, ' ');
                    
                    // Xóa tag [SEARCH_PRODUCTS] nếu có (tạm thời không hiển thị product cards)
                    botMessage = botMessage.replace(/\[SEARCH_PRODUCTS:\s*.+?\]/gi, '').trim();
                    
                    // Chỉ hiển thị nếu message không rỗng
                    if (botMessage && botMessage.length > 0) {
                        addMessage(botMessage, 'bot');
                        // Save conversation ID và messages
                        localStorage.setItem(STORAGE_KEY_CONVERSATION_ID, chatbotConversationId);
                        saveMessagesToStorage();
                    }
                } else {
                    // Hiển thị error message chi tiết
                    let errorMsg = data.error || 'Vui lòng thử lại sau';
                    
                    // Kiểm tra nếu là lỗi quota, thêm thông tin hữu ích
                    if (errorMsg.includes('quota') || errorMsg.includes('hết quota') || errorMsg.includes('billing')) {
                        errorMsg = errorMsg + '<br><br><small style="color: #b0b0b0;">💡 <strong>Gợi ý:</strong> ' +
                                   'Nếu bạn là quản trị viên, vui lòng kiểm tra tài khoản OpenAI và nạp tiền nếu cần. ' +
                                   'Người dùng có thể liên hệ bộ phận hỗ trợ qua email: smartshop686868@gmail.com hoặc hotline: 0833347220</small>';
                    }
                    
                    addMessage('Xin lỗi, ' + errorMsg, 'bot');
                    saveMessagesToStorage();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                removeMessage(loadingId);
                addMessage('Xin lỗi, không thể kết nối đến server. Vui lòng thử lại sau.', 'bot');
                saveMessagesToStorage();
            });
        }
        
        // Note: sendChatbotMessageFull is already exposed in the first script block
        // This is just a backup/re-exposure in case the first one didn't run
        if (typeof window.sendChatbotMessage !== 'function' || typeof window.sendChatbotMessageFull !== 'function') {
            console.log('Re-exposing sendChatbotMessageFull (backup)...');
            window.sendChatbotMessageFull = sendChatbotMessageFull;
            window.sendChatbotMessage = sendChatbotMessageFull;
            console.log('✓ Backup exposure complete');
        } else {
            console.log('✓ sendChatbotMessage already exposed from first script block');
        }

        // Toggle chatbot window - FULL implementation
        function toggleChatbotFull() {
            console.log('toggleChatbot FULL implementation called');
            const chatWindow = document.getElementById('chatbotWindow');
            if (chatWindow) {
                const isActive = chatWindow.classList.contains('active');
                chatWindow.classList.toggle('active');
                console.log('Chat window toggled, isActive now:', !isActive);

                if (!isActive) {
                    // Mở window - focus input sau một chút
                    setTimeout(function () {
                        const input = document.getElementById('chatbotInput');
                        if (input) {
                            input.focus();
                        }
                    }, 100);
                }
            } else {
                console.error('chatbotWindow element not found');
            }
            return false;
        }

            // Also keep simple toggle function
            function toggleChatbot() {
                return toggleChatbotFull();
            }

            // Expose both to global scope
            window.toggleChatbotFull = toggleChatbotFull;
            window.toggleChatbot = toggleChatbot;
            console.log('toggleChatbot FULL implementation exposed to window:', typeof window.toggleChatbot);

            // Auto-hide chatbot when modal opens to prevent conflicts
            function setupModalObserver() {
                const modalElement = document.getElementById('productDetailModal');
                if (modalElement) {
                    // Watch for modal show/hide - chỉ dùng event listener, không dùng setInterval
                    modalElement.addEventListener('show.bs.modal', function () {
                        const chatWindow = document.getElementById('chatbotWindow');
                        if (chatWindow && chatWindow.classList.contains('active')) {
                            chatWindow.classList.remove('active');
                        }
                    });
                }
            }

            // Setup modal observer when DOM is ready
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', setupModalObserver);
            } else {
                setupModalObserver();
            }

            function handleChatbotKeyPress(event) {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    console.log('handleChatbotKeyPress: Enter key pressed');
                    if (typeof window.sendChatbotMessage === 'function') {
                        window.sendChatbotMessage();
                    } else if (typeof window.sendChatbotMessageFull === 'function') {
                        window.sendChatbotMessageFull();
                    } else if (typeof sendChatbotMessageFull === 'function') {
                        sendChatbotMessageFull();
                    } else {
                        console.error('sendChatbotMessage not found in handleChatbotKeyPress');
                    }
                }
            }

            // Setup close button và clear button handler - CHỈ 1 LẦN khi DOM ready
            function setupHeaderButtons() {
                // Setup close button
                const closeButton = document.querySelector('#chatbotWidget .chatbot-header .btn-close');
                if (closeButton && !closeButton.dataset.buttonInitialized) {
                    closeButton.dataset.buttonInitialized = 'true';
                    closeButton.onclick = function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        const chatWindow = document.getElementById('chatbotWindow');
                        if (chatWindow) {
                            chatWindow.classList.remove('active');
                        }
                        return false;
                    };
                }
                
                // Setup clear button
                const clearButton = document.getElementById('chatbotClearBtn');
                if (clearButton && !clearButton.dataset.buttonInitialized) {
                    clearButton.dataset.buttonInitialized = 'true';
                    clearButton.onclick = function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        clearChatHistory();
                        return false;
                    };
                }
            }
            
            // Khởi tạo buttons khi DOM ready
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', function() {
                    setupHeaderButtons();
                });
            } else {
                setupHeaderButtons();
            }

        // Final check: ensure all functions are always exposed
        // This is a safety net in case something went wrong earlier
        if (typeof window.sendChatbotMessageFull === 'undefined') {
            if (typeof sendChatbotMessageFull === 'function') {
                window.sendChatbotMessageFull = sendChatbotMessageFull;
                window.sendChatbotMessage = sendChatbotMessageFull;
            }
        }
    } // Close if (typeof chatbotInitialized === 'undefined')
    </script>