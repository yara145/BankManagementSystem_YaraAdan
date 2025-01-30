package com.example.BankManagementSys.Exceptions;

public class TransactionAmountInvalidException extends RuntimeException {
    public TransactionAmountInvalidException(String message) {
        super(message);
    }
}
