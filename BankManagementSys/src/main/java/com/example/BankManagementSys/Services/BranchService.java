package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
import com.example.BankManagementSys.Reposityories.BranchRepository;
import jakarta.transaction.Transactional;
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

    // __________________________________ C.R.U.D FUNCTIONS __________________________________

    public Branch createNewBranch(Branch branch) {
        if (branch.getName() == null || branch.getName().isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be null or empty.");
        }
        if (branch.getLocation() == null || branch.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Branch location cannot be null or empty.");
        }
        return branchRepository.save(branch);
    }

    public void updateBranch(Branch branch) {
        if (!branchRepository.existsById(branch.getId())) {
            throw new IllegalArgumentException("Branch with ID " + branch.getId() + " does not exist.");
        }
        branchRepository.save(branch);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public void deleteBranch(Branch branch) {
        if (!branchRepository.existsById(branch.getId())) {
            throw new IllegalArgumentException("Branch with ID " + branch.getId() + " does not exist.");
        }
        if (!branch.getBankAccounts().isEmpty()) {
            throw new IllegalStateException("Cannot delete a branch with associated bank accounts.");
        }
        branchRepository.delete(branch);
    }

    // __________________________________ BANK ACCOUNT TO BRANCH __________________________________
    @Transactional
    public void addBankAccountToBranch(int branchId, BankAccount bankAccount) {
        // Fetch the branch from the repository (ensures it's managed)
        Branch branch = getBranchById(branchId);
        if (branch == null) {
            throw new IllegalArgumentException("Branch not found.");
        }

        // Check if the bank account already exists (optional, based on your logic)
        if (bankAccount.getId() > 0) {
            bankAccount = bankAccountService.getBankAccountById(bankAccount.getId());
            if (bankAccount == null) {
                throw new IllegalArgumentException("Bank account not found.");
            }
        }

        // Associate the bank account with the branch
        bankAccount.setBranch(branch);

        // Save the bank account (ensures it's managed)
        bankAccount = bankAccountService.saveBankAccount(bankAccount);

        // Add the bank account to the branch's list
        branch.getBankAccounts().add(bankAccount);

        // Save the branch (ensures the relationship is persisted)
       saveBranch(branch);

        System.out.println("**** BankAccount Has Been Added to Branch ****");
    }


    public Branch getBranchById(int branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with ID: " + branchId));
    }

    public List<BankAccount> getBankAccountsForBranch(int branchId) {
        Branch branch = getBranchById(branchId);
        return branch.getBankAccounts();
    }
    @Transactional
    public Branch saveBranch(Branch branch) {
        if (branch.getId() > 0 && branchRepository.existsById(branch.getId())) {
            // Check if the ID is greater than 0 (indicating a valid existing entity)
            return branchRepository.save(branch); // Update if exists
        }
        return branchRepository.save(branch); // Create new if doesn't exist
    }


}
