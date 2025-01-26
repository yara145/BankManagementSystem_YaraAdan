package com.example.BankManagementSys.Reposityories;



import com.example.BankManagementSys.Entities.DepositTransaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;

public interface WithdrawalTransactionRepository extends JpaRepository <WithdrawalTransaction,Integer> {
    WithdrawalTransaction findByTransactionId(int transactionId);
}
