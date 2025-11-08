package comparelistitemdao;

import model.CompareListItem;
import java.util.List;

/**
 * Interface định nghĩa các thao tác với bảng CompareListItems
 */
public interface ICompareListItemDAO {
    
    /**
     * Thêm sản phẩm vào CompareList
     * @param compareListID ID của CompareList
     * @param productID ID của sản phẩm
     * @return ID của record vừa tạo, -1 nếu lỗi hoặc đã tồn tại
     */
    int addProduct(int compareListID, int productID);
    
    /**
     * Xóa sản phẩm khỏi CompareList
     * @param compareListID ID của CompareList
     * @param productID ID của sản phẩm
     * @return Số record đã xóa
     */
    int removeProduct(int compareListID, int productID);
    
    /**
     * Lấy tất cả sản phẩm trong CompareList
     * @param compareListID ID của CompareList
     * @return Danh sách CompareListItem với thông tin sản phẩm (JOIN với Products)
     */
    List<CompareListItem> getItemsByCompareListID(int compareListID);
    
    /**
     * Kiểm tra xem sản phẩm đã có trong CompareList chưa
     * @param compareListID ID của CompareList
     * @param productID ID của sản phẩm
     * @return true nếu đã có
     */
    boolean isProductInList(int compareListID, int productID);
    
    /**
     * Xóa tất cả sản phẩm trong CompareList
     * @param compareListID ID của CompareList
     * @return Số record đã xóa
     */
    int clearList(int compareListID);
}

