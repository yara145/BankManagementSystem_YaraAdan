package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Services.EmailService;
import com.example.BankManagementSys.Services.OTPStorage;
import com.example.BankManagementSys.Utils.OTPGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OTPController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPStorage otpStorage;

    @GetMapping("/generate")
    public ResponseEntity<String> generateOTP(@RequestParam String email) {
        try {
            String otp = OTPGenerator.generateOTP(6);
            long validityInMillis = 5 * 60 * 1000; // 5 minutes

            otpStorage.storeOTP(email, otp, validityInMillis);

            long expirationTimeInMinutes = validityInMillis / (60 * 1000);

            String emailBody = "Your One-Time Password (OTP) for BankManagement verification is: " + otp + "\n\n"
                    + "Please enter this code in the verification field within " + expirationTimeInMinutes + " minutes to proceed.\n"
                    + "If you did not request this code, please ignore this email.";

            emailService.sendEmail(email, "BankManagement Verification Code", emailBody);

            return ResponseEntity.ok("OTP sent to " + email);
        } catch (Exception e) {
            System.out.println("Error while generating OTP: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }
}
