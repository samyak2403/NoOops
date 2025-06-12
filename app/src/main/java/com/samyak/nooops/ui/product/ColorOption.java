package com.samyak.nooops.ui.product;

/**
 * Model class representing a color option
 */
public class ColorOption {
    private String name;
    private String colorCode;
    private boolean selected;

    public ColorOption(String name, String colorCode) {
        this.name = name;
        this.colorCode = colorCode;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
} 