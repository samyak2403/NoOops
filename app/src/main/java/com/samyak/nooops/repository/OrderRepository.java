package com.samyak.nooops.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samyak.nooops.model.CartItem;
import com.samyak.nooops.model.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderRepository extends FirebaseRepository {
    private static final String TAG = "OrderRepository";
    private static final String ORDERS_COLLECTION = "orders";
    
    private CartRepository cartRepository;
    
    public OrderRepository() {
        super();
        cartRepository = new CartRepository();
    }
    
    // Place a new order
    public LiveData<Order> placeOrder(Order order) {
        MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            orderLiveData.setValue(null);
            return orderLiveData;
        }
        
        // Set the user ID and order date
        order.setUserId(userId);
        order.setOrderDate(new Date());
        
        // If order ID is not set, generate one
        if (order.getId() == null) {
            order.setId(UUID.randomUUID().toString());
        }
        
        // Save the order to Firestore
        firestore.collection(ORDERS_COLLECTION)
                .document(order.getId())
                .set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Clear the cart after successful order
                        cartRepository.clearCart().observeForever(success -> {
                            if (!success) {
                                Log.e(TAG, "Failed to clear cart after placing order");
                            }
                        });
                        
                        orderLiveData.setValue(order);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error placing order", e);
                        orderLiveData.setValue(null);
                    }
                });
        
        return orderLiveData;
    }
    
    // Get all orders for the current user
    public LiveData<List<Order>> getUserOrders() {
        MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            ordersLiveData.setValue(new ArrayList<>());
            return ordersLiveData;
        }
        
        firestore.collection(ORDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Order order = document.toObject(Order.class);
                            orders.add(order);
                        }
                        ordersLiveData.setValue(orders);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting user orders", e);
                        ordersLiveData.setValue(new ArrayList<>());
                    }
                });
        
        return ordersLiveData;
    }
    
    // Get order details
    public LiveData<Order> getOrderDetails(String orderId) {
        MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
        
        firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Order order = documentSnapshot.toObject(Order.class);
                        orderLiveData.setValue(order);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting order details", e);
                        orderLiveData.setValue(null);
                    }
                });
        
        return orderLiveData;
    }
    
    // Cancel an order
    public LiveData<Boolean> cancelOrder(String orderId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        // Check if the order belongs to the current user
        DocumentReference orderRef = firestore.collection(ORDERS_COLLECTION).document(orderId);
        
        orderRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Order order = documentSnapshot.toObject(Order.class);
                        
                        if (order == null || !order.getUserId().equals(userId)) {
                            result.setValue(false);
                            return;
                        }
                        
                        // Check if the order can be cancelled (e.g., not already shipped)
                        if (Order.STATUS_SHIPPED.equals(order.getStatus()) ||
                                Order.STATUS_DELIVERED.equals(order.getStatus())) {
                            result.setValue(false);
                            return;
                        }
                        
                        // Update the order status to cancelled
                        orderRef.update("status", Order.STATUS_CANCELLED)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        result.setValue(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error cancelling order", e);
                                        result.setValue(false);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting order for cancellation", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    // Create a new order from cart items
    public LiveData<Order> createOrderFromCart() {
        MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            orderLiveData.setValue(null);
            return orderLiveData;
        }
        
        // Get cart items
        cartRepository.getCartItems().observeForever(cartItems -> {
            if (cartItems == null || cartItems.isEmpty()) {
                orderLiveData.setValue(null);
                return;
            }
            
            // Create a new order
            String orderId = UUID.randomUUID().toString();
            Order order = new Order(orderId, userId);
            
            // Add items to the order
            for (CartItem item : cartItems) {
                order.addItem(item);
            }
            
            // Set initial taxes and shipping fee (these can be updated later)
            double subtotal = order.getSubtotal();
            order.setTaxes(subtotal * 0.10); // 10% tax
            order.setShippingFee(subtotal > 500 ? 0 : 50); // Free shipping above ₹500
            
            orderLiveData.setValue(order);
        });
        
        return orderLiveData;
    }
}
