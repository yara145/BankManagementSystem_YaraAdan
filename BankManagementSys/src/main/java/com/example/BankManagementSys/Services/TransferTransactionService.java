package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.Bank;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Enums.TransferStatus;
import com.example.BankManagementSys.Exceptions.TransactiomAlreadyExistsException;
import com.example.BankManagementSys.Reposityories.TransferTransactionRepoistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferTransactionService {
    @Autowired
    private TransferTransactionRepoistory transferTransactionRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public TransferTransaction addNewTransferTransaction(TransferTransaction transfer) throws TransactiomAlreadyExistsException {
    Transaction existingTransaction = this.transactionService.getTransactionById(transfer.getTransactionId());

        if(existingTransaction!= null)
        {
            throw new TransactiomAlreadyExistsException();
        }
        return this.transferTransactionRepoistory.save(transfer);
    }

    //** Update **
    public TransferTransaction updateTransferTransaction(TransferTransaction transfer) throws TransactiomAlreadyExistsException {
        if (transactionService.getTransactionById(transfer.getTransactionId()) == null) {
            throw new IllegalArgumentException("Transfer with ID " + transfer.getTransactionId() + " does not exist.");


    }
        return transferTransactionRepoistory.save(transfer);
        }


//** Delete **
    public void deleteTransferTransaction(int transferId) {
        // Find the transaction
        TransferTransaction transfer = transferTransactionRepoistory.findByTransactionId(transferId);

        // Check if the transaction exists
        if (transfer == null) {
            throw new IllegalArgumentException("Transfer with ID " + transferId + " does not exist.");
        }


        // Perform the delete operation
        transferTransactionRepoistory.deleteById(transferId);
    }

    //** Read **

    // Get a transfer by ID
    public TransferTransaction getTransferById(int transferId) {
        TransferTransaction transfer= transferTransactionRepoistory.findByTransactionId(transferId);
        if (transfer == null) {
            throw new IllegalArgumentException("Transfer with ID " + transferId + " does not exist.");
        }
        return transfer;
    }

    // Get all Transfers
    public List<TransferTransaction> getAllTransfers() {
        return transferTransactionRepoistory.findAll();
    }



    public TransferTransaction connectTransactionToBank(TransferTransaction transfer, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(transfer, bankAccountId);

        // Add transfer-specific logic (e.g., default status)
        transfer.setTransferStatus(TransferStatus.PENDING);

        // Save and return the transaction
        return transferTransactionRepoistory.save(transfer);
    }

}
