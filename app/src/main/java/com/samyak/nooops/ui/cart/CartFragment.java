package com.samyak.nooops.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.samyak.nooops.R;
import com.samyak.nooops.databinding.FragmentCartBinding;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        loadCartItems();
    }
    
    private void setupViews() {
        // Setup cart recycler view
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Setup checkout button
        binding.checkoutButton.setOnClickListener(v -> {
            // TODO: Navigate to checkout screen
            Toast.makeText(requireContext(), "Proceed to Checkout clicked", Toast.LENGTH_SHORT).show();
        });
        
        // Setup start shopping button
        binding.startShoppingButton.setOnClickListener(v -> {
            // TODO: Navigate to home screen
            Toast.makeText(requireContext(), "Start Shopping clicked", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadCartItems() {
        // TODO: Load cart items from repository
        // For demo purposes, show loading and then cart items or empty state
        
        showLoading();
        
        // Simulate network delay
        binding.getRoot().postDelayed(() -> {
            // TODO: Load cart items and set adapter
            
            // For now, we'll just show empty cart state
            showEmptyCart();
            
            // Uncomment to show cart with items
            // showCartWithItems();
        }, 1500);
    }
    
    private void showLoading() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.cartRecyclerView.setVisibility(View.GONE);
        binding.cartSummaryCard.setVisibility(View.GONE);
        binding.emptyCart.setVisibility(View.GONE);
    }
    
    private void showCartWithItems() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.cartRecyclerView.setVisibility(View.VISIBLE);
        binding.cartSummaryCard.setVisibility(View.VISIBLE);
        binding.emptyCart.setVisibility(View.GONE);
        
        // TODO: Update cart summary
        updateCartSummary();
    }
    
    private void showEmptyCart() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.cartRecyclerView.setVisibility(View.GONE);
        binding.cartSummaryCard.setVisibility(View.GONE);
        binding.emptyCart.setVisibility(View.VISIBLE);
    }
    
    private void updateCartSummary() {
        // TODO: Calculate and set cart summary values
        binding.subtotal.setText("$199.98");
        binding.shipping.setText("$5.99");
        binding.tax.setText("$19.99");
        binding.total.setText("$225.96");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 