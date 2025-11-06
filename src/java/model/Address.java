package model;

import java.util.Date;

/**
 * 
 */
public class Address {
    
    private int addressID;
    private int userID;
    private String fullName;
    private String phone;
    private String line1;
    private String line2;
    private String city;
    private String district;
    private String ward;
    private String country;
    private String postalCode;
    private boolean isDefault;
    private Date createdAt;
    
    // Relationship với User (Many-to-One) - chỉ để reference, không dùng JPA
    private User user;
    
    public Address() {
        this.isDefault = false;
        this.createdAt = new Date();
    }
    
    public Address(int addressID, int userID, String fullName, String phone, 
                   String line1, String line2, String city, String district, 
                   String ward, String country, String postalCode, 
                   boolean isDefault, Date createdAt) {
        this.addressID = addressID;
        this.userID = userID;
        this.fullName = fullName;
        this.phone = phone;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.country = country;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    
    public int getAddressID() {
        return addressID;
    }
    
    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getLine1() {
        return line1;
    }
    
    public void setLine1(String line1) {
        this.line1 = line1;
    }
    
    public String getLine2() {
        return line2;
    }
    
    public void setLine2(String line2) {
        this.line2 = line2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getWard() {
        return ward;
    }
    
    public void setWard(String ward) {
        this.ward = ward;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userID = user.getUserID();
        }
    }
    
    /**
     * Lấy địa chỉ đầy đủ dạng chuỗi
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (line1 != null) sb.append(line1);
        if (line2 != null && !line2.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(line2);
        }
        if (ward != null && !ward.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ward);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(postalCode);
        }
        return sb.toString();
    }
}

