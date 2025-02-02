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
    public Loan addNewLoan(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null.");
        }

        // Validate Loan Amount
        if (loan.getLoanAmount() == null) {
            throw new IllegalArgumentException("Loan amount cannot be null.");
        }
        if (loan.getLoanAmount().compareTo(minAmount) < 0) {
            throw new TransactionAmountInvalidException(
                    String.format("Loan amount must be at least %s.", minAmount)
            );
        }
        if (loan.getLoanAmount().compareTo(maxAmount) > 0) {
            throw new TransactionAmountInvalidException(
                    String.format("Loan amount must not exceed %s.", maxAmount)
            );
        }

        // Validate Loan Name
        if (loan.getLoanName() == null || loan.getLoanName().trim().isEmpty()) {
            throw new IllegalArgumentException("Loan name cannot be null or empty.");
        }

        // Validate Interest Rate
        if (loan.getInterestRate() < 0 || loan.getInterestRate() > maxInterestRate) {
            throw new IllegalArgumentException(
                    String.format("Interest rate must be between 0%% and %s%%.", maxInterestRate)
            );
        }

        // Validate Start Payment Date
        if (loan.getStartPaymentDate() == null) {
            throw new IllegalArgumentException("Start payment date cannot be null.");
        }
        if (loan.getStartPaymentDate().before(new Date())) {
            throw new IllegalArgumentException("Start payment date cannot be in the past.");
        }

        // Validate End Payment Date
        if (loan.getEndPaymentDate() == null) {
            throw new IllegalArgumentException("End payment date cannot be null.");
        }
        if (!loan.getEndPaymentDate().after(loan.getStartPaymentDate())) {
            throw new IllegalArgumentException(
                    "End payment date must be after the start payment date."
            );
        }

        // Save loan to the repository
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

        // ✅ Ensure `numberOfPayments` is never negative
        if (loan.getNumberOfPayments() < 0) {
            loan.setNumberOfPayments(0);
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

    // ✅ Retrieves all loans linked to a specific bank account.
    public List<Loan> getLoansByAccountId(int accountId) {
        return loanRepoistory.findByBankAccountId(accountId);
    }

}
