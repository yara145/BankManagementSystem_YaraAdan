package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Exceptions.EmployeeNotFoundException;
import com.example.BankManagementSys.Services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // ✅ Get all employees
    @GetMapping("getAll")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ✅ Get an employee by ID
    @GetMapping("get/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    // ✅ Create a new employee
    @PostMapping("add")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.addNewEmployee(employee));
    }

    // ✅ Update employee details
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        updatedEmployee.setIdCode(id);
        employeeService.updateEmployee(updatedEmployee);
        return ResponseEntity.ok("Employee updated successfully.");
    }

    // ✅ Delete an employee
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployeeAndCleanup(id);
        return ResponseEntity.ok("Employee deleted successfully.");
    }

    // 🔗 Assign a bank account to an employee
    @PostMapping("add/{id}/bankAccount")
    public ResponseEntity<String> addBankAccountToEmployee(@PathVariable Long id, @RequestBody BankAccount bankAccount) {
        employeeService.addBankAccountToEmployee(id, bankAccount);
        return ResponseEntity.ok("Bank account assigned to employee successfully.");
    }

    // 🔗 Remove a bank account from an employee
    @DeleteMapping("remove/{id}/bankAccount")
    public ResponseEntity<String> removeBankAccountFromEmployee(@PathVariable Long id, @RequestBody BankAccount bankAccount) {
        employeeService.removeBankAccountFromEmployee(id, bankAccount);
        return ResponseEntity.ok("Bank account removed from employee successfully.");
    }

    // 🔗 Assign an employee to a branch
    @PostMapping("add/{id}/branch")
    public ResponseEntity<String> addBranchToEmployee(@PathVariable Long id, @RequestBody Branch branch) {
        employeeService.addBranchToEmployee(id, branch);
        return ResponseEntity.ok("Employee assigned to branch successfully.");
    }

    // 🔗 Remove an employee from a branch
    @DeleteMapping("remove/{id}/branch")
    public ResponseEntity<String> removeBranchFromEmployee(@PathVariable Long id, @RequestBody Branch branch) {
        employeeService.removeBranchFromEmployee(id, branch);
        return ResponseEntity.ok("Employee removed from branch successfully.");
    }
}
