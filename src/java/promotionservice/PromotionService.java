package promotionservice;

import model.Promotion;
import promotiondao.IPromotionDAO;
import promotiondao.PromotionDAO;
import java.util.List;

/**
 * Implementation của IPromotionService
 * Chứa business logic cho Promotion
 * Sử dụng PromotionDAO (JDBC) để truy cập dữ liệu
 */
public class PromotionService implements IPromotionService {
    
    private final IPromotionDAO promotionDAO;
    
    public PromotionService() {
        this.promotionDAO = new PromotionDAO();
    }
    
    @Override
    public void addPromotion(Promotion promotion) {
        validatePromotion(promotion);
        promotionDAO.insert(promotion);
    }
    
    @Override
    public void updatePromotion(Promotion promotion) {
        validatePromotion(promotion);
        if (promotion.getPromotionID() <= 0) {
            throw new IllegalArgumentException("Promotion ID must be greater than 0");
        }
        boolean updated = promotionDAO.update(promotion);
        if (!updated) {
            throw new RuntimeException("Failed to update promotion with ID: " + promotion.getPromotionID());
        }
    }
    
    @Override
    public void deletePromotion(int promotionID) {
        if (promotionID <= 0) {
            throw new IllegalArgumentException("Promotion ID must be greater than 0");
        }
        boolean deleted = promotionDAO.delete(promotionID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete promotion with ID: " + promotionID);
        }
    }
    
    @Override
    public Promotion getPromotionById(int promotionID) {
        if (promotionID <= 0) {
            throw new IllegalArgumentException("Promotion ID must be greater than 0");
        }
        return promotionDAO.getById(promotionID);
    }
    
    @Override
    public List<Promotion> getAllPromotions(boolean includeInactive) {
        return promotionDAO.getAll(includeInactive);
    }
    
    @Override
    public List<Promotion> getActivePromotions() {
        return promotionDAO.getActivePromotions();
    }
    
    private void validatePromotion(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion cannot be null");
        }
        if (promotion.getTitle() == null || promotion.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
    }
}
