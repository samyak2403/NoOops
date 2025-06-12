package com.samyak.nooops.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.samyak.nooops.R;
import com.samyak.nooops.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        loadData();
    }
    
    private void setupViews() {
        // Setup search bar click listener
        binding.searchCard.setOnClickListener(v -> {
            // TODO: Navigate to search screen
            Toast.makeText(requireContext(), "Search clicked", Toast.LENGTH_SHORT).show();
        });
        
        // Setup category recycler view
        binding.categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        
        // Setup deals recycler view
        binding.dealsRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        
        // Setup new arrivals recycler view
        binding.newArrivalsRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        
        // Setup view all buttons click listeners
        binding.viewAllCategories.setOnClickListener(v -> {
            // TODO: Navigate to all categories screen
            Toast.makeText(requireContext(), "View all categories clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.viewAllDeals.setOnClickListener(v -> {
            // TODO: Navigate to all deals screen
            Toast.makeText(requireContext(), "View all deals clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.viewAllArrivals.setOnClickListener(v -> {
            // TODO: Navigate to all new arrivals screen
            Toast.makeText(requireContext(), "View all new arrivals clicked", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadData() {
        // TODO: Load data from repository
        // For now, we'll just show mock data
        
        // Setup image slider
        setupImageSlider();
        
        // Load categories
        loadCategories();
        
        // Load deals
        loadDeals();
        
        // Load new arrivals
        loadNewArrivals();
    }
    
    private void setupImageSlider() {
        // TODO: Implement image slider with ViewPager2
    }
    
    private void loadCategories() {
        // TODO: Load categories from repository and set adapter
    }
    
    private void loadDeals() {
        // TODO: Load deals from repository and set adapter
    }
    
    private void loadNewArrivals() {
        // TODO: Load new arrivals from repository and set adapter
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 