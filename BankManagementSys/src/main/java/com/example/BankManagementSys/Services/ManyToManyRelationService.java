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

        // Delegate to EmployeeService to add the BankAccount to the Employee
        employeeService.addBankAccountToEmployee(employeeId, bankAccount);
        System.out.println("Employee " + employeeId + " added to BankAccount " + bankAccountId);
    }

    public void removeEmployeeFromBankAccount(Long employeeId, int bankAccountId) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Delegate to EmployeeService to remove the BankAccount from the Employee
        employeeService.removeBankAccountFromEmployee(employeeId, bankAccount);
        System.out.println("Employee " + employeeId + " removed from BankAccount " + bankAccountId);
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
