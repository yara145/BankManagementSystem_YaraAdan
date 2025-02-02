package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.Loan;
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
    public List<Loan> getAll() {
        return loanService.getAllLoans();
    }

    // ✅ Retrieves a specific loan by ID.
    @GetMapping("get/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable int id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    // ✅ Adds a new loan.
    @PostMapping("add")
    public void addLoan(@RequestBody Loan loan) {
        loanService.addNewLoan(loan);
    }

    // ✅ Links a loan to a bank account.
    @PutMapping("connect/{loanId}/{bankAccountId}")
    public ResponseEntity<String> connectLoanToBank(
            @PathVariable int loanId,
            @PathVariable int bankAccountId) {
        try {
            Loan loan = loanService.getLoanById(loanId);
            if (loan == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Loan with ID " + loanId + " not found.");
            }

            Loan updatedLoan = loanService.connectLoanToBank(loan, bankAccountId);
            if (updatedLoan == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to connect loan to bank account.");
            }

            return ResponseEntity.ok("Loan successfully linked to bank account ID " + bankAccountId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Deletes a loan by ID.
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteLoan(@PathVariable int id) {
        try {
            loanService.deleteLoanTransaction(id); // Calls the service method to delete
            return ResponseEntity.ok("Loan with ID " + id + " has been deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ Retrieves all loans linked to a specific bank account.
    @GetMapping("account/{accountId}")
    public ResponseEntity<List<Loan>> getLoansByAccountId(@PathVariable int accountId) {
        List<Loan> loans = loanService.getLoansByAccountId(accountId);

        if (loans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(loans);
    }

    // ✅ Updates an existing loan.
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateLoan(@PathVariable int id, @RequestBody Loan updatedLoan) {
        Loan existingLoan = loanService.getLoanById(id);

        // ✅ Update only the fields that are not null in the request
        if (updatedLoan.getLoanAmount() != null) {
            existingLoan.setLoanAmount(updatedLoan.getLoanAmount());
        }
        if (updatedLoan.getInterestRate() != null) {
            existingLoan.setInterestRate(updatedLoan.getInterestRate());
        }
        if (updatedLoan.getStartPaymentDate() != null) {
            existingLoan.setStartPaymentDate(updatedLoan.getStartPaymentDate());
        }
        if (updatedLoan.getEndPaymentDate() != null) {
            existingLoan.setEndPaymentDate(updatedLoan.getEndPaymentDate());
        }
        if (updatedLoan.getDescription() != null) {  // ✅ Fix: Update description
            existingLoan.setDescription(updatedLoan.getDescription());
        }

        loanService.updateLoan(existingLoan);
        return ResponseEntity.ok("Loan updated successfully.");
    }

}