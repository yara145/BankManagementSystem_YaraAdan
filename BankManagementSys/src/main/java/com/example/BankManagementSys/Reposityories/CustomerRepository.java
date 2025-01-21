package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,String> {
}
