package com.samyak.nooops.ui.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samyak.nooops.R;

import java.util.List;

/**
 * Adapter for displaying size options in the RecyclerView
 */
public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    private final List<String> sizes;
    private OnSizeSelectedListener listener;
    private int selectedPosition = 0;

    public SizeAdapter(List<String> sizes) {
        this.sizes = sizes;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_size_option, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String size = sizes.get(position);
        holder.sizeText.setText(size);
        
        // Highlight selected size
        holder.sizeText.setSelected(position == selectedPosition);
        
        if (position == selectedPosition) {
            holder.sizeText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        } else {
            holder.sizeText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != holder.getAdapterPosition()) {
                // Update selection
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                
                // Redraw the changed items
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                
                // Notify listener
                if (listener != null) {
                    listener.onSizeSelected(selectedPosition, sizes.get(selectedPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    /**
     * Update size list
     * @param newSizes New list of sizes
     */
    public void updateSizes(List<String> newSizes) {
        this.sizes.clear();
        
        if (newSizes != null && !newSizes.isEmpty()) {
            this.sizes.addAll(newSizes);
            this.selectedPosition = 0;
        }
        
        notifyDataSetChanged();
    }

    /**
     * Set size selection listener
     * @param listener OnSizeSelectedListener
     */
    public void setOnSizeSelectedListener(OnSizeSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Interface for size selection events
     */
    public interface OnSizeSelectedListener {
        void onSizeSelected(int position, String size);
    }

    /**
     * ViewHolder for size options
     */
    static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView sizeText;

        SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            sizeText = (TextView) itemView;
        }
    }
} 