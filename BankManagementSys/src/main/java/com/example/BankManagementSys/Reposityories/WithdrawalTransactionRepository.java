package com.example.BankManagementSys.Reposityories;



import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;

import java.util.Optional;

public interface WithdrawalTransactionRepository extends JpaRepository <WithdrawalTransaction,Integer> {
    Optional<WithdrawalTransaction> findByTransactionId(int transactionId);
}
