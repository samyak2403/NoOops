package com.samyak.nooops.util;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Utility class for validating user input in forms
 */
public class ValidationUtils {
    
    /**
     * Validate an email address
     * @param email Email to validate
     * @return True if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate a password (min 8 chars, at least 1 letter and 1 number)
     * @param password Password to validate
     * @return True if the password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            return false;
        }
        
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if two passwords match
     * @param password First password
     * @param confirmPassword Second password
     * @return True if the passwords match, false otherwise
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }
    
    /**
     * Validate a name (min 3 chars)
     * @param name Name to validate
     * @return True if the name is valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 3;
    }
    
    /**
     * Validate a mobile number (basic check for digits only, length between 10-15)
     * @param mobile Mobile number to validate
     * @return True if the mobile number is valid, false otherwise
     */
    public static boolean isValidMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return false;
        }
        
        String digitsOnly = mobile.replaceAll("[^0-9]", "");
        return digitsOnly.length() >= 10 && digitsOnly.length() <= 15;
    }
    
    /**
     * Validate a pin/zip code (5-10 chars)
     * @param pincode Pin/zip code to validate
     * @return True if the pin/zip code is valid, false otherwise
     */
    public static boolean isValidPincode(String pincode) {
        return !TextUtils.isEmpty(pincode) && pincode.trim().length() >= 5 && pincode.trim().length() <= 10;
    }
    
    /**
     * Validate an address line (min 5 chars)
     * @param address Address to validate
     * @return True if the address is valid, false otherwise
     */
    public static boolean isValidAddress(String address) {
        return !TextUtils.isEmpty(address) && address.trim().length() >= 5;
    }
    
    /**
     * Validate a city or state name (min 2 chars)
     * @param name City or state name to validate
     * @return True if the name is valid, false otherwise
     */
    public static boolean isValidCityOrState(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 2;
    }
    
    /**
     * Validate a credit card number using Luhn algorithm
     * @param cardNumber Credit card number to validate
     * @return True if the credit card number is valid, false otherwise
     */
    public static boolean isValidCreditCard(String cardNumber) {
        if (TextUtils.isEmpty(cardNumber)) {
            return false;
        }
        
        String digitsOnly = cardNumber.replaceAll("[^0-9]", "");
        
        if (digitsOnly.length() < 13 || digitsOnly.length() > 19) {
            return false;
        }
        
        // Luhn algorithm
        int sum = 0;
        boolean alternate = false;
        
        for (int i = digitsOnly.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(digitsOnly.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
    
    /**
     * Validate a card expiry date in MM/YY format
     * @param expiryDate Expiry date to validate
     * @return True if the expiry date is valid and not expired, false otherwise
     */
    public static boolean isValidExpiryDate(String expiryDate) {
        if (TextUtils.isEmpty(expiryDate) || expiryDate.length() != 5) {
            return false;
        }
        
        // Check format (MM/YY)
        if (expiryDate.charAt(2) != '/') {
            return false;
        }
        
        try {
            int month = Integer.parseInt(expiryDate.substring(0, 2));
            int year = Integer.parseInt(expiryDate.substring(3, 5));
            
            // Check if month is valid (1-12)
            if (month < 1 || month > 12) {
                return false;
            }
            
            // Get current date
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int currentYear = calendar.get(java.util.Calendar.YEAR) % 100; // Get last 2 digits
            int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1; // January is 0
            
            // Check if card is not expired
            return (year > currentYear) || (year == currentYear && month >= currentMonth);
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate a CVV code (3-4 digits)
     * @param cvv CVV code to validate
     * @return True if the CVV code is valid, false otherwise
     */
    public static boolean isValidCVV(String cvv) {
        if (TextUtils.isEmpty(cvv)) {
            return false;
        }
        
        String digitsOnly = cvv.replaceAll("[^0-9]", "");
        return digitsOnly.length() >= 3 && digitsOnly.length() <= 4;
    }
} 