<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <!-- AI Chatbot Widget - Chỉ hiển thị cho Customer hoặc chưa đăng nhập -->
    <c:choose>
        <c:when test="${empty sessionScope.currentUser || sessionScope.currentUser.roleName == 'Customer'}">
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
                <button type="button" class="btn-close btn-close-white"></button>
            </div>
            <div id="chatbotMessages" class="chatbot-messages">
                <div class="chatbot-message bot-message">
                    <div class="message-content">
                        <i class="bi bi-person-badge"></i>
                        <span>Xin chào! Tôi là nhân viên SmartShop. Bạn cần hỗ trợ gì ạ?</span>
                    </div>
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
            
            // Save messages vào localStorage
            function saveMessagesToStorage() {
                try {
                    const messagesContainer = document.getElementById('chatbotMessages');
                    if (!messagesContainer) return;
                    
                    const messageElements = messagesContainer.querySelectorAll('.chatbot-message');
                    const messages = [];
                    
                    messageElements.forEach(function(msgEl) {
                        const contentSpan = msgEl.querySelector('.message-content span');
                        if (contentSpan) {
                            const text = contentSpan.textContent || contentSpan.innerText;
                            const type = msgEl.classList.contains('user-message') ? 'user' : 'bot';
                            // Bỏ qua loading messages
                            if (!msgEl.id || !msgEl.id.startsWith('loading-message-')) {
                                messages.push({ text: text, type: type });
                            }
                        }
                    });
                    
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
                            
                            // Restore messages (skip save để tránh loop)
                            messages.forEach(function(msg) {
                                addMessage(msg.text, msg.type, true);
                            });
                            // Save một lần sau khi load xong
                            setTimeout(saveMessagesToStorage, 100);
                        }
                    }
                } catch (e) {
                    console.error('Error loading messages from localStorage:', e);
                }
            }
            
            // Helper function: addMessage
            function addMessage(text, type, skipSave) {
                const messagesContainer = document.getElementById('chatbotMessages');
                if (!messagesContainer) return;
                
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
                    
                    if (data.success) {
                        let botMessage = data.message.trim().replace(/\s+/g, ' ');
                        
                        // Xóa tag [SEARCH_PRODUCTS] nếu có (tạm thời không hiển thị product cards)
                        botMessage = botMessage.replace(/\[SEARCH_PRODUCTS:\s*.+?\]/gi, '').trim();
                        
                        if (botMessage && botMessage.length > 0) {
                            addMessage(botMessage, 'bot');
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
                });
            } else {
                setTimeout(loadMessagesFromStorage, 100);
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
            z-index: 1051;
            /* Cao hơn header (1050) nhưng thấp hơn modal (1055) để không chặn modal */
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
            z-index: 1053 !important;
            /* Cao hơn widget container và header để đảm bảo button luôn clickable */
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
            width: 380px;
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
            z-index: 1052;
            /* Cao hơn header (1050) nhưng vẫn thấp hơn modal (1055) để không chặn modal */
            visibility: hidden;
            opacity: 0;
            transform: translateY(20px);
            transition: opacity 0.3s ease, transform 0.3s ease, visibility 0.3s ease;
        }
        
        /* Đảm bảo chatbot không bị header che khi màn hình nhỏ */
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

            // Setup close button handler - CHỈ 1 LẦN khi DOM ready
            function setupCloseButton() {
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
            }
            
            // Khởi tạo close button khi DOM ready
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', function() {
                    setupCloseButton();
                });
            } else {
                setupCloseButton();
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
    </script>