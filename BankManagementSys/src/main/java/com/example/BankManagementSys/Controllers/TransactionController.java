package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.LoanPayment;
import com.example.BankManagementSys.Entities.Transaction;
import com.example.BankManagementSys.Services.LoanPaymentService;
import com.example.BankManagementSys.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @GetMapping("getAll")
    public List<Transaction> getAll(){ return transactionService.getAllTransactions();}

    @GetMapping("get/{bankAccountId}")
    public List<Transaction> getByBankAccount(@PathVariable int bankAccountId) {
        return transactionService.getTransactionsByBankAccount(bankAccountId);
    }
}
