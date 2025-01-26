package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Entities.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan,Integer>{
    //List<Loan> findByBankAccountId(int bankAccountId);

    Loan findByTransactionId(int transactionId);
}
