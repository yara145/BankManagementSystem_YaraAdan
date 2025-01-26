package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Entities.LoanPayment;
import com.example.BankManagementSys.Reposityories.LoanPaymentRepository;
import com.example.BankManagementSys.Reposityories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanPaymentService {

    @Autowired
    private LoanPaymentRepository loanPaymentRepository;

    @Autowired
    private LoanRepository loanRepository;

    // ** Add a New Payment **
    public LoanPayment addLoanPayment(LoanPayment loanPayment, int loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() ->
                new IllegalArgumentException("Loan with ID " + loanId + " does not exist."));

        loanPayment.setLoan(loan); // Link payment to loan
        return loanPaymentRepository.save(loanPayment);
    }

    // ** Update a Payment **
    public LoanPayment updateLoanPayment(LoanPayment loanPayment) {
        if (!loanPaymentRepository.existsById(loanPayment.getPaymentNumber())) {
            throw new IllegalArgumentException("Payment with ID " + loanPayment.getPaymentNumber() + " does not exist.");
        }
        return loanPaymentRepository.save(loanPayment);
    }

    // ** Delete a Payment **
    public void deleteLoanPayment(int paymentId) {
        if (!loanPaymentRepository.existsById(paymentId)) {
            throw new IllegalArgumentException("Payment with ID " + paymentId + " does not exist.");
        }
        loanPaymentRepository.deleteById(paymentId);
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
}
