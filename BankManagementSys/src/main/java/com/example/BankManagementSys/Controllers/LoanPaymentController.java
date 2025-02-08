package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.LoanPayment;
import com.example.BankManagementSys.Exceptions.LoanPaymentNotFoundException;
import com.example.BankManagementSys.Services.LoanPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class LoanPaymentController {
    @Autowired
    LoanPaymentService loanPaymentService;

    // ✅ Retrieves all loan payments.
    @GetMapping("getAll")
    public ResponseEntity<List<LoanPayment>> getAllPayments() {
        List<LoanPayment> payments = loanPaymentService.getAllPayments();
        return payments.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(payments)
                : ResponseEntity.ok(payments);
    }

    // ✅ Retrieves a specific loan payment by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<LoanPayment> getPaymentById(@PathVariable int id) {
        LoanPayment payment = loanPaymentService.getPaymentById(id);
        if (payment == null) {
            throw new LoanPaymentNotFoundException("Loan payment with ID " + id + " not found.");
        }
        return ResponseEntity.ok(payment);
    }

    // ✅ Retrieves all payments for a specific loan.
    @GetMapping("loan/{loanId}")
    public ResponseEntity<List<LoanPayment>> getPaymentsByLoanId(@PathVariable int loanId) {
        List<LoanPayment> payments = loanPaymentService.getPaymentsByLoanId(loanId);
        if (payments.isEmpty()) {
            throw new LoanPaymentNotFoundException("No loan payments found for loan ID " + loanId);
        }
        return ResponseEntity.ok(payments);
    }
}


//    // ✅ Adds a new loan payment to a specific loan.
//    @PostMapping("add/{loanId}")
//    public ResponseEntity<String> addLoanPayment(@PathVariable int loanId, @RequestBody LoanPayment loanPayment) {
//        LoanPayment savedPayment = loanPaymentService.addLoanPayment(loanPayment, loanId);
//        if (savedPayment != null) {
//            return ResponseEntity.ok("Loan payment added successfully.");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process the loan payment.");
//        }
//    }
//
//    // ✅ Deletes a loan payment by ID.
//    @DeleteMapping("delete/{id}")
//    public ResponseEntity<String> deleteLoanPayment(@PathVariable int id) {
//        try {
//            loanPaymentService.deleteLoanPayment(id);
//            return ResponseEntity.ok("✅ Loan payment deleted successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("❌ Deletion failed: " + e.getMessage());
//        }
//    }




//    // ✅ Updates an existing loan payment.
//    @PutMapping("update/{id}")
//    public ResponseEntity<String> updateLoanPayment(@PathVariable int id, @RequestBody LoanPayment updatedPayment) {
//        LoanPayment existingPayment = loanPaymentService.getPaymentById(id);
//
//        // ✅ Update only the fields that are not null
//        if (updatedPayment.getPaymentAmount() != null) {
//            existingPayment.setPaymentAmount(updatedPayment.getPaymentAmount());
//        }
//        if (updatedPayment.getPaymentDateTime() != null) {
//            existingPayment.setPaymentDateTime(updatedPayment.getPaymentDateTime());
//        }
//
//        loanPaymentService.updateLoanPayment(existingPayment);
//        return ResponseEntity.ok("Loan payment updated successfully.");
//    }
//





