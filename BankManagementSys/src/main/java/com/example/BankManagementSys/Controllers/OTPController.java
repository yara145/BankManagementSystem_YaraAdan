package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Services.CustomerService;
import com.example.BankManagementSys.Services.EmailService;
import com.example.BankManagementSys.Services.OTPStorage;
import com.example.BankManagementSys.Utils.OTPGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/otp")
public class OTPController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OTPStorage otpStorage;

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim()
                .toLowerCase()
                .replaceAll("[\\p{C}\\p{Z}]", ""); // Removes control characters & zero-width spaces
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateOTP(
            @RequestParam String email,
            @RequestParam String userName,
            @RequestParam String firstName,  //  Added firstName
            @RequestParam String birthdate,
            @RequestParam String password) {
        try {
            //  Normalize email
            String normalizedEmail = normalizeEmail(email);

            //  Create a temporary customer object for validation
            Customer tempCustomer = new Customer();
            tempCustomer.setEmail(normalizedEmail);
            tempCustomer.setUserName(userName);
            tempCustomer.setName(firstName);  // Set firstName
            tempCustomer.setBirthdate(Date.valueOf(birthdate));
            tempCustomer.setPassword(password);

            //  Validate full user details
            customerService.validateCustomerDetails(tempCustomer);

            //  If all checks pass, generate OTP
            String otp = OTPGenerator.generateOTP(6);
            long validityInMillis = 5 * 60 * 1000; // 5 minutes

            otpStorage.storeOTP(normalizedEmail, otp, validityInMillis);
            emailService.sendEmail(normalizedEmail, "BankManagement Verification Code",
                    "Your OTP is: " + otp + "\n\nPlease enter it within 5 minutes.");

            return ResponseEntity.ok("OTP sent to " + normalizedEmail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Return meaningful validation errors
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error occurred.");
        }
    }

}
