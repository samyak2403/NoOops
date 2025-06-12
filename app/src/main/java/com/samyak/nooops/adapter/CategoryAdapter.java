package com.samyak.nooops.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.ItemCategoryBinding;
import com.samyak.nooops.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = new ArrayList<>();
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }
        notifyDataSetChanged();
    }

    public void addCategories(List<Category> categories) {
        if (categories != null) {
            int startPosition = this.categories.size();
            this.categories.addAll(categories);
            notifyItemRangeInserted(startPosition, categories.size());
        }
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position), position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Category category, final int position) {
            // Set category name
            binding.categoryName.setText(category.getName());
            
            // Load category image
            if (category.getImageUrl() != null) {
                Glide.with(context)
                        .load(category.getImageUrl())
                        .placeholder(R.drawable.ic_category)
                        .error(R.drawable.ic_category)
                        .into(binding.categoryImage);
            } else {
                binding.categoryImage.setImageResource(R.drawable.ic_category);
            }
            
            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category, position);
                }
            });
        }
    }
} 