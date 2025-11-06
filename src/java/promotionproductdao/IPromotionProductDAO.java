package promotionproductdao;

import java.util.List;

/**
 * Interface DAO cho PromotionProduct
 * Quản lý quan hệ many-to-many giữa Promotion và Product
 */
public interface IPromotionProductDAO {
    
    /**
     * Thêm promotion cho product
     * @param promotionID ID của promotion
     * @param productID ID của product
     * @return true nếu thành công
     */
    boolean addPromotionToProduct(int promotionID, int productID);
    
    /**
     * Xóa promotion khỏi product
     * @param promotionID ID của promotion
     * @param productID ID của product
     * @return true nếu thành công
     */
    boolean removePromotionFromProduct(int promotionID, int productID);
    
    /**
     * Xóa tất cả promotions của một product
     * @param productID ID của product
     * @return true nếu thành công
     */
    boolean removeAllPromotionsFromProduct(int productID);
    
    /**
     * Lấy danh sách promotion IDs của một product
     * @param productID ID của product
     * @return List các promotion IDs
     */
    List<Integer> getPromotionIDsByProduct(int productID);
    
    /**
     * Lấy promotion ID đầu tiên (đang active) của product
     * @param productID ID của product
     * @return Promotion ID hoặc 0 nếu không có
     */
    int getActivePromotionIDByProduct(int productID);
}

