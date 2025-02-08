package com.example.BankManagementSys.Services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class OTPStorage {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new ConcurrentHashMap<>();

    public void storeOTP(String email, String otp, long validityInMillis) {
        String normalizedEmail = email.trim().toLowerCase();
        otpStore.put(normalizedEmail, otp);
        otpExpiry.put(normalizedEmail, System.currentTimeMillis() + validityInMillis);

        System.out.println("OTP stored successfully for: " + normalizedEmail);
        System.out.println("Stored OTP: " + otp);
        System.out.println("Updated OTP Storage: " + otpStore);
    }

    public boolean validateOTP(String email, String otp) {
        String normalizedEmail = email.trim().toLowerCase();
        System.out.println("Validating OTP for: " + normalizedEmail);
        System.out.println("Provided OTP: " + otp);
        System.out.println("Current OTP Storage: " + otpStore);

        if (!otpStore.containsKey(normalizedEmail)) {
            System.out.println("OTP not found in store for: " + normalizedEmail);
            return false;
        }

        long expiryTime = otpExpiry.get(normalizedEmail);
        if (System.currentTimeMillis() > expiryTime) {
            System.out.println("OTP expired for: " + normalizedEmail);
            removeOTP(normalizedEmail);
            return false;
        }

        String storedOTP = otpStore.get(normalizedEmail);
        System.out.println("Stored OTP: " + storedOTP);

        boolean isMatch = storedOTP.equals(otp);
        System.out.println("OTP Match Status: " + isMatch);

        if (!isMatch) {
            System.out.println("OTP mismatch! Provided: [" + otp + "] Expected: [" + storedOTP + "]");
            return false;
        }

        removeOTP(normalizedEmail);
        return true;
    }

    public void removeOTP(String email) {
        otpStore.remove(email);
        otpExpiry.remove(email);
        System.out.println("OTP removed for: " + email);
    }

    public Map<String, String> getOtpStore() {
        return otpStore;
    }

    public Map<String, Long> getOtpExpiry() {
        return otpExpiry;
    }

    public void printAllOTPs() {
        System.out.println("Current OTP Storage: " + otpStore);
    }
}
