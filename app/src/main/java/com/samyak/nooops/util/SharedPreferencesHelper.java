package com.samyak.nooops.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class for managing SharedPreferences
 */
public class SharedPreferencesHelper {
    
    private static final String PREF_NAME = "nooops_preferences";
    
    // Preference keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_FIRST_TIME_USER = "first_time_user";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_CART_COUNT = "cart_count";
    private static final String KEY_WISHLIST_COUNT = "wishlist_count";
    private static final String KEY_DEFAULT_ADDRESS_ID = "default_address_id";
    
    private final SharedPreferences sharedPreferences;
    
    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Save user data after successful login/registration
     */
    public void saveUserData(String userId, String name, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    /**
     * Clear user data on logout
     */
    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_AUTH_TOKEN);
        editor.apply();
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Get user ID
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }
    
    /**
     * Get user name
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }
    
    /**
     * Get user email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }
    
    /**
     * Save authentication token
     */
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }
    
    /**
     * Get authentication token
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, "");
    }
    
    /**
     * Set theme mode (0: Light, 1: Dark, 2: System default)
     */
    public void setThemeMode(int mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_THEME_MODE, mode);
        editor.apply();
    }
    
    /**
     * Get theme mode
     */
    public int getThemeMode() {
        return sharedPreferences.getInt(KEY_THEME_MODE, 2); // Default: System default
    }
    
    /**
     * Enable/disable notifications
     */
    public void setNotificationEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, enabled);
        editor.apply();
    }
    
    /**
     * Check if notifications are enabled
     */
    public boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true); // Default: Enabled
    }
    
    /**
     * Set first time user flag
     */
    public void setFirstTimeUser(boolean isFirstTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_TIME_USER, isFirstTime);
        editor.apply();
    }
    
    /**
     * Check if it's the first time using the app
     */
    public boolean isFirstTimeUser() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_USER, true); // Default: true
    }
    
    /**
     * Update last sync time
     */
    public void updateLastSyncTime() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis());
        editor.apply();
    }
    
    /**
     * Get last sync time
     */
    public long getLastSyncTime() {
        return sharedPreferences.getLong(KEY_LAST_SYNC_TIME, 0);
    }
    
    /**
     * Update cart item count
     */
    public void setCartCount(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CART_COUNT, count);
        editor.apply();
    }
    
    /**
     * Get cart item count
     */
    public int getCartCount() {
        return sharedPreferences.getInt(KEY_CART_COUNT, 0);
    }
    
    /**
     * Update wishlist item count
     */
    public void setWishlistCount(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_WISHLIST_COUNT, count);
        editor.apply();
    }
    
    /**
     * Get wishlist item count
     */
    public int getWishlistCount() {
        return sharedPreferences.getInt(KEY_WISHLIST_COUNT, 0);
    }
    
    /**
     * Set default address ID
     */
    public void setDefaultAddressId(String addressId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DEFAULT_ADDRESS_ID, addressId);
        editor.apply();
    }
    
    /**
     * Get default address ID
     */
    public String getDefaultAddressId() {
        return sharedPreferences.getString(KEY_DEFAULT_ADDRESS_ID, "");
    }
} 