package com.samyak.nooops.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samyak.nooops.model.Product;
import com.samyak.nooops.model.WishlistItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishlistRepository extends FirebaseRepository {
    private static final String TAG = "WishlistRepository";
    private static final String USERS_COLLECTION = "users";
    private static final String WISHLIST_COLLECTION = "wishlist";
    
    public WishlistRepository() {
        super();
    }
    
    // Get all wishlist items for the current user
    public LiveData<List<WishlistItem>> getWishlistItems() {
        MutableLiveData<List<WishlistItem>> wishlistLiveData = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            wishlistLiveData.setValue(new ArrayList<>());
            return wishlistLiveData;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WISHLIST_COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<WishlistItem> wishlistItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            WishlistItem item = document.toObject(WishlistItem.class);
                            wishlistItems.add(item);
                        }
                        wishlistLiveData.setValue(wishlistItems);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting wishlist items", e);
                        wishlistLiveData.setValue(new ArrayList<>());
                    }
                });
        
        return wishlistLiveData;
    }
    
    // Check if a product is in the wishlist
    public LiveData<Boolean> isInWishlist(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WISHLIST_COLLECTION)
                .document(productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        result.setValue(documentSnapshot.exists());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error checking wishlist status", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    // Add a product to the wishlist
    public LiveData<Boolean> addToWishlist(Product product) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        // Create wishlist item
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProductId(product.getId());
        wishlistItem.setProductName(product.getName());
        wishlistItem.setProductImage(product.getPrimaryImageUrl());
        wishlistItem.setPrice(product.getPrice());
        wishlistItem.setAddedAt(System.currentTimeMillis());
        
        // Add to Firestore
        DocumentReference wishlistRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WISHLIST_COLLECTION)
                .document(product.getId());
        
        wishlistRef.set(wishlistItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        result.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding to wishlist", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    // Remove a product from the wishlist
    public LiveData<Boolean> removeFromWishlist(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WISHLIST_COLLECTION)
                .document(productId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        result.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error removing from wishlist", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    // Clear the entire wishlist
    public LiveData<Boolean> clearWishlist() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(WISHLIST_COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            result.setValue(true);
                            return;
                        }
                        
                        // Delete each document
                        int totalItems = queryDocumentSnapshots.size();
                        final int[] deletedCount = {0};
                        
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            deletedCount[0]++;
                                            if (deletedCount[0] == totalItems) {
                                                result.setValue(true);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error deleting wishlist item", e);
                                            result.setValue(false);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting wishlist items for deletion", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
} 