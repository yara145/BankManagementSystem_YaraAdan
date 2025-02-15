package com.example.BankManagementSys.Services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class OTPStorage {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new ConcurrentHashMap<>();

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim()
                .toLowerCase()
                .replaceAll("[\\p{C}\\p{Z}]", ""); // Removes control characters & zero-width spaces
    }

    public void storeOTP(String email, String otp, long validityInMillis) {
        String normalizedEmail = normalizeEmail(email);
        otpStore.put(normalizedEmail, otp);
        otpExpiry.put(normalizedEmail, System.currentTimeMillis() + validityInMillis);

        System.out.println("✅ Stored OTP for: " + normalizedEmail);
        System.out.println("Updated OTP Store: " + otpStore);
    }

    public boolean validateOTP(String email, String otp) {
        String normalizedEmail = normalizeEmail(email);

        if (!otpStore.containsKey(normalizedEmail)) {
            return false;
        }

        long expiryTime = otpExpiry.get(normalizedEmail);
        if (System.currentTimeMillis() > expiryTime) {
            removeOTP(normalizedEmail);
            return false;
        }

        return otpStore.get(normalizedEmail).equals(otp);
    }

    public void removeOTP(String email) {
        String normalizedEmail = normalizeEmail(email);
        otpStore.remove(normalizedEmail);
        otpExpiry.remove(normalizedEmail);
        System.out.println("✅ OTP removed for: " + normalizedEmail);
    }

    public Map<String, String> getOtpStore() {
        return otpStore;
    }

    public Map<String, Long> getOtpExpiry() {
        return otpExpiry;
    }
}
