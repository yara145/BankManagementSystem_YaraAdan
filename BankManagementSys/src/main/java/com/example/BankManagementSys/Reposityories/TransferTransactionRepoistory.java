package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Entities.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferTransactionRepoistory extends JpaRepository<TransferTransaction,Integer> {

//    @Query("SELECT t FROM TransferTransaction t WHERE t.bankAccount.id = :bankAccountId")
//    List<TransferTransaction> findByBankAccountId(@Param("bankAccountId") int bankAccountId);

    TransferTransaction findByTransactionId(int transactionId);
}
