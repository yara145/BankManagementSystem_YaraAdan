package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Reposityories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {
    @Value("${bank.account.overdraft-limit}")
    private BigDecimal overdraftLimit;

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    //________________________________C.R.U.D Functions___________________
    public BankAccount createNewBackAccount(BankAccount account)
    {
        if (account.getStatus() == null) {
            account.setStatus(BankAccountStatus.ACTIVE); // Default to ACTIVE status
        }

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO); // Initialize balance to 0
        }
        if (account.getCreatedDate() == null) {
            account.setCreatedDate(LocalDateTime.now()); // Ensure creation date is set
        }


     return this.bankAccountRepository.save(account);
    }

    public void UpdateBankAccount(BankAccount account)
    {
        // Check if balance exceeds overdraft limit
        if (account.getBalance().compareTo(overdraftLimit) < 0) {
            throw new IllegalArgumentException(
                    "balance exceeds the overdraft limit of " + overdraftLimit );
        }
        this.bankAccountRepository.save(account);
    }
    public List<BankAccount> allAccounts(){return this.bankAccountRepository.findAll();}
    /*public List<BankAccount>allCustomersAccounts(Customer customer)

    }*///check if i can do somthing like this..
    public void deleteAccount(BankAccount account)
    {
        if (!BankAccountStatus.CLOSED.equals(account.getStatus())) {
            throw new IllegalStateException("Only inactive accounts can be deleted.");
        }

        this.bankAccountRepository.delete(account);
    }


    //______________________________EmployeeToBankaccaunt____________________________________________________

    //C.R.U.D Functions
    public void addEmployeeToBankAccount(int bankAccountId, Employee employee) {
        Optional<BankAccount> bankAccountOptional = this.bankAccountRepository.findById(bankAccountId);
        if (bankAccountOptional.isEmpty()) {
            throw new IllegalArgumentException("BankAccount not found.");
        }

        BankAccount bankAccount = bankAccountOptional.get();

        // Add the employee to the bank account's list of employees
        if (!bankAccount.getEmployees().contains(employee)) {
            bankAccount.getEmployees().add(employee);
        }

        // Add the bank account to the employee's list of bank accounts
        if (!employee.getBankAccounts().contains(bankAccount)) {
            employee.getBankAccounts().add(bankAccount);
        }

        // Save changes
        this.bankAccountRepository.save(bankAccount);
        employeeService.updateEmployee(employee);

        System.out.println("Employee added to BankAccount successfully.");
    }

    public void removeEmployeeFromBankAccount(int bankAccountId, Employee employee) {
        Optional<BankAccount> bankAccountOptional = this.bankAccountRepository.findById(bankAccountId);
        if (bankAccountOptional.isEmpty()) {
            throw new IllegalArgumentException("BankAccount not found.");
        }

        BankAccount bankAccount = bankAccountOptional.get();

        // Remove the employee from the bank account's list
        bankAccount.getEmployees().remove(employee);

        // Remove the bank account from the employee's list
        employee.getBankAccounts().remove(bankAccount);

        // Save changes
        this.bankAccountRepository.save(bankAccount);
        employeeService.updateEmployee(employee);

        System.out.println("Employee removed from BankAccount successfully.");
    }
    public BankAccount getBankAccountById(int bankAccountId) {
        return bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new IllegalArgumentException("BankAccount not found with ID: " + bankAccountId));
    }


    //_____________________________________________accountToCustomer_________________________
    public List<BankAccount> getAccountsByCustomer(Customer customer) {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account.getCustomer().equals(customer))
                .toList();
    }
    //_____________________________________________accountToBranch_________________________
    public List<BankAccount> getAccountsByBranch(int branchId) {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account.getBranch().getId() == branchId)
                .toList();
    }
    //_____________________________________________other functions_________________________
    public List<BankAccount> getAccountsByStatus(BankAccountStatus status) {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account.getStatus().equals(status))
                .toList();
    }
}
