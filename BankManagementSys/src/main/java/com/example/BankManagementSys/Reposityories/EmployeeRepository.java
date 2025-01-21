package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,String> {
}
