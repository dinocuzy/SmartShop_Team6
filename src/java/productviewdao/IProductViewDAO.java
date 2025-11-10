package productviewdao;

import model.ProductView;

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
}

