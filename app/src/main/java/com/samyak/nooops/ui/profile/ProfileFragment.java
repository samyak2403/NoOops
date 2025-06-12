package com.samyak.nooops.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.samyak.nooops.R;
import com.samyak.nooops.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        loadUserProfile();
    }
    
    private void setupViews() {
        // Setup click listeners for all menu items
        binding.editProfileButton.setOnClickListener(v -> {
            // TODO: Navigate to edit profile screen
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.myOrdersCard.setOnClickListener(v -> {
            // TODO: Navigate to my orders screen
            Toast.makeText(requireContext(), "My Orders clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.myAddressesCard.setOnClickListener(v -> {
            // TODO: Navigate to my addresses screen
            Toast.makeText(requireContext(), "My Addresses clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.paymentMethodsCard.setOnClickListener(v -> {
            // TODO: Navigate to payment methods screen
            Toast.makeText(requireContext(), "Payment Methods clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.notificationsCard.setOnClickListener(v -> {
            // TODO: Navigate to notifications screen
            Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.settingsCard.setOnClickListener(v -> {
            // TODO: Navigate to settings screen
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.helpSupportCard.setOnClickListener(v -> {
            // TODO: Navigate to help & support screen
            Toast.makeText(requireContext(), "Help & Support clicked", Toast.LENGTH_SHORT).show();
        });
        
        binding.logoutButton.setOnClickListener(v -> {
            // TODO: Implement logout
            Toast.makeText(requireContext(), "Logout clicked", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadUserProfile() {
        // TODO: Load user profile from repository
        // For now, we'll just show mock data
        
        binding.profileName.setText("John Doe");
        binding.profileEmail.setText("john.doe@example.com");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 