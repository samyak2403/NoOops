package com.samyak.nooops.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String photoUrl;
    private List<Address> addresses;
    private List<String> wishlist;
    private String defaultAddressId;
    private boolean emailVerified;
    private boolean phoneVerified;
    private long createdAt;
    private long lastLoginAt;
    private long updatedAt;

    public User() {
        // Required empty constructor for Firebase
        addresses = new ArrayList<>();
        wishlist = new ArrayList<>();
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.addresses = new ArrayList<>();
        this.wishlist = new ArrayList<>();
        this.emailVerified = false;
        this.phoneVerified = false;
        this.createdAt = System.currentTimeMillis();
        this.lastLoginAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
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
        this.updatedAt = System.currentTimeMillis();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        this.updatedAt = System.currentTimeMillis();
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addAddress(Address address) {
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
        
        // If this is the first address, set it as default
        if (this.addresses.isEmpty()) {
            this.defaultAddressId = address.getId();
        }
        
        this.addresses.add(address);
        this.updatedAt = System.currentTimeMillis();
    }

    public void removeAddress(String addressId) {
        if (this.addresses != null) {
            for (int i = 0; i < this.addresses.size(); i++) {
                if (this.addresses.get(i).getId().equals(addressId)) {
                    this.addresses.remove(i);
                    break;
                }
            }
            
            // If default address was removed, set a new default if available
            if (this.defaultAddressId != null && this.defaultAddressId.equals(addressId)) {
                this.defaultAddressId = this.addresses.isEmpty() ? null : this.addresses.get(0).getId();
            }
            
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public Address getDefaultAddress() {
        if (this.addresses == null || this.addresses.isEmpty() || this.defaultAddressId == null) {
            return null;
        }

        for (Address address : this.addresses) {
            if (address.getId().equals(this.defaultAddressId)) {
                return address;
            }
        }

        return this.addresses.get(0);
    }

    public String getDefaultAddressId() {
        return defaultAddressId;
    }

    public void setDefaultAddressId(String defaultAddressId) {
        this.defaultAddressId = defaultAddressId;
        this.updatedAt = System.currentTimeMillis();
    }

    public List<String> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<String> wishlist) {
        this.wishlist = wishlist;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addToWishlist(String productId) {
        if (this.wishlist == null) {
            this.wishlist = new ArrayList<>();
        }
        if (!this.wishlist.contains(productId)) {
            this.wishlist.add(productId);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public void removeFromWishlist(String productId) {
        if (this.wishlist != null && this.wishlist.contains(productId)) {
            this.wishlist.remove(productId);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public boolean isInWishlist(String productId) {
        return this.wishlist != null && this.wishlist.contains(productId);
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
} 