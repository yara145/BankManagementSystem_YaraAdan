package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Services.OTPStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
public class OTPValidationController {

    @Autowired
    private OTPStorage otpStorage;

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim()
                .toLowerCase()
                .replaceAll("[\\p{C}\\p{Z}]", ""); // Removes control characters & zero-width spaces
    }
    @GetMapping("/validate")
    public ResponseEntity<String> validateOTP(@RequestParam String email, @RequestParam String otp) {
        String normalizedEmail = normalizeEmail(email);
        String trimmedOTP = otp.trim();

        System.out.println(" Validating OTP for: " + normalizedEmail);
        System.out.println("Provided OTP: " + trimmedOTP);

        if (!otpStorage.getOtpStore().containsKey(normalizedEmail)) {
            return ResponseEntity.badRequest().body("OTP not found. Please request a new one.");
        }

        long expiryTime = otpStorage.getOtpExpiry().get(normalizedEmail);
        if (System.currentTimeMillis() > expiryTime) {
            otpStorage.removeOTP(normalizedEmail);
            return ResponseEntity.badRequest().body("OTP expired. Please request a new one.");
        }

        String storedOTP = otpStorage.getOtpStore().get(normalizedEmail);
        if (!storedOTP.equals(trimmedOTP)) {
            return ResponseEntity.badRequest().body("Incorrect OTP. Try again.");
        }

        otpStorage.removeOTP(normalizedEmail);
        return ResponseEntity.ok("OTP validated successfully.");
    }

}
