<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analytics - SmartShop Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        .stat-card {
            border-radius: 10px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .stat-card.revenue {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .stat-card.orders {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }
        .stat-card.views {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
        }
        .stat-card.avg {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: white;
        }
        .stat-value {
            font-size: 2rem;
            font-weight: bold;
            margin: 0.5rem 0;
        }
        .stat-label {
            font-size: 0.9rem;
            opacity: 0.9;
        }
        .ai-comment {
            background: #f8f9fa;
            border-left: 4px solid #667eea;
            padding: 1.5rem;
            border-radius: 5px;
            margin-top: 2rem;
        }
        .chart-container {
            position: relative;
            height: 400px;
            margin-bottom: 2rem;
        }
        .date-filter {
            margin-bottom: 2rem;
        }
    </style>
</head>
<body>
    <c:if test="${empty sessionScope.currentUser || sessionScope.currentUser.roleName != 'Admin'}">
        <c:redirect url="${pageContext.request.contextPath}/login" />
    </c:if>
    
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="bi bi-arrow-left"></i> Quay về Dashboard
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text text-white">
                    <i class="bi bi-graph-up"></i> Analytics Dashboard
                </span>
            </div>
        </div>
    </nav>
    
    <div class="container-fluid mt-4">
        <div class="row">
            <div class="col-12">
                <h2 class="mb-4"><i class="bi bi-graph-up"></i> Analytics Dashboard</h2>
                
                <!-- Period Type Filter -->
                <div class="card p-3 mb-3">
                    <div class="row">
                        <div class="col-md-12 mb-3">
                            <label class="form-label fw-bold">Chế độ phân tích:</label>
                            <div class="btn-group" role="group">
                                <input type="radio" class="btn-check" name="periodType" id="periodDay" value="day" checked>
                                <label class="btn btn-outline-primary" for="periodDay">
                                    <i class="bi bi-calendar-day"></i> Theo ngày
                                </label>
                                
                                <input type="radio" class="btn-check" name="periodType" id="periodMonth" value="month">
                                <label class="btn btn-outline-primary" for="periodMonth">
                                    <i class="bi bi-calendar-month"></i> Theo tháng
                                </label>
                                
                                <input type="radio" class="btn-check" name="periodType" id="periodYear" value="year">
                                <label class="btn btn-outline-primary" for="periodYear">
                                    <i class="bi bi-calendar"></i> Theo năm
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Date Filter -->
                <div class="date-filter card p-3" id="dateFilterSection">
                    <div class="row">
                        <div class="col-md-4">
                            <label for="startDate" class="form-label">Từ ngày:</label>
                            <input type="date" id="startDate" class="form-control">
                        </div>
                        <div class="col-md-4">
                            <label for="endDate" class="form-label">Đến ngày:</label>
                            <input type="date" id="endDate" class="form-control">
                        </div>
                        <div class="col-md-4 d-flex align-items-end">
                            <button type="button" class="btn btn-primary w-100" onclick="loadAnalytics()">
                                <i class="bi bi-search"></i> Lọc dữ liệu
                            </button>
                        </div>
                    </div>
                </div>
                
                <!-- Month Filter -->
                <div class="date-filter card p-3" id="monthFilterSection" style="display: none;">
                    <div class="row">
                        <div class="col-md-6">
                            <label for="filterYear" class="form-label">Năm:</label>
                            <select id="filterYear" class="form-select">
                                <option value="">Tất cả các năm</option>
                            </select>
                        </div>
                        <div class="col-md-6 d-flex align-items-end">
                            <button type="button" class="btn btn-primary w-100" onclick="loadAnalytics()">
                                <i class="bi bi-search"></i> Lọc dữ liệu
                            </button>
                        </div>
                    </div>
                </div>
                
                <!-- Year Filter -->
                <div class="date-filter card p-3" id="yearFilterSection" style="display: none;">
                    <div class="row">
                        <div class="col-md-12">
                            <p class="text-muted mb-0">Hiển thị dữ liệu theo năm (tự động lấy tất cả các năm có dữ liệu)</p>
                            <button type="button" class="btn btn-primary mt-2" onclick="loadAnalytics()">
                                <i class="bi bi-search"></i> Tải dữ liệu
                            </button>
                        </div>
                    </div>
                </div>
                
                <!-- Loading indicator -->
                <div id="loadingIndicator" class="text-center" style="display: none;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <p class="mt-2">Đang tải dữ liệu...</p>
                </div>
                
                <!-- Statistics Cards -->
                <div id="statsContainer" class="row" style="display: none;">
                    <div class="col-md-3">
                        <div class="stat-card revenue">
                            <div class="stat-label">Tổng Doanh Thu</div>
                            <div class="stat-value" id="totalRevenue">0 ₫</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card orders">
                            <div class="stat-label">Tổng Đơn Hàng</div>
                            <div class="stat-value" id="totalOrders">0</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card views">
                            <div class="stat-label">Tổng Lượt Xem</div>
                            <div class="stat-value" id="totalViews">0</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card avg">
                            <div class="stat-label">TB Doanh Thu/Đơn</div>
                            <div class="stat-value" id="avgRevenuePerOrder">0 ₫</div>
                        </div>
                    </div>
                </div>
                
                <!-- Charts -->
                <div id="chartsContainer" style="display: none;">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="card">
                                <div class="card-header">
                                    <h5><i class="bi bi-bar-chart"></i> <span id="revenueChartTitle">Doanh Thu Theo Ngày</span></h5>
                                </div>
                                <div class="card-body">
                                    <div class="chart-container">
                                        <canvas id="revenueChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row mt-4">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header">
                                    <h5><i class="bi bi-cart"></i> <span id="ordersChartTitle">Đơn Hàng Theo Ngày</span></h5>
                                </div>
                                <div class="card-body">
                                    <div class="chart-container">
                                        <canvas id="ordersChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-header">
                                    <h5><i class="bi bi-eye"></i> <span id="viewsChartTitle">Lượt Xem Theo Ngày</span></h5>
                                </div>
                                <div class="card-body">
                                    <div class="chart-container">
                                        <canvas id="viewsChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- AI Comment -->
                <div id="aiCommentContainer" class="ai-comment" style="display: none;">
                    <h5><i class="bi bi-robot"></i> Nhận Xét Tự Động (AI)</h5>
                    <div id="aiComment" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let revenueChart = null;
        let ordersChart = null;
        let viewsChart = null;
        
        // Load analytics khi trang được tải
        window.addEventListener('DOMContentLoaded', function() {
            // Set default date range (30 days ago to today)
            const today = new Date();
            const thirtyDaysAgo = new Date();
            thirtyDaysAgo.setDate(today.getDate() - 30);
            
            document.getElementById('startDate').value = formatDate(thirtyDaysAgo);
            document.getElementById('endDate').value = formatDate(today);
            
            // Populate year dropdown
            populateYearDropdown();
            
            // Setup period type change handler
            setupPeriodTypeHandlers();
            
            loadAnalytics();
        });
        
        function populateYearDropdown() {
            const yearSelect = document.getElementById('filterYear');
            const currentYear = new Date().getFullYear();
            
            // Add years from 2020 to current year + 1
            for (let year = 2020; year <= currentYear + 1; year++) {
                const option = document.createElement('option');
                option.value = year;
                option.textContent = year;
                if (year === currentYear) {
                    option.selected = true;
                }
                yearSelect.appendChild(option);
            }
        }
        
        function setupPeriodTypeHandlers() {
            const periodTypeRadios = document.querySelectorAll('input[name="periodType"]');
            periodTypeRadios.forEach(radio => {
                radio.addEventListener('change', function() {
                    const periodType = this.value;
                    
                    // Hide all filter sections
                    document.getElementById('dateFilterSection').style.display = 'none';
                    document.getElementById('monthFilterSection').style.display = 'none';
                    document.getElementById('yearFilterSection').style.display = 'none';
                    
                    // Show appropriate filter section
                    if (periodType === 'day') {
                        document.getElementById('dateFilterSection').style.display = 'block';
                    } else if (periodType === 'month') {
                        document.getElementById('monthFilterSection').style.display = 'block';
                    } else if (periodType === 'year') {
                        document.getElementById('yearFilterSection').style.display = 'block';
                    }
                    
                    // Update chart titles
                    updateChartTitles(periodType);
                    
                    // Auto reload data when period type changes
                    loadAnalytics();
                });
            });
        }
        
        function updateChartTitles(periodType) {
            let revenueTitle = 'Doanh Thu Theo Ngày';
            let ordersTitle = 'Đơn Hàng Theo Ngày';
            let viewsTitle = 'Lượt Xem Theo Ngày';
            
            if (periodType === 'month') {
                revenueTitle = 'Doanh Thu Theo Tháng';
                ordersTitle = 'Đơn Hàng Theo Tháng';
                viewsTitle = 'Lượt Xem Theo Tháng';
            } else if (periodType === 'year') {
                revenueTitle = 'Doanh Thu Hằng Năm';
                ordersTitle = 'Đơn Hàng Hằng Năm';
                viewsTitle = 'Lượt Xem Hằng Năm';
            }
            
            document.getElementById('revenueChartTitle').textContent = revenueTitle;
            document.getElementById('ordersChartTitle').textContent = ordersTitle;
            document.getElementById('viewsChartTitle').textContent = viewsTitle;
        }
        
        function formatDate(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        }
        
        function formatPrice(amount) {
            return new Intl.NumberFormat('vi-VN').format(amount) + ' ₫';
        }
        
        function loadAnalytics() {
            // Get period type
            const periodType = document.querySelector('input[name="periodType"]:checked').value;
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            const filterYear = document.getElementById('filterYear').value;
            
            // Show loading
            document.getElementById('loadingIndicator').style.display = 'block';
            document.getElementById('statsContainer').style.display = 'none';
            document.getElementById('chartsContainer').style.display = 'none';
            document.getElementById('aiCommentContainer').style.display = 'none';
            
            // Build URL
            let url = '${pageContext.request.contextPath}/api/analytics';
            const params = new URLSearchParams();
            params.append('periodType', periodType);
            
            if (periodType === 'day') {
                // Chỉ gửi startDate/endDate nếu có giá trị và không rỗng
                if (startDate && startDate.trim() !== '') {
                    params.append('startDate', startDate.trim());
                }
                if (endDate && endDate.trim() !== '') {
                    params.append('endDate', endDate.trim());
                }
            } else if (periodType === 'month') {
                // Chỉ gửi year nếu có giá trị
                if (filterYear && filterYear.trim() !== '') {
                    params.append('year', filterYear.trim());
                }
            }
            // year period type doesn't need parameters
            
            if (params.toString()) {
                url += '?' + params.toString();
            }
            
            // Fetch data
            console.log('Loading analytics with URL:', url);
            console.log('Period Type:', periodType);
            
            fetch(url)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('loadingIndicator').style.display = 'none';
                    
                    if (data.success) {
                        console.log('Analytics data received:', data);
                        console.log('Revenue data sample:', data.revenueByDate && data.revenueByDate.length > 0 ? data.revenueByDate[0] : 'empty');
                        
                        // Get current period type and update chart titles
                        const periodType = document.querySelector('input[name="periodType"]:checked').value;
                        updateChartTitles(periodType);
                        
                        // Update statistics
                        document.getElementById('totalRevenue').textContent = formatPrice(parseFloat(data.totalRevenue));
                        document.getElementById('totalOrders').textContent = data.totalOrders;
                        document.getElementById('totalViews').textContent = data.totalViews;
                        document.getElementById('avgRevenuePerOrder').textContent = formatPrice(parseFloat(data.avgRevenuePerOrder));
                        
                        // Show containers
                        document.getElementById('statsContainer').style.display = 'flex';
                        document.getElementById('chartsContainer').style.display = 'block';
                        document.getElementById('aiCommentContainer').style.display = 'block';
                        
                        // Update AI comment
                        document.getElementById('aiComment').innerHTML = data.comment.replace(/\n/g, '<br>');
                        
                        // Update charts
                        updateCharts(data);
                    } else {
                        alert('Lỗi: ' + (data.error || 'Không thể tải dữ liệu'));
                    }
                })
                .catch(error => {
                    document.getElementById('loadingIndicator').style.display = 'none';
                    console.error('Error:', error);
                    alert('Đã xảy ra lỗi khi tải dữ liệu: ' + error.message);
                });
        }
        
        function updateCharts(data) {
            // Determine label key based on period type
            const periodType = document.querySelector('input[name="periodType"]:checked').value;
            let revenueLabelKey = 'date';
            let ordersLabelKey = 'date';
            let viewsLabelKey = 'date';
            
            if (periodType === 'month') {
                revenueLabelKey = 'month';
                ordersLabelKey = 'month';
                viewsLabelKey = 'month';
            } else if (periodType === 'year') {
                revenueLabelKey = 'year';
                ordersLabelKey = 'year';
                viewsLabelKey = 'year';
            }
            
            console.log('Updating charts with periodType:', periodType);
            console.log('Label keys:', {revenueLabelKey, ordersLabelKey, viewsLabelKey});
            console.log('Revenue data keys:', data.revenueByDate && data.revenueByDate.length > 0 ? Object.keys(data.revenueByDate[0]) : 'empty');
            
            // Revenue Chart
            const revenueCtx = document.getElementById('revenueChart').getContext('2d');
            if (revenueChart) {
                revenueChart.destroy();
            }
            revenueChart = new Chart(revenueCtx, {
                type: 'line',
                data: {
                    labels: data.revenueByDate.map(item => item[revenueLabelKey] || item.date || item.month || item.year),
                    datasets: [{
                        label: 'Doanh Thu (₫)',
                        data: data.revenueByDate.map(item => parseFloat(item.revenue)),
                        borderColor: 'rgb(102, 126, 234)',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        tension: 0.4,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: true
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return 'Doanh Thu: ' + formatPrice(context.parsed.y);
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return formatPrice(value);
                                }
                            }
                        }
                    }
                }
            });
            
            // Orders Chart
            const ordersCtx = document.getElementById('ordersChart').getContext('2d');
            if (ordersChart) {
                ordersChart.destroy();
            }
            ordersChart = new Chart(ordersCtx, {
                type: 'bar',
                data: {
                    labels: data.ordersByDate.map(item => item[ordersLabelKey] || item.date || item.month || item.year),
                    datasets: [{
                        label: 'Số Đơn Hàng',
                        data: data.ordersByDate.map(item => item.orders),
                        backgroundColor: 'rgba(245, 87, 108, 0.8)',
                        borderColor: 'rgb(245, 87, 108)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: true
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                stepSize: 1
                            }
                        }
                    }
                }
            });
            
            // Views Chart
            const viewsCtx = document.getElementById('viewsChart').getContext('2d');
            if (viewsChart) {
                viewsChart.destroy();
            }
            viewsChart = new Chart(viewsCtx, {
                type: 'bar',
                data: {
                    labels: data.viewsByDate.map(item => item[viewsLabelKey] || item.date || item.month || item.year),
                    datasets: [{
                        label: 'Lượt Xem',
                        data: data.viewsByDate.map(item => item.views),
                        backgroundColor: 'rgba(79, 172, 254, 0.8)',
                        borderColor: 'rgb(79, 172, 254)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: true
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                stepSize: 1
                            }
                        }
                    }
                }
            });
        }
    </script>
</body>
</html>

