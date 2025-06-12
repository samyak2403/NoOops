package com.samyak.nooops.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.samyak.nooops.R;
import com.samyak.nooops.databinding.FragmentWishlistBinding;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        loadWishlistItems();
    }
    
    private void setupViews() {
        // Setup wishlist recycler view
        binding.wishlistRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Setup start shopping button
        binding.startShoppingButton.setOnClickListener(v -> {
            // TODO: Navigate to home screen
            Toast.makeText(requireContext(), "Start Shopping clicked", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadWishlistItems() {
        // TODO: Load wishlist items from repository
        // For demo purposes, show loading and then wishlist items or empty state
        
        showLoading();
        
        // Simulate network delay
        binding.getRoot().postDelayed(() -> {
            // TODO: Load wishlist items and set adapter
            
            // For now, we'll just show empty wishlist state
            showEmptyWishlist();
            
            // Uncomment to show wishlist with items
            // showWishlistWithItems();
        }, 1500);
    }
    
    private void showLoading() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.wishlistRecyclerView.setVisibility(View.GONE);
        binding.emptyWishlist.setVisibility(View.GONE);
    }
    
    private void showWishlistWithItems() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.wishlistRecyclerView.setVisibility(View.VISIBLE);
        binding.emptyWishlist.setVisibility(View.GONE);
    }
    
    private void showEmptyWishlist() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.wishlistRecyclerView.setVisibility(View.GONE);
        binding.emptyWishlist.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 