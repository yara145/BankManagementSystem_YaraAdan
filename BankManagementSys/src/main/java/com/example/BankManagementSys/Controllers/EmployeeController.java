package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Exceptions.EmployeeNotFoundException;
import com.example.BankManagementSys.Services.EmployeeService;
import com.example.BankManagementSys.Services.ManyToManyRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ManyToManyRelationService manyToManyRelationService;
    // ✅ Links an employee to a bank account.
    @PutMapping("connect/{employeeId}/bankAccount/{bankAccountId}")
    public ResponseEntity<String> connectEmployeeToBankAccount(
            @PathVariable Long employeeId,
            @PathVariable int bankAccountId) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee with ID " + employeeId + " not found.");
            }

            manyToManyRelationService.addEmployeeToBankAccount(employeeId, bankAccountId);
            return ResponseEntity.ok("Employee successfully linked to bank account ID " + bankAccountId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // ✅ Get Employee ID by Username
    @GetMapping("get/id/username/{username}")
    public ResponseEntity<Long> getEmployeeIdByUsername(@PathVariable String username) {
        return ResponseEntity.ok(employeeService.getEmployeeIdByUsername(username));
    }


    // ✅ Removes an employee from a bank account.
    @DeleteMapping("disconnect/{employeeId}/bankAccount/{bankAccountId}")
    public ResponseEntity<String> disconnectEmployeeFromBankAccount(
            @PathVariable Long employeeId,
            @PathVariable int bankAccountId) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee with ID " + employeeId + " not found.");
            }

            manyToManyRelationService.removeEmployeeFromBankAccount(employeeId, bankAccountId);
            return ResponseEntity.ok("Employee successfully unlinked from bank account ID " + bankAccountId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Links an employee to a branch.
    @PutMapping("connect/{employeeId}/branch/{branchId}")
    public ResponseEntity<String> connectEmployeeToBranch(
            @PathVariable Long employeeId,
            @PathVariable int branchId) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee with ID " + employeeId + " not found.");
            }

            manyToManyRelationService.addEmployeeToBranch(employeeId, branchId);
            return ResponseEntity.ok("Employee successfully linked to branch ID " + branchId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Removes an employee from a branch.
    @DeleteMapping("disconnect/{employeeId}/branch/{branchId}")
    public ResponseEntity<String> disconnectEmployeeFromBranch(
            @PathVariable Long employeeId,
            @PathVariable int branchId) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee with ID " + employeeId + " not found.");
            }

            manyToManyRelationService.removeEmployeeFromBranch(employeeId, branchId);
            return ResponseEntity.ok("Employee successfully unlinked from branch ID " + branchId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Get all employees
    @GetMapping("getAll")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ✅ Get an employee by ID (with error handling)
    @GetMapping("get/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    // ✅ Get an employee by username (with error handling)
    @GetMapping("get/username/{username}")
    public ResponseEntity<Employee> getEmployeeByUsername(@PathVariable String username) {
        return ResponseEntity.ok(employeeService.getEmployeeByUsername(username));
    }

    // ✅ Create a new employee
    @PostMapping("add")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.addNewEmployee(employee));
    }

    // ✅ Update employee details (with error handling)
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        updatedEmployee.setIdCode(id);
        employeeService.updateEmployee(updatedEmployee);
        return ResponseEntity.ok("Employee updated successfully.");
    }

    // ✅ Delete an employee (with error handling)
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployeeAndCleanup(id);
        return ResponseEntity.ok("Employee deleted successfully.");
    }

    // 🔗 Assign a bank account to an employee (with error handling)
    @PostMapping("add/{id}/bankAccount")
    public ResponseEntity<String> addBankAccountToEmployee(@PathVariable Long id, @RequestBody BankAccount bankAccount) {
        employeeService.addBankAccountToEmployee(id, bankAccount);
        return ResponseEntity.ok("Bank account assigned to employee successfully.");
    }

    // 🔗 Remove a bank account from an employee (with error handling)
    @DeleteMapping("remove/{id}/bankAccount")
    public ResponseEntity<String> removeBankAccountFromEmployee(@PathVariable Long id, @RequestBody BankAccount bankAccount) {
        employeeService.removeBankAccountFromEmployee(id, bankAccount);
        return ResponseEntity.ok("Bank account removed from employee successfully.");
    }

    // 🔗 Assign an employee to a branch (with error handling)
    @PostMapping("add/{id}/branch")
    public ResponseEntity<String> addBranchToEmployee(@PathVariable Long id, @RequestBody Branch branch) {
        employeeService.addBranchToEmployee(id, branch);
        return ResponseEntity.ok("Employee assigned to branch successfully.");
    }

    // 🔗 Remove an employee from a branch (with error handling)
    @DeleteMapping("remove/{id}/branch")
    public ResponseEntity<String> removeBranchFromEmployee(@PathVariable Long id, @RequestBody Branch branch) {
        employeeService.removeBranchFromEmployee(id, branch);
        return ResponseEntity.ok("Employee removed from branch successfully.");
    }
}
