package com.example.BankManagementSys.Schedulers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Services.BankAccountService;
import com.example.BankManagementSys.Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OverdraftReminderScheduler {

    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private final EmailService emailService;

    public OverdraftReminderScheduler(BankAccountService bankAccountService, EmailService emailService) {
        this.bankAccountService = bankAccountService;
        this.emailService = emailService;
    }

    // ✅ Scheduled to run daily at 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyOverdraftReminders() {
        List<BankAccount> accounts = bankAccountService.getAllBankAccounts();

        for (BankAccount account : accounts) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                String emailBody = String.format(
                        "Dear %s,\n\nReminder: Your bank account (ID: %d) has a negative balance of %s ILS. " +
                                "Please make a deposit to restore your balance.\n\nThank you,\nYour Bank",
                        account.getCustomer().getName(), account.getId(), account.getBalance()
                );

                emailService.sendEmail(account.getCustomer().getEmail(), "📌 Daily Overdraft Reminder", emailBody);
                System.out.println("📧 Daily overdraft email sent to: " + account.getCustomer().getEmail());
            }
        }
    }
}
