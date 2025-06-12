package com.samyak.nooops.ui.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.samyak.nooops.R;

import java.util.List;

/**
 * Adapter for displaying product images in the ViewPager
 */
public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder> {

    private final List<String> imageUrls;
    private OnImageClickListener listener;

    public ProductImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imageView);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(position, imageUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls == null ? 0 : imageUrls.size();
    }

    /**
     * Update image list
     * @param newImageUrls New list of image URLs
     */
    public void updateImages(List<String> newImageUrls) {
        this.imageUrls.clear();
        if (newImageUrls != null) {
            this.imageUrls.addAll(newImageUrls);
        }
        notifyDataSetChanged();
    }

    /**
     * Set image click listener
     * @param listener OnImageClickListener
     */
    public void setOnImageClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    /**
     * Interface for image click events
     */
    public interface OnImageClickListener {
        void onImageClick(int position, String imageUrl);
    }

    /**
     * ViewHolder for product images
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
        }
    }
} 