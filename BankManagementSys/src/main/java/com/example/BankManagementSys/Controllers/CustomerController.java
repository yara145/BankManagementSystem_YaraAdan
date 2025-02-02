package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Customer;
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

    // ✅ Get all customers (using your preferred name)
    @GetMapping("getAll")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // ✅ Get a customer by ID (keeping "get" in the name)
    @GetMapping("get/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    // ✅ Create a new customer (keeping "add" in the name)
    @PostMapping("add")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.addNewCustomer(customer));
    }

    // ✅ Update customer details (keeping "update" in the name)
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        updatedCustomer.setIdCode(id);
        customerService.updateCustomer(updatedCustomer);
        return ResponseEntity.ok("Customer updated successfully.");
    }

    // ✅ Delete a customer (keeping "delete" in the name)
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully.");
    }

    // ✅ Get all bank accounts for a customer (keeping "get" in the name)
    @GetMapping("get/{id}/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccounts(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getBankAccountsForCustomer(id));
    }

    // ✅ Add a bank account to a customer (keeping "add" in the name)
    @PostMapping("add/{id}/accounts")
    public ResponseEntity<String> addBankAccount(@PathVariable Long id, @RequestBody BankAccount bankAccount) {
        customerService.addBankAccountToCustomer(id, bankAccount);
        return ResponseEntity.ok("Bank account added to customer successfully.");
    }
}
