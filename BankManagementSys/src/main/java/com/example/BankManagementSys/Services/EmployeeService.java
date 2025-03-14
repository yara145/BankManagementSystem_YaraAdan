package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Entities.Employee;
import com.example.BankManagementSys.Enums.BankAccountStatus;
import com.example.BankManagementSys.Exceptions.EmployeeNotFoundException;
import com.example.BankManagementSys.Reposityories.BranchRepository;
import com.example.BankManagementSys.Reposityories.CustomerRepository;
import com.example.BankManagementSys.Reposityories.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService extends UserService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerService customerService;
    @Autowired
   private BankAccountService bankAccountService;

    @Autowired
    private BranchService branchService;



    //  Get Employee by ID with Error Handling
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found."));
    }

    //  Get Employee by Username with Error Handling
    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByUserName(username)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with username '" + username + "' not found."));
    }

    //  Add Employee
    public Employee addNewEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    //  Update Employee
    public void updateEmployee(Employee employee) {
        if (!employeeRepository.existsById(employee.getIdCode())) {
            throw new EmployeeNotFoundException("Employee with ID " + employee.getIdCode() + " not found.");
        }
        employeeRepository.save(employee);
    }

    //  Delete Employee with Error Handling
    public void deleteEmployeeAndCleanup(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    //  Assign Bank Account to Employee (With Error Handling)
    @Transactional
    public void addBankAccountToEmployee(Long id, BankAccount bankAccount) {
        Employee employee = getEmployeeById(id);

        if (employee.getBankAccounts().contains(bankAccount)) {
            throw new IllegalStateException("Bank account is already assigned to this employee.");
        }

        employee.getBankAccounts().add(bankAccount);
        employeeRepository.save(employee);
    }

    //  Remove Bank Account from Employee (With Error Handling)
    @Transactional
    public void removeBankAccountFromEmployee(Long id, BankAccount bankAccount) {
        Employee employee = getEmployeeById(id);

        if (!employee.getBankAccounts().contains(bankAccount)) {
            throw new IllegalArgumentException("Bank account is not assigned to this employee.");
        }

        employee.getBankAccounts().remove(bankAccount);
        employeeRepository.save(employee);
    }

    //  Assign Employee to Branch (With Error Handling)
    @Transactional
    public void addBranchToEmployee(Long id, Branch branch) {
        Employee employee = getEmployeeById(id);
        Branch existingBranch = branchService.getBranchById(branch.getId());


        if (employee.getBranches().contains(existingBranch)) {
            throw new IllegalArgumentException("Employee is already assigned to this branch.");
        }

        employee.getBranches().add(existingBranch);
        employeeRepository.save(employee);
    }

    //  Remove Employee from Branch (With Error Handling)
    @Transactional
    public void removeBranchFromEmployee(Long id, Branch branch) {
        Employee employee = getEmployeeById(id);
        Branch existingBranch = branchService.getBranchById(branch.getId());


        if (!employee.getBranches().contains(existingBranch)) {
            throw new IllegalArgumentException("Employee is not assigned to this branch.");
        }

        employee.getBranches().remove(existingBranch);
        employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    //  Get Employee ID by Username
    public Long getEmployeeIdByUsername(String username) {
        Employee employee = employeeRepository.findByUserName(username)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username: " + username));
        return employee.getIdCode();
    }

    @Transactional
    public Customer addCustomerByEmployee(Long employeeId, Customer customer) {
        // Validate employee existence
        Employee employee = getEmployeeById(employeeId);

        // Ensure the employee is assigned to at least one branch
        if (employee.getBranches().isEmpty()) {
            throw new IllegalArgumentException(" Employee is not assigned to any branch and cannot add customers.");
        }

        // Validate customer details (avoid duplicate emails or usernames)
        customerService.validateCustomerDetails(customer);

        //  Use existing method to add a new customer securely
        return customerService.addNewCustomer(customer);
    }


    //function that suspend a specific bank account
    @Transactional
    public void suspendBankAccount(Long employeeId, int bankAccountId) {
        // Ensure the employee exists
        Employee employee = getEmployeeById(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee with ID " + employeeId + " not found.");
        }

        // Retrieve the bank account
        BankAccount bankAccount =  bankAccountService.getBankAccountById(bankAccountId);

        //  Check if the employee is linked to this account
        if (!employee.getBankAccounts().contains(bankAccount)) {
            throw new IllegalArgumentException("Employee does not have permission to suspend this bank account.");
        }

        //  Prevent re-suspending an already suspended account
        if (bankAccount.getStatus() == BankAccountStatus.SUSPENDED) {
            throw new IllegalArgumentException("Bank account is already suspended.");
        }
        //  Prevent susbending a closed account
        if (bankAccount.getStatus() == BankAccountStatus.CLOSED) {
            throw new IllegalArgumentException("Bank account is closed and it can not be susbended.");
        }

        //  Change status to SUSPENDED
        bankAccount.setStatus(BankAccountStatus.SUSPENDED);

        //  Use existing method to update the account
        bankAccountService.updateBankAccount(bankAccount);
    }



    //  Function that restricts a specific bank account
    @Transactional
    public void restrictBankAccount(Long employeeId, int bankAccountId) {
        // Ensure the employee exists
        Employee employee = getEmployeeById(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee with ID " + employeeId + " not found.");
        }

        // Retrieve the bank account
        BankAccount bankAccount =  bankAccountService.getBankAccountById(bankAccountId);

        //  Ensure employee is linked to the branch that owns this account
        boolean isAuthorized = employee.getBranches().contains(bankAccount.getBranch());
        if (!isAuthorized) {
            throw new IllegalArgumentException("Employee does not have permission to restrict this bank account.");
        }

        //  Prevent re-restricting an already restricted account
        if (bankAccount.getStatus() == BankAccountStatus.RESTRICTED) {
            throw new IllegalArgumentException("Bank account is already restricted.");
        }


        //  Prevent restricting a closed account
        if (bankAccount.getStatus() == BankAccountStatus.CLOSED) {
            throw new IllegalArgumentException("Bank account is closed and it can not be restricted.");
        }

        //  Change status to RESTRICTED
        bankAccount.setStatus(BankAccountStatus.RESTRICTED);

        //  Use existing method to update the account
        bankAccountService.updateBankAccount(bankAccount);
    }

    @Transactional
    public Customer updateCustomerByEmployee(Long employeeId, Long customerId, Customer updatedCustomer) {
        // Validate employee existence
        Employee employee = getEmployeeById(employeeId);

        // Retrieve the existing customer
        Customer existingCustomer = customerService.getCustomerById(customerId);

        //  Ensure employee is assigned to a branch where the customer has an account
        boolean isAuthorized = employee.getBranches().stream()
                .anyMatch(branch -> branch.getBankAccounts().stream()
                        .anyMatch(acc -> acc.getCustomer().getIdCode().equals(customerId)));

        if (!isAuthorized) {
            throw new IllegalArgumentException(" Employee does not have permission to update this customer.");
        }

        //  Only update fields that are provided in the request
        if (updatedCustomer.getName() != null) {
            existingCustomer.setName(updatedCustomer.getName());
        }
        if (updatedCustomer.getAddress() != null) {
            existingCustomer.setAddress(updatedCustomer.getAddress());
        }
        if (updatedCustomer.getEmail() != null && !existingCustomer.getEmail().equals(updatedCustomer.getEmail())) {
            existingCustomer.setEmail(updatedCustomer.getEmail());
            customerService.validateCustomerDetails(existingCustomer); //  Only validate if email changes
        }
        if (updatedCustomer.getBirthdate() != null) {
            existingCustomer.setBirthdate(updatedCustomer.getBirthdate());
        }

        //  Prevent updating `userName`
        if (updatedCustomer.getUserName() != null) {
            throw new IllegalArgumentException(" Username cannot be changed.");
        }

        //  Save and return updated customer
        return customerService.updateCustomer(existingCustomer);
    }

    @Transactional
    public void activateBankAccount(Long employeeId, int bankAccountId) {
        // Ensure the employee exists
        Employee employee = getEmployeeById(employeeId);

        // Retrieve the bank account
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);

        //  Ensure employee is assigned to the branch of this account
        boolean isAuthorized = employee.getBranches().contains(bankAccount.getBranch());
        if (!isAuthorized) {
            throw new IllegalArgumentException("Employee does not have permission to activate this bank account.");
        }

        //  Prevent re-activating an already active account
        if (bankAccount.getStatus() == BankAccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Bank account is already active.");
        }
        //  Prevent activating a closed account
        if (bankAccount.getStatus() == BankAccountStatus.CLOSED) {
            throw new IllegalArgumentException("Bank account is closed and it can not be activated.");
        }

        //  Change status to ACTIVE
        bankAccount.setStatus(BankAccountStatus.ACTIVE);

        //  Use existing method to update the account
        bankAccountService.updateBankAccount(bankAccount);
    }

    @Transactional
    public void createBankAccountForCustomer(Long employeeId, Long customerId, int branchId, BankAccount bankAccount) {
        // Validate employee existence
        Employee employee = getEmployeeById(employeeId);

        // Validate customer existence
        Customer customer = customerService.getCustomerById(customerId);

        // Validate branch existence
        Branch branch = branchService.getBranchById(branchId);

        // Ensure employee is authorized to create an account for the customer
        boolean isAuthorized = employee.getBranches().contains(branch);
        if (!isAuthorized) {
            throw new IllegalArgumentException("Employee does not have permission to create a bank account for this customer.");
        }

        // Assign customer and branch to the bank account
        bankAccount.setCustomer(customer);
        bankAccount.setBranch(branch);
        bankAccount.setStatus(BankAccountStatus.ACTIVE); // Activate account by default


        // Save the bank account
        bankAccountService.saveBankAccount(bankAccount);
        this.addBankAccountToEmployee(employeeId, bankAccount);
    }

    @Transactional
    public void deleteCustomerByEmployee(Long employeeId, Long customerId) {
        Employee employee = getEmployeeById(employeeId);
        Customer customer = customerService.getCustomerById(customerId);

        boolean isAuthorized = employee.getBranches().stream()
                .anyMatch(branch -> branch.getBankAccounts().stream()
                        .anyMatch(account ->
                                account.getCustomer() != null &&
                                        account.getCustomer().getIdCode().equals(customerId)));

        if (!isAuthorized) {
            throw new IllegalArgumentException("Employee does not have permission to delete this customer.");
        }


        List<BankAccount> customerAccounts = customer.getBankAccounts(); //  Get accounts directly
        for (BankAccount account : customerAccounts) {
            account.setCustomer(null);  //  Unlink customer from account
            bankAccountService.saveBankAccount(account);
        }

        customerService.deleteCustomer(customerId);
    }






}
