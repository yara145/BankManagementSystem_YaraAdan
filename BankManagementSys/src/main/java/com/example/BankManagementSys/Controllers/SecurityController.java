package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Services.SecurityService;
import com.example.BankManagementSys.components.LoginData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SecurityController {

    @Autowired
    private SecurityService securityBL;

    //  Existing Customer Login (No changes)
    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            LoginData loginData = securityBL.login(username, password);
            if (loginData != null) {
                return ResponseEntity.ok(loginData);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}
