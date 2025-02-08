package com.example.BankManagementSys.Utils;

import java.security.SecureRandom;

public class OTPGenerator {
    private static final String DIGITS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return otp.toString();
    }
}
