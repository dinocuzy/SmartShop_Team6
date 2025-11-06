package addressservice;

import addressdao.IAddressDAO;
import addressdao.AddressDAO;
import model.Address;
import java.util.List;

/**
 * Implementation của IAddressService
 * Chứa business logic cho Address
 * Sử dụng AddressDAO (JDBC) để truy cập dữ liệu
 */
public class AddressService implements IAddressService {
    
    private final IAddressDAO addressDAO;
    
    public AddressService() {
        this.addressDAO = new AddressDAO();
    }
    
    @Override
    public void addAddress(Address address) {
        validateAddress(address);
        addressDAO.insert(address);
    }
    
    @Override
    public void updateAddress(Address address) {
        validateAddress(address);
        if (address.getAddressID() <= 0) {
            throw new IllegalArgumentException("Address ID must be greater than 0");
        }
        boolean updated = addressDAO.update(address);
        if (!updated) {
            throw new RuntimeException("Failed to update address with ID: " + address.getAddressID());
        }
    }
    
    @Override
    public void deleteAddress(int addressID) {
        if (addressID <= 0) {
            throw new IllegalArgumentException("Address ID must be greater than 0");
        }
        boolean deleted = addressDAO.delete(addressID);
        if (!deleted) {
            throw new RuntimeException("Failed to delete address with ID: " + addressID);
        }
    }
    
    @Override
    public Address getAddressById(int addressID) {
        if (addressID <= 0) {
            throw new IllegalArgumentException("Address ID must be greater than 0");
        }
        return addressDAO.getById(addressID);
    }
    
    @Override
    public List<Address> getAddressesByUser(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        return addressDAO.getByUser(userID);
    }
    
    @Override
    public Address getDefaultAddressByUser(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        return addressDAO.getDefaultByUser(userID);
    }
    
    @Override
    public List<Address> getAllAddresses() {
        return addressDAO.getAll();
    }
    
    private void validateAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (address.getUserID() <= 0) {
            throw new IllegalArgumentException("User ID must be greater than 0");
        }
        if (address.getFullName() == null || address.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (address.getLine1() == null || address.getLine1().trim().isEmpty()) {
            throw new IllegalArgumentException("Line1 is required");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (address.getCountry() == null || address.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
    }
}

