package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Services.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // ✅ Retrieves all loans
    @GetMapping("getAll")
    public ResponseEntity<List<Loan>> getAll() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    // ✅ Retrieves a specific loan by ID
    @GetMapping("get/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable int id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    // ✅ Adds a new loan
    @PostMapping("add")
    public ResponseEntity<Map<String, Object>> addLoan(@RequestBody Loan loan) {
        // Add the loan to the database
        Loan savedLoan = loanService.addNewLoan(loan);

        // Prepare the response with the loanId
        Map<String, Object> response = new HashMap<>();
        response.put("loanId", savedLoan.getTransactionId());  // Return the loanId

        // Return the response with the loanId
        return ResponseEntity.status(201).body(response);
    }



    // ✅ Links a loan to a bank account
    @PutMapping("connect/{loanId}/{bankAccountId}")
    public ResponseEntity<String> connectLoanToBank(
            @PathVariable int loanId, @PathVariable int bankAccountId) {
        loanService.connectLoanToBank(loanId, bankAccountId);
        return ResponseEntity.ok("Loan successfully linked to bank account ID " + bankAccountId);
    }

    // ✅ Retrieves all loans linked to a specific bank account
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<Loan>> getLoansByAccountId(@PathVariable int accountId) {
        return ResponseEntity.ok(loanService.getLoansByAccountId(accountId));
    }
}
