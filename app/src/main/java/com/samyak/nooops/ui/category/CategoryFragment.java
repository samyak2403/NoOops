package com.samyak.nooops.ui.category;

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
import com.samyak.nooops.databinding.FragmentCategoryBinding;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        loadCategories();
    }
    
    private void setupViews() {
        // Setup search bar click listener
        binding.searchCard.setOnClickListener(v -> {
            // TODO: Navigate to search screen
            Toast.makeText(requireContext(), "Search clicked", Toast.LENGTH_SHORT).show();
        });
        
        // Setup category recycler view
        binding.categoriesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Setup retry button
        binding.retryButton.setOnClickListener(v -> {
            showLoading();
            loadCategories();
        });
    }
    
    private void loadCategories() {
        // TODO: Load categories from repository
        // For demo purposes, show loading and then categories
        
        showLoading();
        
        // Simulate network delay
        binding.getRoot().postDelayed(() -> {
            // TODO: Load categories and set adapter
            
            // For now, we'll just show a success state
            showContent();
        }, 1500);
    }
    
    private void showLoading() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.categoriesRecyclerView.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);
    }
    
    private void showContent() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.categoriesRecyclerView.setVisibility(View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);
    }
    
    private void showEmptyState() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.categoriesRecyclerView.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 