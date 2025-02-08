package com.example.BankManagementSys.Exceptions;

public class DepositTransactionNotFoundException extends RuntimeException {
    public DepositTransactionNotFoundException(String message) {
        super(message);
    }
}
