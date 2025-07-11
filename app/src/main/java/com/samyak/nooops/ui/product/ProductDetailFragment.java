package com.samyak.nooops.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.material.tabs.TabLayoutMediator;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.FragmentProductDetailBinding;
import com.samyak.nooops.model.CartItem;
import com.samyak.nooops.model.Product;
import com.samyak.nooops.repository.CartRepository;
import com.samyak.nooops.repository.ProductRepository;
import com.samyak.nooops.repository.WishlistRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment {

    private FragmentProductDetailBinding binding;
    private String productId;
    private boolean isInWishlist = false;
    private ProductImageAdapter imageAdapter;
    private int quantity = 1;
    private String selectedColor = "";
    private String selectedSize = "";
    private Product currentProduct;
    
    // Repositories
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private WishlistRepository wishlistRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize repositories
        productRepository = new ProductRepository();
        cartRepository = new CartRepository();
        wishlistRepository = new WishlistRepository();
        
        // Get product ID from arguments
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            if (productId != null) {
                loadProductDetails(productId);
                checkWishlistStatus(productId);
            }
        }
        
        // Setup quantity buttons
        setupQuantityButtons();
        
        // Setup click listeners
        setupClickListeners();
    }
    
    private void setupImageSlider(List<String> imageUrls) {
        // Initialize adapter with actual product images
        imageAdapter = new ProductImageAdapter(imageUrls);
        binding.viewpagerProductImages.setAdapter(imageAdapter);
        
        // Setup indicators
        new TabLayoutMediator(binding.tabLayoutIndicators, binding.viewpagerProductImages,
                (tab, position) -> {
                    // No text for tabs
                }).attach();
        
        // Set image click listener
        imageAdapter.setOnImageClickListener((position, imageUrl) -> {
            // Implement full-screen image view (future enhancement)
            Toast.makeText(getContext(), "Image " + (position + 1), Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupClickListeners() {
        // Add to cart button
        binding.buttonAddToCart.setOnClickListener(v -> addToCart());
        
        // Buy now button
        binding.buttonBuyNow.setOnClickListener(v -> buyNow());
        
        // Wishlist button
        binding.fabWishlist.setOnClickListener(v -> toggleWishlist());
        
        // See all reviews
        binding.textReviewsLink.setOnClickListener(v -> {
            // For now, just show a toast as the reviews screen isn't implemented yet
            Toast.makeText(getContext(), "Reviews feature coming soon", Toast.LENGTH_SHORT).show();
            
            // Uncomment this when the reviews navigation destination is added
            // if (productId != null) {
            //     Bundle args = new Bundle();
            //     args.putString("productId", productId);
            //     Navigation.findNavController(requireView()).navigate(R.id.navigation_reviews, args);
            // }
        });
        
        // Size guide
        binding.textSizeGuide.setOnClickListener(v -> {
            // Show size guide dialog (future enhancement)
            Toast.makeText(getContext(), "Size guide coming soon", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupQuantityButtons() {
        // Initialize quantity
        binding.textQuantity.setText(String.valueOf(quantity));
        
        // Decrease button
        binding.buttonDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.textQuantity.setText(String.valueOf(quantity));
            }
        });
        
        // Increase button
        binding.buttonIncrease.setOnClickListener(v -> {
            if (currentProduct != null && quantity < currentProduct.getStockQuantity()) {
                quantity++;
                binding.textQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Maximum available quantity reached", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadProductDetails(String productId) {
        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);
        
        productRepository.getProductDetails(productId).observe(getViewLifecycleOwner(), product -> {
            binding.progressBar.setVisibility(View.GONE);
            
            if (product != null) {
                currentProduct = product;
                
                // Set product details
                binding.textProductBrand.setText(product.getBrand());
                binding.textProductName.setText(product.getName());
                binding.ratingProduct.setRating(product.getRating());
                binding.textRatingValue.setText(String.valueOf(product.getRating()));
                binding.textRatingCount.setText("(" + product.getRatingCount() + " ratings)");
                
                // Set price and discount
                binding.textProductPrice.setText("₹" + product.getPrice());
                if (product.getOriginalPrice() > 0 && product.getOriginalPrice() > product.getPrice()) {
                    binding.textProductOriginalPrice.setVisibility(View.VISIBLE);
                    binding.textProductOriginalPrice.setText("₹" + product.getOriginalPrice());
                    
                    int discountPercentage = product.calculateDiscountPercentage();
                    binding.textDiscount.setVisibility(View.VISIBLE);
                    binding.textDiscount.setText(discountPercentage + "% OFF");
                } else {
                    binding.textProductOriginalPrice.setVisibility(View.GONE);
                    binding.textDiscount.setVisibility(View.GONE);
                }
                
                // Set delivery info
                binding.textDeliveryInfo.setText("Free Delivery by Tomorrow");
                binding.textReturnInfo.setText("7 Days Replacement");
                
                // Set description
                binding.textDescription.setText(product.getDescription());
                
                // Setup image slider with actual images
                if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                    setupImageSlider(product.getImageUrls());
                } else {
                    // Set default images if none available
                    List<String> defaultImages = new ArrayList<>();
                    defaultImages.add("https://via.placeholder.com/400");
                    setupImageSlider(defaultImages);
                }
                
                // Setup color options
                setupColorOptions(product.getColors());
                
                // Setup size options
                setupSizeOptions(product.getSizes());
                
                // Enable/disable buy buttons based on stock
                boolean inStock = product.isInStock() && product.getStockQuantity() > 0;
                binding.buttonAddToCart.setEnabled(inStock);
                binding.buttonBuyNow.setEnabled(inStock);
                
                if (!inStock) {
                    binding.textOutOfStock.setVisibility(View.VISIBLE);
                    binding.buttonAddToCart.setText("Out of Stock");
                    binding.buttonBuyNow.setText("Notify Me");
                } else {
                    binding.textOutOfStock.setVisibility(View.GONE);
                    binding.buttonAddToCart.setText("Add to Cart");
                    binding.buttonBuyNow.setText("Buy Now");
                }
            } else {
                // Handle product not found
                Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }
    
    private void setupColorOptions(List<String> colors) {
        List<ColorOption> colorOptions = new ArrayList<>();
        
        if (colors != null && !colors.isEmpty()) {
            for (String color : colors) {
                // Convert color name to color code (this is simplified)
                String colorCode = getColorCodeFromName(color);
                colorOptions.add(new ColorOption(color, colorCode));
            }
        } else {
            // Default colors if none specified
            colorOptions.add(new ColorOption("Black", "#000000"));
            colorOptions.add(new ColorOption("White", "#FFFFFF"));
        }
        
        ColorAdapter colorAdapter = new ColorAdapter(colorOptions);
        colorAdapter.setOnColorSelectedListener((position, colorName, colorCode) -> {
            selectedColor = colorName;
            binding.textSelectedColor.setText(colorName);
        });
        binding.recyclerColors.setAdapter(colorAdapter);
        
        // Set initial selected color
        if (!colorOptions.isEmpty()) {
            selectedColor = colorOptions.get(0).getName();
            binding.textSelectedColor.setText(selectedColor);
        }
    }
    
    private String getColorCodeFromName(String colorName) {
        // This is a simplified version - in a real app, you might have a mapping
        switch (colorName.toLowerCase()) {
            case "black": return "#000000";
            case "white": return "#FFFFFF";
            case "red": return "#FF0000";
            case "green": return "#00FF00";
            case "blue": return "#0000FF";
            default: return "#CCCCCC"; // Default gray
        }
    }
    
    private void setupSizeOptions(List<String> sizes) {
        List<String> sizeOptions = new ArrayList<>();
        
        if (sizes != null && !sizes.isEmpty()) {
            sizeOptions.addAll(sizes);
        } else {
            // Default sizes if none specified
            sizeOptions.add("S");
            sizeOptions.add("M");
            sizeOptions.add("L");
            sizeOptions.add("XL");
        }
        
        SizeAdapter sizeAdapter = new SizeAdapter(sizeOptions);
        sizeAdapter.setOnSizeSelectedListener((position, size) -> {
            selectedSize = size;
            binding.textSelectedSize.setText(size);
        });
        binding.recyclerSizes.setAdapter(sizeAdapter);
        
        // Set initial selected size
        if (!sizeOptions.isEmpty()) {
            selectedSize = sizeOptions.get(0);
            binding.textSelectedSize.setText(selectedSize);
        }
    }
    
    private void checkWishlistStatus(String productId) {
        if (!productRepository.isUserLoggedIn()) {
            return;
        }
        
        wishlistRepository.isInWishlist(productId).observe(getViewLifecycleOwner(), isInWishlist -> {
            this.isInWishlist = isInWishlist != null && isInWishlist;
            updateWishlistIcon();
        });
    }
    
    private void updateWishlistIcon() {
        if (isInWishlist) {
            binding.fabWishlist.setImageResource(R.drawable.ic_wishlist);
        } else {
            binding.fabWishlist.setImageResource(R.drawable.ic_wishlist_outline);
        }
    }
    
    private void toggleWishlist() {
        if (!productRepository.isUserLoggedIn()) {
            Toast.makeText(getContext(), "Please login to add items to wishlist", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (currentProduct == null) {
            return;
        }
        
        if (isInWishlist) {
            // Remove from wishlist
            wishlistRepository.removeFromWishlist(productId).observe(getViewLifecycleOwner(), success -> {
                if (success != null && success) {
                    isInWishlist = false;
                    updateWishlistIcon();
                    Toast.makeText(getContext(), "Removed from wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to remove from wishlist", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Add to wishlist
            wishlistRepository.addToWishlist(currentProduct).observe(getViewLifecycleOwner(), success -> {
                if (success != null && success) {
                    isInWishlist = true;
                    updateWishlistIcon();
                    Toast.makeText(getContext(), "Added to wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add to wishlist", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void addToCart() {
        if (!productRepository.isUserLoggedIn()) {
            Toast.makeText(getContext(), "Please login to add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate selection and product
        if (currentProduct == null) {
            return;
        }
        
        if (selectedSize.isEmpty()) {
            Toast.makeText(getContext(), "Please select a size", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedColor.isEmpty()) {
            Toast.makeText(getContext(), "Please select a color", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Add to cart using repository
        cartRepository.addToCart(currentProduct, quantity).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                String message = String.format("Added %d item(s) to cart: %s, Size: %s, Color: %s", 
                        quantity, currentProduct.getName(), selectedSize, selectedColor);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void buyNow() {
        if (!productRepository.isUserLoggedIn()) {
            Toast.makeText(getContext(), "Please login to proceed", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate selection and product
        if (currentProduct == null) {
            return;
        }
        
        if (selectedSize.isEmpty()) {
            Toast.makeText(getContext(), "Please select a size", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedColor.isEmpty()) {
            Toast.makeText(getContext(), "Please select a color", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // First add to cart, then navigate to checkout
        cartRepository.addToCart(currentProduct, quantity).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                // Show success message for now until checkout screen is implemented
                Toast.makeText(getContext(), "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
                
                // Uncomment this when the checkout navigation destination is added
                 Bundle args = new Bundle();
                 args.putBoolean("directCheckout", true);
                 Navigation.findNavController(requireView()).navigate(R.id.navigation_checkout, args);
            } else {
                Toast.makeText(getContext(), "Failed to process. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
