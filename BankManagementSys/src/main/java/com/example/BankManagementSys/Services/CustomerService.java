package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Exceptions.CustomerNotFoundException;
import com.example.BankManagementSys.Reposityories.BranchRepository;
import com.example.BankManagementSys.Reposityories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService extends UserService {

    @Value("${customer.min-age}")
    private int minCustomerAge;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BranchRepository branchRepository;

    //________________________________C.R.U.D Functions___________________

    public Customer addNewCustomer(Customer customer) throws IllegalArgumentException {
        try {
            // Validate shared attributes using UserService
            validateUser(customer);

            // Check if the age is legal
            int age = customerRepository.calculateAge(customer.getBirthdate());
            if (age < minCustomerAge) {
                throw new IllegalArgumentException("Customer's age is below the minimum required: " + minCustomerAge);
            }

            // Set the join date to today
            customer.setJoinDate(new Date());
            System.out.println("******************Customer created successfully************");

            // Save the new customer
            return this.customerRepository.save(customer);

        } catch (DataIntegrityViolationException e) {
            Throwable rootCause = e.getRootCause();
            String errorMessage = "A record with the provided unique value already exists.";

            if (rootCause instanceof ConstraintViolationException) {
                if (rootCause.getMessage().contains("email")) {
                    errorMessage = "A user with this email already exists.";
                } else if (rootCause.getMessage().contains("userName")) {
                    errorMessage = "A user with this username already exists.";
                }
            }

            System.out.println("DataIntegrityViolationException fired: " + errorMessage);
            throw new DataIntegrityViolationException(errorMessage);
        }
    }

    public void updateCustomer(Customer customer) {
        try {
            this.customerRepository.save(customer);
        } catch (DataIntegrityViolationException e) {
            Throwable rootCause = e.getRootCause();
            String errorMessage = "A record with the provided unique value already exists.";

            if (rootCause instanceof ConstraintViolationException) {
                if (rootCause.getMessage().contains("email")) {
                    errorMessage = "A user with this email already exists.";
                } else if (rootCause.getMessage().contains("userName")) {
                    errorMessage = "A user with this username already exists.";
                }
            }

            System.out.println("DataIntegrityViolationException fired: " + errorMessage);
            throw new DataIntegrityViolationException(errorMessage);
        }
    }

    public List<Customer> getAllCustomers() {
        return this.customerRepository.findAll();
    }

    public void deleteCustomer(Long customerId) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found."));

        if (!existingCustomer.getBankAccounts().isEmpty()) {
            throw new IllegalStateException("Cannot delete a customer with associated bank accounts.");
        }
        customerRepository.deleteById(customerId);
    }

    // ✅ Get customer by ID
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
    }

    //________________________________Bank Account to customer___________________

    @Transactional
    public void addBankAccountToCustomer(Long customerId, BankAccount bankAccount) {
        // Fetch customer from the repository (ensures it's managed)
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        // Set the customer reference
        bankAccount.setCustomer(customer);

        // If branch is assigned, fetch it first to ensure it's managed
        if (bankAccount.getBranch() != null) {
            Branch branch = branchRepository.findById(bankAccount.getBranch().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found."));
            bankAccount.setBranch(branch);
        }

        // Save the bank account (ensures it is managed)
        bankAccount = bankAccountService.updateBankAccount(bankAccount);

        // Add the managed bank account to the customer
        customer.getBankAccounts().add(bankAccount);

        // Save customer to persist relationship
        customerRepository.save(customer);

        System.out.println("**** BankAccount Has Been Added to Customer ****");
    }

    public List<BankAccount> getBankAccountsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        return customer.getBankAccounts();
    }
    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByUserName(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with username: " + username));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));
    }
    public List<BankAccount> getBankAccountsForUsername(String username) {
        Customer customer = customerRepository.findByUserName(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with username '" + username + "' not found."));

        return customer.getBankAccounts();
    }
    // ✅ Delete customer by username
    public void deleteCustomerByUsername(String username) {
        Customer customer = customerRepository.findByUserName(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with username '" + username + "' not found."));

        if (!customer.getBankAccounts().isEmpty()) {
            throw new IllegalStateException("Cannot delete a customer with associated bank accounts.");
        }

        customerRepository.delete(customer);
    }

}
