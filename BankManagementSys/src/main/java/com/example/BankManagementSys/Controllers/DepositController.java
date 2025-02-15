package com.example.BankManagementSys.Controllers;


import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Exceptions.DepositTransactionNotFoundException;
import com.example.BankManagementSys.Services.DepositTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/desposits")
public class DepositController {
    @Autowired
    DepositTransactionService depositService;


    // ✅ Retrieves all deposit transactions.
    @GetMapping("getAll")
    public ResponseEntity<List<DepositTransaction>> getAllDeposits() {
        List<DepositTransaction> deposits = depositService.getAllDeposits();
        if (deposits.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deposits);
        }
        return ResponseEntity.ok(deposits);
    }


    // ✅ Retrieves a specific deposit transaction by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<DepositTransaction> getDepositById(@PathVariable int id) {
        DepositTransaction deposit = depositService.getDepoistById(id);
        if (deposit == null) {
            throw new DepositTransactionNotFoundException("Deposit transaction with ID " + id + " not found.");
        }
        return ResponseEntity.ok(deposit);
    }


    @PostMapping("add")
    public ResponseEntity<?> addDeposit(@RequestBody DepositTransaction deposit) {
        try {
            // ✅ Save the deposit and get the created deposit object
            DepositTransaction savedDeposit = depositService.addNewDepositTransaction(deposit);

            // ✅ Ensure we return the ID in the response
            System.out.println("✅ Deposit Created with ID: " + savedDeposit.getTransactionId()); // Debugging

            return ResponseEntity.status(HttpStatus.CREATED).body(savedDeposit); // Return full deposit object
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error adding deposit transaction: " + e.getMessage());
        }
    }



    // ✅ Links a deposit transaction to a bank account.
    @PutMapping("connect/{depositId}/{bankAccountId}")
    public ResponseEntity<String> connectDepositToBank(@PathVariable int depositId, @PathVariable int bankAccountId) {
        System.out.println("🔍 Connecting Deposit ID: " + depositId + " to Bank Account ID: " + bankAccountId);

        DepositTransaction deposit = depositService.getDepoistById(depositId);
        if (deposit == null) {
            throw new DepositTransactionNotFoundException("Deposit transaction with ID " + depositId + " not found.");
        }

        depositService.connectTransactionToBank(deposit, bankAccountId);
        return ResponseEntity.ok("✅ Deposit transaction successfully linked to bank account ID " + bankAccountId);
    }


    // ✅ Retrieves all deposits linked to a specific bank account.
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<DepositTransaction>> getDepositsByAccountId(@PathVariable int accountId) {
        List<DepositTransaction> deposits = depositService.getDepositsByAccountId(accountId);
        if (deposits.isEmpty()) {
            throw new DepositTransactionNotFoundException("No deposit transactions found for account ID " + accountId);
        }
        return ResponseEntity.ok(deposits);
    }


}
