package socialsharedao;

import model.SocialShare;

/**
 * Interface định nghĩa các thao tác với bảng SocialShares
 */
public interface ISocialShareDAO {
    
    /**
     * Thêm một record chia sẻ sản phẩm
     * @param share SocialShare object
     * @return ID của record vừa tạo, -1 nếu lỗi
     */
    int insert(SocialShare share);
    
    /**
     * Lấy số lượt chia sẻ của một sản phẩm
     * @param productID ID của sản phẩm
     * @return Số lượt chia sẻ
     */
    int getShareCountByProductID(int productID);
    
    /**
     * Lấy record share theo ID
     * @param shareID ID của record
     * @return SocialShare object hoặc null
     */
    SocialShare getById(int shareID);
}

