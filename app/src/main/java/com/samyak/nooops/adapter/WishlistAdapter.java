package com.samyak.nooops.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.ItemWishlistBinding;
import com.samyak.nooops.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final Context context;
    private final List<Product> wishlistItems;
    private final OnWishlistItemActionListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public interface OnWishlistItemActionListener {
        void onWishlistItemClick(Product product, int position);
        void onRemoveFromWishlist(Product product, int position);
        void onMoveToCart(Product product, int position);
    }

    public WishlistAdapter(Context context, OnWishlistItemActionListener listener) {
        this.context = context;
        this.wishlistItems = new ArrayList<>();
        this.listener = listener;
    }

    public void setWishlistItems(List<Product> wishlistItems) {
        this.wishlistItems.clear();
        if (wishlistItems != null) {
            this.wishlistItems.addAll(wishlistItems);
        }
        notifyDataSetChanged();
    }

    public List<Product> getWishlistItems() {
        return new ArrayList<>(wishlistItems);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < wishlistItems.size()) {
            wishlistItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWishlistBinding binding = ItemWishlistBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new WishlistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        holder.bind(wishlistItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    class WishlistViewHolder extends RecyclerView.ViewHolder {
        private final ItemWishlistBinding binding;

        public WishlistViewHolder(@NonNull ItemWishlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Product product, final int position) {
            // Set product name
            binding.productName.setText(product.getName());
            
            // Set price
            binding.productPrice.setText(currencyFormatter.format(product.getPrice()));
            
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
                    listener.onWishlistItemClick(product, position);
                }
            });
            
            binding.removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveFromWishlist(product, position);
                }
            });
            
            binding.moveToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMoveToCart(product, position);
                }
            });
        }
    }
} 