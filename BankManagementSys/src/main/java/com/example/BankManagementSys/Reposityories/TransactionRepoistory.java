package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepoistory extends JpaRepository<Transaction,Integer> {
}
