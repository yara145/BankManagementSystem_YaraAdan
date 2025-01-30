package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Entities.TransferTransaction;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;

import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.LoanRepository;
import com.example.BankManagementSys.Reposityories.TransferTransactionRepoistory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class LoanService {
    @Value("${loan.max.amount}")
    private BigDecimal maxAmount;

    @Value("${loan.min.amount}")
    private BigDecimal minAmount;


    @Value("${loan.max.interest.rate}")
    private double maxInterestRate;

    @Autowired
    private LoanRepository loanRepoistory;

    @Autowired
    private TransactionService transactionService;

    // ************ CRUD ******************

    // ** Add **
    public Loan addNewLoan(Loan loan)  {

        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null.");
        }
        // Validate Loan Amount
        if (loan.getLoanAmount().compareTo(minAmount) < 0) {
            throw new TransactionAmountInvalidException("Loan amount must be greater than minimum amount.");
        }

        if(loan.getLoanAmount().compareTo(maxAmount) > 0){
            throw new TransactionAmountInvalidException("Loan amount must be less than maxAmount.");
        }
        // Validate Loan Name
        if (loan.getLoanName() == null || loan.getLoanName().trim().isEmpty()) {
            throw new IllegalArgumentException("Loan name cannot be empty.");
        }
        // Validate Interest Rate
        if (loan.getInterestRate() < 0 || loan.getInterestRate() > maxInterestRate) {
            throw new IllegalArgumentException("Interest rate must be between 0% and " + maxInterestRate + "%.");
        }
        // Validate Start Payment Date
        if (loan.getStartPaymentDate() == null || loan.getStartPaymentDate().before(new Date())) {
            throw new IllegalArgumentException("Start payment date cannot be in the past.");
        }

        // Validate End Payment Date
        if (loan.getEndPaymentDate() == null || loan.getEndPaymentDate().before(loan.getStartPaymentDate())) {
            throw new IllegalArgumentException("End payment date must be after start payment date.");
        }
        loan.setTransactionDateTime(LocalDateTime.now());
        return this.loanRepoistory.save(loan);
    }

    //** Update **
    public Loan updateLoan(Loan loan)  {
        if (loan == null ) {
            throw new IllegalArgumentException("Loan cannot be null.");
        }
        if (!loanRepoistory.existsById(loan.getTransactionId())) {
            throw new IllegalArgumentException("Loan with ID " + loan.getTransactionId() + " does not exist.");
        }

        return loanRepoistory.save(loan);
    }



    //** Delete **
    public void deleteLoanTransaction(int loanId) {

    Loan loan = loanRepoistory.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + loanId + " does not exist."));

        // Perform the delete operation
        loanRepoistory.delete(loan);

    }

    //** Read **

    // Get a transfer by ID
    public Loan getLoanById(int loanId) {
        return loanRepoistory.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + loanId + " does not exist."));
    }

    // Get all Transfers
    public List<Loan> getAllLoans () {
        return loanRepoistory.findAll();
    }

    @Transactional
    public Loan connectLoanToBank(Loan loan, int bankAccountId) {
        // Connect the transfer to the bank account
        transactionService.connectTransactionToBankAccount(loan, bankAccountId);


        // Save and return the transaction
        return loanRepoistory.save(loan);
    }

}
