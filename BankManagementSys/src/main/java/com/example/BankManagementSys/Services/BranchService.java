package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Reposityories.BankRepository;
import com.example.BankManagementSys.Reposityories.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private EmployeeService employeeService;


    //________________________________C.R.U.D functions_____________________________--
    // Create a new branch
    public Branch createNewBranch(Branch branch) {
        // Validate branch name
        if (branch.getName() == null || branch.getName().isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be null or empty.");
        }

        // Validate branch location
        if (branch.getLocation() == null || branch.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Branch location cannot be null or empty.");
        }



        // Save the new branch
        return branchRepository.save(branch);
    }

    // Update an existing branch
    public void updateBranch(Branch branch) {
        // Check if the branch exists
        if (!branchRepository.existsById(branch.getId())) {
            throw new IllegalArgumentException("Branch with ID " + branch.getId() + " does not exist.");
        }

        // Validate branch name
        if (branch.getName() == null || branch.getName().isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be null or empty.");
        }

        // Validate branch location
        if (branch.getLocation() == null || branch.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Branch location cannot be null or empty.");
        }



        // Save the updated branch
        branchRepository.save(branch);
    }

    // Retrieve all branches
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    // Delete a branch
    public void deleteBranch(Branch branch) {
        // Ensure the branch exists
        if (!branchRepository.existsById(branch.getId())) {
            throw new IllegalArgumentException("Branch with ID " + branch.getId() + " does not exist.");
        }

        // Ensure the branch has no associated bank accounts
        if (!branch.getBankAccounts().isEmpty()) {
            throw new IllegalStateException("Cannot delete a branch with associated bank accounts.");
        }
        if (!branch.getEmployees().isEmpty()) {
            throw new IllegalStateException("Cannot delete a branch with associated employees.");
        }
        // Delete the branch
        branchRepository.delete(branch);
    }

    //______________________________________BankAccountToBranch______________________
    public void addBankAccountToBranch(Integer branchId, BankAccount bankAccount) {
        // Find the branch by ID
        Optional<Branch> branchOptional = this.branchRepository.findById(branchId);
        if (branchOptional.isEmpty()) {
            throw new IllegalArgumentException("Branch not found.");
        }
        Branch branch = branchOptional.get();
        bankAccount.setBranch(branch);
        bankAccountService.UpdateBankAccount(bankAccount);
        branch.getBankAccounts().add(bankAccount);
        // Debugging/logging
        System.out.println("Bank accounts for branch " + branch.getName() + ": " + branch.getBankAccounts());
    }
    public Branch getBranchById(int branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with ID: " + branchId));
    }
    public List<BankAccount> getBankAccountsForBranch(int branchId) {
        Branch branch = getBranchById(branchId);
        return branch.getBankAccounts();
    }

    //________________________________EmployeeToBranch______________________________
    public void addEmployeeToBranch(int branchId, Employee employee) {
        Branch branch = getBranchById(branchId);

        // Add employee to branch's list of employees
        if (!branch.getEmployees().contains(employee)) {
            branch.getEmployees().add(employee);
        }

        // Add branch to employee's list of branches
        if (!employee.getBranches().contains(branch)) {
            employee.getBranches().add(branch);
        }

        // Save changes
        branchRepository.save(branch);
    }

    public void removeEmployeeFromBranch(int branchId, Employee employee) {
        Branch branch = getBranchById(branchId);

        // Remove employee from branch's list of employees
        branch.getEmployees().remove(employee);

        // Remove branch from employee's list of branches
        employee.getBranches().remove(branch);

        // Save changes
        branchRepository.save(branch);
    }
    public List<Employee> getEmployeesForBranch(int branchId) {
        Branch branch = getBranchById(branchId);
        return branch.getEmployees();
    }


}
