package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.Bank;
import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Reposityories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Value("${customer.min-age}")
    private int minCustomerAge;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private UserService userService;

    //________________________________C.R.U.D Functions___________________
    public Customer addNewCustomer(Customer customer) throws IllegalArgumentException {
        // Validate shared attributes using UserService
        userService.validateUser(customer);
        //check if the age is legal
        int age = customerRepository.calculateAge(customer.getBirthdate());
        if (age < minCustomerAge) {
            throw new IllegalArgumentException("Customer's age is below the minimum required: " + minCustomerAge);
        }
        // Set the join date to today
        customer.setJoinDate(new Date());
        System.out.println("******************Customer created successfully************");
        //  Save the new customer
        return this.customerRepository.save(customer);
    }

    public void updateCustomer(Customer customer) {
        // Perform update operations
        this.customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return this.customerRepository.findAll();
    }
    public void deleteCustomer(Long customerId) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        // Check if the customer has associated bank accounts
        if (!existingCustomer.getBankAccounts().isEmpty()) {
            throw new IllegalStateException("Cannot delete a customer with associated bank accounts.");
        }

        // If no bank accounts, delete the customer
        customerRepository.deleteById(customerId);
    }
    //________________________________Bank Account to customer___________________
    @Transactional
    public void addBankAccountToCustomer(Long customerId, BankAccount bankAccount) {
        Optional<Customer> customerOptional = this.customerRepository.findById(customerId);
        if(customerOptional.isEmpty())
        {
            throw new IllegalArgumentException("Customer not found.");
        }
       bankAccount.setCustomer(customerOptional.get());
        bankAccountService.updateBankAccount(bankAccount);
        customerOptional.get().getBankAccounts().add(bankAccount);
        System.out.println(customerOptional.get().getBankAccounts());
        System.out.println("****BankAccount Has Been added to customer****");
    }
    public List<BankAccount> getBankAccountsForCustomer(Long customerId) {//*gettings bankaccounts for the customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        return customer.getBankAccounts();
    }

}
