package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model đại diện cho giỏ hàng
 * Chứa danh sách các CartItem và các phương thức tính toán
 */
public class Cart {
    
    private Map<Integer, CartItem> items; // Map<ProductID, CartItem>
    
    public Cart() {
        this.items = new HashMap<>();
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public void addItem(CartItem item) {
        if (item == null) {
            return;
        }
        
        int productID = item.getProductID();
        
        // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
        if (items.containsKey(productID)) {
            CartItem existingItem = items.get(productID);
            int newQuantity = existingItem.getQuantity() + item.getQuantity();
            
            // Kiểm tra không vượt quá stock
            if (newQuantity > existingItem.getStock()) {
                newQuantity = existingItem.getStock();
            }
            
            existingItem.setQuantity(newQuantity);
        } else {
            // Thêm mới
            items.put(productID, item);
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm
     */
    public void updateQuantity(int productID, int quantity) {
        if (items.containsKey(productID)) {
            CartItem item = items.get(productID);
            
            if (quantity <= 0) {
                removeItem(productID);
            } else {
                // Kiểm tra không vượt quá stock
                if (quantity > item.getStock()) {
                    quantity = item.getStock();
                }
                item.setQuantity(quantity);
            }
        }
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    public void removeItem(int productID) {
        items.remove(productID);
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Lấy item theo productID
     */
    public CartItem getItem(int productID) {
        return items.get(productID);
    }
    
    /**
     * Lấy danh sách tất cả items
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }
    
    /**
     * Kiểm tra giỏ hàng có rỗng không
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Lấy tổng số lượng sản phẩm (tổng quantity của tất cả items)
     */
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items.values()) {
            total += item.getQuantity();
        }
        return total;
    }
    
    /**
     * Lấy số lượng loại sản phẩm (số items khác nhau)
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Tính tổng tiền của giỏ hàng
     */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
    
    /**
     * Kiểm tra sản phẩm có trong giỏ hàng không
     */
    public boolean contains(int productID) {
        return items.containsKey(productID);
    }
    
    /**
     * Lấy Map items (để lưu vào session)
     */
    public Map<Integer, CartItem> getItemsMap() {
        return items;
    }
    
    /**
     * Set Map items (để load từ session)
     */
    public void setItemsMap(Map<Integer, CartItem> items) {
        if (items != null) {
            this.items = items;
        } else {
            this.items = new HashMap<>();
        }
    }
}

