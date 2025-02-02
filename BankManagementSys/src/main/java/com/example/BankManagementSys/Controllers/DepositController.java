package com.example.BankManagementSys.Controllers;


import com.example.BankManagementSys.Entities.DepositTransaction;
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
    public List<DepositTransaction> getAllDeposits() {
        return depositService.getAllDeposits();
    }


    // ✅ Retrieves a specific deposit transaction by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<DepositTransaction> getDepositById(@PathVariable int id) {
        return ResponseEntity.ok(depositService.getDepoistById(id));
    }

    // ✅ Adds a new deposit transaction.
    @PostMapping("add")
    public void addDeposit(@RequestBody DepositTransaction deposit) {
        depositService.addNewDepositTransaction(deposit);
    }

    // ✅ Links a deposit transaction to a bank account.
    @PutMapping("connect/{depositId}/{bankAccountId}")
    public ResponseEntity<String> connectDepositToBank(
            @PathVariable int depositId,
            @PathVariable int bankAccountId) {
        try {
            DepositTransaction deposit = depositService.getDepoistById(depositId);
            if (deposit == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Deposit transaction with ID " + depositId + " not found.");
            }

            DepositTransaction updatedDeposit = depositService.connectTransactionToBank(deposit, bankAccountId);
            if (updatedDeposit == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to connect deposit transaction to bank account.");
            }

            return ResponseEntity.ok("Deposit transaction successfully linked to bank account ID " + bankAccountId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // ✅ Deletes a deposit transaction by ID.
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteDeposit(@PathVariable int id) {
        try {
            depositService.deleteDepositTransaction(id); // Calls the service method to delete
            return ResponseEntity.ok("Deposit transaction with ID " + id + " has been deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ Retrieves all deposits linked to a specific bank account.
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<DepositTransaction>> getDepositsByAccountId(@PathVariable int accountId) {
        List<DepositTransaction> deposits = depositService.getDepositsByAccountId(accountId);

        if (deposits.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(deposits);
    }

    // ✅ Updates an existing deposit transaction.
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateDeposit(@PathVariable int id, @RequestBody DepositTransaction updatedDeposit) {
        DepositTransaction existingDeposit = depositService.getDepoistById(id);

        // ✅ Update only the fields that are not null in the request
        if (updatedDeposit.getDespositAmount() != null) {
            existingDeposit.setDespositAmount(updatedDeposit.getDespositAmount());
        }
        if (updatedDeposit.getDescription() != null) {
            existingDeposit.setDescription(updatedDeposit.getDescription());
        }

        depositService.updateDepositTransaction(existingDeposit);
        return ResponseEntity.ok("Deposit transaction updated successfully.");
    }
}
