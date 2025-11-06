package paymentdao;

import model.Payment;
import util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentDAO implements IPaymentDAO {
    
    @Override
    public List<Payment> getAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.PaymentID, p.OrderID, p.PaymentMethodID, p.Amount, p.PaymentStatus, " +
                     "p.PaymentDate, p.TransactionCode, pm.MethodName " +
                     "FROM Payments p LEFT JOIN PaymentMethods pm ON p.PaymentMethodID = pm.PaymentMethodID " +
                     "ORDER BY p.PaymentID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Payment payment = mapResultSetToPayment(rs);
                payment.setMethodName(rs.getString("MethodName"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }
    
    @Override
    public Payment getById(int paymentID) {
        String sql = "SELECT p.PaymentID, p.OrderID, p.PaymentMethodID, p.Amount, p.PaymentStatus, " +
                     "p.PaymentDate, p.TransactionCode, pm.MethodName " +
                     "FROM Payments p LEFT JOIN PaymentMethods pm ON p.PaymentMethodID = pm.PaymentMethodID " +
                     "WHERE p.PaymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, paymentID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = mapResultSetToPayment(rs);
                    payment.setMethodName(rs.getString("MethodName"));
                    return payment;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting payment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Payment> getByOrder(int orderID) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.PaymentID, p.OrderID, p.PaymentMethodID, p.Amount, p.PaymentStatus, " +
                     "p.PaymentDate, p.TransactionCode, pm.MethodName " +
                     "FROM Payments p LEFT JOIN PaymentMethods pm ON p.PaymentMethodID = pm.PaymentMethodID " +
                     "WHERE p.OrderID = ? ORDER BY p.PaymentID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment payment = mapResultSetToPayment(rs);
                    payment.setMethodName(rs.getString("MethodName"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting payments by order: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }
    
    @Override
    public List<Payment> getByStatus(String status) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.PaymentID, p.OrderID, p.PaymentMethodID, p.Amount, p.PaymentStatus, " +
                     "p.PaymentDate, p.TransactionCode, pm.MethodName " +
                     "FROM Payments p LEFT JOIN PaymentMethods pm ON p.PaymentMethodID = pm.PaymentMethodID " +
                     "WHERE p.PaymentStatus = ? ORDER BY p.PaymentID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment payment = mapResultSetToPayment(rs);
                    payment.setMethodName(rs.getString("MethodName"));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting payments by status: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }
    
    @Override
    public int insert(Payment payment) {
        String sql = "INSERT INTO Payments (OrderID, PaymentMethodID, Amount, PaymentStatus, PaymentDate, TransactionCode) " +
                     "VALUES (?, ?, ?, ?, GETDATE(), ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, payment.getOrderID());
            ps.setInt(2, payment.getPaymentMethodID());
            ps.setBigDecimal(3, payment.getAmount());
            ps.setString(4, payment.getPaymentStatus());
            ps.setString(5, payment.getTransactionCode());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        payment.setPaymentID(generatedId);
                        System.out.println("Inserted payment ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean update(Payment payment) {
        String sql = "UPDATE Payments SET OrderID = ?, PaymentMethodID = ?, Amount = ?, " +
                     "PaymentStatus = ?, TransactionCode = ? WHERE PaymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, payment.getOrderID());
            ps.setInt(2, payment.getPaymentMethodID());
            ps.setBigDecimal(3, payment.getAmount());
            ps.setString(4, payment.getPaymentStatus());
            ps.setString(5, payment.getTransactionCode());
            ps.setInt(6, payment.getPaymentID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Updated payment ID: " + payment.getPaymentID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int paymentID) {
        String sql = "DELETE FROM Payments WHERE PaymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, paymentID);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted payment ID: " + paymentID);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentID(rs.getInt("PaymentID"));
        payment.setOrderID(rs.getInt("OrderID"));
        payment.setPaymentMethodID(rs.getInt("PaymentMethodID"));
        
        BigDecimal amount = rs.getBigDecimal("Amount");
        if (amount != null) {
            payment.setAmount(amount);
        }
        
        payment.setPaymentStatus(rs.getString("PaymentStatus"));
        
        Timestamp paymentDate = rs.getTimestamp("PaymentDate");
        if (paymentDate != null) {
            payment.setPaymentDate(new Date(paymentDate.getTime()));
        }
        
        payment.setTransactionCode(rs.getString("TransactionCode"));
        
        return payment;
    }
}
