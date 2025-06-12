package com.samyak.nooops.model;

import java.io.Serializable;

public class ProductSpecification implements Serializable {
    private String name;
    private String value;

    public ProductSpecification() {
        // Required empty constructor for Firebase
    }

    public ProductSpecification(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
} 