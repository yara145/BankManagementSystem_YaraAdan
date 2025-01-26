package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositTransactionRepository extends JpaRepository<DepositTransaction, Integer> {
    DepositTransaction findByTransactionId(int transactionId);
}
