package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {
    //List<LoanPayment> findByLoanLoanId(String loanId);
}
