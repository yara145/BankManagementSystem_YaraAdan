package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,Integer> {
}
