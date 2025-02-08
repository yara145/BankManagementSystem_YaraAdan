package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Exceptions.EmployeeNotFoundException;
import com.example.BankManagementSys.Reposityories.BranchRepository;
import com.example.BankManagementSys.Reposityories.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService extends UserService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BranchRepository branchRepository;

    // ✅ Get Employee by ID with Error Handling
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found."));
    }

    // ✅ Get Employee by Username with Error Handling
    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByUserName(username)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with username '" + username + "' not found."));
    }

    // ✅ Add Employee
    public Employee addNewEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // ✅ Update Employee
    public void updateEmployee(Employee employee) {
        if (!employeeRepository.existsById(employee.getIdCode())) {
            throw new EmployeeNotFoundException("Employee with ID " + employee.getIdCode() + " not found.");
        }
        employeeRepository.save(employee);
    }

    // ✅ Delete Employee with Error Handling
    public void deleteEmployeeAndCleanup(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    // ✅ Assign Bank Account to Employee (With Error Handling)
    @Transactional
    public void addBankAccountToEmployee(Long id, BankAccount bankAccount) {
        Employee employee = getEmployeeById(id);

        if (employee.getBankAccounts().contains(bankAccount)) {
            throw new IllegalStateException("Bank account is already assigned to this employee.");
        }

        employee.getBankAccounts().add(bankAccount);
        employeeRepository.save(employee);
    }

    // ✅ Remove Bank Account from Employee (With Error Handling)
    @Transactional
    public void removeBankAccountFromEmployee(Long id, BankAccount bankAccount) {
        Employee employee = getEmployeeById(id);

        if (!employee.getBankAccounts().contains(bankAccount)) {
            throw new IllegalStateException("Bank account is not assigned to this employee.");
        }

        employee.getBankAccounts().remove(bankAccount);
        employeeRepository.save(employee);
    }

    // ✅ Assign Employee to Branch (With Error Handling)
    @Transactional
    public void addBranchToEmployee(Long id, Branch branch) {
        Employee employee = getEmployeeById(id);
        Branch existingBranch = branchRepository.findById(branch.getId())
                .orElseThrow(() -> new IllegalStateException("Branch not found."));

        if (employee.getBranches().contains(existingBranch)) {
            throw new IllegalStateException("Employee is already assigned to this branch.");
        }

        employee.getBranches().add(existingBranch);
        employeeRepository.save(employee);
    }

    // ✅ Remove Employee from Branch (With Error Handling)
    @Transactional
    public void removeBranchFromEmployee(Long id, Branch branch) {
        Employee employee = getEmployeeById(id);
        Branch existingBranch = branchRepository.findById(branch.getId())
                .orElseThrow(() -> new IllegalStateException("Branch not found."));

        if (!employee.getBranches().contains(existingBranch)) {
            throw new IllegalStateException("Employee is not assigned to this branch.");
        }

        employee.getBranches().remove(existingBranch);
        employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    // ✅ Get Employee ID by Username
    public Long getEmployeeIdByUsername(String username) {
        Employee employee = employeeRepository.findByUserName(username)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username: " + username));
        return employee.getIdCode();
    }
}
