package paymentmethoddao;

import model.PaymentMethod;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDAO implements IPaymentMethodDAO {
    
    @Override
    public List<PaymentMethod> getAll(boolean includeInactive) {
        List<PaymentMethod> methods = new ArrayList<>();
        String sql;
        
        if (includeInactive) {
            sql = "SELECT PaymentMethodID, MethodName, Provider, IsActive FROM PaymentMethods ORDER BY MethodName ASC";
        } else {
            sql = "SELECT PaymentMethodID, MethodName, Provider, IsActive FROM PaymentMethods WHERE IsActive = 1 ORDER BY MethodName ASC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PaymentMethod method = new PaymentMethod();
                method.setPaymentMethodID(rs.getInt("PaymentMethodID"));
                method.setMethodName(rs.getString("MethodName"));
                method.setProvider(rs.getString("Provider"));
                method.setActive(rs.getBoolean("IsActive"));
                methods.add(method);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all payment methods: " + e.getMessage());
            e.printStackTrace();
        }
        return methods;
    }
    
    @Override
    public PaymentMethod getById(int paymentMethodID) {
        String sql = "SELECT PaymentMethodID, MethodName, Provider, IsActive FROM PaymentMethods WHERE PaymentMethodID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, paymentMethodID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PaymentMethod method = new PaymentMethod();
                    method.setPaymentMethodID(rs.getInt("PaymentMethodID"));
                    method.setMethodName(rs.getString("MethodName"));
                    method.setProvider(rs.getString("Provider"));
                    method.setActive(rs.getBoolean("IsActive"));
                    return method;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting payment method by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public int insert(PaymentMethod paymentMethod) {
        String sql = "INSERT INTO PaymentMethods (MethodName, Provider, IsActive) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, paymentMethod.getMethodName());
            ps.setString(2, paymentMethod.getProvider());
            ps.setBoolean(3, paymentMethod.isActive());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        paymentMethod.setPaymentMethodID(generatedId);
                        System.out.println("Inserted payment method ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting payment method: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(PaymentMethod paymentMethod) {
        String sql = "UPDATE PaymentMethods SET MethodName = ?, Provider = ?, IsActive = ? WHERE PaymentMethodID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, paymentMethod.getMethodName());
            ps.setString(2, paymentMethod.getProvider());
            ps.setBoolean(3, paymentMethod.isActive());
            ps.setInt(4, paymentMethod.getPaymentMethodID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated payment method ID: " + paymentMethod.getPaymentMethodID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment method: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int paymentMethodID) {
        // Soft delete: set IsActive = 0
        String sql = "UPDATE PaymentMethods SET IsActive = 0 WHERE PaymentMethodID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, paymentMethodID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted payment method ID: " + paymentMethodID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment method: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
