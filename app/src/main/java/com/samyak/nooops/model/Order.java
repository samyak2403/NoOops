package com.samyak.nooops.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_SHIPPED = "shipped";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    private String id;
    private String userId;
    private List<CartItem> items;
    private double subtotal;
    private double taxes;
    private double shippingFee;
    private double discount;
    private double total;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;
    private String status;
    private boolean isDiscreetPackaging;
    private Date orderDate;
    private Date deliveryDate;
    private String trackingNumber;
    private boolean isRated;

    public Order() {
        // Required empty constructor for Firestore
        items = new ArrayList<>();
        status = STATUS_PENDING;
        orderDate = new Date();
    }

    public Order(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>();
        this.subtotal = 0;
        this.taxes = 0;
        this.shippingFee = 0;
        this.discount = 0;
        this.total = 0;
        this.status = STATUS_PENDING;
        this.isDiscreetPackaging = false;
        this.orderDate = new Date();
        this.isRated = false;
    }

    // Getters and Setters
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

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateSubtotal();
    }

    public void addItem(CartItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        calculateSubtotal();
    }

    private void calculateSubtotal() {
        double sum = 0;
        if (items != null) {
            for (CartItem item : items) {
                sum += item.getTotalPrice();
            }
        }
        this.subtotal = sum;
        calculateTotal();
    }

    private void calculateTotal() {
        this.total = subtotal + taxes + shippingFee - discount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
        calculateTotal();
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
        calculateTotal();
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
        calculateTotal();
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        calculateTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
        this.isDiscreetPackaging = shippingAddress.isDiscreetPackaging();
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDiscreetPackaging() {
        return isDiscreetPackaging;
    }

    public void setDiscreetPackaging(boolean discreetPackaging) {
        isDiscreetPackaging = discreetPackaging;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public int getItemCount() {
        int count = 0;
        if (items != null) {
            for (CartItem item : items) {
                count += item.getQuantity();
            }
        }
        return count;
    }
}
 