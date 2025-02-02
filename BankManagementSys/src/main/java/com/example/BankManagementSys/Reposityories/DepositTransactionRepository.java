package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepositTransactionRepository extends JpaRepository<DepositTransaction, Integer> {
    Optional<DepositTransaction> findByTransactionId(int transactionId);


    // ✅ Finds all deposit transactions for a specific bank account.
    List<DepositTransaction> findByBankAccountId(int bankAccountId);
}
