package cartservice;

import model.CartItemDB;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Cart
 */
public interface ICartService {
    
    /**
     * Lấy tất cả cart items của một user
     * 
     * @param userID ID của user
     * @return Danh sách CartItemDB
     */
    List<CartItemDB> getCartItemsByUser(int userID);
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     * Nếu sản phẩm đã tồn tại, tăng số lượng
     * 
     * @param userID ID của user
     * @param productID ID của product
     * @param quantity Số lượng cần thêm
     * @return true nếu thành công
     */
    boolean addToCart(int userID, int productID, int quantity);
    
    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * 
     * @param userID ID của user
     * @param productID ID của product
     * @param quantity Số lượng mới
     * @return true nếu thành công
     */
    boolean updateQuantity(int userID, int productID, int quantity);
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * 
     * @param userID ID của user
     * @param productID ID của product
     * @return true nếu thành công
     */
    boolean removeFromCart(int userID, int productID);
    
    /**
     * Xóa toàn bộ giỏ hàng của user
     * 
     * @param userID ID của user
     * @return Số lượng items đã xóa
     */
    int clearCart(int userID);
    
    /**
     * Đồng bộ giỏ hàng từ session vào database
     * Gộp số lượng nếu sản phẩm trùng
     * 
     * @param userID ID của user
     * @param sessionCartItems Danh sách CartItem từ session
     * @return Số lượng items đã đồng bộ
     */
    int syncCartFromSession(int userID, List<model.CartItem> sessionCartItems);
}

