package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Enums.TransferStatus;

import com.example.BankManagementSys.Exceptions.DepositTransactionNotFoundException;
import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.DepositTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepositTransactionService {
    @Value("${deposit.max-amount}")
    private BigDecimal maxAmount;

    @Autowired
    private DepositTransactionRepository depositRepoistory;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private CurrencyExchangeService currencyExchangeService; // Injects the CurrencyExchangeService to handle currency exchange operations

    // ************ CRUD ******************

    // ** Add Deposit Transaction **
    public DepositTransaction addNewDepositTransaction(DepositTransaction deposit) {
        if (deposit == null) {
            throw new IllegalArgumentException("Deposit cannot be null.");
        }
        if (deposit.getDespositAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new TransactionAmountInvalidException("Deposit amount must be greater than zero.");
        }
        if (deposit.getDespositAmount().compareTo(maxAmount) > 0) {
            throw new TransactionAmountInvalidException("Deposit amount exceeds the allowed maximum.");
        }


        deposit.setTransactionDateTime(LocalDateTime.now());
        deposit.setDescription("Deposit of " + deposit.getDespositAmount() +" "+ deposit.getCurrencyCode());
        return depositRepoistory.save(deposit);
    }


    //** Update **
    public DepositTransaction updateDepositTransaction(DepositTransaction deposit) {

        if (deposit == null) {
            throw new IllegalArgumentException("Deposit cannot be null.");
        }
        if (!depositRepoistory.existsById(deposit.getTransactionId())) {
            throw new IllegalArgumentException("Deposit with ID " + deposit.getTransactionId() + " does not exist.");
        }
        return depositRepoistory.save(deposit);
    }


    //** Delete **
    public void deleteDepositTransaction(int depositId) {

        // Find the transaction
        DepositTransaction deposit = depositRepoistory.findByTransactionId(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal with ID " + depositId + " does not exist."));

        // Perform the delete operation
        depositRepoistory.delete(deposit);

    }

    //** Read **

    // Get a Deposit by ID
    public DepositTransaction getDepoistById(int depositId) {

        return depositRepoistory.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Deposit with ID " + depositId + " does not exist."));

    }

    // Get all Deposits
    public List<DepositTransaction> getAllDeposits() {
        return depositRepoistory.findAll();

    }
    //**************** Other Functions *****************

    // ✅ Retrieves all deposits associated with a specific bank account.
    public List<DepositTransaction> getDepositsByAccountId(int accountId) {
        return depositRepoistory.findByBankAccountId(accountId);
    }

    @Transactional
    public DepositTransaction connectTransactionToBank(int depositId, int bankAccountId) {

        DepositTransaction deposit = this.getDepoistById(depositId);
        if (deposit == null) {
            throw new DepositTransactionNotFoundException("Deposit transaction with ID " + depositId + " not found.");
        }
        transactionService.connectTransactionToBankAccount(deposit, bankAccountId);

        BankAccount account = bankAccountService.getBankAccountById(bankAccountId);
        if (account == null) {
            throw new IllegalArgumentException("Deposit transaction must be linked to a valid bank account.");
        }

        // ✅ Ensure the bank account is either ACTIVE or RESTRICTED
        if (account.getStatus() != BankAccountStatus.ACTIVE && account.getStatus() != BankAccountStatus.RESTRICTED) {
            throw new IllegalStateException("❌ Deposit failed: Bank account ID " + bankAccountId + " is " + account.getStatus() + ".");
        }

            BigDecimal depositAmount = deposit.getDespositAmount();
            BigDecimal exchangeRate = currencyExchangeService.getExchangeRateForCurrency(deposit.getCurrencyCode());

            if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Invalid exchange rate received for currency: " + deposit.getCurrencyCode());
            }


        // ✅ Correct conversion logic (supports all currencies)
        if (!account.getCurrencyCode().equalsIgnoreCase(deposit.getCurrencyCode())) {
            if (deposit.getCurrencyCode().equalsIgnoreCase("ILS")) {
                // ✅ Convert ILS → Account Currency
                depositAmount = depositAmount.multiply(exchangeRate);
            } else {
                // ✅ Convert Other Currency → Account Currency
                depositAmount = depositAmount.divide(exchangeRate, 6, RoundingMode.HALF_UP);
            }
        }



        // ✅ Ensure rounding AFTER conversion
            depositAmount = depositAmount.setScale(2, RoundingMode.HALF_UP);

            // ✅ Store exchange rate and converted deposit amount
            deposit.setExchangeRate(exchangeRate);
            deposit.setDespositAmount(depositAmount);

            // ✅ Update balance correctly
            boolean success = bankAccountService.updateBalance(account.getId(), depositAmount, true, false);
            if (!success) {
                System.err.println("❌ Deposit failed for account ID: " + account.getId());
                return null;
            }

            return depositRepoistory.save(deposit);

        }

}







