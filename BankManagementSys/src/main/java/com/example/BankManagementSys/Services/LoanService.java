package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.LoanRepository;
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

    @Value("${loan.default.interest.rate}") // ✅ Set default interest rate from config
    private double defaultInterestRate;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BankAccountService bankAccountService;

    // ** ✅ Add New Loan **
    public Loan addNewLoan(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null.");
        }

        // ✅ Validate Loan Amount
        if (loan.getLoanAmount() == null || loan.getLoanAmount().compareTo(minAmount) < 0) {
            throw new TransactionAmountInvalidException("Loan amount must be at least " + minAmount);
        }
        if (loan.getLoanAmount().compareTo(maxAmount) > 0) {
            throw new TransactionAmountInvalidException("Loan amount must not exceed " + maxAmount);
        }

        // ✅ Set Default Interest Rate (if not provided)
        if (loan.getInterestRate() == null) {
            loan.setInterestRate(defaultInterestRate);
        }

        // ✅ Calculate Fixed Monthly Payment (Shpitzer formula)
        double monthlyInterestRate = loan.getInterestRate() / 100 / 12;
        double fixedMonthlyPayment = (loan.getLoanAmount().doubleValue() * monthlyInterestRate) /
                (1 - Math.pow(1 + monthlyInterestRate, -loan.getNumberOfPayments()));


        loan.setRemainingBalance(loan.getLoanAmount().doubleValue());
        loan.setRemainingPaymentsNum(loan.getNumberOfPayments());
        loan.setTransactionDateTime(LocalDateTime.now());
        // ✅ Save Loan
        return loanRepository.save(loan);
    }

    // ✅ Get Loan by ID
    public Loan getLoanById(int loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + loanId + " does not exist."));
    }

    // ✅ Get All Loans
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // ✅ Connect Loan to Bank Account
    @Transactional
    public Loan connectLoanToBank(Loan loan, int bankAccountId) {
        BankAccount account = bankAccountService.getBankAccountById(bankAccountId);
        if (account == null) {
            throw new IllegalArgumentException("Loan must be linked to a valid bank account.");
        }

        // ✅ Log Bank Account Info
        System.out.println("🏦 Linking Loan to Account: " + bankAccountId);
        System.out.println("🔹 Bank Account Before Loan: " + account);

        // ✅ Ensure the bank account is ACTIVE
        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new IllegalStateException("❌ Loan failed: Bank account ID " + bankAccountId + " is " + account.getStatus());
        }

        // ✅ Connect the Loan to the Bank Account
        loan.setBankAccount(account);

        // ✅ Update bank account balance (Add loan amount)
        boolean success = bankAccountService.updateBalance(bankAccountId, loan.getLoanAmount(), true, false);
        if (!success) {
            System.err.println("❌ Loan deposit failed for account ID: " + bankAccountId);
            return null; // Don't save loan if deposit fails
        }

        // ✅ Log Updated Bank Account Info
        System.out.println("✅ Loan successfully linked to account.");
        System.out.println("🔹 Bank Account After Loan: " + bankAccountService.getBankAccountById(bankAccountId));

        // ✅ Save Loan
        return loanRepository.save(loan);
    }

    // ✅ Get Loans by Bank Account ID
    public List<Loan> getLoansByAccountId(int accountId) {
        return loanRepository.findByBankAccountId(accountId);
    }
}


//    public Loan addNewLoan(Loan loan) {
//        if (loan == null) {
//            throw new IllegalArgumentException("Loan cannot be null.");
//        }
//
//        // ✅ Validate Loan Amount
//        if (loan.getLoanAmount() == null || loan.getLoanAmount().compareTo(minAmount) < 0) {
//            throw new TransactionAmountInvalidException("Loan amount must be at least " + minAmount);
//        }
//        if (loan.getLoanAmount().compareTo(maxAmount) > 0) {
//            throw new TransactionAmountInvalidException("Loan amount must not exceed " + maxAmount);
//        }
//
//        // ✅ Set Default Interest Rate (instead of user input)
//        loan.setInterestRate(defaultInterestRate);
//
//        // ✅ Validate Start Payment Date
//        if (loan.getStartPaymentDate() == null || loan.getStartPaymentDate().before(new Date())) {
//            throw new IllegalArgumentException("Start payment date must be in the future.");
//        }
//
//        // ✅ Log Loan Details
//        System.out.println("🔹 New Loan Details: " + loan);
//
//        // ✅ Save Loan
//        loan.setTransactionDateTime(LocalDateTime.now());
//        loan.setRemainingPaymentsNum(loan.getNumberOfPayments());
//        return loanRepository.save(loan);
//    }
