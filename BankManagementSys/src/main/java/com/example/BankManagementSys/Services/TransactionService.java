package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;

import com.example.BankManagementSys.Reposityories.TransactionRepoistory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TransactionService {
    @Autowired
    private TransactionRepoistory transactionRepoistory;
    @Autowired
    private BankAccountService bankAccountService;


    public Transaction getTransactionById(int transactionId){ return this.transactionRepoistory.findByTransactionId(transactionId); }

    public List<Transaction> getAllTransactions() {return this.transactionRepoistory.findAll();}


    @Transactional
    public Transaction connectTransactionToBankAccount(Transaction transaction, int bankAccountId) {
        // Fetch the BankAccount by ID

        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);

        // Validate if the BankAccount exists
        if (bankAccount == null) {
            throw new IllegalArgumentException("BankAccount not found for ID: " + bankAccountId);
        }
        // Validate if the Transaction exists
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }

        // Associate the transaction with the bank account
        transaction.setBankAccount(bankAccount);

        // Save the transaction with the connection established
        return transactionRepoistory.save(transaction);
    }


    public List<Transaction> getTransactionsByBankAccount(int bankAccountId) {
        return transactionRepoistory.findByBankAccount_Id(bankAccountId);
    }



}
