package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManyToManyRelationService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private BranchService branchService;


    public void addEmployeeToBankAccount(Long employeeId, int bankAccountId) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        if (!bankAccount.getEmployees().contains(employee)) {
            bankAccount.getEmployees().add(employee);
        }

        if (!employee.getBankAccounts().contains(bankAccount)) {
            employee.getBankAccounts().add(bankAccount);
        }

        bankAccountService.updateBankAccount(bankAccount);
        employeeService.updateEmployee(employee);
    }

    public void removeEmployeeFromBankAccount(Long employeeId, int bankAccountId) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        bankAccount.getEmployees().remove(employee);
        employee.getBankAccounts().remove(bankAccount);

        bankAccountService.updateBankAccount(bankAccount);
        employeeService.updateEmployee(employee);
    }
    public void addEmployeeToBranch(Long employeeId, int branchId) {
        Branch branch = branchService.getBranchById(branchId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Use EmployeeService to add branch to employee
        employeeService.addBranchToEmployee(employeeId, branch);

        System.out.println("Employee " + employeeId + " added to Branch " + branchId);
    }

    public void removeEmployeeFromBranch(Long employeeId, int branchId) {
        Branch branch = branchService.getBranchById(branchId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Use EmployeeService to remove branch from employee
        employeeService.removeBranchFromEmployee(employeeId, branch);

        System.out.println("Employee " + employeeId + " removed from Branch " + branchId);
    }
}
