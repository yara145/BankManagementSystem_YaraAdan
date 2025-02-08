package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Exceptions.TransactionNotFoundException;
import com.example.BankManagementSys.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    // ✅ Retrieves all transactions.
    @GetMapping("getAll")
    public ResponseEntity<List<Transaction>> getAll() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return transactions.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(transactions)
                : ResponseEntity.ok(transactions);
    }

    // ✅ Retrieves transactions by bank account ID.
    @GetMapping("get/{bankAccountId}")
    public ResponseEntity<List<Transaction>> getByBankAccount(@PathVariable int bankAccountId) {
        List<Transaction> transactions = transactionService.getTransactionsByBankAccount(bankAccountId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for bank account ID " + bankAccountId);
        }
        return ResponseEntity.ok(transactions);
    }
}
