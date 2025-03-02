package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ✅ Retrieves all transactions
    @GetMapping("getAll")
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // ✅ Retrieves transactions by bank account ID
    @GetMapping("get/{bankAccountId}")
    public ResponseEntity<List<Transaction>> getByBankAccount(@PathVariable int bankAccountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBankAccount(bankAccountId));
    }
}
