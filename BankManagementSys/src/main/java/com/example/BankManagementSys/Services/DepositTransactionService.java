package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Enums.TransferStatus;
import com.example.BankManagementSys.Exceptions.TransactiomAlreadyExistsException;
import com.example.BankManagementSys.Reposityories.DepositTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositTransactionService {

    @Autowired
    private DepositTransactionRepository depositRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public DepositTransaction addNewDepositTransaction(DepositTransaction deposit) throws TransactiomAlreadyExistsException {
        Transaction existingTransaction = this.transactionService.getTransactionById(deposit.getTransactionId());

        if(existingTransaction!= null)
        {
            throw new TransactiomAlreadyExistsException();
        }
        return this.depositRepoistory.save(deposit);
    }

    //** Update **
    public DepositTransaction updateDepositTransaction(DepositTransaction deposit) throws TransactiomAlreadyExistsException {
        if ((transactionService.getTransactionById(deposit.getTransactionId()).getTransactionId() == deposit.getTransactionId())){
            throw new IllegalArgumentException("Transfer with ID " + deposit.getTransactionId() + " does not exist.");
        }

        return depositRepoistory.save(deposit);
    }


    //** Delete **
    public void deleteDepositTransaction(int depositId) {
        // Find the transaction
        DepositTransaction deposit = depositRepoistory.findByTransactionId(depositId);

        // Check if the transaction exists
        if (deposit == null) {
            throw new IllegalArgumentException("Deposit with ID " + depositId + " does not exist.");
        }

        // Perform the delete operation
        depositRepoistory.deleteById(depositId);
    }

    //** Read **

    // Get a Deposit by ID
    public DepositTransaction getDepoistById(int depositId) {
        DepositTransaction deposit=    depositRepoistory.findByTransactionId(depositId);
        if (deposit == null) {
            throw new IllegalArgumentException("Deposit with ID " + depositId+ " does not exist.");
        }
        return deposit;
    }

    // Get all Deposits
    public List<DepositTransaction> getAllDeposits() {
        return depositRepoistory.findAll();

    }

    public DepositTransaction connectTransactionToBank(DepositTransaction deposit, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(deposit, bankAccountId);


        // Save and return the transaction
        return depositRepoistory.save(deposit);
    }
}
