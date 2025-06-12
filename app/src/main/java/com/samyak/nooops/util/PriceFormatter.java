package com.samyak.nooops.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting prices and calculating discounts
 */
public class PriceFormatter {
    
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    
    /**
     * Format a price as currency
     * @param price The price to format
     * @return Formatted price string (e.g. "$99.99")
     */
    public static String formatPrice(double price) {
        return currencyFormatter.format(price);
    }
    
    /**
     * Calculate discount percentage
     * @param originalPrice The original price
     * @param currentPrice The current (discounted) price
     * @return Discount percentage (0-100)
     */
    public static int calculateDiscountPercentage(double originalPrice, double currentPrice) {
        if (originalPrice <= 0 || currentPrice >= originalPrice) {
            return 0;
        }
        
        return (int) (((originalPrice - currentPrice) / originalPrice) * 100);
    }
    
    /**
     * Format discount percentage as a string
     * @param percentage The discount percentage (0-100)
     * @return Formatted discount string (e.g. "20% OFF")
     */
    public static String formatDiscountPercentage(int percentage) {
        if (percentage <= 0) {
            return "";
        }
        
        return percentage + "% OFF";
    }
    
    /**
     * Calculate savings amount (difference between original and current price)
     * @param originalPrice The original price
     * @param currentPrice The current (discounted) price
     * @return Amount saved
     */
    public static double calculateSavings(double originalPrice, double currentPrice) {
        if (originalPrice <= currentPrice) {
            return 0;
        }
        
        return originalPrice - currentPrice;
    }
    
    /**
     * Format savings amount as currency with text
     * @param savings The amount saved
     * @return Formatted savings string (e.g. "You save $20.00")
     */
    public static String formatSavings(double savings) {
        if (savings <= 0) {
            return "";
        }
        
        return "You save " + currencyFormatter.format(savings);
    }
} 