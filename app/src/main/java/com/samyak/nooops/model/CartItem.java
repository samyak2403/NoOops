package com.samyak.nooops.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String id;
    private String productId;
    private String productName;
    private String productImage;
    private double price;
    private int quantity;
    private long addedAt;
    private long updatedAt;

    public CartItem() {
        // Required empty constructor for Firebase
    }

    public CartItem(String id, String productId, String productName, String productImage, double price, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.price = price;
        this.quantity = quantity;
        this.addedAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = System.currentTimeMillis();
    }

    public void incrementQuantity() {
        this.quantity++;
        this.updatedAt = System.currentTimeMillis();
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public double getTotal() {
        return price * quantity;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
} 