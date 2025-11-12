/**
 * Compare List Management
 * Hỗ trợ cả localStorage (chưa đăng nhập) và database (đã đăng nhập)
 */

// Note: addToCompare function should be defined in page head (home.jsp, productList.jsp)
// This file will override it with full implementation if user is logged in

(function() {
    'use strict';
    
    const STORAGE_KEY_COMPARE = 'compare_products';
    const MAX_COMPARE_ITEMS = 2; // Giới hạn số sản phẩm so sánh (chỉ so sánh 2 sản phẩm)
    
    /**
     * Lấy danh sách so sánh từ localStorage
     */
    function getCompareListFromStorage() {
        try {
            const stored = localStorage.getItem(STORAGE_KEY_COMPARE);
            if (stored) {
                return JSON.parse(stored);
            }
        } catch (e) {
            console.error('Error reading compare list from localStorage:', e);
        }
        return [];
    }
    
    /**
     * Lưu danh sách so sánh vào localStorage
     */
    function saveCompareListToStorage(productIDs) {
        try {
            localStorage.setItem(STORAGE_KEY_COMPARE, JSON.stringify(productIDs));
            return true;
        } catch (e) {
            console.error('Error saving compare list to localStorage:', e);
            return false;
        }
    }
    
    /**
     * Kiểm tra user có đăng nhập không
     */
    function isUserLoggedIn() {
        // Kiểm tra qua session hoặc cookie
        // Có thể cải thiện bằng cách gọi API
        return document.cookie.includes('JSESSIONID') || 
               (typeof window.currentUser !== 'undefined' && window.currentUser !== null);
    }
    
    /**
     * Thêm sản phẩm vào danh sách so sánh (implementation)
     * Function này sẽ được gán vào window.addToCompare sau khi IIFE chạy
     */
    function addToCompareImpl(productID) {
        if (!productID) {
            console.error('ProductID is required');
            alert('Lỗi: Không có ID sản phẩm');
            return Promise.reject('ProductID is required');
        }
        
        const contextPath = window.contextPath || '';
        productID = parseInt(productID);
        
        // Kiểm tra số lượng tối đa - check cả localStorage và API
        const localProductIDs = getCompareListFromStorage();
        
        // Kiểm tra số lượng từ localStorage trước (nhanh hơn)
        if (localProductIDs.length >= MAX_COMPARE_ITEMS) {
            alert('Bạn chỉ có thể so sánh tối đa ' + MAX_COMPARE_ITEMS + ' sản phẩm. Vui lòng xóa một sản phẩm trước khi thêm mới.');
            return Promise.reject('Maximum compare items reached');
        }
        
        // Kiểm tra sản phẩm đã có trong localStorage chưa
        if (localProductIDs.includes(productID)) {
            alert('Sản phẩm này đã có trong danh sách so sánh');
            return Promise.reject('Product already in compare list');
        }
        
        // Nếu user chưa đăng nhập, chỉ lưu vào localStorage
        if (!isUserLoggedIn()) {
            localProductIDs.push(productID);
            saveCompareListToStorage(localProductIDs);
            
            // Cập nhật UI
            updateCompareButton(productID, true);
            updateCompareCount();
            
            alert('Đã thêm sản phẩm vào danh sách so sánh');
            
            // Nếu đã có đủ 2 sản phẩm, tự động mở modal so sánh
            if (localProductIDs.length >= MAX_COMPARE_ITEMS) {
                // Đợi một chút để đảm bảo UI đã cập nhật
                setTimeout(function() {
                    if (typeof window.showCompareModal === 'function') {
                        window.showCompareModal();
                    }
                }, 300);
            }
            
            return Promise.resolve({ success: true, useLocalStorage: true });
        }
        
        // User đã đăng nhập - gọi API và kiểm tra lại từ server
        return getCompareList().then(function(products) {
            // Kiểm tra lại từ server
            if (products.length >= MAX_COMPARE_ITEMS) {
                alert('Bạn chỉ có thể so sánh tối đa ' + MAX_COMPARE_ITEMS + ' sản phẩm. Vui lòng xóa một sản phẩm trước khi thêm mới.');
                return Promise.reject('Maximum compare items reached');
            }
            
            // Kiểm tra sản phẩm đã có trong list chưa
            if (products.some(p => p.productID === productID)) {
                alert('Sản phẩm này đã có trong danh sách so sánh');
                return Promise.reject('Product already in compare list');
            }
            
            // Gọi API
            return fetch(contextPath + '/api/compare/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ productID: productID })
            })
            .then(response => response.json())
            .then(function(data) {
                if (data.success) {
                    // Cập nhật UI
                    updateCompareButton(productID, true);
                    updateCompareCount();
                    
                    // Hiển thị thông báo
                    if (typeof showNotification === 'function') {
                        showNotification(data.message || 'Đã thêm sản phẩm vào danh sách so sánh', 'success');
                    } else {
                        alert(data.message || 'Đã thêm sản phẩm vào danh sách so sánh');
                    }
                    
                    // Kiểm tra nếu đã có đủ 2 sản phẩm, tự động mở modal so sánh
                    return getCompareList().then(function(products) {
                        if (products.length >= MAX_COMPARE_ITEMS) {
                            // Đợi một chút để đảm bảo UI đã cập nhật
                            setTimeout(function() {
                                if (typeof window.showCompareModal === 'function') {
                                    window.showCompareModal();
                                }
                            }, 300);
                        }
                        return data;
                    }).catch(function() {
                        return data; // Trả về data ngay cả khi getCompareList lỗi
                    });
                } else {
                    throw new Error(data.error || 'Failed to add product to compare list');
                }
            });
        }).catch(function(error) {
            console.error('Error adding to compare:', error);
            alert('Có lỗi xảy ra: ' + (error.message || error));
            return Promise.reject(error);
        });
    }
    
    /**
     * Xóa sản phẩm khỏi danh sách so sánh
     */
    window.removeFromCompare = function(productID) {
        if (!productID) {
            console.error('ProductID is required');
            return Promise.reject('ProductID is required');
        }
        
        const contextPath = window.contextPath || '';
        
        // Gọi API
        return fetch(contextPath + '/api/compare/remove', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ productID: productID })
        })
        .then(response => response.json())
        .then(function(data) {
            if (data.success) {
                if (data.useLocalStorage || !isUserLoggedIn()) {
                    // Xóa khỏi localStorage
                    const currentList = getCompareListFromStorage();
                    const index = currentList.indexOf(productID);
                    if (index > -1) {
                        currentList.splice(index, 1);
                        saveCompareListToStorage(currentList);
                    }
                }
                
                // Cập nhật UI
                updateCompareButton(productID, false);
                updateCompareCount();
                
                // Nếu đang ở trang compare, reload
                if (window.location.pathname.includes('/compare')) {
                    window.location.reload();
                }
                
                return data;
            } else {
                throw new Error(data.error || 'Failed to remove product from compare list');
            }
        });
    };
    
    /**
     * Lấy danh sách sản phẩm so sánh
     */
    window.getCompareList = function() {
        const contextPath = window.contextPath || '';
        
        return fetch(contextPath + '/api/compare/get')
            .then(response => response.json())
            .then(function(data) {
                if (data.success) {
                    // Nếu user chưa đăng nhập, lấy từ localStorage
                    if (data.count === 0 && !isUserLoggedIn()) {
                        const productIDs = getCompareListFromStorage();
                        if (productIDs.length > 0) {
                            // Fetch product details từ server
                            return Promise.all(productIDs.map(function(productID) {
                                return fetch(contextPath + '/product/api?id=' + productID)
                                    .then(res => res.json())
                                    .then(function(productData) {
                                        if (productData.success && productData.product) {
                                            return {
                                                productID: productData.product.productID,
                                                productName: productData.product.productName,
                                                price: productData.product.price || 0,
                                                imageUrl: productData.product.imageUrl || '',
                                                stock: productData.product.stock || 0,
                                                stockStatus: productData.product.stockStatus || ''
                                            };
                                        }
                                        return null;
                                    })
                                    .catch(function() {
                                        return null;
                                    });
                            }))
                            .then(function(products) {
                                return products.filter(p => p !== null);
                            });
                        }
                    }
                    return data.products || [];
                } else {
                    throw new Error(data.error || 'Failed to get compare list');
                }
            })
            .catch(function(error) {
                console.error('Error getting compare list:', error);
                // Fallback to localStorage
                if (!isUserLoggedIn()) {
                    const productIDs = getCompareListFromStorage();
                    return Promise.resolve(productIDs.map(function(id) {
                        return { productID: id };
                    }));
                }
                return Promise.resolve([]);
            });
    };
    
    /**
     * Kiểm tra sản phẩm có trong danh sách so sánh không
     */
    window.isInCompareList = function(productID) {
        if (!productID) return Promise.resolve(false);
        
        const contextPath = window.contextPath || '';
        
        return fetch(contextPath + '/api/compare/check?productID=' + productID)
            .then(response => response.json())
            .then(function(data) {
                if (data.success) {
                    if (data.inList) {
                        return true;
                    } else if (!isUserLoggedIn()) {
                        // Kiểm tra localStorage
                        const productIDs = getCompareListFromStorage();
                        return productIDs.includes(productID);
                    }
                    return false;
                }
                return false;
            })
            .catch(function(error) {
                console.error('Error checking compare list:', error);
                // Fallback to localStorage
                if (!isUserLoggedIn()) {
                    const productIDs = getCompareListFromStorage();
                    return productIDs.includes(productID);
                }
                return false;
            });
    };
    
    /**
     * Đồng bộ localStorage với database khi user đăng nhập
     */
    window.syncCompareList = function() {
        if (!isUserLoggedIn()) {
            return Promise.resolve();
        }
        
        const productIDs = getCompareListFromStorage();
        if (productIDs.length === 0) {
            return Promise.resolve();
        }
        
        const contextPath = window.contextPath || '';
        
        return fetch(contextPath + '/api/compare/sync', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ productIDs: productIDs })
        })
        .then(response => response.json())
        .then(function(data) {
            if (data.success) {
                // Xóa localStorage sau khi sync thành công
                localStorage.removeItem(STORAGE_KEY_COMPARE);
                updateCompareCount();
                return data;
            } else {
                throw new Error(data.error || 'Failed to sync compare list');
            }
        })
        .catch(function(error) {
            console.error('Error syncing compare list:', error);
        });
    };
    
    /**
     * Cập nhật nút so sánh trên UI
     */
    function updateCompareButton(productID, inList) {
        // Tìm tất cả các nút compare cho productID này
        const buttons = document.querySelectorAll('[data-compare-product-id="' + productID + '"]');
        buttons.forEach(function(btn) {
            if (inList) {
                btn.classList.add('active');
                // Giữ nguyên icon nếu là action-icon, chỉ thêm class active
                if (btn.classList.contains('action-icon')) {
                    btn.style.color = '#28a745';
                    btn.style.borderColor = '#28a745';
                    btn.title = 'Đã thêm vào so sánh';
                } else {
                    btn.innerHTML = '<i class="bi bi-check-circle"></i> Đã thêm';
                }
            } else {
                btn.classList.remove('active');
                // Reset style nếu là action-icon
                if (btn.classList.contains('action-icon')) {
                    btn.style.color = '';
                    btn.style.borderColor = '';
                    btn.title = 'So sánh sản phẩm';
                } else {
                    btn.innerHTML = '<i class="bi bi-arrow-left-right"></i> So sánh';
                }
            }
        });
    }
    
    /**
     * Cập nhật số lượng sản phẩm so sánh trên header
     */
    function updateCompareCount() {
        getCompareList().then(function(products) {
            const count = products.length;
            const countElements = document.querySelectorAll('.compare-count, [data-compare-count]');
            countElements.forEach(function(el) {
                el.textContent = count;
                if (count > 0) {
                    el.style.display = 'inline-block';
                } else {
                    el.style.display = 'none';
                }
            });
        });
    }
    
    /**
     * Khởi tạo: cập nhật UI khi page load
     */
    function init() {
        // Cập nhật số lượng
        updateCompareCount();
        
        // Cập nhật trạng thái các nút compare
        document.querySelectorAll('[data-compare-product-id]').forEach(function(btn) {
            const productID = parseInt(btn.getAttribute('data-compare-product-id'));
            if (productID) {
                isInCompareList(productID).then(function(inList) {
                    updateCompareButton(productID, inList);
                });
            }
        });
        
        // Đồng bộ khi user đăng nhập
        if (isUserLoggedIn()) {
            syncCompareList();
        }
    }
    
    // Khởi tạo khi DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
    
    // Override window.addToCompare với implementation đầy đủ (có sync với server nếu đã đăng nhập)
    // Lưu implementation từ head (nếu có)
    const originalAddToCompare = window.addToCompare || function(productID) {
        // Fallback nếu không có implementation từ head
        alert('Chức năng so sánh đang tải, vui lòng thử lại sau vài giây.');
        return Promise.reject('Function not ready');
    };
    
    window.addToCompare = function(productID) {
        // Nếu user đã đăng nhập, dùng implementation đầy đủ (có gọi API)
        if (isUserLoggedIn()) {
            return addToCompareImpl(productID);
        } else {
            // Nếu chưa đăng nhập, dùng implementation từ head (đã lưu vào localStorage)
            const result = originalAddToCompare(productID);
            // Update count after adding
            if (result && typeof result.then === 'function') {
                result.then(function() {
                    updateCompareCount();
                }).catch(function() {
                    // Ignore errors
                });
            } else {
                updateCompareCount();
            }
            return result;
        }
    };
    
    /**
     * Hiển thị modal so sánh sản phẩm
     */
    window.showCompareModal = function() {
        const contextPath = window.contextPath || '';
        
        // Lấy danh sách sản phẩm so sánh
        getCompareList().then(function(products) {
            if (products.length < 2) {
                alert('Bạn cần chọn ít nhất 2 sản phẩm để so sánh');
                return;
            }
            
            // Lấy 2 sản phẩm đầu tiên
            const product1 = products[0];
            const product2 = products[1];
            
            // Load chi tiết sản phẩm từ API
            Promise.all([
                fetch(contextPath + '/product/api?id=' + product1.productID).then(r => r.json()),
                fetch(contextPath + '/product/api?id=' + product2.productID).then(r => r.json())
            ]).then(function([data1, data2]) {
                // Populate modal với dữ liệu sản phẩm
                populateCompareModal(data1, data2);
                
                // Hiển thị modal
                const modalElement = document.getElementById('compareModal');
                if (modalElement && typeof bootstrap !== 'undefined') {
                    const modal = new bootstrap.Modal(modalElement);
                    modal.show();
                }
            }).catch(function(error) {
                console.error('Error loading product details:', error);
                alert('Không thể tải thông tin sản phẩm để so sánh');
            });
        });
    };
    
    /**
     * Populate modal so sánh với dữ liệu 2 sản phẩm
     */
    function populateCompareModal(product1, product2) {
        const formatCurrency = function(price) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(price);
        };
        
        // Product 1
        const price1 = parseFloat(product1.discountedPrice || product1.price) || 0;
        const originalPrice1 = parseFloat(product1.originalPrice) || 0;
        
        document.getElementById('compareProduct1Image').src = product1.imageUrl || '/img/default-product.png';
        document.getElementById('compareProduct1Name').textContent = product1.productName;
        document.getElementById('compareProduct1Price').textContent = formatCurrency(price1);
        if (originalPrice1 > price1 + 0.01) {
            document.getElementById('compareProduct1OldPrice').textContent = formatCurrency(originalPrice1);
            document.getElementById('compareProduct1OldPrice').style.display = 'block';
        } else {
            document.getElementById('compareProduct1OldPrice').style.display = 'none';
        }
        document.getElementById('compareProduct1Stock').textContent = product1.stock || 0;
        document.getElementById('compareProduct1Category').textContent = product1.categoryName || 'Chưa phân loại';
        document.getElementById('compareProduct1Description').textContent = product1.description || 'Không có mô tả';
        
        // Product 2
        const price2 = parseFloat(product2.discountedPrice || product2.price) || 0;
        const originalPrice2 = parseFloat(product2.originalPrice) || 0;
        
        document.getElementById('compareProduct2Image').src = product2.imageUrl || '/img/default-product.png';
        document.getElementById('compareProduct2Name').textContent = product2.productName;
        document.getElementById('compareProduct2Price').textContent = formatCurrency(price2);
        if (originalPrice2 > price2 + 0.01) {
            document.getElementById('compareProduct2OldPrice').textContent = formatCurrency(originalPrice2);
            document.getElementById('compareProduct2OldPrice').style.display = 'block';
        } else {
            document.getElementById('compareProduct2OldPrice').style.display = 'none';
        }
        document.getElementById('compareProduct2Stock').textContent = product2.stock || 0;
        document.getElementById('compareProduct2Category').textContent = product2.categoryName || 'Chưa phân loại';
        document.getElementById('compareProduct2Description').textContent = product2.description || 'Không có mô tả';
        
        // Set onclick handlers
        document.getElementById('compareProduct1ViewBtn').onclick = function() {
            if (typeof window.openProductModal === 'function') {
                window.openProductModal(product1.productID);
            }
        };
        document.getElementById('compareProduct2ViewBtn').onclick = function() {
            if (typeof window.openProductModal === 'function') {
                window.openProductModal(product2.productID);
            }
        };
    }
    
    // Export functions
    window.compareListManager = {
        add: addToCompareImpl,
        remove: window.removeFromCompare,
        get: window.getCompareList,
        check: window.isInCompareList,
        sync: window.syncCompareList,
        updateCount: updateCompareCount,
        showModal: window.showCompareModal
    };
    
})();

