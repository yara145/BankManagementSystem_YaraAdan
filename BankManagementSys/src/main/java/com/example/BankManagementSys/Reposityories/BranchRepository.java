package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch,Integer> {
}
