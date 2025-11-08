package comparelistservice;

import model.CompareListItem;
import model.CompareList;
import comparelistdao.ICompareListDAO;
import comparelistdao.CompareListDAO;
import comparelistitemdao.ICompareListItemDAO;
import comparelistitemdao.CompareListItemDAO;
import java.util.List;

/**
 * Service implementation cho CompareList
 */
public class CompareListService implements ICompareListService {
    
    private ICompareListDAO compareListDAO;
    private ICompareListItemDAO compareListItemDAO;
    
    public CompareListService() {
        this.compareListDAO = new CompareListDAO();
        this.compareListItemDAO = new CompareListItemDAO();
    }
    
    @Override
    public boolean addProduct(int userID, int productID) {
        // Validate input
        if (userID <= 0 || productID <= 0) {
            return false;
        }
        
        // Đảm bảo CompareList tồn tại cho user
        int compareListID = compareListDAO.createForUser(userID);
        if (compareListID <= 0) {
            // Nếu tạo không được, thử lấy CompareList hiện có
            CompareList existingList = compareListDAO.getByUserID(userID);
            if (existingList == null) {
                return false;
            }
            compareListID = existingList.getCompareListID();
        }
        
        // Thêm sản phẩm vào list
        int result = compareListItemDAO.addProduct(compareListID, productID);
        return result > 0;
    }
    
    @Override
    public boolean removeProduct(int userID, int productID) {
        // Validate input
        if (userID <= 0 || productID <= 0) {
            return false;
        }
        
        // Lấy CompareList của user
        CompareList compareList = compareListDAO.getByUserID(userID);
        if (compareList == null) {
            return false;
        }
        
        // Xóa sản phẩm
        int result = compareListItemDAO.removeProduct(compareList.getCompareListID(), productID);
        return result > 0;
    }
    
    @Override
    public List<CompareListItem> getUserCompareList(int userID) {
        if (userID <= 0) {
            return new java.util.ArrayList<>();
        }
        
        // Lấy CompareList của user
        CompareList compareList = compareListDAO.getByUserID(userID);
        if (compareList == null) {
            return new java.util.ArrayList<>();
        }
        
        // Lấy tất cả items
        return compareListItemDAO.getItemsByCompareListID(compareList.getCompareListID());
    }
    
    @Override
    public boolean clearList(int userID) {
        if (userID <= 0) {
            return false;
        }
        
        // Lấy CompareList của user
        CompareList compareList = compareListDAO.getByUserID(userID);
        if (compareList == null) {
            return false;
        }
        
        // Xóa tất cả items
        int result = compareListItemDAO.clearList(compareList.getCompareListID());
        return result >= 0; // >= 0 vì có thể list đã rỗng
    }
    
    @Override
    public boolean isProductInList(int userID, int productID) {
        if (userID <= 0 || productID <= 0) {
            return false;
        }
        
        // Lấy CompareList của user
        CompareList compareList = compareListDAO.getByUserID(userID);
        if (compareList == null) {
            return false;
        }
        
        return compareListItemDAO.isProductInList(compareList.getCompareListID(), productID);
    }
    
    @Override
    public int getCompareListCount(int userID) {
        if (userID <= 0) {
            return 0;
        }
        
        List<CompareListItem> items = getUserCompareList(userID);
        return items != null ? items.size() : 0;
    }
}

