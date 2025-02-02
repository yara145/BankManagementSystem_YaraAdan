package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.Bank;
import com.example.BankManagementSys.Entities.BankAccount;
import com.example.BankManagementSys.Entities.Branch;
import com.example.BankManagementSys.Entities.Customer;
import com.example.BankManagementSys.Reposityories.BankRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private BranchService branchService;

    //________________________________C.R.U.D Functions___________________
    public Bank addBank(Bank bank) {
        if (bank.getName() == null || bank.getName().isEmpty()) {
            throw new IllegalArgumentException("Bank name cannot be null or empty.");
        }
        System.out.println("***bank created successfully***");
        return bankRepository.save(bank);
    }

    // Update an existing bank
    public Bank updateBank(Bank bank) {
        if (!bankRepository.existsById(bank.getId())) {
            throw new IllegalArgumentException("Bank with ID " + bank.getId() + " does not exist.");
        }
        if (bank.getName() == null || bank.getName().isEmpty()) {
            throw new IllegalArgumentException("Bank name cannot be null or empty.");
        }

        return bankRepository.save(bank);
    }
    // Delete a bank
    public void deleteBank(int bankId) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Bank with ID " + bankId + " does not exist."));

        if (!bank.getBranches().isEmpty()) {
            throw new IllegalStateException("Cannot delete a bank with associated branches.");
        }

        bankRepository.deleteById(bankId);
    }


    // Get a bank by ID
    public Bank getBankById(int bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Bank with ID " + bankId + " does not exist."));
    }
    // Get all banks
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    //__________________________________BranchToBank______________________
    @Transactional
    public void addBranchToBank(int bankId, Branch branch) {
        // Fetch bank using service instead of repository
        Bank bank = getBankById(bankId);

        // Set the bank reference
        branch.setBank(bank);

        // Use branchService to save the branch (instead of repository)
        branch = branchService.saveBranch(branch);

        // Add the branch to the bank's list
        bank.getBranches().add(branch);

        // Save the bank to persist the relationship (use service if needed)
        bankRepository.save(bank);

        System.out.println("**** Branch Has Been Added to Bank ****");
    }

    public List<Branch> getBranchesForBank(int bankId) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Bank with ID " + bankId + " does not exist."));
        return bank.getBranches();
    }
    //_____________________________other functions____________________

}
