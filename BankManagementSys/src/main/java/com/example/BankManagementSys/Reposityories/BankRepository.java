package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank,Integer> {
}
