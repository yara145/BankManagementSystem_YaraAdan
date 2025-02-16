package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Enums.TransferStatus;
import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Exceptions.TransactionAmountInvalidException;
import com.example.BankManagementSys.Reposityories.TransferTransactionRepoistory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransferTransactionService {
    @Value("${transfer.max-amount}")
    private BigDecimal maxAmount;

    @Autowired
    private TransferTransactionRepoistory transferTransactionRepoistory;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private BankService bankService;
    @Autowired DepositTransactionService depositService;
    // ************ CRUD ******************

    // ** Add **
    @Transactional
    public TransferTransaction addNewTransferTransaction(TransferTransaction transfer) throws TransactionAmountInvalidException {
        if (transfer == null) {
            throw new IllegalArgumentException("TransferTransaction cannot be null.");
        }
        if (transfer.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new TransactionAmountInvalidException("Transfer amount must be greater than zero.");
        }
        if (transfer.getAmount().compareTo(maxAmount) > 0) {
            throw new TransactionAmountInvalidException("Transfer amount exceeds the allowed limit.");
        }

        // ✅ At this stage, we only have receiver info (NO sender account yet)
        BankAccount receiverAccount = bankAccountService.getBankAccountById(transfer.getReceiverAccountNum());
        if (receiverAccount == null) {
            throw new IllegalArgumentException("Receiver bank account does not exist.");
        }

        // ✅ Mark the transfer as pending until connected
        transfer.setTransactionDateTime(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.PENDING);

        // ✅ Only store receiver info, sender will be linked later
        return transferTransactionRepoistory.save(transfer);
    }



    @Transactional
    public TransferTransaction connectTransactionToBank(TransferTransaction transfer, int senderAccountId) {
        if (transfer == null) {
            throw new IllegalArgumentException("Transfer cannot be null.");
        }


        // ✅ Fetch sender and receiver accounts
        BankAccount senderAccount = bankAccountService.getBankAccountById(senderAccountId);
        if (senderAccount == null) {
            throw new IllegalArgumentException("Sender bank account does not exist.");
        }

        BankAccount receiverAccount = bankAccountService.getBankAccountById(transfer.getReceiverAccountNum());
        if (receiverAccount == null) {
            throw new IllegalArgumentException("Receiver bank account does not exist.");
        }

        // ✅ Ensure sender has sufficient funds before proceeding
        boolean withdrawSuccess = bankAccountService.updateBalance(senderAccount.getId(), transfer.getAmount().negate(), false, false);
        if (!withdrawSuccess) {
            throw new IllegalArgumentException("❌ Transfer failed: Insufficient funds in sender's account.");
        }

        // ✅ Create a deposit transaction for the receiver
        DepositTransaction deposit = new DepositTransaction();
        deposit.setBankAccount(receiverAccount);
        deposit.setDescription("Transfer from account " + senderAccount.getId());
        deposit.setDespositAmount(transfer.getAmount());

// ✅ Save the deposit transaction (but do not update balance yet)
        DepositTransaction savedDeposit = depositService.addNewDepositTransaction(deposit);
        if (savedDeposit == null) {
            throw new IllegalArgumentException("❌ Transfer failed: Deposit transaction could not be saved.");
        }

// ✅ Now, connect deposit to the bank account (this updates balance automatically)
        depositService.connectTransactionToBank(savedDeposit, receiverAccount.getId());



        // ✅ Now, mark the transfer as COMPLETED
        transfer.setTransferStatus(TransferStatus.COMPLETED);
        transfer.setTransactionDateTime(LocalDateTime.now());
        transfer.setDescription("Transfer To account " + receiverAccount.getId());
        deposit.setDescription("Transfer from account " + senderAccount.getId());

        // connect transfer to sender bank account
        transactionService.connectTransactionToBankAccount(transfer, senderAccountId);
        // ✅ Save the completed transfer
        return transferTransactionRepoistory.save(transfer);
    }















//
//    @Transactional
//    public TransferTransaction connectTransactionToBank(TransferTransaction transfer, int senderAccountId) {
//        if (transfer == null) {
//            throw new IllegalArgumentException("Transfer cannot be null.");
//        }
//
//        // ✅ Fetch sender account
//        BankAccount senderAccount = bankAccountService.getBankAccountById(senderAccountId);
//        if (senderAccount == null) {
//            throw new IllegalArgumentException("Sender bank account does not exist.");
//        }
//
//        // ✅ Fetch receiver account (already stored when transfer was created)
//        BankAccount receiverAccount = bankAccountService.getBankAccountById(transfer.getReceiverAccountNum());
//        if (receiverAccount == null) {
//            throw new IllegalArgumentException("Receiver bank account does not exist.");
//        }
//
//
//
//        // ✅ Ensure sender has sufficient funds before proceeding
//        boolean withdrawSuccess = bankAccountService.updateBalance(senderAccount.getId(), transfer.getAmount().negate(), false, false);
//        if (!withdrawSuccess) {
//            throw new IllegalArgumentException("❌ Transfer failed: Insufficient funds.");
//        }
//
//
//        BigDecimal transferAmount = transfer.getAmount();
//
//        // ✅ Perform deposit to receiver account
//        DepositTransaction deposit = new DepositTransaction();
//        deposit.setBankAccount(receiverAccount);
//        deposit.setDescription("Transfer from account number " + senderAccount.getId() ); // in order to show in user transactions
//        deposit.setDespositAmount(transfer.getAmount());
//        depositService.addNewDepositTransaction(deposit);
//        depositService.connectTransactionToBank(deposit,receiverAccount.getId());  // create new deposit for reciever
//        boolean depositSuccess = bankAccountService.updateBalance(receiverAccount.getId(), transfer.getAmount(), true, false);
//        if (!depositSuccess) {
//            System.err.println("❌ Transfer failed: Unable to deposit into receiver's account.");
//            return null;
//        }
//
//        // ✅ Update transfer status to COMPLETED
//        transfer.setTransferStatus(TransferStatus.COMPLETED);
//
//        transfer.setTransactionDateTime(LocalDateTime.now());
//        transfer.setDescription("Transfer To account number " + receiverAccount.getId() );
//        //Connect the transfer to the bank account
//        transactionService.connectTransactionToBankAccount(transfer, senderAccountId);
//
//        return transferTransactionRepoistory.save(transfer);
//    }






    /*
    @Transactional
    public TransferTransaction addNewTransferTransaction(TransferTransaction transfer) throws TransactionAmountInvalidException {
        if (transfer == null) {
            throw new IllegalArgumentException("TransferTransaction cannot be null.");
        }
        if (transfer.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new TransactionAmountInvalidException("Transfer amount must be greater than zero.");
        }
        if (transfer.getAmount().compareTo(maxAmount) > 0) {
            throw new TransactionAmountInvalidException("Transfer amount must be less than maxAmount.");
        }

        // Fetch all bank accounts
        List<BankAccount> allBankAccounts = bankAccountService.getAllBankAccounts();
        System.out.println("Fetched all bank accounts: " + allBankAccounts);

        // Find sender account
        Optional<BankAccount> senderAccountOpt = allBankAccounts.stream()
                .filter(account -> account.getId() == transfer.getBankAccount().getId())
                .findFirst();

        if (senderAccountOpt.isEmpty()) {
            throw new IllegalArgumentException("Sender bank account does not exist.");
        }
        BankAccount senderAccount = senderAccountOpt.get();

        // Find receiver account
        Optional<BankAccount> receiverAccountOpt = allBankAccounts.stream()
                .filter(account -> account.getId() == transfer.getReceiverAccountNum())
                .findFirst();

        if (receiverAccountOpt.isEmpty()) {
            throw new IllegalArgumentException("Receiver bank account does not exist.");
        }
        BankAccount receiverAccount = receiverAccountOpt.get();

        // Perform withdrawal
        boolean withdrawSuccess = bankAccountService.updateBalance(senderAccount.getId(), transfer.getAmount(), false, false);
        if (!withdrawSuccess) {
            System.err.println("❌ Transfer failed: Insufficient funds in sender's account.");
            return null;
        }

        // Perform deposit
        DepositTransaction deposit = new DepositTransaction();
        deposit.setBankAccount(receiverAccount);
        deposit.setDescription("Transfer from account number " + senderAccount.getId() ); // in order to show in user transactions
        deposit.setDespositAmount(transfer.getAmount());
        depositService.addNewDepositTransaction(deposit);
        depositService.connectTransactionToBank(deposit,receiverAccount.getId());  // create new deposit for reciever
//        boolean depositSuccess = bankAccountService.updateBalance(receiverAccount.getId(), transfer.getAmount(), true, false);
//        if (!depositSuccess) {
//            System.err.println("❌ Transfer failed: Unable to deposit into receiver's account.");
//            return null;
//        }

        // Set transfer status
        transfer.setTransferStatus(TransferStatus.PENDING);
        transfer.setTransactionDateTime(LocalDateTime.now());
        transfer.setDescription("Transfer To account number " + receiverAccount.getId() );

        return transferTransactionRepoistory.save(transfer);
    }
*/

    /**
     * Fetches a bank account using the bank, branch, and account hierarchy.
     */
    private BankAccount getBankAccountByHierarchy(int bankId, int branchId, int accountId) {
        // Get the bank
        Bank bank = bankService.getBankById(bankId);
        if (bank == null) {
            throw new IllegalArgumentException("Bank does not exist.");
        }

        // Get the branch from the bank
        Branch branch = bank.getBranches().stream()
                .filter(b -> b.getId() == branchId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Branch does not exist."));

        // Get the account from the branch
        BankAccount account = branch.getBankAccounts().stream()
                .filter(a -> a.getId() == accountId)
                .findFirst()
                .orElse(null);

        return account;
    }


    //** Read **

    // Get a transfer by ID
    public TransferTransaction getTransferById(int transferId) {
        return transferTransactionRepoistory.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer with ID " + transferId + " does not exist."));
    }

    // Get all Transfers
    public List<TransferTransaction> getAllTransfers() {
        return transferTransactionRepoistory.findAll();
    }
//
//    @Transactional
//    public TransferTransaction connectTransactionToBank(TransferTransaction transfer, int bankAccountId) {
//
//
//        BankAccount account = bankAccountService.getBankAccountById(bankAccountId);
//        if (account == null) {
//            throw new IllegalArgumentException("Transfer must be linked to a valid bank account.");
//        }
//
//        // ✅ Ensure the bank account is ACTIVE
//        if (account.getStatus() != BankAccountStatus.ACTIVE) {
//            throw new IllegalStateException("❌ Transfer failed: Bank account ID " + bankAccountId + " is" + account.getStatus() + ".");
//        }
//
//        // Connect the transfer to the bank account
//        transactionService.connectTransactionToBankAccount(transfer, bankAccountId);
//
//        // Add transfer-specific logic (e.g., default status)
//        transfer.setTransferStatus(TransferStatus.COMPLETED);
//
//        // Save and return the transaction
//        return transferTransactionRepoistory.save(transfer);
//    }

    // ✅ Retrieves all transfers linked to a specific bank account.
    public List<TransferTransaction> getTransfersByAccountId(int accountId) {
        return transferTransactionRepoistory.findByBankAccountId(accountId);
    }

}
