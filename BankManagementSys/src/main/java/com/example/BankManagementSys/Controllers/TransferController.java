package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Exceptions.TransferTransactionNotFoundException;
import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Services.TransferTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    @Autowired
    private TransferTransactionService transferService;

    // ✅ Retrieves all transfer transactions.
    @GetMapping("getAll")
    public ResponseEntity<List<TransferTransaction>> getAllTransfers() {
        List<TransferTransaction> transfers = transferService.getAllTransfers();
        if (transfers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(transfers);
        }
        return ResponseEntity.ok(transfers);
    }

    // ✅ Retrieves a specific transfer transaction by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<TransferTransaction> getTransferById(@PathVariable int id) {
        TransferTransaction transfer = transferService.getTransferById(id);
        if (transfer == null) {
            throw new TransferTransactionNotFoundException("Transfer transaction with ID " + id + " not found.");
        }
        return ResponseEntity.ok(transfer);
    }

    // ✅ Adds a new transfer transaction.
    @PostMapping("add")
    public ResponseEntity<String> addTransfer(@RequestBody TransferTransaction transfer) {
        try {
            transferService.addNewTransferTransaction(transfer);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transfer transaction added successfully.");
        } catch (TransactionAmountInvalidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding transfer transaction: " + e.getMessage());
        }
    }

    // ✅ Links a transfer transaction to a bank account.
    @PutMapping("connect/{transferId}/{bankAccountId}")
    public ResponseEntity<String> connectTransferToBank(@PathVariable int transferId, @PathVariable int bankAccountId) {
        TransferTransaction transfer = transferService.getTransferById(transferId);
        if (transfer == null) {
            throw new TransferTransactionNotFoundException("Transfer transaction with ID " + transferId + " not found.");
        }
        transferService.connectTransactionToBank(transfer, bankAccountId);
        return ResponseEntity.ok("Transfer transaction successfully linked to bank account ID " + bankAccountId);
    }

    @GetMapping("account/{accountId}")
    public ResponseEntity<List<TransferTransaction>> getTransfersByAccountId(@PathVariable int accountId) {
        List<TransferTransaction> transfers = transferService.getTransfersByAccountId(accountId);
        return transfers.isEmpty()
                ? ResponseEntity.noContent().build() // 204 No Content if empty
                : ResponseEntity.ok(transfers);      // 200 OK if found
    }

}
