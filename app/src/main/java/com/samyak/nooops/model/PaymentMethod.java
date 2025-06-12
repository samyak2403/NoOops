package com.samyak.nooops.model;

public class PaymentMethod {
    public static final String TYPE_CARD = "card";
    public static final String TYPE_COD = "cod";
    
    private String id;
    private String type;
    private String cardNumber;  // Last 4 digits only
    private String cardType;    // VISA, MasterCard, etc.
    private String expiryDate;
    private boolean isDefault;

    public PaymentMethod() {
        // Required empty constructor for Firestore
    }

    public PaymentMethod(String id, String type) {
        this.id = id;
        this.type = type;
        this.isDefault = false;
    }

    // For card payments
    public PaymentMethod(String id, String cardNumber, String cardType, String expiryDate) {
        this.id = id;
        this.type = TYPE_CARD;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.expiryDate = expiryDate;
        this.isDefault = false;
    }

    // For COD
    public static PaymentMethod createCOD() {
        return new PaymentMethod("cod", TYPE_COD);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        if (TYPE_CARD.equals(type)) {
            return cardType + " •••• " + cardNumber + " | Expires: " + expiryDate;
        } else if (TYPE_COD.equals(type)) {
            return "Cash on Delivery";
        }
        return "Unknown Payment Method";
    }
} 