package cartdao;

import model.CartItemDB;
import java.util.List;

/**
 * Interface định nghĩa các phương thức DAO cho CartItems
 */
public interface ICartDAO {
    
    /**
     * Lấy tất cả cart items của một user
     * 
     * @param userID ID của user
     * @return Danh sách CartItemDB
     */
    List<CartItemDB> getCartItemsByUser(int userID);
    
    /**
     * Lấy cart item theo ID
     * 
     * @param cartItemID ID của cart item
     * @return CartItemDB hoặc null nếu không tìm thấy
     */
    CartItemDB getCartItemById(int cartItemID);
    
    /**
     * Lấy cart item theo UserID và ProductID
     * 
     * @param userID ID của user
     * @param productID ID của product
     * @return CartItemDB hoặc null nếu không tìm thấy
     */
    CartItemDB getCartItemByUserAndProduct(int userID, int productID);
    
    /**
     * Thêm cart item mới
     * 
     * @param cartItem CartItemDB cần thêm
     * @return ID của cart item vừa tạo (generated key)
     */
    int insert(CartItemDB cartItem);
    
    /**
     * Cập nhật cart item (chủ yếu là quantity)
     * 
     * @param cartItem CartItemDB với thông tin đã cập nhật
     * @return true nếu cập nhật thành công, false nếu không
     */
    boolean update(CartItemDB cartItem);
    
    /**
     * Xóa cart item theo ID
     * 
     * @param cartItemID ID của cart item cần xóa
     * @return true nếu xóa thành công, false nếu không
     */
    boolean delete(int cartItemID);
    
    /**
     * Xóa tất cả cart items của một user
     * 
     * @param userID ID của user
     * @return Số lượng cart items đã xóa
     */
    int deleteAllByUser(int userID);
    
    /**
     * Xóa cart item theo UserID và ProductID
     * 
     * @param userID ID của user
     * @param productID ID của product
     * @return true nếu xóa thành công, false nếu không
     */
    boolean deleteByUserAndProduct(int userID, int productID);
    
    /**
     * Đếm số lượng cart items của một user
     * 
     * @param userID ID của user
     * @return Số lượng cart items
     */
    int countByUser(int userID);
}

