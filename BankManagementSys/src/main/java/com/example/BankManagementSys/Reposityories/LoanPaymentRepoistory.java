package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanPaymentRepoistory extends JpaRepository<LoanPayment, Integer> {
}
