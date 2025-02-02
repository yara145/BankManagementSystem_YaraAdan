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

    // ✅ Get all customers
    @GetMapping("getAll")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // ✅ Get a customer by ID
    @GetMapping("get/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }
        return ResponseEntity.ok(customer);
    }

    // ✅ Create a new customer
    @PostMapping("add")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.addNewCustomer(customer);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating customer: " + e.getMessage());
        }
    }

    // ✅ Update customer details
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        updatedCustomer.setIdCode(id);
        customerService.updateCustomer(updatedCustomer);
        return ResponseEntity.ok("Customer updated successfully.");
    }

    // ✅ Delete a customer
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully.");
    }

    // 🔗 Get all bank accounts for a customer
    @GetMapping("get/{id}/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccounts(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getBankAccountsForCustomer(id));
    }

    // 🔗 Add a bank account to a customer
    @PostMapping("add/{id}/accounts")
    public ResponseEntity<String> addBankAccount(@PathVariable Long id, @RequestBody BankAccount bankAccount) {
        customerService.addBankAccountToCustomer(id, bankAccount);
        return ResponseEntity.ok("Bank account added to customer successfully.");
    }
}
