package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionRepoistory extends JpaRepository<TransferTransaction,Integer> {


}
