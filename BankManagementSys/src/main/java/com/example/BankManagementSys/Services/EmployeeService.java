package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Reposityories.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @Autowired
    @Lazy // Avoid circular dependency
    private ManyToManyRelationService relationService;

    // ___________________________C.R.U.D Functions_________________________
    public Employee addNewEmployee(Employee employee) {
        userService.validateUser(employee);

        if (employee.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required for employees.");
        }

        return employeeRepository.save(employee);
    }

    public void updateEmployee(Employee employee) {
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

        for (Branch branch : employee.getBranches()) {
            branch.getEmployees().remove(employee);
            branchService.updateBranch(branch);
        }

        employeeRepository.delete(employee);
    }

    // _________________________BankAccountToEmployee_______________________
    public void addBankAccountToEmployee(Long employeeId, BankAccount bankAccount) {
        relationService.addEmployeeToBankAccount(employeeId, bankAccount.getId());
    }

    public void removeBankAccountFromEmployee(Long employeeId, BankAccount bankAccount) {
        relationService.removeEmployeeFromBankAccount(employeeId, bankAccount.getId());
    }

    // _________________________BranchToEmployee_________________________
    @Transactional
    public void addBranchToEmployee(Long employeeId, Branch branch) {
        Employee employee = getEmployeeById(employeeId);

        if (!employee.getBranches().contains(branch)) {
            employee.getBranches().add(branch);
        }

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

    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
    }
}
