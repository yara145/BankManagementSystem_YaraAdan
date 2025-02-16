package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Exceptions.BankAccountNotFoundException;
import com.example.BankManagementSys.Reposityories.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.BankManagementSys.Entities.BankAccount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.example.BankManagementSys.Exceptions.EmployeeNotFoundException;
import com.example.BankManagementSys.Entities.Employee;
import org.springframework.scheduling.annotation.Scheduled;
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
            account.setStatus(BankAccountStatus.ACTIVE);
        }
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getCreatedDate() == null) {
            account.setCreatedDate(LocalDateTime.now());
        }

        // 🚀 Do NOT set branch or customer here (optional)
        System.out.println("✅ Bank account created successfully");
        return bankAccountRepository.save(account);
    }

    public BankAccount saveBankAccount(BankAccount bankAccount) {
        // Check if the account already exists by ID
        if (bankAccount.getId() > 0) {
            return updateBankAccount(bankAccount); // Update if exists
        } else {
            return createNewBankAccount(bankAccount); // Create new if doesn't exist
        }
    }
    /**
     * Update an existing BankAccount's details.
     */
    public BankAccount updateBankAccount(BankAccount account) { // Updated method name to follow Java conventions
        if (account.getBalance().compareTo(overdraftLimit) < 0) {
            throw new IllegalArgumentException("Balance exceeds the overdraft limit of " + overdraftLimit);
        }
        return bankAccountRepository.save(account);
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
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount with ID " + bankAccountId + " not found."));
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


    public boolean updateBalance(int accountId, BigDecimal amount, boolean isDeposit, boolean isLoanPayment) {
        try {
            // Fetch account and validate existence
            BankAccount account = bankAccountRepository.findById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found."));

            // Validate account status
            if (account.getStatus() != BankAccountStatus.ACTIVE) {
                System.err.println("❌ Transaction failed: Account is not active.");
                return false;
            }

            // ✅ FIX: Allow negative amounts (withdrawals), but block zero values
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                System.err.println("❌ Transaction failed: Amount must be greater than zero.");
                return false;
            }

            if (isDeposit) {
                // Perform deposit
                account.setBalance(account.getBalance().add(amount));
                System.out.println("✅ Deposit successful. New balance: " + account.getBalance());
            } else {
                // ** Check Overdraft Limit (Only for Regular Withdrawals) **
                BigDecimal newBalance = account.getBalance().add(amount); // ✅ "amount" is already negative for withdrawals

                // 🔍 Debugging: Print values before checking overdraft condition
                boolean isBelowOverdraft = newBalance.compareTo(overdraftLimit) < 0;
                boolean shouldSendEmail = !isLoanPayment && isBelowOverdraft;

                System.out.println("🔍 isLoanPayment: " + isLoanPayment);
                System.out.println("🔍 newBalance: " + newBalance);
                System.out.println("🔍 overdraftLimit: " + overdraftLimit);
                System.out.println("🔍 newBalance.compareTo(overdraftLimit) < 0 -> " + isBelowOverdraft);
                System.out.println("🔍 Condition (!isLoanPayment && newBalance < overdraftLimit) -> " + shouldSendEmail);
                account.setBalance(newBalance);
                if (shouldSendEmail) { // ✅ Send email notification
                    System.out.println("📧 Sending overdraft email...");
                    sendOverdraftEmail(account);
                }

                // Perform withdrawal

                System.out.println("✅ Withdrawal successful. New balance: " + account.getBalance());
            }

            // Update the account balance in the database
            updateBankAccount(account);

            // Return success
            return true;

        } catch (Exception e) {
            // Catch all exceptions and return failure
            System.err.println("❌ Unexpected error: " + e.getMessage());
            return false;
        }
    }


    // Fetch all bank accounts
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }
    // Fetch bank accounts by branch
    public List<BankAccount> getBankAccountsByBranchId(int branchId) {
        return bankAccountRepository.findByBranchId(branchId);
    }

    @Autowired
    private EmailService emailService;

    private void sendOverdraftEmail(BankAccount account) {
        System.out.println("seeeeeeeeeeeeeeend emaaaailllllllll: "+account);
        String emailBody = String.format(
                "Dear %s,\n\nYour bank account (ID: %d) has exceeded the overdraft limit of %s ILS. " +
                        "Your current balance is %s ILS.\n\nPlease deposit funds to avoid additional penalties.\n\nThank you,\nYour Bank",
                account.getCustomer().getName(), account.getId(), overdraftLimit, account.getBalance()
        );

        emailService.sendEmail(account.getCustomer().getEmail(), "⚠️ Overdraft Alert", emailBody);
        System.out.println("📧 Sent overdraft alert to: " + account.getCustomer().getEmail());
    }




}




























//    public boolean updateBalance(int accountId, BigDecimal amount, boolean isDeposit, boolean isLoanPayment) {
//        try {
//            // Fetch account and validate existence
//            BankAccount account = bankAccountRepository.findById(accountId)
//                    .orElseThrow(() -> new IllegalArgumentException("Account not found."));
//
//            // Validate account status
//            if (account.getStatus() != BankAccountStatus.ACTIVE) {
//                System.err.println(" Transaction failed: Account is not active.");
//                return false;
//            }
//
//            // Ensure positive transaction amount (assuming validation is done before calling this)
//            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//                System.err.println(" Transaction failed: Amount must be greater than zero.");
//                return false;
//            }
//
//            if (isDeposit) {
//                // Perform deposit
//                account.setBalance(account.getBalance().add(amount));
//                System.out.println(" Deposit successful. New balance: " + account.getBalance());
//            } else {
//                // ** Check Overdraft Limit (Only for Regular Withdrawals) **
//                BigDecimal newBalance = account.getBalance().subtract(amount);
//
//                if (!isLoanPayment && newBalance.compareTo(overdraftLimit) < 0) {
//                    System.err.println(" Transaction failed: Overdraft limit exceeded.");
//                    return false;
//                }
//
//                // Perform withdrawal
//                account.setBalance(newBalance);
//                System.out.println(" Withdrawal successful. New balance: " + account.getBalance());
//            }
//
//            // Update the account balance in the database
//            updateBankAccount(account);
//
//            // Return success
//            return true;
//
//        } catch (Exception e) {
//            // Catch all exceptions and return failure
//            System.err.println(" Unexpected error: " + e.getMessage());
//            return false;
//        }
//
//    }
