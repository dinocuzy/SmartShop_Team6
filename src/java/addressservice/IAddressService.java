package addressservice;

import model.Address;
import java.util.List;

/**
 * Interface định nghĩa business logic cho Address
 */
public interface IAddressService {
    
    /**
     * Thêm địa chỉ mới
     */
    void addAddress(Address address);
    
    /**
     * Cập nhật địa chỉ
     */
    void updateAddress(Address address);
    
    /**
     * Xóa địa chỉ
     */
    void deleteAddress(int addressID);
    
    /**
     * Lấy địa chỉ theo ID
     */
    Address getAddressById(int addressID);
    
    /**
     * Lấy tất cả địa chỉ của một user
     */
    List<Address> getAddressesByUser(int userID);
    
    /**
     * Lấy địa chỉ mặc định của user
     */
    Address getDefaultAddressByUser(int userID);
    
    /**
     * Lấy tất cả địa chỉ
     */
    List<Address> getAllAddresses();
}

