package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Reposityories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankAccountService {

    @Value("${bank.account.overdraft-limit}")
    private BigDecimal overdraftLimit;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    // ____________________________________ C.R.U.D FUNCTIONS ____________________________________

    /**
     * Create a new BankAccount with default values if not provided.
     */
    public BankAccount createNewBankAccount(BankAccount account) {
        if (account.getStatus() == null) {
            account.setStatus(BankAccountStatus.ACTIVE); // Default status is ACTIVE
        }
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO); // Default balance is 0
        }
        if (account.getCreatedDate() == null) {
            account.setCreatedDate(LocalDateTime.now()); // Default creation date is now
        }
        System.out.println("******************bank account created successfully************");
        return bankAccountRepository.save(account);
    }

    /**
     * Update an existing BankAccount's details.
     */
    public void updateBankAccount(BankAccount account) { // Updated method name to follow Java conventions
        if (account.getBalance().compareTo(overdraftLimit) < 0) {
            throw new IllegalArgumentException("Balance exceeds the overdraft limit of " + overdraftLimit);
        }
        bankAccountRepository.save(account);
    }

    /**
     * Get a list of all BankAccounts.
     */
    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }

    /**
     * Delete a BankAccount if it is closed.
     */
    public void deleteAccount(BankAccount account) {
        if (!BankAccountStatus.CLOSED.equals(account.getStatus())) {
            throw new IllegalStateException("Only closed accounts can be deleted.");
        }
        bankAccountRepository.delete(account);
    }

    // ______________________________________ ACCOUNT QUERIES ______________________________________

    /**
     * Get BankAccount by its ID.
     */
    public BankAccount getBankAccountById(int bankAccountId) {
        return bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new IllegalArgumentException("BankAccount not found with ID: " + bankAccountId));
    }

    /**
     * Get all BankAccounts associated with a specific Customer.
     */
    public List<BankAccount> getAccountsByCustomer(Customer customer) {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account.getCustomer().equals(customer))
                .toList();
    }

    /**
     * Get all BankAccounts associated with a specific branch.
     */
    public List<BankAccount> getAccountsByBranch(int branchId) {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account.getBranch().getId() == branchId)
                .toList();
    }

    /**
     * Get all BankAccounts by their status.
     */
    public List<BankAccount> getAccountsByStatus(BankAccountStatus status) {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account.getStatus().equals(status))
                .toList();
    }
}
