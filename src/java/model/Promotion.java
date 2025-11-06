package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class đại diện cho bảng Promotions trong database SmartShopDB
 * JavaBean với đầy đủ constructor, getter và setter
 */
public class Promotion {
    
    private int promotionID;
    private String title;
    private String description;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private Date startDate;
    private Date endDate;
    private boolean isActive;
    
    /**
     * Constructor mặc định không tham số
     */
    public Promotion() {
        this.isActive = true;
    }
    
    /**
     * Constructor với đầy đủ tham số
     */
    public Promotion(int promotionID, String title, String description, BigDecimal discountPercent,
                     BigDecimal discountAmount, Date startDate, Date endDate, boolean isActive) {
        this.promotionID = promotionID;
        this.title = title;
        this.description = description;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }
    
    // Getter và Setter methods
    
    public int getPromotionID() {
        return promotionID;
    }
    
    public void setPromotionID(int promotionID) {
        this.promotionID = promotionID;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }
    
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * Kiểm tra xem promotion có đang hiệu lực không (trong khoảng thời gian và isActive)
     */
    public boolean isValid() {
        if (!isActive) {
            return false;
        }
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "promotionID=" + promotionID +
                ", title='" + title + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
