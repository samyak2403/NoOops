package com.samyak.nooops.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samyak.nooops.model.CartItem;
import com.samyak.nooops.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartRepository extends FirebaseRepository {
    private static final String TAG = "CartRepository";
    private static final String CART_COLLECTION = "carts";
    
    public CartRepository() {
        super();
    }
    
    // Get cart items for the current user
    public LiveData<List<CartItem>> getCartItems() {
        MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            cartItemsLiveData.setValue(new ArrayList<>());
            return cartItemsLiveData;
        }
        
        firestore.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<CartItem> cartItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            CartItem cartItem = document.toObject(CartItem.class);
                            cartItems.add(cartItem);
                        }
                        cartItemsLiveData.setValue(cartItems);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting cart items", e);
                        cartItemsLiveData.setValue(new ArrayList<>());
                    }
                });
        
        return cartItemsLiveData;
    }
    
    // Add item to cart
    public LiveData<Boolean> addToCart(Product product, int quantity) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        // Check if product already exists in cart
        CollectionReference cartItemsRef = firestore.collection(CART_COLLECTION)
                .document(userId)
                .collection("items");
        
        cartItemsRef.document(product.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Product exists in cart, update quantity
                            CartItem existingItem = documentSnapshot.toObject(CartItem.class);
                            int newQuantity = existingItem.getQuantity() + quantity;
                            
                            cartItemsRef.document(product.getId())
                                    .update("quantity", newQuantity)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            result.setValue(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error updating cart item quantity", e);
                                            result.setValue(false);
                                        }
                                    });
                        } else {
                            // Product doesn't exist in cart, add it
                            CartItem cartItem = CartItem.fromProduct(product, quantity);
                            
                            cartItemsRef.document(product.getId())
                                    .set(cartItem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            result.setValue(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error adding item to cart", e);
                                            result.setValue(false);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error checking cart item", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    // Update cart item quantity
    public LiveData<Boolean> updateCartItemQuantity(String productId, int quantity) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        DocumentReference cartItemRef = firestore.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .document(productId);
        
        if (quantity <= 0) {
            // Remove item from cart if quantity is 0 or negative
            cartItemRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            result.setValue(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error removing item from cart", e);
                            result.setValue(false);
                        }
                    });
        } else {
            // Update quantity
            cartItemRef.update("quantity", quantity)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            result.setValue(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error updating cart item quantity", e);
                            result.setValue(false);
                        }
                    });
        }
        
        return result;
    }
    
    // Remove item from cart
    public LiveData<Boolean> removeFromCart(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        firestore.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
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
                        Log.e(TAG, "Error removing item from cart", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    // Clear cart
    public LiveData<Boolean> clearCart() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        // Get all cart items
        firestore.collection(CART_COLLECTION)
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // If there are no items, return success
                        if (queryDocumentSnapshots.isEmpty()) {
                            result.setValue(true);
                            return;
                        }
                        
                        // Delete each item
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
                                            Log.e(TAG, "Error deleting cart item", e);
                                            result.setValue(false);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting cart items for deletion", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    // Get cart total
    public LiveData<Double> getCartTotal() {
        MutableLiveData<Double> totalLiveData = new MutableLiveData<>();
        
        getCartItems().observeForever(cartItems -> {
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getTotalPrice();
            }
            totalLiveData.setValue(total);
        });
        
        return totalLiveData;
    }
}
