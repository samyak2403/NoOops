package com.samyak.nooops.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.ItemProductHorizontalBinding;
import com.samyak.nooops.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> products;
    private final OnProductClickListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public interface OnProductClickListener {
        void onProductClick(Product product, int position);
        void onWishlistClick(Product product, int position);
    }

    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.products = new ArrayList<>();
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        if (products != null) {
            this.products.addAll(products);
        }
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> products) {
        if (products != null) {
            int startPosition = this.products.size();
            this.products.addAll(products);
            notifyItemRangeInserted(startPosition, products.size());
        }
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductHorizontalBinding binding = ItemProductHorizontalBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position), position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductHorizontalBinding binding;

        public ProductViewHolder(@NonNull ItemProductHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Product product, final int position) {
            // Set product name
            binding.productName.setText(product.getName());
            
            // Set price and original price
            binding.productPrice.setText(currencyFormatter.format(product.getPrice()));
            
            if (product.getOriginalPrice() > product.getPrice()) {
                binding.productOriginalPrice.setVisibility(View.VISIBLE);
                binding.productOriginalPrice.setText(currencyFormatter.format(product.getOriginalPrice()));
                
                // Show discount badge
                if (product.getDiscountPercentage() > 0) {
                    binding.discountBadge.setVisibility(View.VISIBLE);
                    binding.discountBadge.setText(context.getString(R.string.discount_percent, product.getDiscountPercentage()));
                } else {
                    binding.discountBadge.setVisibility(View.GONE);
                }
            } else {
                binding.productOriginalPrice.setVisibility(View.GONE);
                binding.discountBadge.setVisibility(View.GONE);
            }
            
            // Set rating
            if (product.getRating() > 0) {
                binding.productRating.setRating((float) product.getRating());
                binding.ratingCount.setText(String.format("(%d)", product.getRatingCount()));
            } else {
                binding.productRating.setRating(0);
                binding.ratingCount.setText("(0)");
            }
            
            // Load product image
            if (product.getImageUrl() != null) {
                Glide.with(context)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(binding.productImage);
            } else {
                binding.productImage.setImageResource(R.drawable.placeholder_image);
            }
            
            // Set click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product, position);
                }
            });
            
            binding.wishlistButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWishlistClick(product, position);
                }
            });
        }
    }
} 