package com.samyak.nooops.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for formatting dates and timestamps
 */
public class DateFormatter {
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
    private static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
    
    /**
     * Format a timestamp as a date string (e.g. "Jun 15, 2023")
     * @param timestamp The timestamp in milliseconds
     * @return Formatted date string
     */
    public static String formatDate(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
    
    /**
     * Format a timestamp as a date and time string (e.g. "Jun 15, 2023 14:30")
     * @param timestamp The timestamp in milliseconds
     * @return Formatted date and time string
     */
    public static String formatDateTime(long timestamp) {
        return dateTimeFormat.format(new Date(timestamp));
    }
    
    /**
     * Format a timestamp as a short date string (e.g. "06/15/23")
     * @param timestamp The timestamp in milliseconds
     * @return Formatted short date string
     */
    public static String formatShortDate(long timestamp) {
        return shortDateFormat.format(new Date(timestamp));
    }
    
    /**
     * Get a relative time span string (e.g. "2 days ago", "5 minutes ago", "just now")
     * @param timestamp The timestamp in milliseconds
     * @return Relative time span string
     */
    public static String getRelativeTimeSpan(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        if (diff < 60000) { // Less than 1 minute
            return "just now";
        } else if (diff < 3600000) { // Less than 1 hour
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else if (diff < 86400000) { // Less than 1 day
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (diff < 604800000) { // Less than 1 week
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (diff < 2592000000L) { // Less than 30 days
            long weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        } else if (diff < 31536000000L) { // Less than 1 year
            long months = TimeUnit.MILLISECONDS.toDays(diff) / 30;
            return months + (months == 1 ? " month ago" : " months ago");
        } else {
            long years = TimeUnit.MILLISECONDS.toDays(diff) / 365;
            return years + (years == 1 ? " year ago" : " years ago");
        }
    }
    
    /**
     * Format a timestamp as a delivery date (e.g. "Delivery by Jun 20, 2023")
     * @param timestamp The timestamp in milliseconds
     * @return Formatted delivery date string
     */
    public static String formatDeliveryDate(long timestamp) {
        return "Delivery by " + dateFormat.format(new Date(timestamp));
    }
    
    /**
     * Calculate and format an estimated delivery date (3-5 days from now)
     * @return Formatted estimated delivery date string
     */
    public static String getEstimatedDeliveryDate() {
        long now = System.currentTimeMillis();
        long minDeliveryTime = now + TimeUnit.DAYS.toMillis(3);
        long maxDeliveryTime = now + TimeUnit.DAYS.toMillis(5);
        
        return "Delivery between " + 
               dateFormat.format(new Date(minDeliveryTime)) + 
               " and " + 
               dateFormat.format(new Date(maxDeliveryTime));
    }
} 