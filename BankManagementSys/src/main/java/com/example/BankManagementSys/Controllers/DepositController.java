package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Services.DepositTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deposits")
public class DepositController {

    private final DepositTransactionService depositService;

    public DepositController(DepositTransactionService depositService) {
        this.depositService = depositService;
    }

    // ✅ Retrieves all deposit transactions
    @GetMapping("getAll")
    public ResponseEntity<List<DepositTransaction>> getAllDeposits() {
        return ResponseEntity.ok(depositService.getAllDeposits());
    }

    // ✅ Retrieves a specific deposit transaction by ID
    @GetMapping("get/{id}")
    public ResponseEntity<DepositTransaction> getDepositById(@PathVariable int id) {
        return ResponseEntity.ok(depositService.getDepoistById(id));
    }

    // ✅ Adds a new deposit transaction
    @PostMapping("add")
    public ResponseEntity<String> addDeposit(@RequestBody DepositTransaction deposit) {
        depositService.addNewDepositTransaction(deposit);
        return ResponseEntity.status(201).body("Deposit transaction created successfully.");
    }

    // ✅ Links a deposit transaction to a bank account
    @PutMapping("connect/{depositId}/{bankAccountId}")
    public ResponseEntity<String> connectDepositToBank(
            @PathVariable int depositId, @PathVariable int bankAccountId) {
        depositService.connectTransactionToBank(depositId, bankAccountId);
        return ResponseEntity.ok("Deposit transaction successfully linked to bank account ID " + bankAccountId);
    }

    // ✅ Retrieves all deposits linked to a specific bank account
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<DepositTransaction>> getDepositsByAccountId(@PathVariable int accountId) {
        return ResponseEntity.ok(depositService.getDepositsByAccountId(accountId));
    }
}
