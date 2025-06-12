package com.samyak.nooops.model;

import java.io.Serializable;

public class ProductReview implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String userPhotoUrl;
    private String productId;
    private String title;
    private String content;
    private double rating;
    private long createdAt;

    public ProductReview() {
        // Required empty constructor for Firebase
    }

    public ProductReview(String id, String userId, String userName, String productId, String title, String content, double rating) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
} 