package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Services.TransferTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferTransactionService transferService;

    public TransferController(TransferTransactionService transferService) {
        this.transferService = transferService;
    }

    // ✅ Retrieves all transfer transactions
    @GetMapping("getAll")
    public ResponseEntity<List<TransferTransaction>> getAllTransfers() {
        return ResponseEntity.ok(transferService.getAllTransfers());
    }

    // ✅ Retrieves a specific transfer transaction by ID
    @GetMapping("get/{id}")
    public ResponseEntity<TransferTransaction> getTransferById(@PathVariable int id) {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }

    // ✅ Adds a new transfer transaction
    @PostMapping("add")
    public ResponseEntity<String> addTransfer(@RequestBody TransferTransaction transfer) {
        transferService.addNewTransferTransaction(transfer);
        return ResponseEntity.status(201).body("Transfer transaction created successfully.");
    }

    // ✅ Links a transfer transaction to a bank account
    @PutMapping("connect/{transferId}/{bankAccountId}")
    public ResponseEntity<String> connectTransferToBank(
            @PathVariable int transferId, @PathVariable int bankAccountId) {
        transferService.connectTransactionToBank(transferId, bankAccountId);
        return ResponseEntity.ok("Transfer transaction successfully linked to bank account ID " + bankAccountId);
    }

    // ✅ Retrieves all transfers linked to a specific bank account
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<TransferTransaction>> getTransfersByAccountId(@PathVariable int accountId) {
        return ResponseEntity.ok(transferService.getTransfersByAccountId(accountId));
    }
}
