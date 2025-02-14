package com.example.BankManagementSys.Schedulers;

import com.example.BankManagementSys.Services.LoanPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component // Makes this class a Spring-managed Bean
public class LoanPaymentScheduler {

    @Autowired
    private LoanPaymentService loanPaymentService;

    // ✅ Runs on the 1st day of every month at midnight
    //@Scheduled(cron = "0 0 0 1 * ?")
    @Scheduled(cron = "*/1 * * * * ?") // Runs every second
    //@Scheduled(cron = "0 * * * * ?") // Runs every minute (FOR TESTING ONLY)
    public void scheduleLoanPayments() {
        System.out.println("🔄 Processing monthly loan payments...");
        loanPaymentService.processMonthlyLoanPayments(); // Calls the service method
        System.out.println("✅ Monthly loan payments processed!");
    }
}
