package com.samyak.nooops.ui.checkout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.FragmentCheckoutBinding;
import com.samyak.nooops.model.Address;
import com.samyak.nooops.model.Order;
import com.samyak.nooops.util.PriceFormatter;
import com.samyak.nooops.util.SharedPreferencesHelper;
import com.samyak.nooops.util.ValidationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferencesHelper prefsHelper;
    
    private List<Map<String, Object>> cartItems;
    private Address shippingAddress;
    
    private double subtotal = 0.0;
    private double shipping = 5.99;
    private double tax = 0.0;
    private double discount = 0.0;
    private double total = 0.0;
    private String paymentMethod = "Credit Card";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        prefsHelper = new SharedPreferencesHelper(requireContext());
        
        // Initialize cart items array
        cartItems = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupListeners();
        setupCreditCardFormatting();
        loadCartItems();
        loadDefaultAddress();
    }

    private void setupListeners() {
        // Address change button
        binding.textChangeAddress.setOnClickListener(v -> {
            // TODO: Navigate to address selection screen
            Toast.makeText(requireContext(), "Address selection coming soon", Toast.LENGTH_SHORT).show();
        });
        
        // Payment method radio buttons
        binding.radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_credit_card) {
                binding.cardCreditCardDetails.setVisibility(View.VISIBLE);
                paymentMethod = "Credit Card";
            } else {
                binding.cardCreditCardDetails.setVisibility(View.GONE);
                
                if (checkedId == R.id.radio_paypal) {
                    paymentMethod = "PayPal";
                } else if (checkedId == R.id.radio_cash_on_delivery) {
                    paymentMethod = "Cash on Delivery";
                }
            }
        });
        
        // Place order button
        binding.buttonPlaceOrder.setOnClickListener(v -> {
            if (validateOrder()) {
                placeOrder();
            }
        });
    }

    private void setupCreditCardFormatting() {
        // Format credit card number with spaces
        binding.editCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove spacing char
                String text = s.toString().replaceAll("\\s", "");
                
                // Insert char where needed
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    if (i % 4 == 0 && i > 0) {
                        formatted.append(" ");
                    }
                    formatted.append(text.charAt(i));
                }
                
                if (!s.toString().equals(formatted.toString())) {
                    s.replace(0, s.length(), formatted.toString());
                }
            }
        });
        
        // Format expiry date with slash
        binding.editExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                
                if (text.length() == 2 && !text.contains("/")) {
                    s.append("/");
                }
            }
        });
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subtotal = 0.0;
                    cartItems.clear();
                    
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> item = document.getData();
                        double price = document.getDouble("price");
                        int quantity = document.getLong("quantity").intValue();
                        subtotal += price * quantity;
                        
                        cartItems.add(item);
                    }
                    
                    calculateOrderSummary();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error loading cart: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDefaultAddress() {
        String userId = auth.getCurrentUser().getUid();
        String defaultAddressId = prefsHelper.getDefaultAddressId();
        
        if (!TextUtils.isEmpty(defaultAddressId)) {
            db.collection("users").document(userId)
                    .collection("addresses")
                    .document(defaultAddressId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            shippingAddress = documentSnapshot.toObject(Address.class);
                            displayAddress();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error loading address: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Load first address if no default is set
            db.collection("users").document(userId)
                    .collection("addresses")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            shippingAddress = documentSnapshot.toObject(Address.class);
                            displayAddress();
                            
                            // Set as default
                            prefsHelper.setDefaultAddressId(documentSnapshot.getId());
                        } else {
                            // No addresses found
                            Toast.makeText(requireContext(), "Please add a shipping address", 
                                    Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to add address screen
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error loading address: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void displayAddress() {
        if (shippingAddress != null) {
            binding.textRecipientName.setText(shippingAddress.getFullName());
            binding.textPhone.setText(shippingAddress.getPhone());
            binding.textAddressLine.setText(shippingAddress.getAddressLine1() + 
                    (TextUtils.isEmpty(shippingAddress.getAddressLine2()) ? "" : 
                            ", " + shippingAddress.getAddressLine2()));
            binding.textCityStateZip.setText(shippingAddress.getCity() + ", " + 
                    shippingAddress.getState() + " " + shippingAddress.getZipCode());
        }
    }

    private void calculateOrderSummary() {
        // Calculate tax (e.g., 8% of subtotal)
        tax = Math.round(subtotal * 0.08 * 100.0) / 100.0;
        
        // Apply discount if applicable
        // For this example, let's apply a fixed discount
        if (subtotal > 100) {
            discount = 20.0;
        } else {
            discount = 0.0;
        }
        
        // Calculate total
        total = subtotal + shipping + tax - discount;
        
        // Display values
        binding.textSubtotalValue.setText(PriceFormatter.formatPrice(subtotal));
        binding.textShippingValue.setText(PriceFormatter.formatPrice(shipping));
        binding.textTaxValue.setText(PriceFormatter.formatPrice(tax));
        binding.textDiscountValue.setText("-" + PriceFormatter.formatPrice(discount));
        binding.textTotalValue.setText(PriceFormatter.formatPrice(total));
    }

    private boolean validateOrder() {
        boolean isValid = true;
        
        // Validate shipping address
        if (shippingAddress == null) {
            Toast.makeText(requireContext(), "Please add a shipping address", 
                    Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Validate cart items
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is empty", 
                    Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Validate payment information if credit card is selected
        if (binding.radioCreditCard.isChecked()) {
            String cardNumber = binding.editCardNumber.getText().toString().replaceAll("\\s", "");
            String expiryDate = binding.editExpiryDate.getText().toString();
            String cvv = binding.editCvv.getText().toString();
            String nameOnCard = binding.editNameOnCard.getText().toString();
            
            if (!ValidationUtils.isValidCreditCard(cardNumber)) {
                binding.layoutCardNumber.setError("Invalid card number");
                isValid = false;
            } else {
                binding.layoutCardNumber.setError(null);
            }
            
            if (!ValidationUtils.isValidExpiryDate(expiryDate)) {
                binding.layoutExpiryDate.setError("Invalid expiry date");
                isValid = false;
            } else {
                binding.layoutExpiryDate.setError(null);
            }
            
            if (!ValidationUtils.isValidCVV(cvv)) {
                binding.layoutCvv.setError("Invalid CVV");
                isValid = false;
            } else {
                binding.layoutCvv.setError(null);
            }
            
            if (TextUtils.isEmpty(nameOnCard)) {
                binding.layoutNameOnCard.setError("Required");
                isValid = false;
            } else {
                binding.layoutNameOnCard.setError(null);
            }
        }
        
        return isValid;
    }

    private void placeOrder() {
        showProgressBar();
        
        String userId = auth.getCurrentUser().getUid();
        String orderId = UUID.randomUUID().toString();
        
        // Create order object
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setItems(cartItems);
        order.setShippingAddress(shippingAddress);
        order.setSubtotal(subtotal);
        order.setShipping(shipping);
        order.setTax(tax);
        order.setDiscount(discount);
        order.setTotal(total);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("Pending");
        order.setCreatedAt(System.currentTimeMillis());
        
        // Save to Firestore
        db.collection("orders").document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    // Also save to user's orders collection
                    db.collection("users").document(userId)
                            .collection("orders")
                            .document(orderId)
                            .set(order)
                            .addOnSuccessListener(aVoid1 -> {
                                // Clear cart
                                clearCart(userId);
                            })
                            .addOnFailureListener(e -> {
                                hideProgressBar();
                                Toast.makeText(requireContext(), "Error saving order: " + e.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toast.makeText(requireContext(), "Error placing order: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void clearCart(String userId) {
        db.collection("users").document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Delete each cart item
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        document.getReference().delete();
                    }
                    
                    // Update cart count in preferences
                    prefsHelper.setCartCount(0);
                    
                    hideProgressBar();
                    
                    // Show success message and navigate
                    Toast.makeText(requireContext(), "Order placed successfully!", 
                            Toast.LENGTH_SHORT).show();
                    
                    // Navigate to order confirmation
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_checkout_to_orderConfirmation);
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toast.makeText(requireContext(), "Error clearing cart: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonPlaceOrder.setEnabled(false);
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
        binding.buttonPlaceOrder.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 