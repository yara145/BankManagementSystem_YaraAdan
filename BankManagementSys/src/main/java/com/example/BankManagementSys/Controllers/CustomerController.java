package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Exceptions.BankAccountNotFoundException;
import com.example.BankManagementSys.Exceptions.CustomerNotFoundException;
import com.example.BankManagementSys.Services.BankAccountService;
import com.example.BankManagementSys.Services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private BankAccountService bankAccountService;
    @GetMapping("getAll")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    @GetMapping("get/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }
        return ResponseEntity.ok(customer);
    }

    @PostMapping("add")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.addNewCustomer(customer);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating customer: " + e.getMessage());
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<String> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        updatedCustomer.setIdCode(id);
        customerService.updateCustomer(updatedCustomer);
        return ResponseEntity.ok("Customer updated successfully.");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully.");
    }

    @GetMapping("get/{id}/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccounts(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getBankAccountsForCustomer(id));
    }


    @GetMapping("get/username/{username}")
    public ResponseEntity<Customer> getCustomerByUsername(@PathVariable String username) {
        return ResponseEntity.ok(customerService.getCustomerByUsername(username));
    }

    @GetMapping("get/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }
    // ✅ Delete customer by username
    @DeleteMapping("delete/username/{username}")
    public ResponseEntity<String> deleteCustomerByUsername(@PathVariable String username) {
        customerService.deleteCustomerByUsername(username);
        return ResponseEntity.ok("Customer with username '" + username + "' deleted successfully.");
    }

    // ✅ Get bank accounts by username
    @GetMapping("get/username/{username}/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccountsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(customerService.getBankAccountsForUsername(username));
    }

    @PutMapping("connect/{customerId}/bankAccount/{accountId}")
    public ResponseEntity<String> connectBankAccountToCustomer(
            @PathVariable Long customerId,
            @PathVariable int accountId) {
        try {
            // Fetch customer
            Customer customer = customerService.getCustomerById(customerId);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Customer with ID " + customerId + " not found.");
            }

            // Fetch bank account
            BankAccount bankAccount = bankAccountService.getBankAccountById(accountId);
            if (bankAccount == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Bank account with ID " + accountId + " not found.");
            }

            // Connect the bank account to the customer
            customerService.addBankAccountToCustomer(customerId, bankAccount);
            return ResponseEntity.ok("Bank account successfully linked to customer ID " + customerId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // ✅ Disconnect Bank Account from Customer
    @DeleteMapping("disconnect/{customerId}/bankAccount/{accountId}")
    public ResponseEntity<String> disconnectBankAccountFromCustomer(
            @PathVariable Long customerId, @PathVariable int accountId) {
        try {
            Customer customer = customerService.getCustomerById(customerId);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Customer with ID " + customerId + " not found.");
            }

            BankAccount bankAccount = bankAccountService.getBankAccountById(accountId);
            if (bankAccount == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Bank account with ID " + accountId + " not found.");
            }

            customerService.removeBankAccountFromCustomer(customerId, accountId);
            return ResponseEntity.ok("Bank account successfully unlinked from customer ID " + customerId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // ✅ Get Customer ID by Username
    @GetMapping("get/id/username/{username}")
    public ResponseEntity<Long> getCustomerIdByUsername(@PathVariable String username) {
        return ResponseEntity.ok(customerService.getCustomerIdByUsername(username));
    }



}
