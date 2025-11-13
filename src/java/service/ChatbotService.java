package service;

import model.Product;
import model.Promotion;
import model.Category;
import model.Order;
import model.OrderItem;
import model.CartItemDB;
import productdao.IProductDAO;
import productdao.ProductDAO;
import productservice.IProductService;
import productservice.ProductService;
import promotionservice.IPromotionService;
import promotionservice.PromotionService;
import categoryservice.ICategoryService;
import categoryservice.CategoryService;
import orderservice.IOrderService;
import orderservice.OrderService;
import orderitemservice.IOrderItemService;
import orderitemservice.OrderItemService;
import cartservice.ICartService;
import cartservice.CartService;
import model.User;
import userservice.IUserService;
import userservice.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service chính để xử lý chatbot
 * Kết hợp NLU + DAO + Answer Service
 */
public class ChatbotService {
    
    private NluService nluService;
    private AnswerService answerService;
    private IProductDAO productDAO;
    private IProductService productService;
    private IPromotionService promotionService;
    private ICategoryService categoryService;
    private IOrderService orderService;
    private IOrderItemService orderItemService;
    private ICartService cartService;
    private IUserService userService;
    
    public ChatbotService() {
        this.nluService = new NluService();
        this.answerService = new AnswerService();
        this.productDAO = new ProductDAO();
        this.productService = new ProductService();
        this.promotionService = new PromotionService();
        this.categoryService = new CategoryService();
        this.orderService = new OrderService();
        this.orderItemService = new OrderItemService();
        this.cartService = new CartService();
        this.userService = new UserService();
    }
    
    /**
     * Xử lý câu hỏi của khách hàng (theo hướng dẫn)
     * @param userId ID của user (có thể null)
     * @param msg Câu hỏi
     * @return ChatResponse chứa câu trả lời và danh sách sản phẩm
     */
    public ChatResponse handle(Integer userId, String msg) {
        User user = null;
        if (userId != null && userId > 0) {
            try {
                // Lấy user từ service
                user = userService.getUserById(userId);
                if (user != null) {
                    System.out.println("ChatbotService: User found - ID: " + user.getUserID() + ", Name: " + user.getFullName());
                } else {
                    System.out.println("ChatbotService: User not found for ID: " + userId);
                }
            } catch (Exception e) {
                System.err.println("Error getting user: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        NluResult nlu = nluService.analyze(msg);
        
        List<Product> list;
        
        String intent = nlu.getIntent();
        if (intent == null) {
            intent = NluResult.INTENT_SMALL_TALK;
        }
        
        switch (intent) {
            case NluResult.INTENT_FIND_PRODUCT:
            case NluResult.INTENT_PRODUCT_SEARCH:
                list = productDAO.searchAdvanced(
                    mapCategory(nlu.getCategory()),
                    nlu.getPriceMin(),
                    nlu.getPriceMax(),
                    nlu.getBrand(),
                    nlu.getFeatures()
                );
                
                if (list == null || list.isEmpty()) {
                    list = productDAO.getTrendingProducts(6);
                }
                break;
                
            case NluResult.INTENT_ASK_PROMOTION:
            case NluResult.INTENT_PROMOTION:
                // TODO: gọi PromotionDAO để lấy thông tin khuyến mãi hiện hành
                list = new ArrayList<>();
                break;
                
            case NluResult.INTENT_ORDER_STATUS:
                // TODO: hỏi thêm mã đơn / số điện thoại
                list = new ArrayList<>();
                break;
                
            default:
                list = productDAO.getTrendingProducts(6);
                break;
        }
        
        // Lấy thông tin đơn hàng và giỏ hàng của user để truyền vào prompt
        return answerService.answer(user, msg, list, userId);
    }
    
    /**
     * Map category string sang categoryID
     */
    private Integer mapCategory(String c) {
        if (c == null || c.isEmpty() || c.equals("null")) {
            return null;
        }
        
        c = c.toLowerCase();
        switch (c) {
            case "phone":
            case "điện thoại":
            case "smartphone":
                return 1; // Giả sử categoryID = 1 là điện thoại
            case "laptop":
            case "máy tính":
            case "máy tính xách tay":
                return 2; // Giả sử categoryID = 2 là laptop
            case "tablet":
            case "máy tính bảng":
                return 3; // Giả sử categoryID = 3 là tablet
            default:
                // Tìm categoryID từ database
                try {
                    List<Category> categories = categoryService.getAllCategories();
                    for (Category cat : categories) {
                        if (cat.getCategoryName() != null && 
                            cat.getCategoryName().toLowerCase().contains(c)) {
                            return cat.getCategoryID();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error mapping category: " + e.getMessage());
                }
                return null;
        }
    }
    
    /**
     * Xử lý câu hỏi của khách hàng (backward compatibility)
     * @param message Câu hỏi
     * @param currentUser User hiện tại (có thể null)
     * @return ChatbotResponse chứa câu trả lời và dữ liệu
     */
    public ChatbotResponse processMessage(String message, User currentUser) {
        ChatbotResponse response = new ChatbotResponse();
        
        try {
            // Bước 1: Phân tích NLU
            NluResult nluResult = nluService.analyze(message);
            response.setNluResult(nluResult);
            
            // Bước 2: Query database dựa trên intent
            List<Map<String, Object>> data = queryDatabase(nluResult, currentUser);
            response.setData(data);
            
            // Bước 3: Tạo câu trả lời
            String answer = answerService.generateAnswer(message, data);
            response.setAnswer(answer);
            
            // Bước 4: Xác định action (cho Agentic AI)
            String action = determineAction(nluResult, data);
            response.setAction(action);
            
        } catch (Exception e) {
            System.err.println("Error in ChatbotService.processMessage: " + e.getMessage());
            e.printStackTrace();
            response.setAnswer("Xin lỗi, đã xảy ra lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại sau.");
        }
        
        return response;
    }
    
    /**
     * Query database dựa trên intent và entities
     */
    private List<Map<String, Object>> queryDatabase(NluResult nluResult, User currentUser) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String intent = nluResult.getIntent();
        
        try {
            switch (intent) {
                case NluResult.INTENT_PRODUCT_SEARCH:
                    results = searchProducts(nluResult);
                    break;
                    
                case NluResult.INTENT_PRODUCT_DETAIL:
                    results = getProductDetail(nluResult);
                    break;
                    
                case NluResult.INTENT_PROMOTION:
                    results = getPromotions();
                    break;
                    
                case NluResult.INTENT_ORDER_STATUS:
                    if (currentUser != null) {
                        results = getOrderStatus(currentUser);
                    }
                    break;
                    
                case NluResult.INTENT_CART:
                    if (currentUser != null) {
                        results = getCartItems(currentUser);
                    }
                    break;
                    
                case NluResult.INTENT_CATEGORY:
                    results = getCategories();
                    break;
                    
                default:
                    // Không cần query database
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error querying database: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Tìm kiếm sản phẩm
     */
    private List<Map<String, Object>> searchProducts(NluResult nluResult) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            String keyword = nluResult.getKeyword();
            String category = nluResult.getCategory();
            Map<String, Double> priceRange = nluResult.getPriceRange();
            
            List<Product> products;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Tìm kiếm theo keyword
                products = productDAO.searchForChatbot(keyword, false);
                // Giới hạn 10 sản phẩm
                if (products != null && products.size() > 10) {
                    products = products.subList(0, 10);
                }
            } else if (category != null && !category.trim().isEmpty()) {
                // Tìm kiếm theo category - cần tìm categoryID từ categoryName
                try {
                    List<Category> categories = categoryService.getAllCategories();
                    int categoryID = -1;
                    for (Category cat : categories) {
                        if (cat.getCategoryName() != null && 
                            cat.getCategoryName().toLowerCase().contains(category.toLowerCase())) {
                            categoryID = cat.getCategoryID();
                            break;
                        }
                    }
                    if (categoryID > 0) {
                        products = productDAO.getByCategory(categoryID, false);
                        // Giới hạn 10 sản phẩm
                        if (products != null && products.size() > 10) {
                            products = products.subList(0, 10);
                        }
                    } else {
                        products = new ArrayList<>();
                    }
                } catch (Exception e) {
                    System.err.println("Error getting products by category: " + e.getMessage());
                    products = new ArrayList<>();
                }
            } else {
                // Lấy sản phẩm mới nhất - dùng getPagedProducts
                products = productDAO.getPagedProducts(1, 10, "ProductID", "DESC", null, 0, false);
            }
            
            // Filter theo price range nếu có
            if (priceRange != null && products != null) {
                Double minPrice = priceRange.get("min");
                Double maxPrice = priceRange.get("max");
                
                if (minPrice != null || maxPrice != null) {
                    List<Product> filteredProducts = new ArrayList<>();
                    for (Product product : products) {
                        double price = product.getPrice() != null ? product.getPrice().doubleValue() : 0;
                        if ((minPrice == null || price >= minPrice) && 
                            (maxPrice == null || price <= maxPrice)) {
                            filteredProducts.add(product);
                        }
                    }
                    products = filteredProducts;
                }
            }
            
            // Convert to Map
            if (products != null) {
                for (Product product : products) {
                    Map<String, Object> productMap = productToMap(product);
                    results.add(productMap);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error searching products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Lấy chi tiết sản phẩm
     */
    private List<Map<String, Object>> getProductDetail(NluResult nluResult) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            Object productIDObj = nluResult.getEntity("productID");
            if (productIDObj != null) {
                int productID = Integer.parseInt(productIDObj.toString());
                Product product = productService.getProductById(productID);
                if (product != null) {
                    results.add(productToMap(product));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting product detail: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Lấy danh sách khuyến mãi
     */
    private List<Map<String, Object>> getPromotions() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            List<Promotion> promotions = promotionService.getActivePromotions();
            if (promotions != null) {
                for (Promotion promotion : promotions) {
                    Map<String, Object> promoMap = new HashMap<>();
                    promoMap.put("promotionID", promotion.getPromotionID());
                    promoMap.put("promotionName", promotion.getTitle() != null ? promotion.getTitle() : "");
                    promoMap.put("description", promotion.getDescription());
                    promoMap.put("discountPercent", promotion.getDiscountPercent());
                    promoMap.put("discountAmount", promotion.getDiscountAmount());
                    results.add(promoMap);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting promotions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Lấy trạng thái đơn hàng
     */
    private List<Map<String, Object>> getOrderStatus(User currentUser) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            List<Order> orders = orderService.getOrdersByUser(currentUser.getUserID());
            if (orders != null) {
                for (Order order : orders) {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("orderID", order.getOrderID());
                    orderMap.put("orderDate", order.getOrderDate());
                    orderMap.put("status", order.getOrderStatus() != null ? order.getOrderStatus() : "");
                    orderMap.put("totalAmount", order.getTotalAmount());
                    results.add(orderMap);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting order status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Lấy giỏ hàng
     */
    private List<Map<String, Object>> getCartItems(User currentUser) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            List<CartItemDB> cartItems = cartService.getCartItemsByUser(currentUser.getUserID());
            if (cartItems != null) {
                for (CartItemDB item : cartItems) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("productID", item.getProductID());
                    itemMap.put("quantity", item.getQuantity());
                    Product product = productService.getProductById(item.getProductID());
                    if (product != null) {
                        itemMap.put("productName", product.getProductName());
                        itemMap.put("price", product.getPrice());
                    }
                    results.add(itemMap);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting cart items: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Lấy danh sách danh mục
     */
    private List<Map<String, Object>> getCategories() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            List<Category> categories = categoryService.getAllCategories();
            if (categories != null) {
                for (Category category : categories) {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("categoryID", category.getCategoryID());
                    categoryMap.put("categoryName", category.getCategoryName());
                    categoryMap.put("description", category.getDescription());
                    results.add(categoryMap);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting categories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Convert Product to Map
     */
    private Map<String, Object> productToMap(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("productID", product.getProductID());
        map.put("productName", product.getProductName());
        map.put("price", product.getPrice() != null ? product.getPrice().doubleValue() : 0);
        map.put("stock", product.getStock());
        map.put("stockStatus", product.getStockStatus());
        map.put("imageUrl", product.getImageUrl());
        map.put("categoryName", product.getCategoryName());
        map.put("description", product.getDescription());
        return map;
    }
    
    /**
     * Xác định action cho Agentic AI
     */
    private String determineAction(NluResult nluResult, List<Map<String, Object>> data) {
        String intent = nluResult.getIntent();
        
        // Nếu có sản phẩm, có thể suggest action
        if (data != null && !data.isEmpty() && data.get(0).containsKey("productID")) {
            if (intent.equals(NluResult.INTENT_PRODUCT_SEARCH) || 
                intent.equals(NluResult.INTENT_PRODUCT_DETAIL)) {
                return "show_products";
            }
        }
        
        return "none";
    }
}

