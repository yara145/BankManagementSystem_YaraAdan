package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Enums.TransferStatus;

import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.DepositTransactionRepository;
import com.example.BankManagementSys.Reposityories.WithdrawalTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalTransactionService {
    @Value("${withdrawal.max-amount}")
    private BigDecimal maxAmount;

    @Autowired
    private WithdrawalTransactionRepository withdrawalRepoistory;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private CurrencyExchangeService currencyExchangeService; // Injects the CurrencyExchangeService to handle currency exchange operations
    // ************ CRUD ******************

    // ** Add **
    public WithdrawalTransaction addNewWithdrawalTransaction(WithdrawalTransaction withdrawal) {
        if (withdrawal == null) {
            throw new IllegalArgumentException("❌ Error: Withdrawal transaction cannot be null.");
        }
        if (withdrawal.getWithdrawalAmount() == null || withdrawal.getWithdrawalAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new TransactionAmountInvalidException("❌ Withdrawal amount must be greater than zero.");
        }
        if (maxAmount == null || withdrawal.getWithdrawalAmount().compareTo(maxAmount) > 0) {
            throw new TransactionAmountInvalidException("❌ Withdrawal amount exceeds the allowed maximum.");
        }

        // ✅ Ensure transaction date is set
        withdrawal.setTransactionDateTime(LocalDateTime.now());

        // ✅ Log for debugging
        System.out.println("📌 Saving Withdrawal: " + withdrawal);

        return withdrawalRepoistory.save(withdrawal);
    }


    //** Read **

    // Get a Withdrawal by ID
    public WithdrawalTransaction getWithdrawalById(int withdrawalId) {

        return withdrawalRepoistory.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawa with ID " + withdrawalId + " does not exist."));

    }

    // Get all Withdrawal
    public List<WithdrawalTransaction> getAllWithdrawals() {
        return withdrawalRepoistory.findAll();

    }

    @Transactional
    public WithdrawalTransaction connectTransactionToBank(WithdrawalTransaction withdrawal, int bankAccountId) {
        BankAccount account = bankAccountService.getBankAccountById(bankAccountId);
        // ✅ Ensure the bank account is ACTIVE
        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new IllegalStateException("❌ Withdrawal failed: Bank account ID " + bankAccountId + " is " + account.getStatus() + ".");
        }

        if (account == null) {
            throw new IllegalArgumentException("Withdrawal transaction must be linked to a valid bank account.");
        }

        transactionService.connectTransactionToBankAccount(withdrawal, bankAccountId);



        BigDecimal withdrawalAmount = withdrawal.getWithdrawalAmount();
        BigDecimal exchangeRate = currencyExchangeService.getExchangeRateForCurrency(withdrawal.getCurrencyCode());

        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid exchange rate received for currency: " + withdrawal.getCurrencyCode());
        }

        // ✅ Correct Conversion Logic
        if (!account.getCurrencyCode().equalsIgnoreCase(withdrawal.getCurrencyCode())) {
            if (withdrawal.getCurrencyCode().equalsIgnoreCase("ILS")) {
                // Convert ILS → EUR (Divide by 3.7037)
                withdrawalAmount = withdrawalAmount.divide(BigDecimal.valueOf(3.7037), 6, RoundingMode.HALF_UP);
            } else {
                // Convert EUR → ILS (Multiply by 3.7037)
                withdrawalAmount = withdrawalAmount.multiply(BigDecimal.valueOf(3.7037));
            }
        }

        // ✅ Ensure rounding AFTER conversion
        withdrawalAmount = withdrawalAmount.setScale(2, RoundingMode.HALF_UP);

        System.out.println("🚀 DEBUG: Withdrawal Transaction");
        System.out.println("Original Withdrawal Amount: " + withdrawal.getWithdrawalAmount() + " " + withdrawal.getCurrencyCode());
        System.out.println("Converted Withdrawal Amount: " + withdrawalAmount + " " + account.getCurrencyCode());

        if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("❌ Error: Withdrawal amount must be greater than zero after conversion.");
        }

        // ✅ Store correct exchange rate and final withdrawal amount
        withdrawal.setExchangeRate(exchangeRate);
        withdrawal.setWithdrawalAmount(withdrawalAmount);

        // ✅ Properly negate withdrawal amount before updating balance
        boolean success = bankAccountService.updateBalance(account.getId(), withdrawalAmount.negate(), false, true);
        if (!success) {
            System.err.println("❌ Withdrawal failed for account ID: " + account.getId());
            return null;
        }

        return withdrawalRepoistory.save(withdrawal);
    }



    public List<WithdrawalTransaction> getWithdrawalsByAccountId(int accountId) {
        return withdrawalRepoistory.findByBankAccountId(accountId);
    }
}




//    @Transactional
//    public WithdrawalTransaction connectTransactionToBank(WithdrawalTransaction withdrawal, int bankAccountId) {
//        // Connect the transfer to the bank account
//        transactionService.connectTransactionToBankAccount(withdrawal, bankAccountId);
//
//        // Ensure transaction is linked to a valid bank account
//        if (withdrawal.getBankAccount() == null) {
//            throw new IllegalArgumentException("Withdrawal transaction must be linked to a bank account.");
//        }
//
//        int accountId = withdrawal.getBankAccount().getId();
//
//        // Update bank account balance
//        boolean success = bankAccountService.updateBalance(accountId, withdrawal.getWithdrawalAmount(), false, false);
//        if (!success) {
//            System.err.println("❌ Withdrawal failed for account ID: " + accountId);
//            return null; // Do not save the transaction if balance update failed
//        }
//
//
//        // Save and return the transaction
//        return withdrawalRepoistory.save(withdrawal);
//    }




//    @Transactional
//    public WithdrawalTransaction connectTransactionToBank(WithdrawalTransaction withdrawal, int bankAccountId) {
//        // Connect the transfer to the bank account
//        transactionService.connectTransactionToBankAccount(withdrawal, bankAccountId);
//
//        // Ensure transaction is linked to a valid bank account
//        BankAccount account = withdrawal.getBankAccount();
//        if (account == null) {
//            throw new IllegalArgumentException("Withdrawal transaction must be linked to a valid bank account.");
//        }
//
//        // ✅ Fixed: Correctly handle currency conversion
//        BigDecimal withdrawalAmount = withdrawal.getWithdrawalAmount();
//        BigDecimal exchangeRate = currencyExchangeService.getExchangeRateForCurrency(withdrawal.getCurrencyCode());
//
//        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
//            throw new IllegalArgumentException("Invalid exchange rate received for currency: " + withdrawal.getCurrencyCode());
//        }
//
//        // ✅ Fixed: Ensure correct conversion logic based on target currency
//        if (!account.getCurrencyCode().equalsIgnoreCase(withdrawal.getCurrencyCode())) {
//            if (withdrawal.getCurrencyCode().equalsIgnoreCase("ILS")) {
//                // ✅ Convert from ILS → Foreign Currency (Multiply)
//                withdrawalAmount = withdrawalAmount.multiply(exchangeRate);
//            } else {
//                // ✅ Convert from Foreign Currency → ILS (Divide)
//                withdrawalAmount = withdrawalAmount.divide(exchangeRate, 6, RoundingMode.HALF_UP);
//            }
//        }
//
//
//        // ✅ Ensure rounding AFTER conversion
//        withdrawalAmount = withdrawalAmount.setScale(2, RoundingMode.HALF_UP);
//
//        // ✅ Debugging Logs to Check Values
//        System.out.println("🚀 DEBUG: Withdrawal Transaction");
//        System.out.println("Original Withdrawal Amount: " + withdrawal.getWithdrawalAmount() + " " + withdrawal.getCurrencyCode());
//        System.out.println("Converted Withdrawal Amount: " + withdrawalAmount + " " + account.getCurrencyCode());
//
//        if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("❌ Error: Withdrawal amount must be greater than zero after conversion.");
//        }
//
//        // ✅ Store the correct exchange rate in the transaction
//        withdrawal.setExchangeRate(exchangeRate);
//        withdrawal.setWithdrawalAmount(withdrawalAmount);
//
//        // ✅ Fixed: Properly negate the amount and round before updating balance
//        boolean success = bankAccountService.updateBalance(account.getId(), withdrawalAmount.negate().setScale(2, RoundingMode.HALF_UP), false, true);
//        if (!success) {
//            System.err.println("❌ Withdrawal failed for account ID: " + account.getId());
//            return null; // Do not save the transaction if balance update failed
//        }
//
//        return withdrawalRepoistory.save(withdrawal);
//    }
