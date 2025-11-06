package addressdao;

import model.Address;
import java.util.List;

public interface IAddressDAO {
    List<Address> getAll();
    Address getById(int addressID);
    List<Address> getByUser(int userID);
    Address getDefaultByUser(int userID);
    int insert(Address address);
    boolean update(Address address);
    boolean delete(int addressID);
}
