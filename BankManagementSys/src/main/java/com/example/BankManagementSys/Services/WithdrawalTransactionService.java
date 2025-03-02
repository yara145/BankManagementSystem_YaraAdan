package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Enums.TransferStatus;

import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Exceptions.WithdrawalTransactionNotFoundException;
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
    public WithdrawalTransaction connectTransactionToBank(int withdrawalId, int bankAccountId) {
        WithdrawalTransaction withdrawal = this.getWithdrawalById(withdrawalId);
        if (withdrawal == null) {
            throw new WithdrawalTransactionNotFoundException("Withdrawal transaction with ID " + withdrawalId + " not found.");
        }
        BankAccount account = bankAccountService.getBankAccountById(bankAccountId);

        // ✅ Ensure the account exists BEFORE using it
        if (account == null) {
            throw new IllegalArgumentException("Withdrawal transaction must be linked to a valid bank account.");
        }

        // ✅ Ensure the bank account is ACTIVE
        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new IllegalStateException("❌ Withdrawal failed: Bank account ID " + bankAccountId + " is " + account.getStatus() + ".");
        }

        transactionService.connectTransactionToBankAccount(withdrawal, bankAccountId);

        BigDecimal withdrawalAmount = withdrawal.getWithdrawalAmount();
        BigDecimal exchangeRate = currencyExchangeService.getExchangeRateForCurrency(withdrawal.getCurrencyCode());

        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid exchange rate received for currency: " + withdrawal.getCurrencyCode());
        }
        withdrawal.setDescription("Withdrawal of " + withdrawal.getWithdrawalAmount() +" "+ withdrawal.getCurrencyCode());
        // ✅ Convert only if the withdrawal currency is different from the account currency
        if (!account.getCurrencyCode().equalsIgnoreCase(withdrawal.getCurrencyCode())) {
            withdrawalAmount = withdrawalAmount.divide(exchangeRate, 6, RoundingMode.HALF_UP);
        }

        // ✅ Ensure rounding AFTER conversion
        withdrawalAmount = withdrawalAmount.setScale(2, RoundingMode.HALF_UP);

        System.out.println("🚀 DEBUG: Withdrawal Transaction");
        System.out.println("Original Withdrawal Amount: " + withdrawal.getWithdrawalAmount() + " " + withdrawal.getCurrencyCode());
        System.out.println("Converted Withdrawal Amount: " + withdrawalAmount + " " + account.getCurrencyCode());

        if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("❌ Error: Withdrawal amount must be greater than zero after conversion.");
        }

        // ✅ Store the actual exchange rate used
        BigDecimal finalExchangeRate = withdrawal.getWithdrawalAmount().divide(withdrawalAmount, 6, RoundingMode.HALF_UP);
        withdrawal.setExchangeRate(finalExchangeRate);
        withdrawal.setWithdrawalAmount(withdrawalAmount);


        // ✅ Pass positive withdrawalAmount, let updateBalance handle subtraction
        boolean success = bankAccountService.updateBalance(account.getId(), withdrawalAmount, false, false);
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






