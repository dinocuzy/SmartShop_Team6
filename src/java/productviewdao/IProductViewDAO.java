package productviewdao;

import model.Product;
import model.ProductView;
import java.util.List;

/**
 * Interface định nghĩa các thao tác với bảng ProductViews
 */
public interface IProductViewDAO {
    
    /**
     * Thêm một record view sản phẩm
     * @param view ProductView object
     * @return ID của record vừa tạo, -1 nếu lỗi
     */
    int insert(ProductView view);
    
    /**
     * Lấy số lượt xem của một sản phẩm
     * @param productID ID của sản phẩm
     * @return Số lượt xem
     */
    int getViewCountByProductID(int productID);
    
    /**
     * Lấy record view theo ID
     * @param viewID ID của record
     * @return ProductView object hoặc null
     */
    ProductView getById(int viewID);
    
    /**
     * Lấy danh sách sản phẩm được xem nhiều nhất
     * @param limit Số lượng sản phẩm cần lấy
     * @return Danh sách Product
     */
    List<Product> getMostViewedProducts(int limit);
    
    /**
     * Lấy danh sách sản phẩm gợi ý dựa trên lượt xem của user
     * @param userID ID của user (null nếu anonymous)
     * @param limit Số lượng sản phẩm cần lấy
     * @return Danh sách Product
     */
    List<Product> getRecommendedProductsByUserViews(Integer userID, int limit);
    
    /**
     * Lấy danh sách sản phẩm cùng category với sản phẩm đã xem nhiều
     * @param categoryID ID của category
     * @param excludeProductID ID sản phẩm cần loại trừ
     * @param limit Số lượng sản phẩm cần lấy
     * @return Danh sách Product
     */
    List<Product> getRecommendedProductsByCategory(int categoryID, int excludeProductID, int limit);
}

