package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Exceptions.BankAccountNotFoundException;
import com.example.BankManagementSys.Exceptions.EmployeeNotFoundException;
import com.example.BankManagementSys.Services.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    @Autowired
    private CustomerService customerService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private BranchService branchService;

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




//************** Employee - Customer *********************8

    // ✅ Add a new customer by an authorized employee
    @PostMapping("{employeeId}/addCustomer")
    public ResponseEntity<String> addCustomerByEmployee(
            @PathVariable Long employeeId,
            @RequestBody Customer customer) {

        employeeService.addCustomerByEmployee(employeeId, customer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("✅ Customer '" + customer.getUserName() + "' added successfully.");
    }


    // ✅ Create a bank account for a customer by an authorized employee
    @PostMapping("{employeeId}/createBankAccount/customer/{customerId}/branch/{branchId}")
    public ResponseEntity<String> createBankAccountForCustomer(
            @PathVariable Long employeeId,
            @PathVariable Long customerId,
            @PathVariable int branchId,
            @RequestBody BankAccount bankAccount) {

        employeeService.createBankAccountForCustomer(employeeId, customerId, branchId, bankAccount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("✅ Bank account successfully created for customer ID " + customerId);
    }


    // ✅ Update customer by an authorized employee
    @PutMapping("{employeeId}/updateCustomer/{customerId}")
    public ResponseEntity<Customer> updateCustomerByEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long customerId,
            @RequestBody Customer updatedCustomer) {

        Customer updated = employeeService.updateCustomerByEmployee(employeeId, customerId, updatedCustomer);
        return ResponseEntity.ok(updated);
    }

    // ✅ Delete customer by an authorized employee
    @DeleteMapping("{employeeId}/deleteCustomer/{customerId}")
    public ResponseEntity<String> deleteCustomerByEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long customerId) {

        employeeService.deleteCustomerByEmployee(employeeId, customerId);
        return ResponseEntity.ok("✅ Customer deleted successfully, and their bank accounts were closed.");
    }

    // ✅ Suspend a bank account by an authorized employee
    @PutMapping("{employeeId}/suspendBankAccount/{bankAccountId}")
    public ResponseEntity<String> suspendBankAccount(
            @PathVariable Long employeeId,
            @PathVariable int bankAccountId) {

        employeeService.suspendBankAccount(employeeId, bankAccountId);
        return ResponseEntity.ok("✅ Bank account ID " + bankAccountId + " has been suspended.");
    }

    // ✅ Restrict a bank account by an authorized employee
    @PutMapping("{employeeId}/restrictBankAccount/{bankAccountId}")
    public ResponseEntity<String> restrictBankAccount(
            @PathVariable Long employeeId,
            @PathVariable int bankAccountId) {

        employeeService.restrictBankAccount(employeeId, bankAccountId);
        return ResponseEntity.ok("✅ Bank account ID " + bankAccountId + " has been restricted.");
    }

    // ✅ Activate a bank account by an authorized employee
    @PutMapping("{employeeId}/activateBankAccount/{bankAccountId}")
    public ResponseEntity<String> activateBankAccount(
            @PathVariable Long employeeId,
            @PathVariable int bankAccountId) {

        employeeService.activateBankAccount(employeeId, bankAccountId);
        return ResponseEntity.ok("✅ Bank account ID " + bankAccountId + " has been activated.");
    }
















//    // ✅ Enable an employee to add a new customer
//    @PostMapping("{employeeId}/addCustomer")
//    public ResponseEntity<String> addCustomerByEmployee(
//            @PathVariable Long employeeId,
//            @RequestBody Customer customer) {
//
//        Customer newCustomer = employeeService.addCustomerByEmployee(employeeId, customer);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body("✅ Customer '" + newCustomer.getUserName() + "' added successfully.");
//    }
//
//    // ✅ Enable an employee to update a customer
//    @PutMapping("{employeeId}/updateCustomer/{customerId}")
//    public ResponseEntity<Customer> updateCustomerByEmployee(
//            @PathVariable Long employeeId,
//            @PathVariable Long customerId,
//            @RequestBody Customer updatedCustomer) {
//
//        Customer updated = employeeService.updateCustomerByEmployee(employeeId, customerId, updatedCustomer);
//        return ResponseEntity.ok(updated);
//    }
//
//    // ✅ Enable an employee to delete a customer
//    @DeleteMapping("{employeeId}/deleteCustomer/{customerId}")
//    public ResponseEntity<String> deleteCustomerByEmployee(
//            @PathVariable Long employeeId,
//            @PathVariable Long customerId) {
//
//        employeeService.deleteCustomerByEmployee(employeeId, customerId);
//        return ResponseEntity.ok("✅ Customer deleted successfully, and their bank accounts were closed.");
//    }
//
//
//    // ✅ Enable an employee to create a bank account for a customer
//    @PostMapping("createBankAccount/{employeeId}/customer/{customerId}/branch/{branchId}")
//    public ResponseEntity<String> createBankAccountForCustomer(
//            @PathVariable Long employeeId,
//            @PathVariable Long customerId,
//            @PathVariable int branchId,
//            @RequestBody BankAccount bankAccount) {
//
//        employeeService.createBankAccountForCustomer(employeeId, customerId, branchId, bankAccount);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body("✅ Bank account successfully created for customer ID " + customerId);
//    }
//
//    // ✅ Enable an employee to suspend a specific bank account
//    @PutMapping("suspendBankAccount/{employeeId}/{bankAccountId}")
//    public ResponseEntity<String> suspendBankAccount(
//            @PathVariable Long employeeId,
//            @PathVariable int bankAccountId) {
//
//        employeeService.suspendBankAccount(employeeId, bankAccountId);
//        return ResponseEntity.ok("✅ Bank account ID " + bankAccountId + " has been suspended.");
//    }
//
//    // ✅ Enable an employee to restrict a specific bank account
//    @PutMapping("restrictBankAccount/{employeeId}/{bankAccountId}")
//    public ResponseEntity<String> restrictBankAccount(
//            @PathVariable Long employeeId,
//            @PathVariable int bankAccountId) {
//
//        employeeService.restrictBankAccount(employeeId, bankAccountId);
//        return ResponseEntity.ok("✅ Bank account ID " + bankAccountId + " has been restricted.");
//    }
//
//    // ✅ Enable an employee to activate a bank account
//    @PutMapping("activateBankAccount/{employeeId}/{bankAccountId}")
//    public ResponseEntity<String> activateBankAccount(
//            @PathVariable Long employeeId,
//            @PathVariable int bankAccountId) {
//
//        employeeService.activateBankAccount(employeeId, bankAccountId);
//        return ResponseEntity.ok("✅ Bank account ID " + bankAccountId + " has been activated.");
//    }
//
//
















}






/*

// function that enable the employee to create bank account for a customer
@PostMapping("createBankAccount/{employeeId}/customer/{customerId}/branch/{branchId}")
public ResponseEntity<String> createBankAccountForCustomer(
        @PathVariable Long employeeId,
        @PathVariable Long customerId,
        @PathVariable int branchId,
        @RequestBody BankAccount bankAccount) {
    try {
        // Validate employee existence
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Ensure employee is linked to a branch that has the customer
        boolean isAuthorized = employee.getBranches().stream()
                .anyMatch(branch -> branch.getBankAccounts().stream()
                        .anyMatch(acc -> acc.getCustomer().getIdCode().equals(customerId)));

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Employee does not have permission to create a bank account for this customer.");
        }

        // Assign the bank account to the customer
        customerService.addBankAccountToCustomer(customerId, bankAccount);
        branchService.addBankAccountToBranch(branchId, bankAccount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Bank account successfully created for customer ID " + customerId);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

// function that enable the employee to suspend a specific bank account
@PutMapping("suspendBankAccount/{employeeId}/{bankAccountId}")
public ResponseEntity<String> suspendBankAccount(
        @PathVariable Long employeeId,
        @PathVariable int bankAccountId) {
    try {
        // Validate employee existence
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Get bank account details
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);

        // Ensure employee is assigned to the branch of the bank account
        boolean isAuthorized = employee.getBranches().contains(bankAccount.getBranch());

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Employee does not have permission to suspend this bank account.");
        }

        // Use existing method to suspend the account
        employeeService.suspendBankAccount(employeeId, bankAccountId);

        return ResponseEntity.ok("Bank account ID " + bankAccountId + " has been suspended.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

// ✅ Function to restrict a specific bank account (by an authorized employee)
@PutMapping("restrictBankAccount/{employeeId}/{bankAccountId}")
public ResponseEntity<String> restrictBankAccount(
        @PathVariable Long employeeId,
        @PathVariable int bankAccountId) {
    try {
        // Validate employee existence
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Get bank account details
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);

        // Ensure employee is assigned to the branch of the bank account
        boolean isAuthorized = employee.getBranches().contains(bankAccount.getBranch());

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Employee does not have permission to restrict this bank account.");
        }

        // Use existing method to restrict the account
        employeeService.restrictBankAccount(employeeId, bankAccountId);

        return ResponseEntity.ok("Bank account ID " + bankAccountId + " has been restricted.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}





//add new customer
@PostMapping("{employeeId}/addCustomer")
public ResponseEntity<String> addCustomerByEmployee(
        @PathVariable Long employeeId,
        @RequestBody Customer customer) {
    try {
        // Validate employee existence
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Ensure the employee is assigned to at least one branch
        if (employee.getBranches().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ Employee is not assigned to any branch and cannot add customers.");
        }

        // Securely create the customer
        Customer newCustomer = employeeService.addCustomerByEmployee(employeeId, customer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("✅ Customer '" + newCustomer.getUserName() + "' added successfully.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Error: " + e.getMessage());
    }

}

@PutMapping("{employeeId}/updateCustomer/{customerId}")
public ResponseEntity<?> updateCustomerByEmployee(
        @PathVariable Long employeeId,
        @PathVariable Long customerId,
        @RequestBody Customer updatedCustomer) {
    try {
        // ✅ Call the updated method
        Customer updated = employeeService.updateCustomerByEmployee(employeeId, customerId, updatedCustomer);
        return ResponseEntity.ok(updated); // ✅ Return updated customer
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("❌ Email already exists.");
    }
}


@DeleteMapping("{employeeId}/deleteCustomer/{customerId}")
public ResponseEntity<String> deleteCustomerByEmployee(
        @PathVariable Long employeeId,
        @PathVariable Long customerId) {
    try {
        employeeService.getEmployeeById(employeeId); // Ensure employee exists
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("✅ Customer deleted successfully, and their bank accounts were closed.");
    } catch (IllegalArgumentException | IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Error: " + e.getMessage());
    }
}

@PutMapping("activateBankAccount/{employeeId}/{bankAccountId}")
public ResponseEntity<String> activateBankAccount(
        @PathVariable Long employeeId,
        @PathVariable int bankAccountId) {
    try {
        // Call the service method to activate the bank account
        employeeService.activateBankAccount(employeeId, bankAccountId);

        return ResponseEntity.ok("✅ Bank account ID " + bankAccountId + " has been activated.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Error: " + e.getMessage());
    }
}


*/
