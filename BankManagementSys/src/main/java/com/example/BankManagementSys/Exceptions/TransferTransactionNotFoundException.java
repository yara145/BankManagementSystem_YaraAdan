package com.example.BankManagementSys.Exceptions;

public class TransferTransactionNotFoundException extends RuntimeException {
    public TransferTransactionNotFoundException(String message) {
        super(message);
    }
}
