package com.example.BankManagementSys.Exceptions;

public class WithdrawalTransactionNotFoundException extends RuntimeException {
    public WithdrawalTransactionNotFoundException(String message) {
        super(message);
    }
}
