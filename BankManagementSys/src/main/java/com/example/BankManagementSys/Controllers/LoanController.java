package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Exceptions.LoanNotFoundException;
import com.example.BankManagementSys.Services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // ✅ Retrieves all loans.
    @GetMapping("getAll")
    public ResponseEntity<List<Loan>> getAll() {
        List<Loan> loans = loanService.getAllLoans();
        return loans.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(loans)
                : ResponseEntity.ok(loans);
    }

    // ✅ Retrieves a specific loan by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable int id) {
        Loan loan = loanService.getLoanById(id);
        if (loan == null) {
            throw new LoanNotFoundException("Loan with ID " + id + " not found.");
        }
        return ResponseEntity.ok(loan);
    }

    // ✅ Adds a new loan.
    @PostMapping("add")
    public ResponseEntity<String> addLoan(@RequestBody Loan loan) {
        try {
            loanService.addNewLoan(loan);
            return ResponseEntity.status(HttpStatus.CREATED).body("Loan added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding loan: " + e.getMessage());
        }
    }

    // ✅ Links a loan to a bank account.
    @PutMapping("connect/{loanId}/{bankAccountId}")
    public ResponseEntity<String> connectLoanToBank(@PathVariable int loanId, @PathVariable int bankAccountId) {
        Loan loan = loanService.getLoanById(loanId);
        if (loan == null) {
            throw new LoanNotFoundException("Loan with ID " + loanId + " not found.");
        }
        loanService.connectLoanToBank(loan, bankAccountId);
        return ResponseEntity.ok("Loan successfully linked to bank account ID " + bankAccountId);
    }

    // ✅ Retrieves all loans linked to a specific bank account.
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<Loan>> getLoansByAccountId(@PathVariable int accountId) {
        List<Loan> loans = loanService.getLoansByAccountId(accountId);
        if (loans.isEmpty()) {
            throw new LoanNotFoundException("No loans found for account ID " + accountId);
        }
        return ResponseEntity.ok(loans);
    }
}
