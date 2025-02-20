package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Entities.LoanPayment;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Reposityories.LoanPaymentRepository;
import com.example.BankManagementSys.Reposityories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling // Enables scheduled tasks
public class LoanPaymentService {

    @Autowired
    private LoanPaymentRepository loanPaymentRepository;

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BankAccountService bankAccountService;
    @Value("${loan.default.interest.rate}")
    private double defaultInterestRate;


    // ✅ This method is called by the scheduler
    public void processMonthlyLoanPayments() {
        List<Loan> loans = loanRepository.findAll();

        for (Loan loan : loans) {
            if (loan.getRemainingBalance() > 0 && loan.getNumberOfPayments() > 0) {
                makeLoanPayment(loan); // ✅ Now it works correctly
            }
        }
    }

    private void makeLoanPayment(Loan loan) {
        BankAccount account = loan.getBankAccount();

        // ✅ Ensure the bank account is ACTIVE
        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new IllegalStateException("❌ Loan Payment failed: Bank account ID " + account.getId() + " is " + account.getStatus() + ".");
        }

        // ✅ Get the monthly interest rate
        double monthlyInterestRate = defaultInterestRate / 100 / 12;

        // ✅ Corrected Shpitzer Formula for Monthly Loan Payment
        double monthlyPayment = (loan.getLoanAmount().doubleValue() * monthlyInterestRate) /
                (1 - Math.pow(1 + monthlyInterestRate, -loan.getNumberOfPayments()));

        // ✅ Calculate interest for this month
        double interestAmount = loan.getRemainingBalance() * monthlyInterestRate;

        // ✅ Calculate principal payment (Total Payment - Interest)
        double principalPayment = monthlyPayment - interestAmount;

        // ✅ Ensure last payment does not overpay
        if (loan.getRemainingBalance() < monthlyPayment) {
            monthlyPayment = loan.getRemainingBalance();
            principalPayment = monthlyPayment; // Last payment should only cover remaining amount
        }

        // ✅ Check if the account has enough balance including overdraft
        BigDecimal accountBalance = loan.getBankAccount().getBalance();




        // ✅ Deduct from bank account
        boolean success = bankAccountService.updateBalance(
                loan.getBankAccount().getId(), BigDecimal.valueOf(monthlyPayment), false, true);

        if (!success) return;

        // ✅ Update loan balance
        loan.setRemainingBalance(loan.getRemainingBalance() - principalPayment);

        // ✅ Reduce remaining payments
        if (loan.getRemainingPaymentsNum() > 0) {
            loan.setRemainingPaymentsNum(loan.getRemainingPaymentsNum() - 1);
        }

        // ✅ Save Payment Record
        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoan(loan);
        loanPayment.setPaymentAmount(monthlyPayment);
        loanPayment.setPaymentDateTime(LocalDateTime.now());

        loanPaymentRepository.save(loanPayment);
        loanRepository.save(loan);
    }

    // ** Get Payments by Loan ID **
    public List<LoanPayment> getPaymentsByLoanId(int loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() ->
                new IllegalArgumentException("Loan with ID " + loanId + " does not exist."));
        return loan.getPayments();
    }

    // ** Get a Specific Payment by ID **
    public LoanPayment getPaymentById(int paymentId) {
        return loanPaymentRepository.findById(paymentId).orElseThrow(() ->
                new IllegalArgumentException("Payment with ID " + paymentId + " does not exist."));
    }

    // ** Get All Payments **
    public List<LoanPayment> getAllPayments() {
        return loanPaymentRepository.findAll();
    }








//    private void makeLoanPayment(Loan loan) {
//        BankAccount account = loan.getBankAccount();
//
//        // ✅ Ensure the bank account is ACTIVE
//        if (account.getStatus() != BankAccountStatus.ACTIVE) {
//            throw new IllegalStateException("❌ Loan Payment failed: Bank account ID " + account.getId() + " is " + account.getStatus() + ".");
//        }
//
//        // ✅ Get the monthly interest rate
//        double monthlyInterestRate = defaultInterestRate / 100 / 12;
//
//        // ✅ Corrected Shpitzer Formula for Monthly Loan Payment
//        double monthlyPayment = (loan.getLoanAmount().doubleValue() * monthlyInterestRate) /
//                (1 - Math.pow(1 + monthlyInterestRate, -loan.getNumberOfPayments()));
//
//
//        // ✅ Calculate interest for this month
//        double interestAmount = loan.getRemainingBalance() * monthlyInterestRate;
//
//        // ✅ Calculate principal payment (Total Payment - Interest)
//        double principalPayment = monthlyPayment - interestAmount;
//
//        // ✅ Ensure last payment does not overpay
//        if (loan.getRemainingBalance() < monthlyPayment) {
//            monthlyPayment = loan.getRemainingBalance();
//            principalPayment = monthlyPayment; // Last payment should only cover remaining amount
//        }
//
//        // ✅ Ensure the bank account has enough funds before deducting
//        BigDecimal accountBalance = loan.getBankAccount().getBalance();
//        if (accountBalance.compareTo(BigDecimal.valueOf(monthlyPayment)) < 0) {
//            System.err.println("❌ Insufficient funds. Skipping loan payment.");
//            return;
//        }
//
//        // ✅ Deduct from bank account
//        boolean success = bankAccountService.updateBalance(
//                loan.getBankAccount().getId(), BigDecimal.valueOf(monthlyPayment), false, true);
//
//        if (!success) return;
//
//        // ✅ Update loan balance
//        loan.setRemainingBalance(loan.getRemainingBalance() - principalPayment);
//
//        // ✅ Reduce remaining payments
//        if (loan.getRemainingPaymentsNum() > 0) {
//            loan.setRemainingPaymentsNum(loan.getRemainingPaymentsNum() - 1);
//        }
//
//        // ✅ Save Payment Record
//        LoanPayment loanPayment = new LoanPayment();
//        loanPayment.setLoan(loan);
//        loanPayment.setPaymentAmount(monthlyPayment);
//        loanPayment.setPaymentDateTime(LocalDateTime.now());
//
//        loanPaymentRepository.save(loanPayment);
//        loanRepository.save(loan);
//    }


//    private void makeLoanPayment(Loan loan) {
//
//        BankAccount account = loan.getBankAccount();
//
//        // ✅ Ensure the bank account is ACTIVE
//        if (account.getStatus() != BankAccountStatus.ACTIVE) {
//            throw new IllegalStateException("❌ Loan Payment failed: Bank account ID " + account.getId() + " is" + account.getStatus() + ".");
//        }
//
//        double monthlyInterestRate = loan.getInterestRate() / 100 / 12;
//        double totalPayment;
//
//        if (loan.getLoanType() == LoanType.SHPITZER) {
//            // Shpitzer formula: Fixed Monthly Payment
//            totalPayment = (loan.getLoanAmount().doubleValue() * monthlyInterestRate) /
//                    (1 - Math.pow(1 + monthlyInterestRate, -loan.getNumberOfPayments()));
//        } else {
//            // Equal Principal: Fixed Principal, Decreasing Interest
//            double principalPayment = loan.getLoanAmount().doubleValue() / loan.getNumberOfPayments();
//            double interestAmount = loan.getRemainingBalance() * monthlyInterestRate;
//            totalPayment = principalPayment + interestAmount;
//        }
//
//        // Ensure last payment does not overpay
//        if (loan.getRemainingBalance() < totalPayment) {
//            totalPayment = loan.getRemainingBalance();
//        }
//
//        // Deduct from bank account
//        boolean success = bankAccountService.updateBalance(loan.getBankAccount().getId(),
//                BigDecimal.valueOf(totalPayment), false, true);
//        if (!success) return;
//
//        // Update loan balance
//        loan.setRemainingBalance(loan.getRemainingBalance() - totalPayment);
//
//        // Decrease remaining payments, but never below zero
//        if (loan.getRemainingPaymentsNum() > 0) {
//            loan.setRemainingPaymentsNum(loan.getRemainingPaymentsNum() - 1);
//        }
//
//        // Save payment
//        LoanPayment loanPayment = new LoanPayment();
//        loanPayment.setLoan(loan);
//        loanPayment.setPaymentAmount(totalPayment);
//        loanPayment.setPaymentDateTime(LocalDateTime.now());
//
//        loanPaymentRepository.save(loanPayment);
//        loanRepository.save(loan);
//    }
















/*
    private void makeLoanPayment(Loan loan) {
        double monthlyInterestRate = loan.getInterestRate() / 100 / 12;

        // ✅ Ensure equal payments by using remaining payments
        double principalPayment = loan.getRemainingBalance() / loan.getNumberOfPayments();
        double interestAmount = loan.getRemainingBalance() * monthlyInterestRate;
        double totalPayment = principalPayment + interestAmount;

        // ✅ Ensure last payment does not overpay
        if (loan.getRemainingBalance() < totalPayment) {
            totalPayment = loan.getRemainingBalance();
            principalPayment = loan.getRemainingBalance();
        }

        // ✅ Deduct from bank account only if balance allows
        boolean success = bankAccountService.updateBalance(
                loan.getBankAccount().getId(), BigDecimal.valueOf(totalPayment), false, true
        );
        if (!success) return;

        // ✅ Update loan balance
        loan.setRemainingBalance(loan.getRemainingBalance() - principalPayment);

        // ✅ Decrease remaining payments, but never below zero
        if (loan.getNumberOfPayments() > 0) {
            loan.setNumberOfPayments(loan.getNumberOfPayments() - 1);
        }

        // ✅ Save payment
        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoan(loan);
        loanPayment.setPaymentAmount(totalPayment);
        loanPayment.setPaymentDateTime(LocalDateTime.now());

        loanPaymentRepository.save(loanPayment);
        loanRepository.save(loan);
    }

*/















    // ** Add a New Payment **
//    public LoanPayment addLoanPayment(LoanPayment loanPayment, int loanId) {
//        // Fetch loan details
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + loanId + " does not exist."));
//
//        // Ensure loan has a valid interest rate and remaining balance
//        // ** Prevent loan payment issue **
////        if (loan.getRemainingBalance() <= 0) {
////            System.out.println("⚠️ Loan has already been fully paid. Skipping additional payments.");
////            return null; // ✅ Just return null instead of throwing an exception
////        }
//        if (loan.getRemainingBalance() <= 0) {
//            throw new IllegalArgumentException("❌ Cannot add payment. Loan is already fully paid.");
//        }
//
//
//        // ** Calculate Monthly Interest **
//        double monthlyInterestRate = loan.getInterestRate() / 100 / 12;  // Convert annual rate to monthly decimal
//        double interestAmount = loan.getRemainingBalance() * monthlyInterestRate;
//
//// ✅ Add interest to the remaining balance before deducting the principal
//        loan.setRemainingBalance(loan.getRemainingBalance() + interestAmount);
//        // ** Calculate Principal Payment **
//        double principalPayment = loanPayment.getPaymentAmount() - interestAmount;
//        if (principalPayment <= 0) {
//            throw new IllegalArgumentException("Payment must be greater than the interest amount.");
//        }// ✅ ודא שהתשלום לקרן לא חורג מהיתרה
//        if (principalPayment > loan.getRemainingBalance()) {
//            principalPayment = loan.getRemainingBalance(); // הקרן לא תעלה על היתרה
//        }
//
//        int accountId = loan.getBankAccount().getId();
//        // ** Deduct Payment from Bank Account **
//        boolean success = bankAccountService.updateBalance(accountId, BigDecimal.valueOf(loanPayment.getPaymentAmount()), false,true);
//        if (!success) {
//            System.err.println("❌ Loan payment failed: Insufficient balance in bank account.");
//            return null; // Do not save loan payment if balance update failed.
//        }
//
//        // ** Deduct from Loan Balance **
//        //loan.setRemainingBalance(loan.getRemainingBalance() - principalPayment);
//
//
//
//
//// ✅ Correctly deduct payment from the remaining balance
//        loan.setRemainingBalance(loan.getRemainingBalance() - principalPayment);
//      //  loan.setNumberOfPayments(loan.getNumberOfPayments() - 1);
//        // ✅ Decrease `numberOfPayments` only if it's greater than 0
//        if (loan.getNumberOfPayments() > 0) {
//            loan.setNumberOfPayments(Math.max(0, loan.getNumberOfPayments() - 1)); // ✅ Prevents negative payments
//        }
//
//
//        // Ensure the remaining balance doesn't go negative
//        // ✅ Ensure balance never goes negative
//        if (loan.getRemainingBalance() < 0) {
//            loan.setRemainingBalance(0);
//        }
//
//
//
//        // ** Set Payment Details **
//        loanPayment.setLoan(loan);
//        loanPayment.setPaymentDateTime(LocalDateTime.now());
//
//// ✅ Ensure the payment is added to the loan’s payment list
//        loan.getPayments().add(loanPayment);
//
//
//        // Save the updated loan and payment transaction
//        loanRepository.save(loan);
//        return loanPaymentRepository.save(loanPayment);
//    }


    // ** Delete a Payment **
//    public void deleteLoanPayment(int paymentId) {
//        if (!loanPaymentRepository.existsById(paymentId)) {
//            throw new IllegalArgumentException("Payment with ID " + paymentId + " does not exist.");
//        }
//        loanPaymentRepository.deleteById(paymentId);
//    }


//    @Transactional // ✅ Ensures transaction consistency
//    public void deleteLoanPayment(int paymentId) {
//        LoanPayment payment = loanPaymentRepository.findById(paymentId)
//                .orElseThrow(() -> new IllegalArgumentException("Payment with ID " + paymentId + " does not exist."));
//
//        // ✅ Step 1: Remove payment reference from Loan before deleting
//        Loan loan = payment.getLoan();
//        if (loan != null) {
//            loan.getPayments().removeIf(p -> p.getPaymentNumber().equals(paymentId)); // ✅ Remove by ID
//            loanRepository.save(loan);  // ✅ Save Loan to persist changes
//        }
//
//        // ✅ Step 2: Delete the LoanPayment
//        loanPaymentRepository.delete(payment);
//
//        // ✅ Step 3: Verify deletion
//        if (loanPaymentRepository.existsById(paymentId)) {
//            throw new RuntimeException("❌ Deletion failed: Loan payment still exists in the database.");
//        }
//    }


    // ✅ Updates an existing loan payment.
//    public LoanPayment updateLoanPayment(LoanPayment loanPayment) {
//        if (loanPayment == null || loanPayment.getPaymentNumber() == null) {
//            throw new IllegalArgumentException("Invalid loan payment data.");
//        }
//
//        if (!loanPaymentRepository.existsById(loanPayment.getPaymentNumber())) {
//            throw new IllegalArgumentException("Loan payment with ID " + loanPayment.getPaymentNumber() + " does not exist.");
//        }
//
//        return loanPaymentRepository.save(loanPayment);
//    }

}
