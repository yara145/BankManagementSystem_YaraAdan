package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount,Integer> {
    List<BankAccount> findByBranchId(int branchId);
}
