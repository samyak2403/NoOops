package com.samyak.nooops.model;

import java.io.Serializable;
import java.util.UUID;

public class Address implements Serializable {
    private String id;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String phone;
    private boolean isDefault;
    
    public Address() {
        // Required empty constructor for Firebase
        this.id = UUID.randomUUID().toString();
    }
    
    public Address(String name, String addressLine1, String city, 
                  String state, String country, String pincode, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.phone = phone;
        this.isDefault = false;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getPincode() {
        return pincode;
    }
    
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public String getFullAddress() {
        StringBuilder builder = new StringBuilder();
        builder.append(addressLine1);
        
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            builder.append(", ").append(addressLine2);
        }
        
        builder.append(", ").append(city)
               .append(", ").append(state)
               .append(", ").append(country)
               .append(" - ").append(pincode);
        
        return builder.toString();
    }
} 