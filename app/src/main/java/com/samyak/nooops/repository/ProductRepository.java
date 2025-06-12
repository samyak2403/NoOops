package com.samyak.nooops.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samyak.nooops.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository extends FirebaseRepository {
    private static final String TAG = "ProductRepository";
    private static final String PRODUCTS_COLLECTION = "products";
    private static final int LIMIT = 20;

    public ProductRepository() {
        super();
    }

    // Get featured products
    public LiveData<List<Product>> getFeaturedProducts() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
        
        firestore.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("isFeatured", true)
                .limit(LIMIT)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            products.add(product);
                        }
                        productsLiveData.setValue(products);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting featured products", e);
                        productsLiveData.setValue(new ArrayList<>());
                    }
                });
        
        return productsLiveData;
    }

    // Get products on sale
    public LiveData<List<Product>> getProductsOnSale() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
        
        firestore.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("isOnSale", true)
                .limit(LIMIT)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            products.add(product);
                        }
                        productsLiveData.setValue(products);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting products on sale", e);
                        productsLiveData.setValue(new ArrayList<>());
                    }
                });
        
        return productsLiveData;
    }

    // Get product details
    public LiveData<Product> getProductDetails(String productId) {
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();
        
        firestore.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Product product = documentSnapshot.toObject(Product.class);
                        productLiveData.setValue(product);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting product details", e);
                        productLiveData.setValue(null);
                    }
                });
        
        return productLiveData;
    }

    // Get all products
    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
        
        firestore.collection(PRODUCTS_COLLECTION)
                .limit(LIMIT)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            products.add(product);
                        }
                        productsLiveData.setValue(products);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting all products", e);
                        productsLiveData.setValue(new ArrayList<>());
                    }
                });
        
        return productsLiveData;
    }
}
