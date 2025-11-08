package comparelistservice;

import model.CompareListItem;
import java.util.List;

/**
 * Interface Service cho CompareList
 */
public interface ICompareListService {
    
    /**
     * Thêm sản phẩm vào CompareList của user
     * @param userID ID của user
     * @param productID ID của sản phẩm
     * @return true nếu thành công
     */
    boolean addProduct(int userID, int productID);
    
    /**
     * Xóa sản phẩm khỏi CompareList của user
     * @param userID ID của user
     * @param productID ID của sản phẩm
     * @return true nếu thành công
     */
    boolean removeProduct(int userID, int productID);
    
    /**
     * Lấy tất cả sản phẩm trong CompareList của user
     * @param userID ID của user
     * @return Danh sách CompareListItem
     */
    List<CompareListItem> getUserCompareList(int userID);
    
    /**
     * Xóa tất cả sản phẩm trong CompareList của user
     * @param userID ID của user
     * @return true nếu thành công
     */
    boolean clearList(int userID);
    
    /**
     * Kiểm tra xem sản phẩm đã có trong CompareList chưa
     * @param userID ID của user
     * @param productID ID của sản phẩm
     * @return true nếu đã có
     */
    boolean isProductInList(int userID, int productID);
    
    /**
     * Lấy số lượng sản phẩm trong CompareList của user
     * @param userID ID của user
     * @return Số lượng sản phẩm
     */
    int getCompareListCount(int userID);
}

