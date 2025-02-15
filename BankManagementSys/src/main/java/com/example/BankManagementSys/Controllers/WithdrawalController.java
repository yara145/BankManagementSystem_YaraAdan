package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.WithdrawalTransaction;
import com.example.BankManagementSys.Exceptions.WithdrawalTransactionNotFoundException;
import com.example.BankManagementSys.Services.WithdrawalTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/withdrawals")
public class WithdrawalController {

    @Autowired
    WithdrawalTransactionService withdrawalService;

    // ✅ Retrieves all withdrawal transactions.
    @GetMapping("getAll")
    public ResponseEntity<List<WithdrawalTransaction>> getAllWithdrawals() {
        List<WithdrawalTransaction> withdrawals = withdrawalService.getAllWithdrawals();
        return withdrawals.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(withdrawals)
                : ResponseEntity.ok(withdrawals);
    }

    // ✅ Retrieves a specific withdrawal transaction by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<WithdrawalTransaction> getWithdrawalById(@PathVariable int id) {
        WithdrawalTransaction withdrawal = withdrawalService.getWithdrawalById(id);
        if (withdrawal == null) {
            throw new WithdrawalTransactionNotFoundException("Withdrawal transaction with ID " + id + " not found.");
        }
        return ResponseEntity.ok(withdrawal);
    }

    @PostMapping("add")
    public ResponseEntity<?> addWithdrawal(@RequestBody WithdrawalTransaction withdrawal) {
        System.out.println("📌 Debugging Withdrawal: " + withdrawal); // Debugging

        try {
            // ✅ Validate withdrawal amount
            if (withdrawal.getWithdrawalAmount() == null || withdrawal.getWithdrawalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("❌ Error: Withdrawal amount must be greater than zero.");
            }

            // ✅ Ensure transaction date is set
            withdrawal.setTransactionDateTime(LocalDateTime.now());

            // ✅ Save withdrawal transaction
            WithdrawalTransaction savedWithdrawal = withdrawalService.addNewWithdrawalTransaction(withdrawal);

            System.out.println("✅ Withdrawal Created with ID: " + savedWithdrawal.getTransactionId()); // Debugging

            return ResponseEntity.status(HttpStatus.CREATED).body(savedWithdrawal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error adding withdrawal transaction: " + e.getMessage());
        }
    }

    // ✅ Links a withdrawal transaction to a bank account.
    @PutMapping("connect/{withdrawalId}/{bankAccountId}")
    public ResponseEntity<String> connectWithdrawalToBank(
            @PathVariable int withdrawalId,
            @PathVariable int bankAccountId) {

        WithdrawalTransaction withdrawal = withdrawalService.getWithdrawalById(withdrawalId);
        if (withdrawal == null) {
            throw new WithdrawalTransactionNotFoundException("Withdrawal transaction with ID " + withdrawalId + " not found.");
        }
        withdrawalService.connectTransactionToBank(withdrawal, bankAccountId);
        return ResponseEntity.ok("Withdrawal transaction successfully linked to bank account ID " + bankAccountId);
    }

    // ✅ Retrieves all withdrawals linked to a specific bank account.
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<WithdrawalTransaction>> getWithdrawalsByAccountId(@PathVariable int accountId) {
        List<WithdrawalTransaction> withdrawals = withdrawalService.getWithdrawalsByAccountId(accountId);
        if (withdrawals.isEmpty()) {
            throw new WithdrawalTransactionNotFoundException("No withdrawal transactions found for account ID " + accountId);
        }
        return ResponseEntity.ok(withdrawals);
    }
}
