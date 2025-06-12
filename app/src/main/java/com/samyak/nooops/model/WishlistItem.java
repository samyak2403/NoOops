package com.samyak.nooops.model;

import java.io.Serializable;

/**
 * Model class representing an item in a user's wishlist
 */
public class WishlistItem implements Serializable {
    private String productId;
    private String productName;
    private String productImage;
    private double price;
    private long addedAt;

    // Required empty constructor for Firebase
    public WishlistItem() {
    }

    public WishlistItem(String productId, String productName, String productImage, double price) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.price = price;
        this.addedAt = System.currentTimeMillis();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }
} 