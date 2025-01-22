package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.DepositTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespositTransactionRepoistory extends JpaRepository<DepositTransaction, Integer> {
}
