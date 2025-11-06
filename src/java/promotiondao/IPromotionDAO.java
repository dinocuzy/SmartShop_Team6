package promotiondao;

import model.Promotion;
import java.util.List;

public interface IPromotionDAO {
    List<Promotion> getAll(boolean includeInactive);
    Promotion getById(int promotionID);
    List<Promotion> getActivePromotions();
    int insert(Promotion promotion);
    boolean update(Promotion promotion);
    boolean delete(int promotionID); // Soft delete
}
