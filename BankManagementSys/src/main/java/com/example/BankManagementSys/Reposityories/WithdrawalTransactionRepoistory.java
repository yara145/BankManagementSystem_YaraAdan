package com.example.BankManagementSys.Reposityories;



import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BankManagementSys.Entities.WithdrawalTransaction;

public interface WithdrawalTransactionRepoistory extends JpaRepository <WithdrawalTransaction,Integer> {

}
