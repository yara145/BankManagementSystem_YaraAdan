package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Loan;
import com.example.BankManagementSys.Entities.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {

    // ✅ Correct method to find payments by bank account ID
    List<LoanPayment> findByLoan_BankAccount_Id(int bankAccountId);

    // ✅ Correct method to find a loan payment by paymentNumber
    LoanPayment findByPaymentNumber(int paymentNumber);
}
