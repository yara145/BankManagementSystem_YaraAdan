package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Exceptions.BranchNotFoundException;
import com.example.BankManagementSys.Services.BankAccountService;
import com.example.BankManagementSys.Services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;
    @Autowired
    private BankAccountService bankAccountService;

    // ✅ Get all branches
    @GetMapping("getAll")
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    // ✅ Get a branch by ID
    @GetMapping("get/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable int id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }
    // ✅ Links a bank account to a branch.
    @PutMapping("connect/{branchId}/bankAccount/{bankAccountId}")
    public ResponseEntity<String> connectBankAccountToBranch(
            @PathVariable int branchId,
            @PathVariable int bankAccountId) {
        try {
            Branch branch = branchService.getBranchById(branchId);
            if (branch == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Branch with ID " + branchId + " not found.");
            }

            branchService.addBankAccountToBranch(branchId, bankAccountService.getBankAccountById(bankAccountId));
            return ResponseEntity.ok("Bank account successfully linked to branch ID " + branchId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Removes a bank account from a branch.
    @DeleteMapping("disconnect/{branchId}/bankAccount/{bankAccountId}")
    public ResponseEntity<String> disconnectBankAccountFromBranch(
            @PathVariable int branchId,
            @PathVariable int bankAccountId) {
        try {
            Branch branch = branchService.getBranchById(branchId);
            if (branch == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Branch with ID " + branchId + " not found.");
            }

            BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);
            if (bankAccount == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Bank account with ID " + bankAccountId + " not found.");
            }

            branch.getBankAccounts().remove(bankAccount);
            bankAccount.setBranch(null);

            branchService.updateBranch(branch);
            bankAccountService.updateBankAccount(bankAccount);

            return ResponseEntity.ok("Bank account successfully unlinked from branch ID " + branchId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Create a new branch
    @PostMapping("add")
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        return ResponseEntity.ok(branchService.createNewBranch(branch));
    }

    // ✅ Update branch details
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateBranch(@PathVariable int id, @RequestBody Branch updatedBranch) {
        updatedBranch.setId(id);
        branchService.updateBranch(updatedBranch);
        return ResponseEntity.ok("Branch updated successfully.");
    }

    // ✅ Delete a branch (only if it has no associated bank accounts)
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable int id) {
        Branch branch = branchService.getBranchById(id);
        branchService.deleteBranch(branch);
        return ResponseEntity.ok("Branch deleted successfully.");
    }

    // 🔗 Get all bank accounts for a branch
    @GetMapping("get/{id}/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccountsForBranch(@PathVariable int id) {
        return ResponseEntity.ok(branchService.getBankAccountsForBranch(id));
    }

    // 🔗 Add a bank account to a branch
    @PostMapping("add/{id}/accounts")
    public ResponseEntity<String> addBankAccountToBranch(@PathVariable int id, @RequestBody BankAccount bankAccount) {
        branchService.addBankAccountToBranch(id, bankAccount);
        return ResponseEntity.ok("Bank account added to branch successfully.");
    }

}
