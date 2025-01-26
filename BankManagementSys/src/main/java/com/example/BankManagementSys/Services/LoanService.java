package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;
import com.example.BankManagementSys.Exceptions.TransactiomAlreadyExistsException;
import com.example.BankManagementSys.Reposityories.LoanRepository;
import com.example.BankManagementSys.Reposityories.TransferTransactionRepoistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public Loan addNewLoan(Loan loan) throws TransactiomAlreadyExistsException {
        Transaction existingTransaction = this.transactionService.getTransactionById(loan.getTransactionId());

        if(existingTransaction!= null)
        {
            throw new TransactiomAlreadyExistsException();
        }
        return this.loanRepoistory.save(loan);
    }

    //** Update **
    public Loan updateLoan(Loan loan) throws TransactiomAlreadyExistsException {
        if ((transactionService.getTransactionById(loan.getTransactionId()).getTransactionId() == loan.getTransactionId())){
            throw new IllegalArgumentException("Loan with ID " + loan.getTransactionId() + " does not exist.");
        }

        return loanRepoistory.save(loan);
    }


    //** Delete **
    public void deleteLoanTransaction(int loanId) {
        // Find the transaction
     Loan loan = loanRepoistory.findByTransactionId(loanId);

        // Check if the transaction exists
        if (loan == null) {
            throw new IllegalArgumentException("Loan with ID " + loanId + " does not exist.");
        }

        // Perform the delete operation
        loanRepoistory.deleteById(loanId);
    }

    //** Read **

    // Get a transfer by ID
    public Loan getLoanById(int loanId) {
      Loan loan= loanRepoistory.findByTransactionId(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan with ID " + loanId + " does not exist.");
        }
        return loan;
    }

    // Get all Transfers
    public List<Loan> getAllLoans () {
        return loanRepoistory.findAll();
    }


    public Loan connectLoanToBank(Loan loan, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(loan, bankAccountId);


        // Save and return the transaction
        return loanRepoistory.save(loan);
    }

}
