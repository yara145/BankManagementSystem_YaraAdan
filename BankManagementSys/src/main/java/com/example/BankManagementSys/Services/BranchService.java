package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.*;
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

    public void addBankAccountToBranch(Integer branchId, BankAccount bankAccount) {
        Optional<Branch> branchOptional = branchRepository.findById(branchId);
        if (branchOptional.isEmpty()) {
            throw new IllegalArgumentException("Branch not found.");
        }
        Branch branch = branchOptional.get();
        bankAccount.setBranch(branch);

        // Updated method call
        bankAccountService.updateBankAccount(bankAccount);

        branch.getBankAccounts().add(bankAccount);
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
}
