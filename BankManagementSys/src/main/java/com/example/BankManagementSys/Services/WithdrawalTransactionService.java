package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;
import com.example.BankManagementSys.Enums.TransferStatus;

import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.DepositTransactionRepository;
import com.example.BankManagementSys.Reposityories.WithdrawalTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalTransactionService {
    @Value("${withdrawal.max-amount}")
    private BigDecimal maxAmount;

    @Autowired
    private WithdrawalTransactionRepository withdrawalRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public WithdrawalTransaction addNewWithdrawalTransaction(WithdrawalTransaction withdrawal) {

        if (withdrawal == null) {
            throw new IllegalArgumentException("Withdrawal Transaction cannot be null.");
        }
        if (withdrawal.getWithdrawalAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new TransactionAmountInvalidException("withdrawal amount must be greater than zero.");
        }

        if(withdrawal.getWithdrawalAmount().compareTo(maxAmount) > 0){
            throw new TransactionAmountInvalidException("withdrawal amount must be less than maxAmount.");
        }

        withdrawal.setTransactionDateTime(LocalDateTime.now());
        return this.withdrawalRepoistory.save(withdrawal);
    }

    //** Update **
    public WithdrawalTransaction updateWithdrawalTransaction(WithdrawalTransaction withdrawal) {

        if (withdrawal == null ) {
            throw new IllegalArgumentException("Withdrawl cannot be null.");
        }
        if (!withdrawalRepoistory.existsById(withdrawal.getTransactionId())) {
            throw new IllegalArgumentException("Withdrawal with ID " + withdrawal.getTransactionId() + " does not exist.");
        }
        return withdrawalRepoistory.save(withdrawal);
    }


    //** Delete **
    public void WithdrawalTransaction(int withdrawalId) {
        // Find the transaction
        WithdrawalTransaction withdrawal = withdrawalRepoistory.findByTransactionId(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal with ID " + withdrawalId + " does not exist."));



        // Perform the delete operation
        withdrawalRepoistory.delete(withdrawal);
    }

    //** Read **

    // Get a Withdrawal by ID
    public WithdrawalTransaction getWithdrawalById(int withdrawalId) {

        return withdrawalRepoistory.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawa with ID " + withdrawalId + " does not exist."));

    }

    // Get all Withdrawal
    public List<WithdrawalTransaction> getAllWithdrawals() {
        return withdrawalRepoistory.findAll();

    }

    @Transactional
    public WithdrawalTransaction connectTransactionToBank(WithdrawalTransaction withdrawal, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(withdrawal, bankAccountId);

        // Save and return the transaction
        return withdrawalRepoistory.save(withdrawal);
    }
}
