package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    Optional<Employee> findByUserName(String username);
}
