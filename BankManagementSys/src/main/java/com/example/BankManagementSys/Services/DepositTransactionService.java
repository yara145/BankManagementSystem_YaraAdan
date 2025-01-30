package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;
import com.example.BankManagementSys.Enums.TransferStatus;

import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.DepositTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepositTransactionService {
    @Value("${deposit.max-amount}")
    private BigDecimal maxAmount;

    @Autowired
    private DepositTransactionRepository depositRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public DepositTransaction addNewDepositTransaction(DepositTransaction deposit)  {
        if (deposit == null) {
            throw new IllegalArgumentException("Deposit cannot be null.");
        }
        if (deposit.getDespositAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new TransactionAmountInvalidException("Deposit amount must be greater than zero.");
        }

        if(deposit.getDespositAmount().compareTo(maxAmount) > 0){
            throw new TransactionAmountInvalidException("Deposit amount must be less than maxAmount.");
        }

        deposit.setTransactionDateTime(LocalDateTime.now());
        return this.depositRepoistory.save(deposit);
    }

    //** Update **
    public DepositTransaction updateDepositTransaction(DepositTransaction deposit)  {

        if (deposit == null ) {
            throw new IllegalArgumentException("Deposit cannot be null.");
        }
        if (!depositRepoistory.existsById(deposit.getTransactionId())) {
            throw new IllegalArgumentException("Deposit with ID " + deposit.getTransactionId() + " does not exist.");
        }
        return depositRepoistory.save(deposit);
    }


    //** Delete **
    public void deleteDepositTransaction(int depositId) {

        // Find the transaction
     DepositTransaction deposit = depositRepoistory.findByTransactionId(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal with ID " + depositId + " does not exist."));

        // Perform the delete operation
        depositRepoistory.delete(deposit);

    }

    //** Read **

    // Get a Deposit by ID
    public DepositTransaction getDepoistById(int depositId) {

        return depositRepoistory.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Deposit with ID " +depositId + " does not exist."));

    }

    // Get all Deposits
    public List<DepositTransaction> getAllDeposits() {
        return depositRepoistory.findAll();

    }

    //**************** Other Functions *****************
    @Transactional
    public DepositTransaction connectTransactionToBank(DepositTransaction deposit, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(deposit, bankAccountId);

        // Save and return the transaction
        return depositRepoistory.save(deposit);
    }
}
