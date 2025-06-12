package com.samyak.nooops.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.ItemCartBinding;
import com.samyak.nooops.model.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItem> cartItems;
    private final OnCartItemActionListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public interface OnCartItemActionListener {
        void onCartItemClick(CartItem cartItem, int position);
        void onRemoveItem(CartItem cartItem, int position);
        void onIncreaseQuantity(CartItem cartItem, int position);
        void onDecreaseQuantity(CartItem cartItem, int position);
    }

    public CartAdapter(Context context, OnCartItemActionListener listener) {
        this.context = context;
        this.cartItems = new ArrayList<>();
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems.clear();
        if (cartItems != null) {
            this.cartItems.addAll(cartItems);
        }
        notifyDataSetChanged();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateItem(CartItem cartItem, int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.set(position, cartItem);
            notifyItemChanged(position);
        }
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotal();
        }
        return total;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;

        public CartViewHolder(@NonNull ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final CartItem cartItem, final int position) {
            // Set product name
            binding.productName.setText(cartItem.getProductName());
            
            // Set price
            binding.productPrice.setText(currencyFormatter.format(cartItem.getPrice()));
            
            // Set quantity
            binding.quantity.setText(String.valueOf(cartItem.getQuantity()));
            
            // Set total
            binding.itemTotal.setText(context.getString(
                    R.string.total_price, currencyFormatter.format(cartItem.getTotal())));
            
            // Load product image
            if (cartItem.getProductImage() != null) {
                Glide.with(context)
                        .load(cartItem.getProductImage())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(binding.productImage);
            } else {
                binding.productImage.setImageResource(R.drawable.placeholder_image);
            }
            
            // Set click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCartItemClick(cartItem, position);
                }
            });
            
            binding.removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(cartItem, position);
                }
            });
            
            binding.increaseQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIncreaseQuantity(cartItem, position);
                }
            });
            
            binding.decreaseQuantity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDecreaseQuantity(cartItem, position);
                }
            });
        }
    }
} 