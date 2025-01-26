package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Reposityories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private BranchService branchService;

    // ___________________________C.R.U.D Functions_________________________
    public Employee addNewEmployee(Employee employee) {
        // Validate shared attributes using UserService
        userService.validateUser(employee);

        // Validate start date
        if (employee.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required for employees.");
        }

        return employeeRepository.save(employee);
    }

    public void updateEmployee(Employee employee) {
        // Ensure the employee exists before updating
        if (!employeeRepository.existsById(employee.getIdCode())) {
            throw new IllegalArgumentException("Employee not found with ID: " + employee.getIdCode());
        }
        employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void deleteEmployeeAndCleanup(Long employeeId) {
        Employee employee = getEmployeeById(employeeId);

        // Remove associations with branches
        for (Branch branch : employee.getBranches()) {
            branch.getEmployees().remove(employee);
            branchService.updateBranch(branch);
        }

        // Remove associations with bank accounts
        for (BankAccount account : employee.getBankAccounts()) {
            account.getEmployees().remove(employee);
            bankAccountService.UpdateBankAccount(account);
        }

        employeeRepository.delete(employee);
    }

    // _________________________BankAccountToEmployee_______________________
    public void addBankAccountToEmployee(Long employeeId, BankAccount bankAccount) {
        Employee employee = getEmployeeById(employeeId);

        // Add employee to bank account
        if (!bankAccount.getEmployees().contains(employee)) {
            bankAccount.getEmployees().add(employee);
            bankAccountService.UpdateBankAccount(bankAccount);
        }

        // Add bank account to employee
        if (!employee.getBankAccounts().contains(bankAccount)) {
            employee.getBankAccounts().add(bankAccount);
        }

        employeeRepository.save(employee);
    }

    public void removeBankAccountFromEmployee(Long employeeId, BankAccount bankAccount) {
        Employee employee = getEmployeeById(employeeId);

        employee.getBankAccounts().remove(bankAccount);
        bankAccount.getEmployees().remove(employee);

        employeeRepository.save(employee);
        bankAccountService.UpdateBankAccount(bankAccount);
    }

    // _________________________BranchToEmployee_________________________
    public void addBranchToEmployee(Long employeeId, Branch branch) {
        Employee employee = getEmployeeById(employeeId);

        // Add branch to employee's list of branches
        if (!employee.getBranches().contains(branch)) {
            employee.getBranches().add(branch);
        }

        // Add employee to branch's list of employees
        if (!branch.getEmployees().contains(employee)) {
            branch.getEmployees().add(employee);
        }

        employeeRepository.save(employee);
        branchService.updateBranch(branch);
    }

    public void removeBranchFromEmployee(Long employeeId, Branch branch) {
        Employee employee = getEmployeeById(employeeId);

        employee.getBranches().remove(branch);
        branch.getEmployees().remove(employee);

        employeeRepository.save(employee);
        branchService.updateBranch(branch);
    }

    // ___________________________Utility Functions_________________________
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
    }

    public List<Employee> getEmployeesByBranch(int branchId) {
        Branch branch = branchService.getBranchById(branchId);
        return branch.getEmployees();
    }

    public List<Employee> getEmployeesByBankAccount(int bankAccountId) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);
        return bankAccount.getEmployees();
    }
}
