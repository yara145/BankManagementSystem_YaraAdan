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

    // ✅ Get all withdrawal transactions
    @GetMapping("getAll")
    public ResponseEntity<List<WithdrawalTransaction>> getAllWithdrawals() {
        return ResponseEntity.ok(withdrawalService.getAllWithdrawals());
    }


    // ✅ Get a withdrawal transaction by ID
    @GetMapping("get/{id}")
    public ResponseEntity<WithdrawalTransaction> getWithdrawalById(@PathVariable int id) {
        return ResponseEntity.ok(withdrawalService.getWithdrawalById(id));
    }

    // ✅ Add a new withdrawal transaction
    @PostMapping("add")
    public ResponseEntity<String> addWithdrawal(@RequestBody WithdrawalTransaction withdrawal) {
        withdrawalService.addNewWithdrawalTransaction(withdrawal);
        return ResponseEntity.status(201).body("Withdrawal transaction created successfully.");
    }

    // ✅ Link withdrawal to a bank account
    @PutMapping("connect/{withdrawalId}/{bankAccountId}")
    public ResponseEntity<String> connectWithdrawalToBank(
            @PathVariable int withdrawalId,
            @PathVariable int bankAccountId) {
        withdrawalService.connectTransactionToBank(withdrawalId, bankAccountId);
        return ResponseEntity.ok("Withdrawal transaction linked to bank account successfully.");
    }

    // ✅ Get all withdrawals linked to a specific bank account
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<WithdrawalTransaction>> getWithdrawalsByAccountId(@PathVariable int accountId) {
        return ResponseEntity.ok(withdrawalService.getWithdrawalsByAccountId(accountId));
    }
}
