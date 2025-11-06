package promotionservice;

import model.Promotion;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Promotion
 */
public interface IPromotionService {

    /**
     * Thêm promotion mới
     *
     * @param promotion Promotion object cần thêm
     */
    void addPromotion(Promotion promotion);

    /**
     * Cập nhật promotion
     *
     * @param promotion Promotion object với thông tin đã cập nhật
     */
    void updatePromotion(Promotion promotion);

    /**
     * Xóa promotion (soft delete)
     *
     * @param promotionID ID promotion cần xóa
     */
    void deletePromotion(int promotionID);

    /**
     * Lấy promotion theo ID
     *
     * @param promotionID ID promotion
     * @return Promotion object hoặc null nếu không tìm thấy
     */
    Promotion getPromotionById(int promotionID);

    /**
     * Lấy tất cả promotions
     *
     * @param includeInactive Nếu true, lấy cả promotions inactive (IsActive = 0)
     * @return Danh sách các Promotion
     */
    List<Promotion> getAllPromotions(boolean includeInactive);

    /**
     * Lấy các promotions đang hiệu lực
     *
     * @return Danh sách các Promotion đang hiệu lực
     */
    List<Promotion> getActivePromotions();
}
