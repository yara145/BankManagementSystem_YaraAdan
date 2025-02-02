package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Exceptions.CustomerNotFoundException;
import com.example.BankManagementSys.Services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

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

    @PostMapping("add/{id}/accounts")
    public ResponseEntity<String> addBankAccount(@PathVariable Long id, @RequestBody BankAccount bankAccount) {
        customerService.addBankAccountToCustomer(id, bankAccount);
        return ResponseEntity.ok("Bank account added to customer successfully.");
    }
}
