package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.Bank;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Enums.TransferStatus;
import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.TransferTransactionRepoistory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransferTransactionService {
    @Value("${transfer.max-amount}")
    private BigDecimal maxAmount;

    @Autowired
    private TransferTransactionRepoistory transferTransactionRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public TransferTransaction addNewTransferTransaction(TransferTransaction transfer) throws TransactionAmountInvalidException {

        if (transfer == null) {
            throw new IllegalArgumentException("TransferTransaction cannot be null.");
        }
        if (transfer.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new TransactionAmountInvalidException("Transfer amount must be greater than zero.");
        }

        if(transfer.getAmount().compareTo(maxAmount) > 0){
            throw new TransactionAmountInvalidException("Transfer amount must be less than maxAmount.");
        }

        transfer.setTransactionDateTime(LocalDateTime.now());
        return this.transferTransactionRepoistory.save(transfer);
    }

    //** Update **
    public TransferTransaction updateTransferTransaction(TransferTransaction transfer) {
        if (transfer == null ) {
            throw new IllegalArgumentException("Transfer  cannot be null.");
        }
        if (!transferTransactionRepoistory.existsById(transfer.getTransactionId())) {
            throw new IllegalArgumentException("Transfer with ID " + transfer.getTransactionId() + " does not exist.");
        }
        return transferTransactionRepoistory.save(transfer);
    }


    //** Delete **

    public void deleteTransferTransaction(int transferId) {
        TransferTransaction transfer = transferTransactionRepoistory.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer with ID " + transferId + " does not exist."));
        // Perform the delete operation
        transferTransactionRepoistory.delete(transfer);
    }

    //** Read **

    // Get a transfer by ID
    public TransferTransaction getTransferById(int transferId) {
        return transferTransactionRepoistory.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer with ID " + transferId + " does not exist."));
    }

    // Get all Transfers
    public List<TransferTransaction> getAllTransfers() {
        return transferTransactionRepoistory.findAll();
    }

    @Transactional
    public TransferTransaction connectTransactionToBank(TransferTransaction transfer, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(transfer, bankAccountId);

        // Add transfer-specific logic (e.g., default status)
        transfer.setTransferStatus(TransferStatus.PENDING);

        // Save and return the transaction
        return transferTransactionRepoistory.save(transfer);
    }

}
