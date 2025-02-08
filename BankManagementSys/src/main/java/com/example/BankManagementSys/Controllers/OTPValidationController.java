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

    @GetMapping("/validate")
    public ResponseEntity<String> validateOTP(@RequestParam String email, @RequestParam String otp) {
        String normalizedEmail = email.trim().toLowerCase();
        String trimmedOTP = otp.trim();

        System.out.println("Validating OTP for: " + normalizedEmail);
        System.out.println("Provided OTP: " + trimmedOTP);

        if (!otpStorage.getOtpStore().containsKey(normalizedEmail)) {
            return ResponseEntity.badRequest().body("OTP not found. Please request a new one.");
        }

        long expiryTime = otpStorage.getOtpExpiry().get(normalizedEmail);
        long remainingTime = (expiryTime - System.currentTimeMillis()) / 1000;

        if (expiryTime < System.currentTimeMillis()) {
            otpStorage.removeOTP(normalizedEmail);
            return ResponseEntity.badRequest().body("OTP expired. Please request a new one.");
        }

        String storedOTP = otpStorage.getOtpStore().get(normalizedEmail);
        boolean isValid = storedOTP.equals(trimmedOTP);

        System.out.println("Stored OTP: " + storedOTP);
        System.out.println("OTP Match Status: " + isValid);

        if (!isValid) {
            return ResponseEntity.badRequest().body("Wrong OTP. Please try again.");
        }

        otpStorage.removeOTP(normalizedEmail);
        return ResponseEntity.ok("OTP is valid! You have successfully verified your email. Remaining time: " + remainingTime + " seconds.");
    }
}
