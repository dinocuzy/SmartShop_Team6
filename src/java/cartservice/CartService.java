package cartservice;

import cartdao.ICartDAO;
import cartdao.CartDAO;
import model.CartItemDB;
import model.CartItem;
import productservice.IProductService;
import productservice.ProductService;
import model.Product;

import java.util.Date;
import java.util.List;

/**
 * Implementation của ICartService
 * Chứa business logic cho Cart
 */
public class CartService implements ICartService {
    
    private ICartDAO cartDAO;
    private IProductService productService;
    
    public CartService() {
        this.cartDAO = new CartDAO();
        this.productService = new ProductService();
    }
    
    @Override
    public List<CartItemDB> getCartItemsByUser(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        return cartDAO.getCartItemsByUser(userID);
    }
    
    @Override
    public boolean addToCart(int userID, int productID, int quantity) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        if (productID <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        // Kiểm tra sản phẩm có tồn tại không
        Product product = productService.getProductById(productID);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        // Kiểm tra stock
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock());
        }
        
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItemDB existingItem = cartDAO.getCartItemByUserAndProduct(userID, productID);
        
        if (existingItem != null) {
            // Đã có, cập nhật số lượng
            int newQuantity = existingItem.getQuantity() + quantity;
            
            // Kiểm tra không vượt quá stock
            if (newQuantity > product.getStock()) {
                newQuantity = product.getStock();
            }
            
            existingItem.setQuantity(newQuantity);
            existingItem.setAddedDate(new Date());
            return cartDAO.update(existingItem);
        } else {
            // Chưa có, thêm mới
            CartItemDB newItem = new CartItemDB(userID, productID, quantity);
            int generatedId = cartDAO.insert(newItem);
            return generatedId > 0;
        }
    }
    
    @Override
    public boolean updateQuantity(int userID, int productID, int quantity) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        if (productID <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        if (quantity <= 0) {
            // Nếu quantity <= 0, xóa item
            return removeFromCart(userID, productID);
        }
        
        // Kiểm tra sản phẩm có tồn tại không
        Product product = productService.getProductById(productID);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        // Kiểm tra stock
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock());
        }
        
        // Lấy cart item hiện tại
        CartItemDB cartItem = cartDAO.getCartItemByUserAndProduct(userID, productID);
        if (cartItem == null) {
            throw new IllegalArgumentException("Cart item not found");
        }
        
        cartItem.setQuantity(quantity);
        cartItem.setAddedDate(new Date());
        return cartDAO.update(cartItem);
    }
    
    @Override
    public boolean removeFromCart(int userID, int productID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        if (productID <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        
        return cartDAO.deleteByUserAndProduct(userID, productID);
    }
    
    @Override
    public int clearCart(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        
        return cartDAO.deleteAllByUser(userID);
    }
    
    @Override
    public int syncCartFromSession(int userID, List<model.CartItem> sessionCartItems) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        if (sessionCartItems == null || sessionCartItems.isEmpty()) {
            return 0;
        }
        
        int syncedCount = 0;
        
        for (model.CartItem sessionItem : sessionCartItems) {
            try {
                // Kiểm tra sản phẩm có tồn tại trong DB cart chưa
                CartItemDB dbItem = cartDAO.getCartItemByUserAndProduct(userID, sessionItem.getProductID());
                
                if (dbItem != null) {
                    // Đã có, gộp số lượng
                    int newQuantity = dbItem.getQuantity() + sessionItem.getQuantity();
                    
                    // Kiểm tra stock
                    Product product = productService.getProductById(sessionItem.getProductID());
                    if (product != null && newQuantity > product.getStock()) {
                        newQuantity = product.getStock();
                    }
                    
                    dbItem.setQuantity(newQuantity);
                    dbItem.setAddedDate(new Date());
                    cartDAO.update(dbItem);
                } else {
                    // Chưa có, thêm mới
                    CartItemDB newItem = new CartItemDB(
                        userID, 
                        sessionItem.getProductID(), 
                        sessionItem.getQuantity()
                    );
                    cartDAO.insert(newItem);
                }
                
                syncedCount++;
            } catch (Exception e) {
                System.err.println("Error syncing cart item: " + e.getMessage());
                e.printStackTrace();
                // Tiếp tục với item tiếp theo
            }
        }
        
        return syncedCount;
    }
}

