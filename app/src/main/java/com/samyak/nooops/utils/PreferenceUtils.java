package com.samyak.nooops.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for handling app preferences
 */
public class PreferenceUtils {
    private static final String PREF_NAME = "NoOopsPrefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATION_ENABLED = "notifications_enabled";
    private static final String KEY_SEARCH_HISTORY = "search_history";
    private static final String KEY_LAST_VIEWED_PRODUCTS = "last_viewed_products";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
    
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    // Dark mode preferences
    public static boolean isDarkModeEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_DARK_MODE, false);
    }
    
    public static void setDarkModeEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }
    
    // Notification preferences
    public static boolean areNotificationsEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }
    
    public static void setNotificationsEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
    }
    
    // Search history (stored as comma-separated string)
    public static String getSearchHistory(Context context) {
        return getPreferences(context).getString(KEY_SEARCH_HISTORY, "");
    }
    
    public static void saveSearchQuery(Context context, String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        // Get existing history
        String history = getSearchHistory(context);
        String[] queries = history.isEmpty() ? new String[0] : history.split(",");
        
        // Check if the query already exists
        boolean exists = false;
        for (String existingQuery : queries) {
            if (existingQuery.equalsIgnoreCase(query)) {
                exists = true;
                break;
            }
        }
        
        // If it doesn't exist, add it to the history
        if (!exists) {
            StringBuilder newHistory = new StringBuilder(query);
            if (!history.isEmpty()) {
                newHistory.append(",").append(history);
            }
            
            // Limit to 10 most recent searches
            String[] newQueries = newHistory.toString().split(",");
            if (newQueries.length > 10) {
                StringBuilder limitedHistory = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    limitedHistory.append(newQueries[i]);
                    if (i < 9) {
                        limitedHistory.append(",");
                    }
                }
                newHistory = limitedHistory;
            }
            
            getPreferences(context).edit().putString(KEY_SEARCH_HISTORY, newHistory.toString()).apply();
        }
    }
    
    public static void clearSearchHistory(Context context) {
        getPreferences(context).edit().putString(KEY_SEARCH_HISTORY, "").apply();
    }
    
    // Last viewed products (stored as comma-separated product IDs)
    public static String getLastViewedProducts(Context context) {
        return getPreferences(context).getString(KEY_LAST_VIEWED_PRODUCTS, "");
    }
    
    public static void addLastViewedProduct(Context context, String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return;
        }
        
        // Get existing history
        String history = getLastViewedProducts(context);
        String[] productIds = history.isEmpty() ? new String[0] : history.split(",");
        
        // Check if the product ID already exists
        boolean exists = false;
        for (String existingId : productIds) {
            if (existingId.equals(productId)) {
                exists = true;
                break;
            }
        }
        
        // If it exists, remove it so we can add it to the beginning (most recent)
        if (exists) {
            StringBuilder newHistory = new StringBuilder();
            for (String existingId : productIds) {
                if (!existingId.equals(productId)) {
                    if (newHistory.length() > 0) {
                        newHistory.append(",");
                    }
                    newHistory.append(existingId);
                }
            }
            history = newHistory.toString();
        }
        
        // Add the product ID to the beginning
        StringBuilder newHistory = new StringBuilder(productId);
        if (!history.isEmpty()) {
            newHistory.append(",").append(history);
        }
        
        // Limit to 20 most recent viewed products
        String[] newProductIds = newHistory.toString().split(",");
        if (newProductIds.length > 20) {
            StringBuilder limitedHistory = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                limitedHistory.append(newProductIds[i]);
                if (i < 19) {
                    limitedHistory.append(",");
                }
            }
            newHistory = limitedHistory;
        }
        
        getPreferences(context).edit().putString(KEY_LAST_VIEWED_PRODUCTS, newHistory.toString()).apply();
    }
    
    public static void clearLastViewedProducts(Context context) {
        getPreferences(context).edit().putString(KEY_LAST_VIEWED_PRODUCTS, "").apply();
    }
    
    // Authentication token
    public static String getAuthToken(Context context) {
        return getPreferences(context).getString(KEY_AUTH_TOKEN, null);
    }
    
    public static void setAuthToken(Context context, String token) {
        getPreferences(context).edit().putString(KEY_AUTH_TOKEN, token).apply();
    }
    
    public static void clearAuthToken(Context context) {
        getPreferences(context).edit().remove(KEY_AUTH_TOKEN).apply();
    }
    
    // User ID
    public static String getUserId(Context context) {
        return getPreferences(context).getString(KEY_USER_ID, null);
    }
    
    public static void setUserId(Context context, String userId) {
        getPreferences(context).edit().putString(KEY_USER_ID, userId).apply();
    }
    
    public static void clearUserId(Context context) {
        getPreferences(context).edit().remove(KEY_USER_ID).apply();
    }
    
    // First launch
    public static boolean isFirstLaunch(Context context) {
        return getPreferences(context).getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }
    
    public static void setFirstLaunchComplete(Context context) {
        getPreferences(context).edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply();
    }
    
    // Clear all preferences
    public static void clearAllPreferences(Context context) {
        getPreferences(context).edit().clear().apply();
    }
} 