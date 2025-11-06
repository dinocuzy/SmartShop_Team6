package addressdao;

import model.Address;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddressDAO implements IAddressDAO {
    
    @Override
    public List<Address> getAll() {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT AddressID, UserID, FullName, Phone, Line1, Line2, City, District, " +
                     "Ward, Country, PostalCode, IsDefault, CreatedAt FROM Addresses ORDER BY AddressID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                addresses.add(mapResultSetToAddress(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all addresses: " + e.getMessage());
            e.printStackTrace();
        }
        return addresses;
    }
    
    @Override
    public Address getById(int addressID) {
        String sql = "SELECT AddressID, UserID, FullName, Phone, Line1, Line2, City, District, " +
                     "Ward, Country, PostalCode, IsDefault, CreatedAt FROM Addresses WHERE AddressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, addressID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting address by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Address> getByUser(int userID) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT AddressID, UserID, FullName, Phone, Line1, Line2, City, District, " +
                     "Ward, Country, PostalCode, IsDefault, CreatedAt FROM Addresses " +
                     "WHERE UserID = ? ORDER BY IsDefault DESC, AddressID ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting addresses by user: " + e.getMessage());
            e.printStackTrace();
        }
        return addresses;
    }
    
    @Override
    public Address getDefaultByUser(int userID) {
        String sql = "SELECT AddressID, UserID, FullName, Phone, Line1, Line2, City, District, " +
                     "Ward, Country, PostalCode, IsDefault, CreatedAt FROM Addresses " +
                     "WHERE UserID = ? AND IsDefault = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting default address by user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public int insert(Address address) {
        String sql = "INSERT INTO Addresses (UserID, FullName, Phone, Line1, Line2, City, District, " +
                     "Ward, Country, PostalCode, IsDefault, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, address.getUserID());
            ps.setString(2, address.getFullName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getLine1());
            ps.setString(5, address.getLine2());
            ps.setString(6, address.getCity());
            ps.setString(7, address.getDistrict());
            ps.setString(8, address.getWard());
            ps.setString(9, address.getCountry());
            ps.setString(10, address.getPostalCode());
            ps.setBoolean(11, address.isDefault());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        address.setAddressID(generatedId);
                        System.out.println("Inserted address ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting address: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(Address address) {
        String sql = "UPDATE Addresses SET UserID = ?, FullName = ?, Phone = ?, Line1 = ?, Line2 = ?, " +
                     "City = ?, District = ?, Ward = ?, Country = ?, PostalCode = ?, IsDefault = ? " +
                     "WHERE AddressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, address.getUserID());
            ps.setString(2, address.getFullName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getLine1());
            ps.setString(5, address.getLine2());
            ps.setString(6, address.getCity());
            ps.setString(7, address.getDistrict());
            ps.setString(8, address.getWard());
            ps.setString(9, address.getCountry());
            ps.setString(10, address.getPostalCode());
            ps.setBoolean(11, address.isDefault());
            ps.setInt(12, address.getAddressID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated address ID: " + address.getAddressID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating address: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int addressID) {
        String sql = "DELETE FROM Addresses WHERE AddressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, addressID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted address ID: " + addressID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting address: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setAddressID(rs.getInt("AddressID"));
        address.setUserID(rs.getInt("UserID"));
        address.setFullName(rs.getString("FullName"));
        address.setPhone(rs.getString("Phone"));
        address.setLine1(rs.getString("Line1"));
        address.setLine2(rs.getString("Line2"));
        address.setCity(rs.getString("City"));
        address.setDistrict(rs.getString("District"));
        address.setWard(rs.getString("Ward"));
        address.setCountry(rs.getString("Country"));
        address.setPostalCode(rs.getString("PostalCode"));
        address.setDefault(rs.getBoolean("IsDefault"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            address.setCreatedAt(new Date(createdAt.getTime()));
        }
        
        return address;
    }
}
