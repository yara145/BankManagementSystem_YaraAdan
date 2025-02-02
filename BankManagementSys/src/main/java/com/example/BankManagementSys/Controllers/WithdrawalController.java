package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;
import com.example.BankManagementSys.Services.WithdrawalTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/withdrawals")
public class WithdrawalController {

    @Autowired
    WithdrawalTransactionService withdrawalService;


    // ✅ Retrieves all withdrawal transactions.
    @GetMapping("getAll")
    public List<WithdrawalTransaction> getAllWithdrawals()
    {
        return withdrawalService.getAllWithdrawals() ;
    }



    // ✅ Retrieves a specific withdrawal transaction by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<WithdrawalTransaction> getWithdrawalById(@PathVariable int id) {
        return ResponseEntity.ok(withdrawalService.getWithdrawalById(id));
    }


    // ✅ Adds a new withdrawal transaction.
    @PostMapping("add")
    public void addWithdrawal(@RequestBody WithdrawalTransaction withdrawal) {
        this.withdrawalService.addNewWithdrawalTransaction(withdrawal);
    }


    // ✅ Links a withdrawal transaction to a bank account.
    @PutMapping("connect/{withdrawalId}/{bankAccountId}")
    public ResponseEntity<String> connectWithdrawalToBank(
            @PathVariable int withdrawalId,
            @PathVariable int bankAccountId) {
        try {
            WithdrawalTransaction withdrawal = withdrawalService.getWithdrawalById(withdrawalId);
            if (withdrawal == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Withdrawal transaction with ID " + withdrawalId + " not found.");
            }

            WithdrawalTransaction updatedWithdrawal = withdrawalService.connectTransactionToBank(withdrawal, bankAccountId);
            if (updatedWithdrawal == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to connect withdrawal transaction to bank account. Insufficient balance or other issue.");
            }

            return ResponseEntity.ok("Withdrawal transaction successfully linked to bank account ID " + bankAccountId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // ✅ Deletes a withdrawal transaction by ID.
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteWithdrawal(@PathVariable int id) {
        try {
            withdrawalService.DeleteWithdrawalTransaction(id); // Calls the service method to delete
            return ResponseEntity.ok("Withdrawal transaction with ID " + id + " has been deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // ✅ Retrieves all withdrawals linked to a specific bank account.
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<WithdrawalTransaction>> getWithdrawalsByAccountId(@PathVariable int accountId) {
        List<WithdrawalTransaction> withdrawals = withdrawalService.getWithdrawalsByAccountId(accountId);

        if (withdrawals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(withdrawals);
    }



    // ✅ Updates an existing withdrawal transaction.
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateWithdrawal(@PathVariable int id, @RequestBody WithdrawalTransaction updatedWithdrawal) {
        WithdrawalTransaction existingWithdrawal = withdrawalService.getWithdrawalById(id);

        // ✅ Update only the fields that are not null in the request
        if (updatedWithdrawal.getWithdrawalAmount() != null) {
            existingWithdrawal.setWithdrawalAmount(updatedWithdrawal.getWithdrawalAmount());
        }
        if (updatedWithdrawal.getDescription() != null) {
            existingWithdrawal.setDescription(updatedWithdrawal.getDescription());
        }

        withdrawalService.updateWithdrawalTransaction(existingWithdrawal);
        return ResponseEntity.ok("Withdrawal transaction updated successfully.");
    }




}

