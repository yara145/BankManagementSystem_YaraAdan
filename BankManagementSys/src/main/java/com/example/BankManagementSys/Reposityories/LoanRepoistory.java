package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepoistory extends JpaRepository<Loan,Integer>{
}
