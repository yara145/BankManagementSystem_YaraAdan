package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Enums.TransferStatus;

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
        return  depositRepoistory.save(deposit);
    }


    //** Update **
    public DepositTransaction updateDepositTransaction(DepositTransaction deposit)  {

        if (deposit == null ) {
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
                .orElseThrow(() -> new IllegalArgumentException("Deposit with ID " +depositId + " does not exist."));

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

//    @Transactional
//    public DepositTransaction connectTransactionToBank(DepositTransaction deposit, int bankAccountId) {
//        // Connect the transfer to the bank account
//        transactionService.connectTransactionToBankAccount(deposit, bankAccountId);
//
//        // Ensure transaction is linked to a valid bank account
//        if (deposit.getBankAccount() == null) {
//            throw new IllegalArgumentException("Deposit transaction must be linked to a bank account.");
//        }
//
//        int accountId = deposit.getBankAccount().getId();
//
//        // Update bank account balance
//        boolean success = bankAccountService.updateBalance(accountId, deposit.getDespositAmount(), true, false);
//        if (!success) {
//            System.err.println("❌ Deposit failed for account ID: " + accountId);
//            return null; // Do not save the transaction if balance update failed
//        }
//        // Save and return the transaction
//        return depositRepoistory.save(deposit);
//    }

//    @Transactional
//    public DepositTransaction connectTransactionToBank(DepositTransaction deposit, int bankAccountId) {
//        transactionService.connectTransactionToBankAccount(deposit, bankAccountId);
//
//        BankAccount account = deposit.getBankAccount();
//        if (account == null) {
//            throw new IllegalArgumentException("Deposit transaction must be linked to a valid bank account.");
//        }
//
//        BigDecimal depositAmount = deposit.getDespositAmount();
//
//        // 🟢 Convert if account currency != transaction currency
//        if (!account.getCurrencyCode().equalsIgnoreCase(deposit.getCurrencyCode())) {
//            BigDecimal exchangeRate = currencyExchangeService.getExchangeRateForCurrency(account.getCurrencyCode());
//            depositAmount = depositAmount.multiply(exchangeRate);
//            deposit.setExchangeRate(exchangeRate); // Store exchange rate
//        }
//
//        // ✅ Update account balance in the correct currency
//        boolean success = bankAccountService.updateBalance(account.getId(), depositAmount, true, false);
//        if (!success) {
//            System.err.println("❌ Deposit failed for account ID: " + account.getId());
//            return null;
//        }
//
//        return depositRepoistory.save(deposit);
//    }




    @Transactional
    public DepositTransaction connectTransactionToBank(DepositTransaction deposit, int bankAccountId) {
        transactionService.connectTransactionToBankAccount(deposit, bankAccountId);

        BankAccount account = deposit.getBankAccount();
        if (account == null) {
            throw new IllegalArgumentException("Deposit transaction must be linked to a valid bank account.");
        }

        BigDecimal depositAmount = deposit.getDespositAmount();
        BigDecimal exchangeRate = currencyExchangeService.getExchangeRateForCurrency(deposit.getCurrencyCode());

        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid exchange rate received for currency: " + deposit.getCurrencyCode());
        }

        // ✅ Correct Conversion Logic
        if (!account.getCurrencyCode().equalsIgnoreCase(deposit.getCurrencyCode())) {
            if (account.getCurrencyCode().equalsIgnoreCase("ILS")) {
                // ✅ Convert from EUR → ILS (Divide)
                depositAmount = depositAmount.divide(exchangeRate, 6, RoundingMode.HALF_UP);
            } else {
                // ✅ Convert from ILS → Other Currency (Multiply)
                depositAmount = depositAmount.multiply(exchangeRate);
            }
        }

        // ✅ Ensure correct rounding AFTER conversion
        depositAmount = depositAmount.setScale(2, RoundingMode.HALF_UP);

        // 🟢 Store the correct exchange rate in the transaction
        deposit.setExchangeRate(exchangeRate);
        deposit.setDespositAmount(depositAmount);

        // ✅ Update the account balance with the correct converted amount
        boolean success = bankAccountService.updateBalance(account.getId(), depositAmount, true, false);
        if (!success) {
            System.err.println("❌ Deposit failed for account ID: " + account.getId());
            return null;
        }

        return depositRepoistory.save(deposit);
    }







}
