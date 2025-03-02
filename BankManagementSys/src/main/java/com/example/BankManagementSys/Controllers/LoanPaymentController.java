package com.example.BankManagementSys.Controllers;
import com.example.BankManagementSys.Entities.LoanPayment;
import com.example.BankManagementSys.Services.LoanPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class LoanPaymentController {

    private final LoanPaymentService loanPaymentService;

    public LoanPaymentController(LoanPaymentService loanPaymentService) {
        this.loanPaymentService = loanPaymentService;
    }

    // ✅ Retrieves all loan payments
    @GetMapping("getAll")
    public ResponseEntity<List<LoanPayment>> getAllPayments() {
        return ResponseEntity.ok(loanPaymentService.getAllPayments());
    }

    // ✅ Retrieves a specific loan payment by ID
    @GetMapping("get/{id}")
    public ResponseEntity<LoanPayment> getPaymentById(@PathVariable int id) {
        return ResponseEntity.ok(loanPaymentService.getPaymentById(id));
    }

    // ✅ Retrieves all payments for a specific loan
    @GetMapping("loan/{loanId}")
    public ResponseEntity<List<LoanPayment>> getPaymentsByLoanId(@PathVariable int loanId) {
        return ResponseEntity.ok(loanPaymentService.getPaymentsByLoanId(loanId));
    }
}

