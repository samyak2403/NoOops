package com.samyak.nooops.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate password
     * @param password Password to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        // Minimum 8 characters, at least one letter and one number
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$";
        return !TextUtils.isEmpty(password) && password.matches(passwordPattern);
    }
    
    /**
     * Validate mobile number
     * @param mobile Mobile number to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidMobile(String mobile) {
        // Allow only digits, 10-12 characters
        return !TextUtils.isEmpty(mobile) && mobile.matches("^[0-9]{10,12}$");
    }
    
    /**
     * Validate name
     * @param name Name to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.length() >= 3;
    }
    
    /**
     * Validate pincode
     * @param pincode Pincode to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidPincode(String pincode) {
        return !TextUtils.isEmpty(pincode) && pincode.matches("^[0-9]{6}$");
    }
    
    /**
     * Validate address
     * @param address Address to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidAddress(String address) {
        return !TextUtils.isEmpty(address) && address.length() >= 10;
    }
    
    /**
     * Validate card number
     * @param cardNumber Card number to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidCardNumber(String cardNumber) {
        // Remove spaces and dashes
        String cleanCardNumber = cardNumber.replaceAll("[ -]", "");
        
        // Check if the card number contains only digits and has valid length
        if (!cleanCardNumber.matches("^[0-9]{13,19}$")) {
            return false;
        }
        
        // Luhn algorithm for card number validation
        int sum = 0;
        boolean alternate = false;
        for (int i = cleanCardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cleanCardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
    
    /**
     * Validate card expiry date
     * @param expiryDate Expiry date in MM/YY format
     * @return True if valid, false otherwise
     */
    public static boolean isValidExpiryDate(String expiryDate) {
        if (TextUtils.isEmpty(expiryDate) || !expiryDate.matches("^\\d{2}/\\d{2}$")) {
            return false;
        }
        
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000; // Convert to 4-digit year
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int currentYear = calendar.get(java.util.Calendar.YEAR);
        int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1; // Calendar months are 0-based
        
        // Check if the month is valid
        if (month < 1 || month > 12) {
            return false;
        }
        
        // Check if the card is not expired
        return (year > currentYear) || (year == currentYear && month >= currentMonth);
    }
    
    /**
     * Validate CVV
     * @param cvv CVV to validate
     * @return True if valid, false otherwise
     */
    public static boolean isValidCVV(String cvv) {
        return !TextUtils.isEmpty(cvv) && cvv.matches("^[0-9]{3,4}$");
    }
} 