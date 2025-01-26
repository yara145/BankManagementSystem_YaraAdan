package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Employee;
import jakarta.transaction.Transactional;
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

    @Transactional
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

        System.out.println("Employee " + employeeId + " linked to Bank Account " + bankAccountId);
    }

    @Transactional
    public void removeEmployeeFromBankAccount(Long employeeId, int bankAccountId) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        bankAccount.getEmployees().remove(employee);
        employee.getBankAccounts().remove(bankAccount);

        bankAccountService.updateBankAccount(bankAccount);
        employeeService.updateEmployee(employee);

        System.out.println("Employee " + employeeId + " unlinked from Bank Account " + bankAccountId);
    }

    @Transactional
    public void addEmployeeToBranch(Long employeeId, int branchId) {
        Branch branch = branchService.getBranchById(branchId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        if (!branch.getEmployees().contains(employee)) {
            branch.getEmployees().add(employee);
        }

        if (!employee.getBranches().contains(branch)) {
            employee.getBranches().add(branch);
        }

        branchService.updateBranch(branch);
        employeeService.updateEmployee(employee);

        System.out.println("Employee " + employeeId + " linked to Branch " + branchId);
    }

    @Transactional
    public void removeEmployeeFromBranch(Long employeeId, int branchId) {
        Branch branch = branchService.getBranchById(branchId);
        Employee employee = employeeService.getEmployeeById(employeeId);

        branch.getEmployees().remove(employee);
        employee.getBranches().remove(branch);

        branchService.updateBranch(branch);
        employeeService.updateEmployee(employee);

        System.out.println("Employee " + employeeId + " unlinked from Branch " + branchId);
    }
}


