package com.example.BankManagementSys.Exceptions;

public class LoanPaymentNotFoundException extends RuntimeException {
    public LoanPaymentNotFoundException(String message) {
        super(message);
    }
}
