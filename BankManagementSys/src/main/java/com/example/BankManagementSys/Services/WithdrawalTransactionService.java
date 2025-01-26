package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;
import com.example.BankManagementSys.Enums.TransferStatus;
import com.example.BankManagementSys.Exceptions.TransactiomAlreadyExistsException;
import com.example.BankManagementSys.Reposityories.DepositTransactionRepository;
import com.example.BankManagementSys.Reposityories.WithdrawalTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WithdrawalTransactionService {

    @Autowired
    private WithdrawalTransactionRepository withdrawalRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public WithdrawalTransaction addNewWithdrawalTransaction(WithdrawalTransaction withdrawal) throws TransactiomAlreadyExistsException {
        Transaction existingTransaction = this.transactionService.getTransactionById(withdrawal.getTransactionId());

        if(existingTransaction!= null)
        {
            throw new TransactiomAlreadyExistsException();
        }
        return this.withdrawalRepoistory.save(withdrawal);
    }

    //** Update **
    public WithdrawalTransaction updateWithdrawalTransaction(WithdrawalTransaction withdrawal) throws TransactiomAlreadyExistsException {
        if ((transactionService.getTransactionById(withdrawal.getTransactionId())) == null){
            throw new IllegalArgumentException("Transfer with ID " + withdrawal.getTransactionId() + " does not exist.");
        }

        return withdrawalRepoistory.save(withdrawal);
    }


    //** Delete **
    public void WithdrawalTransaction(int withdrawalId) {
        // Find the transaction
        WithdrawalTransaction withdrawal = withdrawalRepoistory.findByTransactionId(withdrawalId);

        // Check if the transaction exists
        if (withdrawal == null) {
            throw new IllegalArgumentException("Deposit with ID " + withdrawalId + " does not exist.");
        }

        // Perform the delete operation
        withdrawalRepoistory.deleteById(withdrawalId);
    }

    //** Read **

    // Get a transfer by ID
    public WithdrawalTransaction getWithdrawalById(int withdrawalId) {
        WithdrawalTransaction withdrawal =  withdrawalRepoistory.findByTransactionId(withdrawalId);
        if (withdrawal== null) {
            throw new IllegalArgumentException("Withdrawal with ID " + withdrawalId+ " does not exist.");
        }
        return withdrawal;
    }

    // Get all Transfers
    public List<WithdrawalTransaction> getAllWithdrawals() {
        return withdrawalRepoistory.findAll();

    }


    public WithdrawalTransaction connectTransactionToBank(WithdrawalTransaction withdrawal, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(withdrawal, bankAccountId);


        // Save and return the transaction
        return withdrawalRepoistory.save(withdrawal);
    }
}
