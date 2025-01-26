package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepoistory extends JpaRepository<Transaction,Integer> {

    //List<Transaction> findByBankAccountId(int bankAccountId);

    Transaction findByTransactionId(int transactionId);


}
