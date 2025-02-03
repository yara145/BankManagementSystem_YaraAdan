package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Exceptions.BankAccountNotFoundException;
import com.example.BankManagementSys.Services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/bankAccounts")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    // ✅ Get all bank accounts
    @GetMapping("getAll")
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllAccounts());
    }

    // ✅ Get a bank account by ID
    @GetMapping("get/{id}")
    public ResponseEntity<BankAccount> getBankAccountById(@PathVariable int id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        return ResponseEntity.ok(bankAccount);
    }

    // ✅ Create a new bank account
    @PostMapping("add")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount bankAccount) {
        return ResponseEntity.ok(bankAccountService.createNewBankAccount(bankAccount));
    }

    // ✅ Update bank account details
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateBankAccount(@PathVariable int id, @RequestBody BankAccount updatedAccount) {
        updatedAccount.setId(id);
        bankAccountService.updateBankAccount(updatedAccount);
        return ResponseEntity.ok("Bank account updated successfully.");
    }

    // ✅ Delete a bank account (only if status is CLOSED)
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteBankAccount(@PathVariable int id) {
        BankAccount account = bankAccountService.getBankAccountById(id);
        bankAccountService.deleteAccount(account);
        return ResponseEntity.ok("Bank account deleted successfully.");
    }

    // 🔗 Get bank accounts by status
    @GetMapping("get/status/{status}")
    public ResponseEntity<List<BankAccount>> getAccountsByStatus(@PathVariable BankAccountStatus status) {
        return ResponseEntity.ok(bankAccountService.getAccountsByStatus(status));
    }

    // 🔗 Deposit funds into a bank account
    @PostMapping("deposit/{id}")
    public ResponseEntity<String> deposit(@PathVariable int id, @RequestParam BigDecimal amount) {
        boolean success = bankAccountService.updateBalance(id, amount, true, false);
        return success ? ResponseEntity.ok("Deposit successful.") : ResponseEntity.badRequest().body("Deposit failed.");
    }

    // 🔗 Withdraw funds from a bank account
    @PostMapping("withdraw/{id}")
    public ResponseEntity<String> withdraw(@PathVariable int id, @RequestParam BigDecimal amount) {
        boolean success = bankAccountService.updateBalance(id, amount, false, false);
        return success ? ResponseEntity.ok("Withdrawal successful.") : ResponseEntity.badRequest().body("Withdrawal failed.");
    }

    // 🔗 Get all bank accounts for a branch
    @GetMapping("get/branch/{branchId}")
    public ResponseEntity<List<BankAccount>> getBankAccountsByBranch(@PathVariable int branchId) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByBranchId(branchId));
    }

}
