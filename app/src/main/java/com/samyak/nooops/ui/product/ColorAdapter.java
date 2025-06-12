package com.samyak.nooops.ui.product;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samyak.nooops.R;

import java.util.List;

/**
 * Adapter for displaying color options in the RecyclerView
 */
public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private final List<ColorOption> colors;
    private OnColorSelectedListener listener;
    private int selectedPosition = 0;

    public ColorAdapter(List<ColorOption> colors) {
        this.colors = colors;
        if (!colors.isEmpty()) {
            colors.get(0).setSelected(true);
        }
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_color_option, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorOption color = colors.get(position);
        
        try {
            holder.colorView.setBackgroundColor(Color.parseColor(color.getColorCode()));
        } catch (IllegalArgumentException e) {
            // Fallback color if parsing fails
            holder.colorView.setBackgroundColor(Color.GRAY);
        }
        
        // Show check mark if selected
        holder.selectedIcon.setVisibility(color.isSelected() ? View.VISIBLE : View.GONE);
        
        holder.itemView.setOnClickListener(v -> {
            // Update selection
            if (selectedPosition != holder.getAdapterPosition()) {
                // Deselect previous
                colors.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);
                
                // Select new
                selectedPosition = holder.getAdapterPosition();
                colors.get(selectedPosition).setSelected(true);
                notifyItemChanged(selectedPosition);
                
                // Notify listener
                if (listener != null) {
                    ColorOption selectedColor = colors.get(selectedPosition);
                    listener.onColorSelected(selectedPosition, 
                            selectedColor.getName(), 
                            selectedColor.getColorCode());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    /**
     * Update color list
     * @param newColors New list of color options
     */
    public void updateColors(List<ColorOption> newColors) {
        this.colors.clear();
        
        if (newColors != null && !newColors.isEmpty()) {
            this.colors.addAll(newColors);
            this.colors.get(0).setSelected(true);
            this.selectedPosition = 0;
        }
        
        notifyDataSetChanged();
    }

    /**
     * Set color selection listener
     * @param listener OnColorSelectedListener
     */
    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Interface for color selection events
     */
    public interface OnColorSelectedListener {
        void onColorSelected(int position, String colorName, String colorCode);
    }

    /**
     * ViewHolder for color options
     */
    static class ColorViewHolder extends RecyclerView.ViewHolder {
        View colorView;
        ImageView selectedIcon;

        ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.view_color);
            selectedIcon = itemView.findViewById(R.id.image_selected);
        }
    }
} 